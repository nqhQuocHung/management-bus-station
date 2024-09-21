import { useContext, useEffect, useState } from 'react';
import './styles.css';
import { CartContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import { Link } from 'react-router-dom';
import moment from 'moment';
import { toast } from 'react-toastify';
import * as ultils from '../../config/utils';

const Cart = () => {
  const { cart, cartDispatcher } = useContext(CartContext);
  const [tickets, setTickets] = useState([]);

  const handleDeleteCart = async (ticketId) => {
    if (!ticketId) {
      console.error('Ticket ID is missing:', ticketId);
      return;
    }

    try {
      const response = await apis(null).delete(endpoints.ticket(ticketId));

      if (response.status === 200 || response.status === 204) {
        cartDispatcher({
          type: 'DELETE_ITEM',
          payload: {
            id: ticketId,
          },
        });
        fetchTickets();
        toast.success('Xóa vé thành công!', {
          position: 'top-center',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: 'colored',
        });
      } else {
        toast.error('Xóa vé thất bại, vui lòng thử lại!', {
          position: 'top-center',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: 'colored',
        });
      }
    } catch (ex) {
      console.error('Error deleting ticket:', ex);
      toast.error('Có lỗi xảy ra khi xóa vé, vui lòng thử lại sau!', {
        position: 'top-center',
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
    }
  };

  const fetchTickets = async () => {
    try {
      const ticketIds = cart['data'].map((item) => item.id);
      if (ticketIds.length > 0) {
        const response = await apis(null).post(endpoints.cart_details, ticketIds);
        setTickets(response.data);
      } else {
        setTickets([]);
      }
    } catch (ex) {
      console.error('Error fetching ticket details:', ex);
    }
  };

  useEffect(() => {
    fetchTickets();
  }, [cart['data']]);

  return (
    <div
      className="offcanvas offcanvas-end w-50"
      data-bs-scroll="true"
      tabIndex="-1"
      id="offcanvasWithBothOptions"
      aria-labelledby="offcanvasWithBothOptionsLabel"
    >
      <div className="offcanvas-header">
        <h5 className="offcanvas-title mt-5 p-3" id="offcanvasWithBothOptionsLabel">
          Giỏ hàng
        </h5>
        <button
          type="button"
          className="btn-close text-reset"
          data-bs-dismiss="offcanvas"
          aria-label="Close"
        ></button>
      </div>
      <div className="offcanvas-body p-4">
        <table className="table">
          <thead>
            <tr>
              <th>Thông tin vé</th>
              <th>Giao hàng</th>
              <th>Thao tác</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((item, index) => (
              <tr key={index}>
                <td>
                  <ul className="list-unstyled">
                    <li>
                      Mã chuyến: <span className="fw-bold">{item.routeName || 'N/A'}</span>
                    </li>
                    <li>
                      Tên công ty: <span className="fw-bold">{item.companyName || 'N/A'}</span>
                    </li>
                    <li>
                      Chuyến đi: {item.fromStation || 'N/A'} đến {item.toStation || 'N/A'}
                    </li>
                    <li>
                      Khởi hành lúc: {item.departAt ? moment(item.departAt).format('HH[h]mm [ngày] D [tháng] M [năm] YYYY') : 'N/A'}
                    </li>
                    <li>Mã ghế: {item.seatCode || 'N/A'}</li>
                    <li>
                      Giá vé: {ultils.formatToVND(item.seatPrice || 0)}
                    </li>
                    {item.cargoPrice !== null && item.cargoPrice > 0 && (
                      <li>
                        Giá giao hàng: {ultils.formatToVND(item.cargoPrice)}
                      </li>
                    )}
                    <li>
                      Tổng cộng: {ultils.formatToVND((item.seatPrice || 0) + (item.cargoPrice || 0))}
                    </li>
                  </ul>
                </td>
                <td>
                  <input
                    className="form-check-input"
                    type="checkbox"
                    checked={item.cargoPrice !== null && item.cargoPrice > 0}
                    readOnly
                  />
                </td>
                <td>
                  <button
                    onClick={() => handleDeleteCart(item.ticketId)}
                    className="btn btn-danger"
                  >
                    Xóa
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <div className="d-flex justify-content-end">
          <Link to={'/checkout'} className="btn btn-primary">
            Thanh toán
          </Link>
        </div>
      </div>
    </div>
  );
};

export default Cart;