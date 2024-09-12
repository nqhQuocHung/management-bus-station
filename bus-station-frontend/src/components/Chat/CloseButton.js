import React from 'react';
import './styles.css';

const CloseButton = ({ onClose }) => {
  return (
    <button className="custom-close-button" onClick={onClose}>
      X
    </button>
  );
};

export default CloseButton;