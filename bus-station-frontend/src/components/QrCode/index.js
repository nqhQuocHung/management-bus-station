import React from 'react';
import { QRCodeCanvas } from 'qrcode.react';
import './styles.css';

const QRCodeComponent = ({ paymentUrl }) => {
  return (
    <div className="qr-code">
      <p>Quét mã QR để thanh toán:</p>
      <QRCodeCanvas value={paymentUrl} size={300} />
    </div>
  );
};

export default QRCodeComponent;
