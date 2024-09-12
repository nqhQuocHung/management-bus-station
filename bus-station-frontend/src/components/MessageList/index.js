import React, { useEffect, useState } from 'react';
import databaseRef from '../../config/firebase';
import { apis, endpoints } from '../../config/apis';
import './styles.css';

const MessageList = ({ onSelectChat, companyId, accessToken }) => {
  const [messages, setMessages] = useState([]);

  // Lấy tin nhắn từ firebase
  useEffect(() => {
    if (!companyId) return;

    const fetchMessages = async () => {
      const messagesRef = databaseRef.child(`/companies_keys/${companyId}`);
      messagesRef.on('value', async (snapshot) => {
        const data = snapshot.val();
        if (data) {
          const messageList = await Promise.all(Object.keys(data).map(async (key) => {
            const messageRef = databaseRef.child(`/chats/${key}/messages`).orderByChild('timestamp').limitToLast(1);
            const messageSnapshot = await messageRef.once('value');
            const lastMessageData = messageSnapshot.val();
            const lastMessage = lastMessageData ? Object.values(lastMessageData)[0] : {};

            const unreadRef = databaseRef.child(`/companies_keys/${companyId}/${key}/unread`);
            const unreadSnapshot = await unreadRef.once('value');
            const unreadCount = unreadSnapshot.val() || 0;

            const api = apis(accessToken);
            try {
              const userResponse = await api.get(endpoints.get_user_by_id(data[key].opponentId));
              const userData = userResponse.data;

              return {
                id: key,
                opponentId: data[key].opponentId,
                avatar: userData.avatar,
                firstname: userData.firstname,
                lastname: userData.lastname,
                timestamp: lastMessage.timestamp,
                lastMessage: lastMessage.message,
                unread: unreadCount
              };
            } catch (error) {
              console.error('Error fetching user data:', error);
              return {
                id: key,
                opponentId: data[key].opponentId,
                avatar: '/images/default-avatar.png',
                firstname: 'Unknown',
                lastname: 'User',
                timestamp: lastMessage.timestamp,
                lastMessage: lastMessage.message,
                unread: unreadCount
              };
            }
          }));
          //Sắp xếp tin nhắn từ mới đến cũ
          messageList.sort((a, b) => a.timestamp - b.timestamp);
          setMessages(messageList);
        }
      });

      return () => messagesRef.off('value');
    };

    fetchMessages();
  }, [companyId, accessToken]);

  return (
    <div className="message-list">
      {messages.map((message) => (
        <div key={message.id} className="message-item" onClick={() => onSelectChat(message)}>
          <img src={message.avatar || '/images/default-avatar.png'} alt="avatar" className="avatar" />
          <div className="message-info">
            <p className="message-sender">{`${message.firstname || ''} ${message.lastname || ''}`}</p>
            <p className="message-text">{message.lastMessage || 'No recent messages'}</p>
            <p className="message-time">{message.timestamp ? new Date(message.timestamp).toLocaleString() : 'Invalid Date'}</p>
            {message.unread > 0 && <span className="unread-count">{message.unread}</span>}
          </div>
        </div>
      ))}
    </div>
  );
};

export default MessageList;