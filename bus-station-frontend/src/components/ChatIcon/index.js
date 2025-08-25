import React, { useState, useContext, useEffect } from "react";
import ChatSocket from "../ChatSocket";
import "./styles.css";
import { LoadingContext, AuthenticationContext } from "../../config/context";
import { apis } from "../../config/apis";
import { RiMessage3Line } from "react-icons/ri";
import { io } from "socket.io-client";

const socket = io("http://localhost:4000");

const ChatIcon = () => {
  const [showMessages, setShowMessages] = useState(false);
  const [selectedChat, setSelectedChat] = useState(null);
  const [receiverInfo, setReceiverInfo] = useState({});
  const [conversations, setConversations] = useState([]);
  const { user } = useContext(AuthenticationContext);
  const accessToken = localStorage.getItem("accessToken");
  const { setLoading } = useContext(LoadingContext);
  const [companyId, setCompanyId] = useState(null);

  // lấy thông tin công ty
  useEffect(() => {
    const fetchCompany = async () => {
      try {
        setLoading("flex");
        const api = apis(accessToken);
        const response = await api.get(`/api/companies/manager/${user.id}`);
        const company = Array.isArray(response.data)
          ? response.data[0]
          : response.data;
        if (company) setCompanyId(company.id);
      } catch (error) {
        console.error("Error fetching company", error);
      } finally {
        setLoading("none");
      }
    };
    if (user && user.id) fetchCompany();
  }, [user, accessToken, setLoading]);

  // Đăng ký userId để nhận cập nhật hội thoại
  useEffect(() => {
    if (companyId) {
      socket.emit("register user", companyId);
    }
  }, [companyId]);

  // lấy danh sách hội thoại
  const fetchConversations = async () => {
    if (!companyId) return;
    try {
      setLoading("flex");
      const response = await fetch(
        `http://localhost:4000/api/conversations/${companyId}`
      );
      const data = await response.json();

      const api = apis(accessToken);
      const enrichedData = await Promise.all(
        data.map(async (conv) => {
          const opponentId =
            conv.user1_id === companyId ? conv.user2_id : conv.user1_id;
          try {
            const res = await api.get(`/api/users/${opponentId}`);
            return { ...conv, ...res.data, isUnread: false };
          } catch (err) {
            return { ...conv, isUnread: false };
          }
        })
      );

      setConversations(enrichedData);
    } catch (error) {
      console.error("Error fetching conversations", error);
    } finally {
      setLoading("none");
    }
  };

  useEffect(() => {
    if (showMessages && companyId) {
      fetchConversations();
    }
  }, [showMessages, companyId]);

  // Nghe sự kiện cập nhật hội thoại từ server
  useEffect(() => {
    const api = apis(accessToken);

    socket.on("conversation updated", async (conv) => {
      setConversations((prev) => {
        const idx = prev.findIndex(
          (c) => c.conversationId === conv.conversationId
        );

        if (idx >= 0) {
          // 🔹 Update hội thoại cũ
          const updated = [...prev];
          updated[idx] = {
            ...updated[idx],
            lastMessage: conv.lastMessage,
            lastTime: conv.lastTime,
            isUnread:
              !selectedChat ||
              conv.conversationId !== selectedChat.conversationId,
          };
          return updated.sort(
            (a, b) => new Date(b.lastTime) - new Date(a.lastTime)
          );
        } else {
          // 🔹 Hội thoại mới
          const opponentId =
            conv.senderId === companyId ? conv.receiverId : conv.senderId;

          const newConv = {
            conversationId: conv.conversationId,
            user1_id: conv.senderId,
            user2_id: conv.receiverId,
            lastMessage: conv.lastMessage,
            lastTime: conv.lastTime,
            isUnread: true,
          };

          // Fetch thêm info user đối phương
          (async () => {
            try {
              const res = await api.get(`/api/users/${opponentId}`);
              setConversations((prev2) =>
                prev2.map((c) =>
                  c.conversationId === conv.conversationId
                    ? { ...c, ...res.data }
                    : c
                )
              );
            } catch (err) {
              console.error("Error fetching user info for new conv", err);
            }
          })();

          return [newConv, ...prev];
        }
      });
    });

    return () => {
      socket.off("conversation updated");
    };
  }, [selectedChat, companyId, accessToken]);

  // lấy info đối phương khi mở box
  useEffect(() => {
    const fetchReceiverInfo = async (receiverId) => {
      try {
        setLoading("flex");
        const api = apis(accessToken);
        const response = await api.get(`/api/users/${receiverId}`);
        setReceiverInfo(response.data || {});
      } catch (error) {
        console.error("Error fetching receiver info", error);
      } finally {
        setLoading("none");
      }
    };

    if (selectedChat) {
      const opponentId =
        selectedChat.user1_id === companyId
          ? selectedChat.user2_id
          : selectedChat.user1_id;
      fetchReceiverInfo(opponentId);

      // reset unread
      setConversations((prev) =>
        prev.map((c) =>
          c.conversationId === selectedChat.conversationId
            ? { ...c, isUnread: false }
            : c
        )
      );
    }
  }, [selectedChat, companyId]);

  return (
    <div className="chat-icon-container">
      <div className="chat-icon" onClick={() => setShowMessages(!showMessages)}>
        <RiMessage3Line size="2em" color="#6D75D5" />
      </div>

      {/* danh sách hội thoại */}
      {showMessages && (
        <div className="message-list-container">
          {Array.isArray(conversations) &&
            conversations.map((conv) => {
              const opponentId =
                conv.user1_id === companyId ? conv.user2_id : conv.user1_id;
              return (
                <div
                  key={conv.conversationId}
                  className="message-list-item"
                  onClick={() => {
                    setSelectedChat(conv);
                    setShowMessages(false);
                  }}
                >
                  <img
                    src={conv.avatar || "https://via.placeholder.com/40"}
                    alt="avatar"
                    className="message-list-avatar"
                  />
                  <div className="message-list-content">
                    <div className="message-list-name">
                      {conv.username || `User ${opponentId}`}
                      {conv.isUnread && (
                        <span className="message-list-unread-dot"></span>
                      )}
                    </div>
                    <div className="message-list-preview">
                      {conv.lastMessage || "Chưa có tin nhắn"}
                    </div>
                  </div>
                  <div className="message-list-time">
                    {conv.lastTime
                      ? new Date(conv.lastTime).toLocaleTimeString([], {
                          hour: "2-digit",
                          minute: "2-digit",
                        })
                      : ""}
                  </div>
                </div>
              );
            })}
        </div>
      )}

      {/* box chat */}
      {selectedChat && receiverInfo && (
        <ChatSocket
          key={selectedChat.conversationId}
          senderId={companyId}
          receiverId={
            selectedChat.user1_id === companyId
              ? selectedChat.user2_id
              : selectedChat.user1_id
          }
          avatar={receiverInfo.avatar}
          subtitle={`Trò chuyện với ${receiverInfo.firstname || ""} ${
            receiverInfo.lastname || ""
          } [${receiverInfo.username || ""}]`}
          isCompany={true}
          onClose={() => setSelectedChat(null)}
        />
      )}
    </div>
  );
};

export default ChatIcon;
