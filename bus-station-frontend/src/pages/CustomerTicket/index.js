import {useContext, useState} from 'react';
import './styles.css';
import {useEffect} from 'react';
import {LoadingContext} from '../../config/context';
import {apis, endpoints} from '../../config/apis';
import moment from 'moment';

const CustomerTicket = () => {
  const [tickets, setTickets] = useState([]);
  const {setLoading} = useContext(LoadingContext);
  const fetchTickets = async () => {
    try {
      setLoading('flex');
      const accessToken = localStorage.getItem('accessToken');
      const response = await apis(accessToken).get(endpoints['self_ticket']);
      setTickets(response['data']);
    } catch (ex) {
      console.error(ex);
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
        console.log(endpoints.ticket(id));
        const response = await apis(accessToken).delete(endpoints.ticket(id));

        if (response['status'] === 204) {
          const temp = tickets;
          temp.splice(index, 1);
          setTickets(temp);
        }
      } catch (ex) {
        console.error(ex);
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
              <th>Đã thanh toán</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody className="ticket-table-body">
            {tickets.map((ticket, index) => (
              <tr key={ticket['ticketId']}>
                <td>{ticket['ticketId']}</td>
                <td>{ticket['routeInfo']['name']}</td>
                <td>{ticket['routeInfo']['company']['name']}</td>
                <td>{ticket['seatPrice']}</td>
                <td>{ticket['cargoPrice']}</td>
                <td>{ticket['routeInfo']['fromStation']['address']}</td>
                <td>{ticket['routeInfo']['toStation']['address']}</td>
                <td>{ticket['seat']['code']}</td>
                <td>{moment(ticket['tripInfo']['departAt']).format('LLL')}</td>
                <td>{ticket['paymentMethod']['name']}</td>
                <td>
                  {ticket['paidAt']
                    ? moment(ticket['paidAt']).format('LLL')
                    : 'Chưa thanh toán'}
                </td>
                <td>
                  {ticket['paidAt'] ? (
                    <button className="btn btn-danger" disabled>
                      Hủy vé
                    </button>
                  ) : (
                    <button
                      className="btn btn-danger"
                      onClick={() =>
                        handleDeleteTicket(index, ticket['ticketId'])
                      }
                    >
                      Hủy vé
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
