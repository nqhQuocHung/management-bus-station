import {Link, useParams} from 'react-router-dom';
import './styles.css';
import {useContext, useEffect, useState} from 'react';
import * as utils from '../../config/utils';
import {apis, endpoints} from '../../config/apis';
import {CartContext, LoadingContext} from '../../config/context';
import moment from 'moment';
import 'moment/locale/vi';
import {toast} from 'react-toastify';

const RouteInfo = () => {
  let {id} = useParams();
  const [route, setRoute] = useState(null);
  const {setLoading} = useContext(LoadingContext);
  const {cartDispatcher} = useContext(CartContext);
  const [withCargo, setWithCargo] = useState(true);
  const [trips, setTrips] = useState([]);
  const [tripId, setTripId] = useState(null);
  const [seatDetails, setSeatDetails] = useState([]);
  const [selectedSeats, setSelectedSeats] = useState([]);

  const addToCart = () => {
    if (selectedSeats.length === 0) {
      toast.warning('Please select your seat', {
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
    const payload = selectedSeats.map((seat) => {
      return {
        routeId: id,
        tripId: tripId,
        seatId: seat,
        withCargo: withCargo,
      };
    });

    cartDispatcher({
      type: 'ADD_TO_CART',
      payload: payload,
    });
  };

  const handleToggleSeat = (event) => {
    if (event.target.checked) {
      const newSelectedSeats = selectedSeats;
      newSelectedSeats.push(event.target.value);
      setSelectedSeats(newSelectedSeats);
    } else {
      const id = event.target.value;
      const index = selectedSeats.indexOf(id);
      const newSelectedSeats = selectedSeats;
      newSelectedSeats.splice(index, 1);
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
      const response = await apis(null).get(
        endpoints.trip_seat_details(tripId),
      );
      const availableSeat = response['data']['availableSeat'];
      const unAvailableSeat = response['data']['unAvailableSeat'];
      let seats = availableSeat.map((a) => {
        a['available'] = true;
        return a;
      });
      seats = seats.concat(
        unAvailableSeat.map((a) => {
          a['available'] = false;
          return a;
        }),
      );
      seats.sort((a, b) => {
        return a['id'] - b['id'];
      });
      setSeatDetails(seats);
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
                  Giá vé:{' '}
                  <span className="text-primary">
                    {utils.formatToVND(route['seatPrice'])}
                  </span>
                </li>
                <li className="mb-2">
                  Giá giao hàng:{' '}
                  <span className="text-success">
                    {utils.formatToVND(route['cargoPrice'])}
                  </span>
                </li>

                <li className="row mb-2">
                  <div className="col-md-6">
                    <p>
                      Bắt đầu từ: <span>{route['fromStation']['address']}</span>
                    </p>
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
                    <p>
                      Đến: <span>{route['toStation']['address']}</span>
                    </p>
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
              <button onClick={addToCart} className="btn btn-primary">
                Thêm vào giỏ hàng
              </button>
              <div className="form-check ms-3">
                <input
                  checked={withCargo}
                  className="form-check-input"
                  type="checkbox"
                  onChange={(event) => setWithCargo(event.target.checked)}
                  id="flexCheckChecked"
                />
                <label className="form-check-label" htmlFor="flexCheckChecked">
                  Có giao hàng
                </label>
              </div>
            </div>
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
                      Khởi hành lúc:{' '}
                      <span className="fw-bold">
                        {moment(trip['departAt']).format('LLL')}
                      </span>
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
                    <div key={seat['id']} className="form-check w-100 p-0">
                      <input
                        value={seat['id']}
                        type="checkbox"
                        className="btn-check"
                        id={seat['id']}
                        autoComplete="off"
                        onChange={(event) => handleToggleSeat(event)}
                        disabled={!seat['available']}
                      />
                      <label
                        className={[
                          'btn',
                          seat['available']
                            ? 'btn-outline-primary'
                            : 'btn-danger',
                        ].join(' ')}
                        htmlFor={seat['id']}
                      >
                        {seat['code']}
                      </label>
                    </div>
                  );
                })}
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RouteInfo;
