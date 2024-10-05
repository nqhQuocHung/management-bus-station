import React, { useState, useEffect, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import './styles.css';
import QRCodeComponent from '../../components/QrCode';

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
    totalPrice: 0
  });
  const [paymentUrl, setPaymentUrl] = useState('');
  const [isTicketCreated, setIsTicketCreated] = useState(false);

  useEffect(() => {
    const fetchTrips = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.get_trips_by_driver(user.id));
        setTrips(response.data);
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

  const handleMarkComplete = async (tripId) => {
    try {
      setLoading('flex');
      const api = apis(accessToken);
      await api.put(endpoints.update_trip_status(tripId), { status: true });
      setTrips((prevTrips) =>
        prevTrips.map((trip) =>
          trip.id === tripId ? { ...trip, status: true } : trip
        )
      );
    } finally {
      setLoading('none');
    }
  };

  const handleCreateTicket = async () => {
    try {
      setLoading('flex');
      const orderInfo = `Payment for ticket #${Math.random().toString(36).substr(2, 9)}`;
      const amount = parseFloat((ticketInfo.totalPrice || '').replace(/[^0-9]/g, ''));
      const response = await apis(accessToken).post(endpoints.payment_url, {
        amount: amount,
        orderInfo: orderInfo,
      });
      const paymentUrl = response.data;
      setPaymentUrl(paymentUrl);
      setIsTicketCreated(true);
    } finally {
      setLoading('none');
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
  };

  const handleCheckboxChange = () => {
    setTicketInfo((prev) => ({ ...prev, includeCargo: !prev.includeCargo }));
  };

  const handleDiscountChange = (e) => {
    setTicketInfo((prev) => ({ ...prev, discount: e.target.value }));
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

                {isTicketCreated && paymentUrl && (
                  <div className="qr-code">
                    <h4>Quét mã QR để thanh toán</h4>
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
    </div>
  );
};

export default ManageWork;
