import { Link, useParams } from 'react-router-dom';
import './styles.css';
import { useContext, useEffect, useState, useRef } from 'react';
import * as utils from '../../config/utils';
import { apis, endpoints } from '../../config/apis';
import { CartContext, LoadingContext, AuthenticationContext } from '../../config/context';
import moment from 'moment';
import 'moment/locale/vi';
import { toast } from 'react-toastify';
import { Modal } from 'react-bootstrap';

const RouteInfo = () => {
  let { id } = useParams();
  const [route, setRoute] = useState(null);
  const { setLoading } = useContext(LoadingContext);
  const { cart, cartDispatcher } = useContext(CartContext);
  const { user } = useContext(AuthenticationContext);
  const [withCargo, setWithCargo] = useState(false);
  const [trips, setTrips] = useState([]);
  const [tripId, setTripId] = useState(null);
  const [seatDetails, setSeatDetails] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);
  const [showCargoForm, setShowCargoForm] = useState(false);
  const [showMap, setShowMap] = useState(false);
  const mapRef = useRef(null);
  const [cargoInfo, setCargoInfo] = useState({
    receiverName: '',
    receiverEmail: '',
    receiverPhone: '',
    receiverAddress: '',
    description: '',
  });

  const addToCart = async () => {
    if (!user) {
      toast.warning('Bạn phải đăng nhập trước khi mua vé!', {
        position: 'top-center',
        autoClose: 4000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
      return;
    }
  
    if (selectedSeats.length === 0) {
      toast.warning('Hãy chọn ghế bạn muốn!', {
        position: 'top-center',
        autoClose: 4000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
      return;
    }
  
    if (selectedSeats.length > 1 && withCargo) {
      toast.warning('Bạn chỉ có thể thêm từng vé khi có nhu cầu vận chuyển hàng!', {
        position: 'top-center',
        autoClose: 4000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
      return;
    }
  
    const payload = selectedSeats.map((seat) => ({
      seatId: seat,
      tripId: tripId,
      ...(user ? { customerId: user.id } : {}),
      seatPrice: route?.seatPrice || 0,
      cargoPrice: withCargo ? (route?.cargoPrice || null) : null,
      withCargo: withCargo,
      paidAt: null,
    }));
  
    try {
      setLoading('flex');
      const response = await apis(null).post(endpoints.add_cart, payload);
  
      if (response.status === 200) {
        toast.success('Thêm vào giỏ hàng thành công!', {
          position: 'top-center',
          autoClose: 3000,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: 'colored',
        });
  
        cartDispatcher({
          type: 'ADD_TO_CART',
          payload: response.data,
        });
  
        setSelectedSeats([]);
  
        if (withCargo) {
          await createCargo(response.data[0]?.id);
        }
  
        fetchTripSeatDetails();
      }
    } catch (ex) {
      console.error('Error adding to cart:', ex.response ? ex.response.data : ex);
      toast.error('Không thể thêm vào giỏ hàng, vui lòng thử lại sau!', {
        position: 'top-center',
        autoClose: 3000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
    } finally {
      setLoading('none');
    }
  };
  
  const createCargo = async (ticketId) => {
    const payload = {
      receiverName: cargoInfo.receiverName,
      receiverEmail: cargoInfo.receiverEmail,
      receiverPhone: cargoInfo.receiverPhone,
      receiverAddress: cargoInfo.receiverAddress,
      cargoPrice: route?.cargoPrice || 0,
      description: cargoInfo.description,
      ticketId: ticketId,
    };

    try {
      setLoading('flex');
      await apis(null).post(endpoints.add_cargo, payload);
    } catch (error) {
      console.error('Error creating cargo:', error.response ? error.response.data : error);
    } finally {
      setLoading('none');
    }
  };

  const handleToggleSeat = (event) => {
    if (event.target.checked) {
      const newSelectedSeats = [...selectedSeats, event.target.value];
      setSelectedSeats(newSelectedSeats);
    } else {
      const id = event.target.value;
      const newSelectedSeats = selectedSeats.filter((seat) => seat !== id);
      setSelectedSeats(newSelectedSeats);
    }
  };

  const fetchAllTrips = async () => {
    setLoading('flex');
    try {
      const response = await apis(null).get(endpoints.route_trip_list(id));
      if (response) {
        setTrips(response['data']);
        setTripId(response['data'][0]['id']);
      }
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };

  const fetchRouteInfo = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(endpoints.route_info(id));
      const data = response['data'];
      setRoute(data);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };

  const fetchTripSeatDetails = async () => {
    setLoading('flex');
    try {
      const response = await apis(null).get(endpoints.get_available_seat(tripId));
      const availableSeats = response.data;

      setSeatDetails(
        availableSeats.map((seat) => ({
          id: seat.id,
          code: seat.code,
          available: true,
        }))
      );
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };

  useEffect(() => {
    fetchRouteInfo();
    fetchAllTrips();
  }, []);

  useEffect(() => {
    if (tripId !== null) {
      fetchTripSeatDetails();
    }
  }, [tripId]);

  useEffect(() => {
    if (showMap && mapRef.current) {
      const map = new window.google.maps.Map(mapRef.current, {
        center: { lat: 21.028511, lng: 105.804817 },
        zoom: 15,
      });
  
      map.addListener('click', (event) => {
        handleMapClick(event);
      });
    }
  }, [showMap]);

  useEffect(() => {
    if (cart.refreshSeats) {
      fetchTripSeatDetails();
      cartDispatcher({ type: 'TRIGGER_REFRESH_SEATS' });
    }
  }, [cart.refreshSeats]);

  const openMapModal = () => setShowMap(true);
  const closeMapModal = () => setShowMap(false);

  const handleMapClick = (event) => {
    const lat = event.latLng.lat();
    const lng = event.latLng.lng();
    const geocoder = new window.google.maps.Geocoder();
    
    geocoder.geocode({ location: { lat, lng } }, (results, status) => {
      if (status === 'OK' && results[0]) {
        setCargoInfo({ ...cargoInfo, receiverAddress: results[0].formatted_address });
        closeMapModal();
      }
    });
  };

  return (
    <div className="container py-5">
      <div className="row">
        {route && (
          <div className="col-md-6 p-3">
            <div className="border-bottom mb-4">
              <Link className="fs-2" to={`/company/${route['company']['id']}`}>
                {route['company']['name']}
              </Link>
            </div>
            <div>
              <ul className="list-unstyled">
                <li className="mb-2">
                  Mã tuyến: <span className="fw-bold">{route['name']}</span>
                </li>
                <li className="mb-2">
                  Giá vé: <span className="text-primary">{utils.formatToVND(route['seatPrice'])}</span>
                </li>
                <li className="mb-2">
                  Giá giao hàng: <span className="text-success">{utils.formatToVND(route['cargoPrice'])}</span>
                </li>
                <li className="row mb-2">
                  <div className="col-md-6">
                    <p>Bắt đầu từ: <span>{route['fromStation']['address']}</span></p>
                    <iframe
                      src={route['fromStation']['mapUrl']}
                      width="300"
                      height="300"
                      allowFullScreen={true}
                      loading="lazy"
                      referrerPolicy="no-referrer-when-downgrade"
                    ></iframe>
                  </div>
                  <div className="col-md-6">
                    <p>Đến: <span>{route['toStation']['address']}</span></p>
                    <iframe
                      src={route['toStation']['mapUrl']}
                      width="300"
                      height="300"
                      allowFullScreen={true}
                      loading="lazy"
                      referrerPolicy="no-referrer-when-downgrade"
                    ></iframe>
                  </div>
                </li>
              </ul>
            </div>
            <div className="mt-5 d-flex align-items-center">
              <button onClick={addToCart} className="btn btn-primary">Thêm vào giỏ hàng</button>
              <div className="form-check ms-3">
                <input
                  checked={withCargo}
                  className="form-check-input"
                  type="checkbox"
                  onChange={(event) => {
                    setWithCargo(event.target.checked);
                    setShowCargoForm(event.target.checked);
                  }}
                  id="flexCheckChecked"
                />
                <label className="form-check-label" htmlFor="flexCheckChecked">
                  Có giao hàng
                </label>
              </div>
            </div>

            {showCargoForm && (
              <div className="cargo-form mt-4">
                <h6>Thông tin giao hàng</h6>
                <div className="mb-3">
                  <label>Tên người nhận:</label>
                  <input
                    type="text"
                    value={cargoInfo.receiverName}
                    onChange={(e) =>
                      setCargoInfo({ ...cargoInfo, receiverName: e.target.value })
                    }
                    required
                    className="form-control"
                  />
                </div>
                <div className="mb-3">
                  <label>Email người nhận:</label>
                  <input
                    type="email"
                    value={cargoInfo.receiverEmail}
                    onChange={(e) =>
                      setCargoInfo({ ...cargoInfo, receiverEmail: e.target.value })
                    }
                    required
                    className="form-control"
                  />
                </div>
                <div className="mb-3">
                  <label>Số điện thoại:</label>
                  <input
                    type="text"
                    value={cargoInfo.receiverPhone}
                    onChange={(e) =>
                      setCargoInfo({ ...cargoInfo, receiverPhone: e.target.value })
                    }
                    required
                    className="form-control"
                  />
                </div>
                <div className="mb-3">
                  <label>Địa chỉ:</label>
                  <div className="input-group">
                    <input
                      type="text"
                      value={cargoInfo.receiverAddress}
                      onChange={(e) =>
                        setCargoInfo({ ...cargoInfo, receiverAddress: e.target.value })
                      }
                      required
                      className="form-control"
                    />
                    <button
                      className="btn btn-secondary"
                      onClick={openMapModal}
                    >
                      Chọn trên bản đồ
                    </button>
                  </div>
                </div>
                <div className="mb-3">
                  <label>Ghi chú:</label>
                  <textarea
                    value={cargoInfo.description}
                    onChange={(e) =>
                      setCargoInfo({ ...cargoInfo, description: e.target.value })
                    }
                    required
                    className="form-control"
                  />
                </div>
              </div>
            )}
          </div>
        )}
        <div className="col-md-6 p-3">
          <div>
            <h5 className="text-center">Các chuyến</h5>
            <div className="mt-4 border-bottom pb-3 d-flex flex-direction-column">
              {trips.map((trip) => {
                return (
                  <div key={trip['id']} className="form-check my-2">
                    <input
                      className="form-check-input"
                      type="radio"
                      name="tripId"
                      id={trip['id']}
                      onChange={() => {
                        setTripId(trip['id']);
                      }}
                      checked={trip['id'] === tripId}
                    />
                    <label
                      className="form-check-label"
                      htmlFor="flexRadioDefault2"
                    >
                      Khởi hành lúc: <span className="fw-bold">{moment(trip['departAt']).format('LLL')}</span>
                    </label>
                  </div>
                );
              })}
            </div>
            <div className="mt-4 d-flex align-items-center flex-column">
              <h5>Ghế</h5>
              <div className="mt-3 seat-grid">
                {seatDetails.map((seat) => {
                  return (
                    <div key={seat.id} className="form-check w-100 p-0">
                      <input
                        value={seat.id}
                        type="checkbox"
                        className="btn-check"
                        id={`seat-${seat.id}`}
                        autoComplete="off"
                        onChange={(event) => handleToggleSeat(event)}
                        disabled={!seat.available}
                      />
                      <label
                        className={['btn', seat.available ? 'btn-outline-primary' : 'btn-danger'].join(' ')}
                        htmlFor={`seat-${seat.id}`}
                      >
                        {seat.code}
                      </label>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </div>
      <Modal show={showMap} onHide={closeMapModal}>
        <Modal.Header closeButton>
          <Modal.Title>Chọn vị trí trên bản đồ</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div ref={mapRef} style={{ height: '400px', width: '100%' }} />
        </Modal.Body>
      </Modal>
    </div>
  );
};

export default RouteInfo;
