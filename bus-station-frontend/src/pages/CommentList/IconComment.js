import React from 'react';
import './styles.css';

const IconComment = ({ comment }) => {
  const { avatar, firstname, lastname, content, rating } = comment;

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
              className={`star-icon ${index < rating ? 'selected' : ''}`}
            />
          ))}
          <span className="rating-value">({rating} sao)</span>
        </div>
      </div>
    </div>
  );
};

export default IconComment;
