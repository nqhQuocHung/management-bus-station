import {
  Widget,
  addUserMessage,
  addResponseMessage,
  renderCustomComponent,
  deleteMessages,
  setBadgeCount,
} from 'react-chat-widget';
import 'react-chat-widget/lib/styles.css';
import './styles.css';
import {useEffect, useRef} from 'react';
import databaseRef from '../../config/firebase';
import moment from 'moment';
const Chat = ({
  subtitle,
  avatar,
  senderId,
  receiverId,
  conversationKey,
  isCompany,
}) => {
  const messageRef = useRef(
    databaseRef.child(`/chats/${conversationKey}/messages`),
  );
  const badgeRef = useRef(null);
  const receiverBadgeRef = useRef(null);

  const handleSendMessage = (newMessage) => {
    messageRef.current.push({
      message: newMessage,
      senderId: senderId,
      timestamp: new Date().getTime(),
    });

    badgeRef.current.set(0);

    receiverBadgeRef.current.once('value', (snapshot) => {
      const data = snapshot.val();
      receiverBadgeRef.current.set(data + 1);
    });

    renderCustomComponent(SendTime, {
      timestamp: new Date().getTime(),
      isSender: true,
    });
  };

  const fetchMessages = () => {
    messageRef.current.once('value', (snapshot) => {
      snapshot.forEach((child) => {
        const data = child.val();
        if (data['senderId'] === receiverId) {
          addResponseMessage(data['message']);
          renderCustomComponent(SendTime, {
            timestamp: data['timestamp'],
            isSender: false,
          });
        }
        if (data['senderId'] === senderId) {
          addUserMessage(data['message']);
          renderCustomComponent(SendTime, {
            timestamp: data['timestamp'],
            isSender: true,
          });
        }
      });
    });
  };

  useEffect(() => {
    // listen new messages
    messageRef.current
      .orderByChild('timestamp')
      .startAt(Date.now())
      .limitToLast(1)
      .on('child_added', (snapshot) => {
        const data = snapshot.val();
        console.log(data);
        if (data['senderId'] === receiverId) {
          addResponseMessage(data['message']);
          renderCustomComponent(SendTime, {
            timestamp: data['timestamp'],
            isSender: false,
          });
        }
      });

    // listen to badge

    if (isCompany) {
      badgeRef.current = databaseRef
        .child('/companies_keys/')
        .child(senderId)
        .child(conversationKey)
        .child('unread');
      receiverBadgeRef.current = databaseRef
        .child('/users_keys/')
        .child(receiverId)
        .child(conversationKey)
        .child('unread');
    } else {
      badgeRef.current = databaseRef
        .child('/users_keys/')
        .child(senderId)
        .child(conversationKey)
        .child('unread');
      receiverBadgeRef.current = databaseRef
        .child('/companies_keys/')
        .child(receiverId)
        .child(conversationKey)
        .child('unread');
    }

    badgeRef.current.on('value', (snapshot) => {
      const data = snapshot.val();

      if (data !== 0) {
        setBadgeCount(data);
      }
    });

    // init
    fetchMessages();

    return () => {
      deleteMessages();
      badgeRef.current.off('value');
      messageRef.current.off('child_added');
    };
  }, []);

  return (
    <Widget
      showCloseButton={true}
      chatId={conversationKey}
      emojis={true}
      titleAvatar={avatar}
      title="Tư vấn trực tuyến"
      subtitle={subtitle}
      handleNewUserMessage={handleSendMessage}
      senderPlaceHolder={'Nhập tin nhắn của bạn'}
      showTimeStamp={false}
    />
  );
};

const SendTime = ({timestamp, isSender}) => {
  return (
    <div
      className={[
        'text-muted',
        'chat-time',
        isSender ? 'sender' : 'receiver',
      ].join(' ')}
    >
      {moment(timestamp).fromNow()}
    </div>
  );
};

export default Chat;
