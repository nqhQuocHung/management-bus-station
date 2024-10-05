import { useContext, useState, useEffect } from 'react';
import './styles.css';
import { LoadingContext, AuthenticationContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import moment from 'moment';
import { PDFDocument, rgb } from 'pdf-lib';
import fontkit from '@pdf-lib/fontkit';

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

  const exportTicketToPDF = async (ticket) => {
    try {
      const pdfDoc = await PDFDocument.create();
      pdfDoc.registerFontkit(fontkit);
  
      const fontUrl = '/font/Roboto-Regular.ttf'; 
      const fontBytes = await fetch(fontUrl).then((res) => res.arrayBuffer());
  
      const customFont = await pdfDoc.embedFont(fontBytes);
  
      const page = pdfDoc.addPage([600, 400]);
      const { height } = page.getSize();
  
      const leftMargin = 50;
      const lineSpacing = 20;
      let yPosition = height - 50;
  
      page.drawText('Thông tin vé', {
        x: leftMargin,
        y: yPosition,
        size: 18,
        font: customFont,
        color: rgb(0.1, 0.1, 0.1),
      });
  
      page.setFontSize(12);
      yPosition -= lineSpacing * 1.5;
      page.drawText(`Mã vé: ${ticket.ticketId}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Chuyến: ${ticket.routeName}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Công ty: ${ticket.companyName}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Bến khởi hành: ${ticket.fromStation}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Bến đến: ${ticket.toStation}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Mã ghế: ${ticket.seatCode}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Giá vé: ${ticket.seatPrice.toLocaleString('vi-VN')} VND`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      if (ticket.cargoPrice && ticket.cargoPrice > 0) {
        yPosition -= lineSpacing;
        page.drawText(`Giá giao hàng: ${ticket.cargoPrice.toLocaleString('vi-VN')} VND`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
      }
  
      yPosition -= lineSpacing;
      page.drawText(`Giờ khởi hành: ${moment(ticket.departAt).format('LLL')}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Phương thức thanh toán: ${ticket.paymentMethod}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      yPosition -= lineSpacing;
      page.drawText(`Thời gian thanh toán: ${moment(ticket.paidAt).format('LLL')}`, { x: leftMargin, y: yPosition, size: 12, font: customFont, color: rgb(0, 0, 0) });
  
      const pdfBytes = await pdfDoc.save();
  
      const blob = new Blob([pdfBytes], { type: 'application/pdf' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = `ticket_${ticket.ticketId}.pdf`;
      link.click();
    } catch (error) {
      console.error('Lỗi khi tạo PDF:', error);
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
