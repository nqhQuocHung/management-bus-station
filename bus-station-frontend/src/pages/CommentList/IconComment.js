import React from 'react';
import './styles.css';

const IconComment = ({ comment }) => {
  const { avatar, firstname, lastname, content, rating, createdAt } = comment;

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    const time = date.toLocaleTimeString('vi-VN', { hour: '2-digit', minute: '2-digit' });
    const formattedDate = date.toLocaleDateString('vi-VN', { day: '2-digit', month: '2-digit', year: 'numeric' });
    return `${time} ${formattedDate}`;
  };

  return (
    <div className="icon-comment">
      <img
        src={avatar || '/images/avatar.png'}
        alt={`${firstname} ${lastname}`}
        className="comment-avatar"
      />
      <div className="comment-details">
        <h5 className="comment-user">
          {firstname} {lastname}
        </h5>
        <p className="comment-content">{content}</p>
        <div className="comment-rating">
          {[...Array(5)].map((_, index) => (
            <img
              key={index}
              src="/images/rating.png"
              alt="Rating Star"
              className={`star-icon ${index < rating ? 'selected' : 'unselected'}`}
            />
          ))}
          <span className="rating-value">({rating} sao)</span>
          <span className="comment-date">{formatDate(createdAt)}</span>
        </div>
      </div>
    </div>
  );
};

export default IconComment;
