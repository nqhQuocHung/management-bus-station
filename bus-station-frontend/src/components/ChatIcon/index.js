import React, {useState, useContext, useEffect} from 'react';
import MessageList from '../MessageList';
import Chat from '../Chat';
import './styles.css';
import {LoadingContext, AuthenticationContext} from '../../config/context';
import {apis, endpoints} from '../../config/apis';
import {RiMessage3Line} from 'react-icons/ri';
const ChatIcon = () => {
  const [showMessages, setShowMessages] = useState(false);
  const [selectedChat, setSelectedChat] = useState(null);
  const [receiverInfo, setReceiverInfo] = useState({});
  const {user} = useContext(AuthenticationContext);
  const accessToken = localStorage.getItem('accessToken');
  const {setLoading} = useContext(LoadingContext);
  const [companyId, setCompanyId] = useState(null);
  const [companyAvt, setCompanyAvt] = useState(null);

  useEffect(() => {
    const fetchCompany = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(
          endpoints.get_company_managerid(user.id),
        );
        setCompanyId(response.data.id);
        setCompanyAvt(response.data.avatar);
        console.log('Url avatar: ', response.data.avatar);
      } catch (error) {
        console.error('Error fetching company', error);
      } finally {
        setLoading('none');
      }
    };

    if (user && user.id) {
      fetchCompany();
    }
  }, [user, accessToken, setLoading]);

  useEffect(() => {
    const fetchReceiverInfo = async (receiverId) => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.get_user_by_id(receiverId));
        setReceiverInfo(response.data);
        console.log('Receiver info:', response.data);
      } catch (error) {
        console.error('Error fetching receiver info', error);
      } finally {
        setLoading('none');
      }
    };

    if (selectedChat && selectedChat.opponentId) {
      fetchReceiverInfo(selectedChat.opponentId);
    }
  }, [selectedChat, accessToken, setLoading]);

  const toggleMessages = () => {
    setShowMessages(!showMessages);
  };

  const handleSelectChat = (chat) => {
    setSelectedChat(chat);
    setShowMessages(false);
  };

  const handleCloseChat = () => {
    setSelectedChat(null);
  };

  return (
    <div className="chat-icon-container">
      <div className="chat-icon" onClick={toggleMessages}>
        <RiMessage3Line size="2em" color="#6D75D5" />
      </div>
      {showMessages && (
        <div className="message-list-container">
          <MessageList
            onSelectChat={handleSelectChat}
            companyId={companyId}
            accessToken={accessToken}
          />
        </div>
      )}
      {selectedChat && receiverInfo && (
        <Chat
          key={selectedChat.id}
          conversationKey={selectedChat.id}
          senderId={companyId}
          receiverId={selectedChat.opponentId}
          avatar={receiverInfo.avatar}
          subtitle={`Trò chuyện với ${receiverInfo.firstname} ${receiverInfo.lastname} [${receiverInfo.username}]`}
          isCompany={true}
          onCloseChat={handleCloseChat}
        />
      )}
    </div>
  );
};

export default ChatIcon;
