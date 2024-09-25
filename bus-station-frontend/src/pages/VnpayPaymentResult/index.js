import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import './styles.css';

const VnpayPaymentResult = () => {
  const { setLoading } = useContext(LoadingContext);
  const [paymentData, setPaymentData] = useState(null);
  const [ticketIds, setTicketIds] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPaymentResult = async () => {
      try {
        setLoading('flex');
        
        const storedTicketIds = localStorage.getItem('ticketIds');
        if (storedTicketIds) {
          const parsedTicketIds = JSON.parse(storedTicketIds);
          setTicketIds(parsedTicketIds);
        }

        const queryParams = new URLSearchParams(window.location.search);
        const paymentDetails = {
          amount: queryParams.get('vnp_Amount'),
          paymentCode: queryParams.get('vnp_TmnCode') || 'TXN123456789',
          bankCode: queryParams.get('vnp_BankCode'),
          transactionNo: queryParams.get('vnp_TransactionNo'),
          bankTransactionNo: queryParams.get('vnp_BankTranNo'),
          cardType: queryParams.get('vnp_CardType'),
          confirmAt: new Date().toISOString()
        };

        const response = await apis().post(endpoints.payment_bill, paymentDetails);
        setPaymentData(paymentDetails);

        const paymentId = response.data.id;
        await updateTicketPayment(ticketIds, paymentId);

      } catch (err) {
        console.error('Error fetching payment data or creating bill:', err);
      } finally {
        setLoading('none');
      }
    };

    const updateTicketPayment = async (ticketIds, paymentId) => {
      try {
        const payload = {
          ticketIds: ticketIds,
          paymentId: paymentId,
          paymentMethodId: 2 // Cập nhật phương thức thanh toán là 2
        };

        const updateResponse = await apis().put(endpoints.update_payment_ticket, payload);
        console.log('Cập nhật payment_id thành công:', updateResponse.data);
      } catch (error) {
        console.error('Error updating ticket payment:', error);
      }
    };

    fetchPaymentResult();
  }, [setLoading]);

  return (
    <div className="payment-result-container">
      <h2>Payment Result</h2>
      {paymentData && (
        <div className="payment-details">
          <p><strong>Tổng hóa đơn:</strong> {paymentData.amount ? `${paymentData.amount / 100} VND` : ''}</p>
          <p><strong>Mã ngân hàng:</strong> {paymentData.bankCode}</p>
          <p><strong>Mã giao dịch:</strong> {paymentData.transactionNo}</p>
          <p><strong>Trạng thái:</strong> Payment successful</p>
        </div>
      )}
      <button onClick={() => navigate('/')}>
        Về Trang Chủ
      </button>
    </div>
  );
};

export default VnpayPaymentResult;
