import React, { useState, useEffect, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext, AuthenticationContext } from '../../config/context';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './styles.css';

const CreateCar = () => {
  const [cars, setCars] = useState([]);
  const [carNumber, setCarNumber] = useState('');
  const [companyId, setCompanyId] = useState('');
  const [showDialog, setShowDialog] = useState(false);
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');
  const { user } = useContext(AuthenticationContext);

  const fetchCars = async (companyId) => {
    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
      const response = await apiInstance.get(endpoints.get_car_by_company(companyId));
      console.log(companyId);
      setCars(response.data);
    } catch (error) {
      toast.error('Không thể lấy dữ liệu xe');
    } finally {
      setLoading('none');
    }
  };
  

  useEffect(() => {
    const fetchCompany = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.get_company_managerid(user.id));
        setCompanyId(response.data.id);
        fetchCars(response.data.id);
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

  const handleCreateCar = async () => {
    if (!carNumber) {
      toast.error('Vui lòng nhập biển số xe');
      return;
    }
  
    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
  
      const url = `${endpoints.create_car}?carNumber=${carNumber}&companyId=${companyId}`;
  
      await apiInstance.post(url);
      toast.success('Tạo xe thành công!');
      setCarNumber('');
      setShowDialog(false);
      fetchCars(companyId);
    } catch (error) {
      toast.error('Có lỗi xảy ra khi tạo xe');
    } finally {
      setLoading('none');
    }
  };
  

  return (
    <div className="car-list-container">
      <ToastContainer />
      <h1>Danh Sách Xe Thuộc Công Ty</h1>
      <div className="car-list">
        {cars.map((car) => (
          <div key={car.id} className="car-item">
            <span>ID: {car.id}</span>
            <span>Biển số: {car.carNumber}</span>
          </div>
        ))}
      </div>
      <button className="create-car-btn" onClick={() => setShowDialog(true)}>
        Tạo Xe Mới
      </button>

      {showDialog && (
        <div className="dialog-overlay">
          <div className="dialog">
            <h2>Tạo Xe Mới</h2>
            <input
              type="text"
              value={carNumber}
              onChange={(e) => setCarNumber(e.target.value)}
              placeholder="Nhập biển số xe"
              className="dialog-input"
            />
            <div className="dialog-actions">
              <button className="dialog-btn" onClick={handleCreateCar}>
                Tạo
              </button>
              <button className="dialog-btn" onClick={() => setShowDialog(false)}>
                Hủy
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CreateCar;
