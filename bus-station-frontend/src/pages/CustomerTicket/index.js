import { useContext, useState, useEffect } from 'react';
import './styles.css';
import { LoadingContext, AuthenticationContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import moment from 'moment';

const CustomerTicket = () => {
  const [tickets, setTickets] = useState([]);
  const { setLoading } = useContext(LoadingContext);
  const { user } = useContext(AuthenticationContext);

  const fetchTickets = async () => {
    if (!user || !user.id) {
      alert('Bạn cần đăng nhập để xem vé.');
      return;
    }

    try {
      setLoading('flex');
      const accessToken = localStorage.getItem('accessToken');
      const response = await apis(accessToken).get(endpoints.get_ticket_of_user(user.id));
      setTickets(response.data);
    } catch (ex) {
      console.error('Lỗi khi lấy vé:', ex);
    } finally {
      setLoading('none');
    }
  };

  useEffect(() => {
    fetchTickets();
  }, []);

  const handleDeleteTicket = async (index, id) => {
    if (window.confirm(`Bạn có chắc hủy vé? Mã vé: ${id}`)) {
      try {
        setLoading('flex');
        const accessToken = localStorage.getItem('accessToken');
        const response = await apis(accessToken).delete(endpoints.ticket(id));

        if (response.status === 204) {
          const temp = [...tickets];
          temp.splice(index, 1);
          setTickets(temp);
        }
      } catch (ex) {
        console.error('Lỗi khi hủy vé:', ex);
      } finally {
        setLoading('none');
      }
    }
  };

  return (
    <div className="container mt-5 shadow p-3 mb-5 bg-body rounded">
      <div className="row">
        <h3>Vé xe của bạn</h3>
      </div>
      <div className="row">
        <table className="table table-hover">
          <thead className="ticket-table-header">
            <tr>
              <th>Mã vé</th>
              <th>Mã chuyến</th>
              <th>Công ty phụ trách</th>
              <th>Giá vé</th>
              <th>Giá giao hàng</th>
              <th>Bến khởi hành</th>
              <th>Bến đến</th>
              <th>Mã ghế</th>
              <th>Khởi hành lúc</th>
              <th>Phương thức thanh toán</th>
              <th>Trạng thái thanh toán</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody className="ticket-table-body">
            {tickets.map((ticket, index) => (
              <tr key={ticket.ticketId}>
                <td>{ticket.ticketId}</td>
                <td>{ticket.routeName}</td>
                <td>{ticket.companyName}</td>
                <td>{ticket.seatPrice}</td>
                <td>{ticket.cargoPrice}</td>
                <td>{ticket.fromStation}</td>
                <td>{ticket.toStation}</td>
                <td>{ticket.seatCode}</td>
                <td>{moment(ticket.departAt).format('LLL')}</td>
                <td>{ticket.paidAt ? ticket.paymentMethod : 'Không xác định'}</td>
                <td>{ticket.paidAt ? moment(ticket.paidAt).format('LLL') : 'Chưa thanh toán'}</td>
                <td>
                  {ticket.paidAt ? (
                    <button className="btn btn-danger" disabled>
                      Xóa
                    </button>
                  ) : (
                    <button
                      className="btn btn-danger"
                      onClick={() => handleDeleteTicket(index, ticket.ticketId)}
                    >
                      Xóa
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default CustomerTicket;
