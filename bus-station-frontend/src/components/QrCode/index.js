import React from 'react';
import { QRCodeCanvas } from 'qrcode.react';
import './styles.css';

const QRCodeComponent = ({ value }) => {
  return (
    <div className="qr-code">
      <p>Quét mã QR để thanh toán:</p>
      <QRCodeCanvas value={value} size={300} />
    </div>
  );
};

export default QRCodeComponent;
