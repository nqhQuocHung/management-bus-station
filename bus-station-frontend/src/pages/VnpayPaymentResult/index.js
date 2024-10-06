import React, { useContext, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import './styles.css';

const VnpayPaymentResult = () => {
  const { setLoading } = useContext(LoadingContext);
  const [paymentData, setPaymentData] = useState(null);
  const [ticketIds, setTicketIds] = useState([]);
  const accessToken = localStorage.getItem('accessToken');
  const [tickets, setTickets] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPaymentResult = async () => {
      try {
        setLoading('flex');
        
        const storedTicketIds = localStorage.getItem('ticketIds');
        let parsedTicketIds = [];
        if (storedTicketIds) {
          parsedTicketIds = JSON.parse(storedTicketIds);
          setTicketIds(parsedTicketIds);
          console.log(parsedTicketIds);
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

        console.log(paymentDetails);

        const response = await apis().post(endpoints.payment_bill, paymentDetails);
        setPaymentData(paymentDetails);

        const paymentId = response.data.id;
        console.log(paymentId);

        await updateTicketPayment(parsedTicketIds, paymentId);
        await fetchTicketDetails(parsedTicketIds);

        localStorage.removeItem('ticketIds');
        localStorage.removeItem('paymentUrl');

      } catch (err) {
        console.error(err);
      } finally {
        setLoading('none');
      }
    };

    const updateTicketPayment = async (ticketIds, paymentId) => {
      try {
        const payload = {
          ticketIds: ticketIds,
          paymentId: paymentId,
          paymentMethodId: 2
        };

        console.log(payload);
        await apis(accessToken).put(endpoints.update_payment_ticket, payload);
      } catch (error) {
        console.error(error);
      }
    };

    const fetchTicketDetails = async (ticketIds) => {
      try {
        console.log(ticketIds);
        const response = await apis(accessToken).post(endpoints.get_ticket_details, { ticketIds });
        setTickets(response.data);
        console.log(response.data);
      } catch (error) {
        console.error(error);
      }
    };

    fetchPaymentResult();
  }, [setLoading]);

  return (
    <div className="payment-result-container">
      <h2>Kết quả thanh toán</h2>
      {paymentData && (
        <div className="payment-details">
          <p><strong>Tổng hóa đơn:</strong> {paymentData.amount ? `${paymentData.amount / 100} VND` : ''}</p>
          <p><strong>Mã ngân hàng:</strong> {paymentData.bankCode}</p>
          <p><strong>Mã giao dịch:</strong> {paymentData.transactionNo}</p>
          <p><strong>Trạng thái:</strong> Thanh toán thành công</p>
        </div>
      )}

      {tickets.length > 0 && (
        <div className="ticket-list">
          <h3>Danh sách vé nhận được:</h3>
          <ul>
            {tickets.map(ticket => (
              <li key={ticket.id}>
                <p><strong>Mã vé:</strong> {ticket.code}</p>
                <p><strong>Chuyến đi:</strong> {ticket.routeName}</p>
                <p><strong>Ghế:</strong> {ticket.seatNumber}</p>
              </li>
            ))}
          </ul>
        </div>
      )}

      <button onClick={() => navigate('/')}>
        Về Trang Chủ
      </button>
    </div>
  );
};

export default VnpayPaymentResult;
