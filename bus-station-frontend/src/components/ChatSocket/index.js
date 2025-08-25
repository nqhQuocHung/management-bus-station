import React, { useState, useEffect, useRef } from "react";
import { io } from "socket.io-client";
import { apis } from "../../config/apis";
import "./styles.css";
import EmojiPicker from "emoji-picker-react";

const socket = io("http://localhost:4000");

function ChatSocket({ senderId, receiverId, avatar, subtitle, isCompany, onClose }) {
  const [message, setMessage] = useState("");
  const [chat, setChat] = useState([]);
  const [receiverInfo, setReceiverInfo] = useState(null);

  // ✅ state cho emoji
  const [showEmoji, setShowEmoji] = useState(false);

  const accessToken = localStorage.getItem("accessToken");
  const api = apis(accessToken);

  const conversationKey = `room_${Math.min(senderId, receiverId)}_${Math.max(senderId, receiverId)}`;

  // ✅ ref để scroll xuống cuối
  const messagesEndRef = useRef(null);

  // 📌 Scroll tự động khi chat thay đổi
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: "smooth" });
    }
  }, [chat]);

  // 📌 Lấy info người nhận
  useEffect(() => {
    const fetchReceiverInfo = async () => {
      try {
        const res = await api.get(`/api/users/${receiverId}`);
        setReceiverInfo(res.data);
      } catch (err) {
        console.error("❌ Error fetching receiver info:", err);
      }
    };
    if (receiverId) fetchReceiverInfo();
  }, [receiverId]);

  // 📌 Socket join room và nhận tin nhắn
  useEffect(() => {
    if (!conversationKey) return;

    socket.emit("join room", { conversationId: conversationKey, user1Id: senderId, user2Id: receiverId });

    socket.on("chat history", ({ conversationId, messages }) => {
      if (conversationId === conversationKey) {
        const normalized = messages.map((m) => ({
          ...m,
          senderId: Number(m.senderId ?? m.sender_id),
          receiverId: Number(m.receiverId ?? m.receiver_id),
          createdAt: m.createdAt || m.created_at,
        }));
        setChat(normalized);
      }
    });

    socket.on("chat message", (msg) => {
      if (msg.conversationId === conversationKey) {
        setChat((prev) => [
          ...prev,
          {
            ...msg,
            senderId: Number(msg.senderId ?? msg.sender_id),
            receiverId: Number(msg.receiverId ?? msg.receiver_id),
            createdAt: msg.createdAt || msg.created_at || new Date(),
          },
        ]);
      }
    });

    return () => {
      socket.off("chat history");
      socket.off("chat message");
    };
  }, [conversationKey, senderId, receiverId]);

  const sendMessage = () => {
    if (message.trim() !== "") {
      socket.emit("chat message", {
        conversationId: conversationKey,
        senderId,
        receiverId,
        username: isCompany ? "Company" : "User",
        text: message,
      });
      setMessage("");
    }
  };

  return (
    <div className="ChatSocket__box">
      <div className="ChatSocket__header">
        <div className="ChatSocket__headerInfo">
          <img src={avatar} alt="avatar" className="ChatSocket__avatar" />
          <span className="ChatSocket__subtitle">
            {receiverInfo
              ? `${receiverInfo.firstname} ${receiverInfo.lastname}`
              : subtitle}
          </span>
        </div>
        <button className="ChatSocket__closeBtn" onClick={onClose}>✖</button>
      </div>

      <div className="ChatSocket__messages">
        {chat.map((msg, idx) => {
          const isMe = Number(msg.senderId) === Number(senderId);
          const prev = chat[idx - 1];
          const sameSender = prev && prev.senderId === msg.senderId;

          const time = new Date(msg.createdAt || Date.now()).toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
          });

          return (
            <div key={idx} className={`ChatSocket__message ${isMe ? "me" : "other"}`}>
              {!isMe && !sameSender && receiverInfo && (
                <div className="ChatSocket__messageHeader">
                  <img src={receiverInfo.avatar} alt="avatar" className="ChatSocket__msgAvatar" />
                  <span className="ChatSocket__msgName">
                    {receiverInfo.firstname} {receiverInfo.lastname}
                  </span>
                </div>
              )}
              <div
                className="ChatSocket__bubble"
                style={{
                  borderTopLeftRadius: !isMe && sameSender ? 6 : 18,
                  borderTopRightRadius: isMe && sameSender ? 6 : 18,
                }}
              >
                {msg.text}
                <div className="ChatSocket__time">{time}</div>
              </div>
            </div>
          );
        })}
        <div ref={messagesEndRef} />
      </div>

      <div className="ChatSocket__input">
        <input
          className="ChatSocket__inputField"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && sendMessage()}
          placeholder="Type a message..."
        />

        {/* nút emoji */}
        <button
          type="button"
          className="ChatSocket__emojiBtn"
          onClick={() => setShowEmoji((prev) => !prev)}
        >
          😊
        </button>

        {showEmoji && (
          <div className="ChatSocket__emojiPicker">
            <EmojiPicker
              onEmojiClick={(emojiData) => {
                setMessage((prev) => prev + emojiData.emoji);
                setShowEmoji(false);
              }}
            />
          </div>
        )}


        <button className="ChatSocket__button" onClick={sendMessage}>
          Send
        </button>
      </div>
    </div>
  );
}

export default ChatSocket;
