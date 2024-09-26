import React, { useEffect, useState, useContext } from 'react';
import { useLocation } from 'react-router-dom';
import { apis, endpoints } from '../../config/apis';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './styles.css';

const DriverList = () => {
  const [drivers, setDrivers] = useState([]);
  const [companyId, setCompanyId] = useState(null);
  const { user } = useContext(AuthenticationContext);
  const { setLoading } = useContext(LoadingContext);
  const location = useLocation();
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
    const fetchCompanyId = async () => {
      if (location.state?.companyId) {
        setCompanyId(location.state.companyId);
      } else {
        try {
          setLoading('flex');
          const api = apis(accessToken);
          const response = await api.get(endpoints.get_company_managerid(user.id));
          setCompanyId(response.data.id);
        } catch (error) {
          console.error('Error fetching company ID:', error);
        } finally {
          setLoading('none');
        }
      }
    };

    if (user && user.id) {
      fetchCompanyId();
    }
  }, [user, location.state?.companyId, accessToken, setLoading]);

  useEffect(() => {
    const fetchDrivers = async () => {
      if (!companyId) {
        console.error('Company ID is not available');
        return;
      }

      try {
        setLoading('flex');
        const response = await apis(accessToken).get(endpoints.driver_list_by_company(companyId));
        setDrivers(response.data);
      } catch (error) {
        console.error('Error fetching drivers:', error);
      } finally {
        setLoading('none');
      }
    };

    if (companyId) {
      fetchDrivers();
    }
  }, [companyId, accessToken, setLoading]);

  const handleVerifyDriver = async (id) => {
    try {
      setLoading('flex');
      await apis(accessToken).put(endpoints.driver_verify(id));
      toast.success('Xác nhận tài xế thành công');
      setDrivers((prevDrivers) =>
        prevDrivers.map((driver) =>
          driver.id === id ? { ...driver, isVerified: true } : driver
        )
      );
    } catch (error) {
      console.error('Error verifying driver:', error);
      toast.error('Xác nhận tài xế thất bại');
    } finally {
      setLoading('none');
    }
  };

  const handleLockDriver = async (id) => {
    try {
      setLoading('flex');
      await apis(accessToken).put(endpoints.driver_verify(id));
      toast.success('Khóa tài xế thành công');
      setDrivers((prevDrivers) =>
        prevDrivers.map((driver) =>
          driver.id === id ? { ...driver, isVerified: false } : driver
        )
      );
    } catch (error) {
      console.error('Error locking driver:', error);
      toast.error('Khóa tài xế thất bại');
    } finally {
      setLoading('none');
    }
  };

  return (
    <div>
      <ToastContainer /> 
      <table className="table table-hover">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tài khoản</th>
            <th>Họ</th>
            <th>Tên</th>
            <th>Email</th>
            <th>Số điện thoại</th>
            <th>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {drivers.length > 0 ? (
            drivers.map((driver) => (
              <tr key={driver.id}>
                <td>{driver.id}</td>
                <td>{driver.username}</td>
                <td>{driver.firstname}</td>
                <td>{driver.lastname}</td>
                <td>{driver.email}</td>
                <td>{driver.phone}</td>
                <td>
                  {driver.isVerified ? (
                    <button
                      className="btn btn-danger"
                      onClick={() => handleLockDriver(driver.id)}
                    >
                      Khóa
                    </button>
                  ) : (
                    <button
                      className="btn btn-success"
                      onClick={() => handleVerifyDriver(driver.id)}
                    >
                      Xác nhận
                    </button>
                  )}
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="7">Không có tài xế nào để hiển thị.</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default DriverList;
