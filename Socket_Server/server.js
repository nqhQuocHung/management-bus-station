const express = require("express");
const http = require("http");
const { Server } = require("socket.io");
const mysql = require("mysql2/promise");
const cors = require("cors");
const crypto = require("crypto");
require("dotenv").config();

const app = express();
const server = http.createServer(app);

app.use(
  cors({
    origin: "http://localhost:3000",
    methods: ["GET", "POST", "PUT", "DELETE", "OPTIONS"],
    allowedHeaders: ["Content-Type", "Authorization"],
  })
);
app.use(express.json());

const io = new Server(server, {
  cors: {
    origin: "http://localhost:3000",
    methods: ["GET", "POST"],
  },
});

// ✅ MySQL connection
let db;
(async () => {
  db = await mysql.createPool({
    host: "localhost",
    user: "root",
    password: "2410Mythao.@",
    database: "chat_service",
  });
  console.log("✅ Connected to MySQL (chat_service)");
})();

// ================= ENCRYPT / DECRYPT =================
const ALGORITHM = "aes-256-cbc";
const SECRET_KEY = crypto
  .createHash("sha256")
  .update(process.env.CHAT_SECRET_KEY || "default-secret-key")
  .digest("base64")
  .substr(0, 32);
const IV = Buffer.from(
  process.env.CHAT_SECRET_IV || "1234567890abcdef" // 16 bytes
);

function encrypt(text) {
  const cipher = crypto.createCipheriv(ALGORITHM, SECRET_KEY, IV);
  let encrypted = cipher.update(text, "utf8", "hex");
  encrypted += cipher.final("hex");
  return encrypted;
}

function decrypt(encryptedText) {
  try {
    const decipher = crypto.createDecipheriv(ALGORITHM, SECRET_KEY, IV);
    let decrypted = decipher.update(encryptedText, "hex", "utf8");
    decrypted += decipher.final("utf8");
    return decrypted;
  } catch (e) {
    console.error("❌ Error decrypting:", e.message);
    return "[Cannot decrypt]";
  }
}

// ================== API ==================

/**
 * API: Lấy danh sách hội thoại của 1 user
 */
app.get("/api/conversations/:userId", async (req, res) => {
  const { userId } = req.params;
  try {
    const [rows] = await db.query(
      `SELECT c.id as conversationId,
              c.user1_id,
              c.user2_id,
              m.text as lastMessage,
              m.created_at as lastTime
       FROM conversations c
       LEFT JOIN messages m 
            ON m.id = (
              SELECT id FROM messages 
              WHERE conversation_id = c.id 
              ORDER BY created_at DESC LIMIT 1
            )
       WHERE c.user1_id = ? OR c.user2_id = ?
       ORDER BY m.created_at DESC`,
      [userId, userId]
    );

    // giải mã lastMessage
    const convs = rows.map((row) => ({
      ...row,
      lastMessage: row.lastMessage ? decrypt(row.lastMessage) : null,
    }));

    res.json(convs);
  } catch (err) {
    console.error("❌ Error fetching conversations:", err);
    res.status(500).json({ error: "Cannot fetch conversations" });
  }
});

/**
 * API: Lấy danh sách công ty theo managerId
 */
app.get("/api/companies/manager/:id", async (req, res) => {
  const { id } = req.params;
  try {
    const [rows] = await db.query(
      "SELECT * FROM companies WHERE manager_id = ?",
      [id]
    );
    res.json(rows);
  } catch (err) {
    console.error("❌ Error fetching companies:", err);
    res.status(500).json({ error: "Cannot fetch companies" });
  }
});

// ================= SOCKET.IO =================
io.on("connection", (socket) => {
  console.log("🔵 User connected:", socket.id);

  // Mỗi client khi login sẽ emit userId để join room riêng
  socket.on("register user", (userId) => {
    socket.join(`user-${userId}`);
    console.log(`👤 User ${userId} joined personal room user-${userId}`);
  });

  // Join room hội thoại (dùng trong ChatSocket)
  socket.on("join room", async ({ conversationId, user1Id, user2Id }) => {
    try {
      socket.join(conversationId);
      console.log(`💬 User joined conversation room ${conversationId}`);

      // Nếu chưa có conversation thì tạo
      const [rows] = await db.query(
        "SELECT * FROM conversations WHERE id = ?",
        [conversationId]
      );
      if (rows.length === 0) {
        await db.query(
          "INSERT INTO conversations (id, user1_id, user2_id) VALUES (?, ?, ?)",
          [conversationId, user1Id, user2Id]
        );
        console.log(`📌 Created conversation ${conversationId}`);
      }

      // Load lịch sử tin nhắn
      const [rowsMsg] = await db.query(
        `SELECT 
            id,
            conversation_id AS conversationId,
            sender_id AS senderId,
            receiver_id AS receiverId,
            username,
            text,
            created_at AS createdAt
         FROM messages
         WHERE conversation_id = ?
         ORDER BY created_at ASC
         LIMIT 50`,
        [conversationId]
      );

      const messages = rowsMsg.map((m) => ({
        ...m,
        text: decrypt(m.text),
        senderId: Number(m.senderId),
        receiverId: Number(m.receiverId),
      }));

      socket.emit("chat history", { conversationId, messages });
    } catch (err) {
      console.error("❌ Error join room:", err);
    }
  });

  // Khi có tin nhắn mới
  socket.on("chat message", async (msg) => {
    try {
      const { conversationId, senderId, receiverId, username, text } = msg;

      // Mã hoá trước khi lưu DB
      const encryptedText = encrypt(text);

      const [result] = await db.query(
        "INSERT INTO messages (conversation_id, sender_id, receiver_id, username, text) VALUES (?, ?, ?, ?, ?)",
        [conversationId, senderId, receiverId, username, encryptedText]
      );

      const newMsg = {
        id: result.insertId,
        conversationId,
        senderId: Number(senderId),
        receiverId: Number(receiverId),
        username,
        text, // gửi bản đã giải mã cho client
        createdAt: new Date(),
      };

      // 🔹 Emit tin nhắn cho room hội thoại (chat box)
      io.to(conversationId).emit("chat message", newMsg);

      // 🔹 Emit cập nhật hội thoại cho cả 2 user liên quan (list chat)
      const convUpdate = {
        conversationId,
        lastMessage: text,
        lastTime: newMsg.createdAt,
        senderId: Number(senderId),
        receiverId: Number(receiverId),
      };

      io.to(`user-${senderId}`).emit("conversation updated", convUpdate);
      io.to(`user-${receiverId}`).emit("conversation updated", convUpdate);

      console.log(
        `📢 Emit conv update: ${conversationId} -> sender ${senderId}, receiver ${receiverId}`
      );
    } catch (err) {
      console.error("❌ Error saving message:", err);
    }
  });

  socket.on("disconnect", () => {
    console.log("🔴 User disconnected:", socket.id);
  });
});

// Start server
server.listen(4000, () => {
  console.log("🚀 Server running at http://localhost:4000");
});
