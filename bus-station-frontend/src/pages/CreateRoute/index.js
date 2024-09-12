import React, {useState, useEffect, useContext} from 'react';
import './styles.css';

import {apis, endpoints} from '../../config/apis';
import {LoadingContext, AuthenticationContext} from '../../config/context';
import {useNavigate} from 'react-router-dom';

const CreateRoute = () => {
  const [stations, setStations] = useState([]);
  const [formData, setFormData] = useState({
    fromStation: '',
    toStation: '',
    routeName: '',
    seatPrice: '',
    cargoPrice: '',
    isActive: true,
    companyId: '',
  });

  const {setLoading} = useContext(LoadingContext);
  const { user} = useContext(AuthenticationContext);
  const accessToken = localStorage.getItem('accessToken');
  const navigate = useNavigate();

  useEffect(() => {
    const fetchCompany = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(
          endpoints.get_company_managerid(user.id),
        );
        setFormData((prevData) => ({
          ...prevData,
          companyId: response.data.id,
        }));
      } catch (error) {
        console.error('Error fetching company', error);
      } finally {
        setLoading('none');
      }
    };

    if (user && user.id) {
      fetchCompany();
    }
  }, [user, accessToken, setLoading]);

  useEffect(() => {
    const fetchStations = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.list_station);
        setStations(response.data);
      } catch (error) {
        console.error('Error fetching stations', error);
      } finally {
        setLoading('none');
      }
    };

    fetchStations();
  }, [accessToken, setLoading]);

  const handleChange = (e) => {
    const {name, value} = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading(true);
      const api = apis(accessToken);
      const routeData = {
        name: formData.routeName,
        companyId: formData.companyId,
        seatPrice: formData.seatPrice,
        cargoPrice: formData.cargoPrice,
        fromStationId: formData.fromStation,
        toStationId: formData.toStation,
        isActive: formData.isActive,
      };
      await api.post(endpoints.create_route, routeData);
      alert('Route created successfully');
      navigate(-1);
    } catch (error) {
      console.error('Error creating route', error);
      alert('Failed to create route');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <form onSubmit={handleSubmit} className="custom-route-form">
        <div className="custom-form-group">
          <label>Nơi đi:</label>
          <select
            name="fromStation"
            value={formData.fromStation}
            onChange={handleChange}
          >
            <option value="">Chọn nơi đi</option>
            {stations &&
              stations.map((station) => (
                <option key={station.id} value={station.id}>
                  {station.address}
                </option>
              ))}
          </select>
        </div>
        <div className="custom-form-group">
          <label>Nơi đến:</label>
          <select
            name="toStation"
            value={formData.toStation}
            onChange={handleChange}
          >
            <option value="">Chọn nơi đến</option>
            {stations &&
              stations.map((station) => (
                <option key={station.id} value={station.id}>
                  {station.address}
                </option>
              ))}
          </select>
        </div>
        <div className="custom-form-group">
          <label>Tên tuyến:</label>
          <input
            type="text"
            name="routeName"
            value={formData.routeName}
            onChange={handleChange}
          />
        </div>
        <div className="custom-form-group">
          <label>Giá ghế:</label>
          <input
            type="number"
            name="seatPrice"
            value={formData.seatPrice}
            onChange={handleChange}
          />
        </div>
        <div className="custom-form-group">
          <label>Giá vận chuyển hàng:</label>
          <input
            type="number"
            name="cargoPrice"
            value={formData.cargoPrice}
            onChange={handleChange}
          />
        </div>
        <div className="custom-button-group">
          <button type="submit" className="custom-btn custom-btn-primary">
            Tạo tuyến
          </button>
          <button
            type="button"
            className="custom-btn custom-btn-secondary"
            onClick={() => navigate('/manage-company')}
          >
            Hủy
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateRoute;
