import React, { useState, useEffect, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import './styles.css';
import QRCodeComponent from '../../components/QrCode';
import { toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { Modal } from 'react-bootstrap';

const ManageWork = () => {
  const { user } = useContext(AuthenticationContext);
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');
  const [trips, setTrips] = useState([]);
  const [selectedTrip, setSelectedTrip] = useState(null);
  const [showDialog, setShowDialog] = useState(false);
  const [isPassengerList, setIsPassengerList] = useState(false);
  const [passengerList, setPassengerList] = useState([]);
  const [ticketInfo, setTicketInfo] = useState({
    seatPrice: 0,
    cargoPrice: 0,
    includeCargo: false,
    discount: '10',
    totalPrice: 0,
  });
  const [paymentUrl, setPaymentUrl] = useState('');
  const [isTicketCreated, setIsTicketCreated] = useState(false);
  const [availableSeats, setAvailableSeats] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [cargoInfo, setCargoInfo] = useState({
    receiverName: '',
    receiverEmail: '',
    receiverPhone: '',
    receiverAddress: '',
    description: '',
  });
  const [showCargoDialog, setShowCargoDialog] = useState(false);

  const [username, setUsername] = useState('');
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    const fetchTrips = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.get_trips_by_driver(user.id));
        const sortedTrips = response.data.sort((a, b) => new Date(a.departAt) - new Date(b.departAt));
        setTrips(sortedTrips);
      } catch {
        setTrips([]);
      } finally {
        setLoading('none');
      }
    };

    if (user && user.id) {
      fetchTrips();
    }
  }, [user, accessToken, setLoading]);

  const handleSearchUser = async () => {
    try {
      setLoading('flex');
      const api = apis(accessToken);
      const response = await api.get(endpoints.get_user_by_username(username));
      const foundUser = response.data;
      setUserId(foundUser.id);
      setUsername(`${foundUser.id} - ${foundUser.lastname} ${foundUser.firstname}`);
      toast.success(`User found with ID: ${foundUser.id}`);
    } catch {
      toast.error('Không tìm thấy người dùng!');
    } finally {
      setLoading('none');
    }
  };

  const handleMarkComplete = async (tripId) => {
    try {
      setLoading('flex');
      const api = apis(accessToken);
      await api.put(endpoints.update_trip_status(tripId), { status: true });
      setTrips((prevTrips) =>
        prevTrips.map((trip) => (trip.id === tripId ? { ...trip, status: true } : trip))
      );
    } finally {
      setLoading('none');
    }
  };

  const handleCreateTicket = async () => {
    try {
      setLoading('flex');
  
      const baseSeatPrice = parseFloat(ticketInfo.seatPrice) || 0;
      const cargoPrice = ticketInfo.includeCargo ? parseFloat(ticketInfo.cargoPrice) || 0 : 0;
      const discountPercentage = parseFloat(ticketInfo.discount) || 0;
      const discountAmountSeat = (baseSeatPrice * discountPercentage) / 100;
      const discountAmountCargo = (cargoPrice * discountPercentage) / 100;
      const seatPriceAfterDiscount = baseSeatPrice - discountAmountSeat;
      const cargoPriceAfterDiscount = cargoPrice - discountAmountCargo;
      const orderInfo = `Payment for ticket #${Math.random().toString(36).substr(2, 9)}`;
      const amount = seatPriceAfterDiscount + cargoPriceAfterDiscount;
  
      const payload = selectedSeats.map((seat) => ({
        seatId: seat,
        tripId: selectedTrip.id,
        customerId: userId,
        seatPrice: seatPriceAfterDiscount,
        cargoPrice: ticketInfo.includeCargo ? cargoPriceAfterDiscount : null,
        withCargo: ticketInfo.includeCargo,
        paidAt: null,
      }));
  
      const response = await apis(accessToken).post(endpoints.add_cart, payload);
  
      if (response.status === 200) {
        const ticketIds = response.data.map(ticket => ticket.id);
        console.log('Ticket IDs:', ticketIds);
  
        localStorage.setItem('ticketIds', JSON.stringify(ticketIds));
  
        toast.success('Thêm vé thành công!', {
          position: 'top-center',
          autoClose: 3000,
          theme: 'colored',
        });
  
        if (ticketInfo.includeCargo) {
          await createCargo(ticketIds[0]);
        }
  
        await createPaymentUrl(orderInfo, amount, ticketIds);
      }
    } catch (error) {
      console.error('Error adding to cart:', error);
      toast.error('Không thể thêm vé vào giỏ hàng!');
    } finally {
      setLoading('none');
    }
  };
  

  const createCargo = async (ticketId) => {
    try {
      setLoading('flex');
      const cargoPrice = ticketInfo.cargoPrice || 0;
      const discountPercentage = parseFloat(ticketInfo.discount) || 0;
      const discountAmountCargo = (cargoPrice * discountPercentage) / 100;
      const cargoPriceAfterDiscount = cargoPrice - discountAmountCargo;

      const cargoPayload = {
        receiverName: cargoInfo.receiverName,
        receiverEmail: cargoInfo.receiverEmail,
        receiverPhone: cargoInfo.receiverPhone,
        receiverAddress: cargoInfo.receiverAddress,
        description: cargoInfo.description,
        ticketId: ticketId,
        cargoPrice: cargoPriceAfterDiscount,
      };

      await apis(accessToken).post(endpoints.add_cargo, cargoPayload);
    } catch (error) {
      console.error('Error creating cargo:', error);
      toast.error('Không thể thêm thông tin giao hàng!');
    } finally {
      setLoading('none');
    }
  };

  const createPaymentUrl = async (orderInfo, amount) => {
    try {
      const response = await apis(accessToken).post(endpoints.payment_url, {
        orderInfo: orderInfo,
        amount: amount,
      });
  
      const paymentUrl = response.data;
      console.log('Payment Url: ', paymentUrl);
      setPaymentUrl(paymentUrl);
      setIsTicketCreated(true);
  
      
      localStorage.setItem('paymentUrl', paymentUrl);
  
      toast.success('URL thanh toán đã được tạo!', {
        position: 'top-center',
        autoClose: 3000,
        theme: 'colored',
      });
  
      if (selectedTrip) {
        await fetchAvailableSeats(selectedTrip.id);
      }
  
    } catch (error) {
      console.error('Error creating payment URL:', error);
      toast.error('Không thể tạo URL thanh toán!');
    }
  };
  
  

  const fetchPassengerList = async (tripId) => {
    try {
      setLoading('flex');
      const api = apis(accessToken);
      const response = await api.get(endpoints.get_list_passengers(tripId));
      setPassengerList(response.data);
      setIsPassengerList(true);
      setShowDialog(true);
    } finally {
      setLoading('none');
    }
  };

  const fetchAvailableSeats = async (tripId) => {
    try {
      setLoading('flex');
      const api = apis(accessToken);
      const response = await api.get(endpoints.get_available_seat(tripId));
      setAvailableSeats(response.data);
    } catch (error) {
      toast.error('Không thể tải ghế khả dụng!');
    } finally {
      setLoading('none');
    }
  };

  const calculateTotalPrice = () => {
    const basePrice = parseFloat(ticketInfo.seatPrice) || 0;
    const cargoFee = ticketInfo.includeCargo ? parseFloat(ticketInfo.cargoPrice) || 0 : 0;
    const discountPercentage = parseFloat(ticketInfo.discount) || 0;
    const discountAmount = (basePrice + cargoFee) * (discountPercentage / 100);
    const totalPrice = basePrice + cargoFee - discountAmount;
    const formattedTotalPrice = Math.round(totalPrice).toLocaleString('vi-VN') + ' VND';
    setTicketInfo((prev) => ({ ...prev, totalPrice: formattedTotalPrice }));
  };

  useEffect(() => {
    if (selectedTrip) {
      setTicketInfo({
        seatPrice: selectedTrip.seatPrice,
        cargoPrice: selectedTrip.cargoPrice,
        includeCargo: false,
        discount: '10',
        totalPrice: selectedTrip.seatPrice,
      });
      fetchAvailableSeats(selectedTrip.id);
    }
  }, [selectedTrip]);

  useEffect(() => {
    calculateTotalPrice();
  }, [ticketInfo.includeCargo, ticketInfo.discount]);

  const openDialog = (trip) => {
    setSelectedTrip(trip);
    setIsPassengerList(false);
    setShowDialog(true);
  };

  const closeDialog = () => {
    setShowDialog(false);
    setPaymentUrl('');
    setIsTicketCreated(false);
    setSelectedSeats([]);
  };

  const handleCheckboxChange = () => {
    setTicketInfo((prev) => ({ ...prev, includeCargo: !prev.includeCargo }));
    if (!ticketInfo.includeCargo) {
      setShowCargoDialog(true);
    }
  };

  const handleDiscountChange = (e) => {
    setTicketInfo((prev) => ({ ...prev, discount: e.target.value }));
  };

  const handleSeatSelection = (seatId) => {
    if (selectedSeats.includes(seatId)) {
      setSelectedSeats(selectedSeats.filter((seat) => seat !== seatId));
    } else {
      setSelectedSeats([...selectedSeats, seatId]);
    }
  };

  const closeCargoDialog = () => {
    setShowCargoDialog(false);
  };

  const handleCargoInfoChange = (e) => {
    const { name, value } = e.target;
    setCargoInfo((prevInfo) => ({ ...prevInfo, [name]: value }));
  };

  return (
    <div className="manage-work-container">
      <h2>Quản lý công việc</h2>
      {trips.length > 0 ? (
        <table className="work-table">
          <thead>
            <tr>
              <th className="trip-id-column">Mã chuyến</th>
              <th>Mã tuyến</th>
              <th>Biển số xe</th>
              <th>Thời gian xuất phát</th>
              <th>Trạng thái</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {trips.map((trip) => (
              <React.Fragment key={trip.id}>
                <tr>
                  <td>{trip.id}</td>
                  <td>{trip.routeName}</td>
                  <td>{trip.carNumber}</td>
                  <td>{new Date(trip.departAt).toLocaleString()}</td>
                  <td>
                    {trip.status ? (
                      <span className="completed-status">Hoàn thành</span>
                    ) : (
                      <button
                        className="complete-button"
                        onClick={() => handleMarkComplete(trip.id)}
                      >
                        Hoàn thành
                      </button>
                    )}
                  </td>
                  <td>
                    {!trip.status && (
                      <>
                        <button
                          className="create-ticket-button"
                          onClick={() => openDialog(trip)}
                        >
                          Tạo vé
                        </button>
                      </>
                    )}
                    <button
                      className="view-passenger-button"
                      onClick={() => fetchPassengerList(trip.id)}
                    >
                      Xem hành khách
                    </button>
                  </td>
                </tr>
              </React.Fragment>
            ))}
          </tbody>
        </table>
      ) : (
        <p>Không có công việc nào để hiển thị.</p>
      )}

      {showDialog && (
        <div className="dialog-overlay" onClick={closeDialog}>
          <div className="dialog" onClick={(e) => e.stopPropagation()}>
            {isPassengerList ? (
              <>
                <h3>Danh sách hành khách</h3>
                <table className="passenger-table">
                  <thead>
                    <tr>
                      <th>Số thứ tự</th>
                      <th>Tên khách hàng</th>
                      <th>Email</th>
                      <th>Số điện thoại</th>
                      <th>Mã ghế</th>
                    </tr>
                  </thead>
                  <tbody>
                    {passengerList.map((passenger, index) => (
                      <tr key={index}>
                        <td>{index + 1}</td>
                        <td>{passenger.firstName} {passenger.lastName}</td>
                        <td>{passenger.email}</td>
                        <td>{passenger.phone}</td>
                        <td>
                          {passenger.seatCode && passenger.seatCode.includes('code=')
                            ? passenger.seatCode.split('code=')[1].replace(')', '')
                            : 'N/A'}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </>
            ) : (
              <>
                <h3>Tạo vé cho chuyến {selectedTrip.routeName}</h3>
                <p>Giá vé: {ticketInfo.seatPrice} VND</p>

                <label>
                  <input
                    type="checkbox"
                    checked={ticketInfo.includeCargo}
                    onChange={handleCheckboxChange}
                  />
                  Thêm giá chuyển hàng ({ticketInfo.cargoPrice} VND)
                </label>

                <label>Giảm giá (%):</label>
                <select
                  value={ticketInfo.discount}
                  onChange={handleDiscountChange}
                >
                  <option value="10">10%</option>
                  <option value="20">20%</option>
                  <option value="30">30%</option>
                  <option value="40">40%</option>
                  <option value="50">50%</option>
                  <option value="60">60%</option>
                </select>

                <label>Tổng tiền:</label>
                <input
                  type="text"
                  name="totalPrice"
                  value={ticketInfo.totalPrice}
                  readOnly
                />

                <div>
                  <h4>Tìm người dùng</h4>
                  <input
                    type="text"
                    placeholder="Nhập tên người dùng"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                  />
                  <button onClick={handleSearchUser}>Tìm kiếm</button>
                </div>

                <div>
                  <h4>Chọn ghế</h4>
                  <select
                    className="form-select"
                    value={selectedSeats[0] || ''}
                    onChange={(e) => {
                      setSelectedSeats([e.target.value]);
                    }}
                  >
                    <option value="" disabled>Chọn ghế</option>
                    {availableSeats.length > 0 ? (
                      availableSeats.map((seat) => (
                        <option key={seat.id} value={seat.id}>
                          {seat.code}
                        </option>
                      ))
                    ) : (
                      <option disabled>Không có ghế nào khả dụng</option>
                    )}
                  </select>
                </div>

                {isTicketCreated && paymentUrl && (
                  <div className="qr-code">
                    <QRCodeComponent value={paymentUrl} />
                  </div>
                )}

                <div className="dialog-buttons">
                  {!isTicketCreated && (
                    <button className="create-button" onClick={handleCreateTicket}>
                      Tạo vé
                    </button>
                  )}
                  <button onClick={closeDialog} className="cancel-button">
                    Hủy
                  </button>
                </div>
              </>
            )}
          </div>
        </div>
      )}

      <Modal show={showCargoDialog} onHide={closeCargoDialog}>
        <Modal.Header closeButton>
          <Modal.Title>Thông tin giao hàng</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="cargo-form">
            <div className="mb-3">
              <label>Tên người nhận:</label>
              <input
                type="text"
                name="receiverName"
                value={cargoInfo.receiverName}
                onChange={handleCargoInfoChange}
                className="form-control"
              />
            </div>
            <div className="mb-3">
              <label>Email người nhận:</label>
              <input
                type="email"
                name="receiverEmail"
                value={cargoInfo.receiverEmail}
                onChange={handleCargoInfoChange}
                className="form-control"
              />
            </div>
            <div className="mb-3">
              <label>Số điện thoại:</label>
              <input
                type="text"
                name="receiverPhone"
                value={cargoInfo.receiverPhone}
                onChange={handleCargoInfoChange}
                className="form-control"
              />
            </div>
            <div className="mb-3">
              <label>Địa chỉ người nhận:</label>
              <input
                type="text"
                name="receiverAddress"
                value={cargoInfo.receiverAddress}
                onChange={handleCargoInfoChange}
                className="form-control"
              />
            </div>
            <div className="mb-3">
              <label>Ghi chú:</label>
              <textarea
                name="description"
                value={cargoInfo.description}
                onChange={handleCargoInfoChange}
                className="form-control"
              ></textarea>
            </div>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <button className="btn btn-secondary" onClick={closeCargoDialog}>Đóng</button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default ManageWork;
