import React, { useEffect, useState, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './styles.css';

const StationList = () => {
  const [stations, setStations] = useState([]);
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');
  
  const fetchStations = async () => {
    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
      const response = await apiInstance.get(endpoints.list_station);
      setStations(response.data);
      toast.success('Danh sách bến xe đã được tải.');
    } catch (error) {
      toast.error('Lỗi khi tải danh sách bến xe.');
      console.error('Error fetching stations:', error);
    } finally {
      setLoading('none');
    }
  };

  useEffect(() => {
    fetchStations();
  }, [accessToken]);

  return (
    <div className="station-list-container">
      <ToastContainer />
      <h1>Danh Sách Bến Xe</h1>
      <table className="station-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Địa chỉ</th>
            <th>URL Bản đồ</th>
          </tr>
        </thead>
        <tbody>
          {stations.length > 0 ? (
            stations.map((station) => (
              <tr key={station.id}>
                <td>{station.id}</td>
                <td>{station.address}</td>
                <td>
                  <a href={station.mapUrl} target="_blank" rel="noopener noreferrer">
                    Bản đồ
                  </a>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan="3">Không có bến xe nào</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};

export default StationList;
