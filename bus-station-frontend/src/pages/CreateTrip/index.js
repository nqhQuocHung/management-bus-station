import React, { useState, useEffect, useContext } from 'react';
import './styles.css';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext, AuthenticationContext } from '../../config/context';
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const CreateTrip = () => {
  const [routes, setRoutes] = useState([]);
  const [cars, setCars] = useState([]);
  const [drivers, setDrivers] = useState([]);
  const [formData, setFormData] = useState({
    routeId: '',
    carId: '',
    driverId: '',
    departAt: '',
    isActive: true,
  });

  const { setLoading } = useContext(LoadingContext);
  const { user } = useContext(AuthenticationContext);
  const accessToken = localStorage.getItem('accessToken');
  const navigate = useNavigate();
  const [companyId, setCompanyId] = useState(null);

  useEffect(() => {
    const fetchCompanyAndRoutes = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);

        const companyResponse = await api.get(endpoints.get_company_managerid(user.id));
        const companyId = companyResponse.data.id;
        setCompanyId(companyId);

        const routesResponse = await api.get(endpoints.get_route_by_companyid(companyId));
        const sortedRoutes = (routesResponse.data || []).sort((a, b) => b.id - a.id);
        setRoutes(sortedRoutes);
      } catch (error) {
        console.error('Error fetching company and routes', error);
      } finally {
        setLoading('none');
      }
    };

    if (user && user.id) {
      fetchCompanyAndRoutes();
    }
  }, [user, accessToken, setLoading]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });

    if (name === 'departAt' && value) {
      handleFetchCars(value);
      handleFetchDrivers(value);
    }
  };

  useEffect(() => {
    if (formData.departAt) {
      handleFetchDrivers(formData.departAt);
    }
  }, [formData.departAt]);

  const handleFetchCars = async (departAt) => {
    if (!departAt || !companyId) return;

    try {
      setLoading('flex');
      const api = apis(accessToken);

      const response = await api.get(endpoints.available_cars, {
        params: {
          date: departAt.split('T')[0],
          busStationId: companyId,
        },
      });

      setCars(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error fetching available cars', error);
      setCars([]);
    } finally {
      setLoading('none');
    }
  };

  const handleFetchDrivers = async (departAt) => {
    if (!departAt || !companyId) return;

    try {
      setLoading('flex');
      const api = apis(accessToken);

      const response = await api.get(endpoints.driver_available, {
        params: {
          date: departAt.split('T')[0],
          companyId: companyId,
        },
      });

      setDrivers(Array.isArray(response.data) ? response.data : []);
    } catch (error) {
      console.error('Error fetching available drivers', error);
      setDrivers([]);
    } finally {
      setLoading('none');
    }
  };

  const formatDepartAt = (departAt) => {
    if (!departAt) return '';
    const [date, time] = departAt.split('T');
    return `${date} ${time}:00`;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      setLoading('flex');
      const api = apis(accessToken);

      const tripData = {
        routeId: Number(formData.routeId),
        carId: Number(formData.carId),
        driverId: Number(formData.driverId),
        departAt: formatDepartAt(formData.departAt),
        isActive: formData.isActive,
      };

      await api.post(endpoints.creat_trip, tripData);
      toast.success('Tạo chuyến thành công');
      navigate(-1);
    } catch (error) {
      console.error('Error creating trip', error);
      toast.error('Tạo chuyến thất bại');
    } finally {
      setLoading('none');
    }
  };

  return (
    <div>
      <ToastContainer />
      <form onSubmit={handleSubmit} className="custom-trip-form">
        <div className="custom-form-group">
          <label>Tuyến:</label>
          <select
            name="routeId"
            value={formData.routeId}
            onChange={handleChange}
          >
            <option value="">Chọn tuyến</option>
            {routes.map((route) => (
              <option key={route.id} value={route.id}>
                {`${route.id} - ${route.name}`}
              </option>
            ))}
          </select>
        </div>
        <div className="custom-form-group">
          <label>Giờ khởi hành:</label>
          <input
            type="datetime-local"
            name="departAt"
            value={formData.departAt}
            onChange={handleChange}
          />
        </div>
        <div className="custom-form-group">
          <label>Xe:</label>
          <select name="carId" value={formData.carId} onChange={handleChange}>
            <option value="">Chọn xe</option>
            {cars.map((car) => (
              <option key={car.id} value={car.id}>
                {`${car.id} - ${car.carNumber}`}
              </option>
            ))}
          </select>
        </div>
        <div className="custom-form-group">
          <label>Tài xế:</label>
          <select
            name="driverId"
            value={formData.driverId}
            onChange={handleChange}
          >
            <option value="">Chọn tài xế</option>
            {drivers.map((driver) => (
              <option key={driver.id} value={driver.id}>
                {`${driver.id} - ${driver.firstname} ${driver.lastname}`}
              </option>
            ))}
          </select>
        </div>
        <div className="custom-button-group">
          <button type="submit" className="custom-btn custom-btn-primary">
            Xác nhận
          </button>
          <button
            type="button"
            className="custom-btn custom-btn-secondary"
            onClick={() => navigate(-1)}
          >
            Hủy
          </button>
        </div>
      </form>
    </div>
  );
};

export default CreateTrip;
