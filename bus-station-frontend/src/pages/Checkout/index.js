import { useContext, useEffect, useState } from 'react';
import './styles.css';
import moment from 'moment';
import {
  AuthenticationContext,
  CartContext,
  LoadingContext,
} from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import * as ultils from '../../config/utils';
import { toast } from 'react-toastify';

const Checkout = () => {
  const { user } = useContext(AuthenticationContext);
  const { setLoading } = useContext(LoadingContext);
  const { cart, cartDispatcher } = useContext(CartContext);
  const [paymentMethods, setPaymentMethods] = useState([]);
  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState(0);
  const [tickets, setTickets] = useState([]);

  useEffect(() => {
    fetchPaymentMethod();
    fetchCartDetails();
  }, [cart.key]);

  const fetchPaymentMethod = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(endpoints.payment_method_list);
      setPaymentMethods(response.data);
      setSelectedPaymentMethod(response.data[0]?.id || 0);
    } catch (ex) {
      console.error('Error fetching payment methods:', ex);
    } finally {
      setLoading('none');
    }
  };

  const fetchCartDetails = async () => {
    try {
      setLoading('flex');
      const ticketIds = cart.data.map((item) => item.id);
      const response = await apis(null).post(endpoints.cart_details, ticketIds);
      setTickets(response.data);
  
      console.log('Danh sách vé trong giỏ hàng:', response.data);
    } catch (ex) {
      console.error('Error fetching cart details:', ex);
    } finally {
      setLoading('none');
    }
  };
  

  const handleCheckout = async () => {
    try {
      setLoading('flex');
  
      if (selectedPaymentMethod !== 2) {
        toast.error('Chúng tôi chỉ mới hỗ trợ được thanh toán online bằng VNPAY!');
        setLoading('none');
        return;
      }
  
      const amount = tickets.reduce(
        (total, ticket) =>
          total + (ticket.seatPrice || 0) + (ticket.cargoPrice || 0),
        0
      );
  
      const orderInfo = `Payment for order #${Math.random().toString(36).substr(2, 9)}`;
  
      const ticketIds = tickets.map((ticket) => ticket.ticketId);
      localStorage.setItem('ticketIds', JSON.stringify(ticketIds)); 
  
      const response = await apis(null).post(endpoints.payment_url, {
        amount: amount,
        orderInfo: orderInfo,
      });
  
      const paymentUrl = response.data;
  
      if (paymentUrl) {
        cartDispatcher({
          type: 'CLEAR_CART',
        });
  
        window.open(paymentUrl, '_blank');
      } else {
        toast.error('Không thể tạo URL thanh toán!');
      }
    } catch (error) {
      toast.error('Đã xảy ra lỗi khi tạo thanh toán!');
      console.error('Error creating payment:', error);
    } finally {
      setLoading('none');
    }
  };
  
  
  
  

  return (
    <div className="container-fluid mt-5">
      <div className="row">
        <div className="col-md-6">
          <div className="shadow-sm p-3 mb-5 bg-body rounded">
            <h4>Thông tin của bạn</h4>
            <div className="form mt-4">
              <div className="mb-3">
                <label htmlFor="firstname" className="form-label">
                  Họ và tên lót
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="firstname"
                  value={user?.firstname || ''}
                  disabled
                  readOnly
                />
              </div>
              <div className="mb-3">
                <label htmlFor="lastname" className="form-label">
                  Tên
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="lastname"
                  value={user?.lastname || ''}
                  disabled
                  readOnly
                />
              </div>
              <div className="mb-3">
                <label htmlFor="email" className="form-label">
                  Email
                </label>
                <input
                  type="email"
                  className="form-control"
                  id="email"
                  value={user?.email || ''}
                  disabled
                  readOnly
                />
              </div>
              <div className="mb-3">
                <label htmlFor="phone" className="form-label">
                  Số điện thoại
                </label>
                <input
                  type="text"
                  className="form-control"
                  id="phone"
                  value={user?.phone || ''}
                  disabled
                  readOnly
                />
              </div>
              <div className="mb-3">
                <label htmlFor="paymentMethod" className="form-label">
                  Phương thức thanh toán
                </label>
                <select
                  onChange={(e) => setSelectedPaymentMethod(Number(e.target.value))}
                  value={selectedPaymentMethod}
                  className="form-select"
                  id="paymentMethod"
                >
                  {paymentMethods.map((method) => (
                    <option value={method.id} key={method.id}>
                      {method.name}
                    </option>
                  ))}
                </select>
              </div>
              <div className="mt-3 d-flex justify-content-center align-items-center">
                <button
                  disabled={tickets.length === 0}
                  onClick={handleCheckout}
                  className="btn btn-primary"
                >
                  Đặt vé
                </button>
              </div>
            </div>
          </div>
        </div>
        <div className="col-md-6">
          <div className="shadow-sm p-3 mb-5 bg-body rounded">
            <h4>Đơn hàng</h4>
            <table className="table">
              <thead>
                <tr>
                  <th>Công ty & Mã chuyến</th>
                  <th>Tuyến</th>
                  <th>Khởi hành lúc</th>
                  <th>Ghế</th>
                  <th>Giá ghế</th>
                  <th>Giá giao hàng</th>
                  <th>Tổng cộng</th>
                </tr>
              </thead>
              <tbody>
                {tickets.map((ticket, index) => (
                  <tr key={index}>
                    <td>
                      <p>{ticket.companyName}</p>
                      <p>{ticket.routeName}</p>
                    </td>
                    <td>
                      <p>
                        {ticket.fromStation} - {ticket.toStation}
                      </p>
                    </td>
                    <td>{moment(ticket.departAt).format('LLL')}</td>
                    <td>{ticket.seatCode}</td>
                    <td>{ultils.formatToVND(ticket.seatPrice)}</td>
                    <td>{ultils.formatToVND(ticket.cargoPrice)}</td>
                    <td>
                      {ultils.formatToVND(ticket.seatPrice + ticket.cargoPrice)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="d-flex justify-content-between align-items-center">
              <h5 className="ps-2 fw-bold">Tổng cộng</h5>
              <p className="fw-bold fs-3 text-danger">
                {ultils.formatToVND(
                  tickets.reduce(
                    (total, current) =>
                      total + (current.seatPrice || 0) + (current.cargoPrice || 0),
                    0
                  )
                )}
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Checkout;