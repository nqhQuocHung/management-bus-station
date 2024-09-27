import { useContext, useState, useEffect } from 'react';
import './styles.css';
import { LoadingContext, AuthenticationContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import moment from 'moment';
import jsPDF from 'jspdf';

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

  const exportTicketToPDF = (ticket) => {
    const pdf = new jsPDF();
    
    // Set font size for titles and content
    pdf.setFont('helvetica', 'normal');
    pdf.setFontSize(16);
    pdf.text('Ticket Information', 10, 10);
    
    pdf.setFontSize(12); // Smaller font for details
    
    // Add ticket details
    pdf.text(`Ticket ID: ${ticket.ticketId}`, 10, 20);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Route:', 10, 30);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${ticket.routeName}`, 40, 30);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Company:', 10, 40);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${ticket.companyName}`, 40, 40);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Departure Station:', 10, 50);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${ticket.fromStation}`, 50, 50);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Arrival Station:', 10, 60);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${ticket.toStation}`, 50, 60);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Seat:', 10, 70);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${ticket.seatCode}`, 50, 70);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Departure Time:', 10, 80);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${moment(ticket.departAt).format('LLL')}`, 50, 80);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Payment Method:', 10, 90);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${ticket.paymentMethod}`, 50, 90);
    
    pdf.setFont('helvetica', 'bold');
    pdf.text('Payment Time:', 10, 100);
    pdf.setFont('helvetica', 'normal');
    pdf.text(`${moment(ticket.paidAt).format('LLL')}`, 50, 100);
    
    const currentDate = moment().format('DD-MM-YYYY');
    const fileName = `${user.username}_${currentDate}.pdf`;
    
    pdf.save(fileName);
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
                    <button
                      className="btn btn-success"
                      onClick={() => exportTicketToPDF(ticket)}
                    >
                      In vé
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
