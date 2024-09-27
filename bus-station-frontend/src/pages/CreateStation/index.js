import React, { useState, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './styles.css';

const CreateStation = () => {
  const [address, setAddress] = useState('');
  const [mapUrl, setMapUrl] = useState('');
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!address || !mapUrl) {
      toast.error('Vui lòng điền đầy đủ thông tin!');
      return;
    }

    const stationData = {
      address,
      mapUrl,
    };

    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
      const response = await apiInstance.post(endpoints.create_station, stationData);
      toast.success('Bến xe đã được tạo thành công!');
      setAddress('');
      setMapUrl('');
    } catch (error) {
      toast.error('Có lỗi xảy ra khi tạo bến xe.');
      console.error('Error creating station:', error);
    } finally {
      setLoading('none');
    }
  };

  return (
    <div className="create-station-container">
      <ToastContainer />
      <h1>Tạo Bến Xe Mới</h1>
      <form onSubmit={handleSubmit} className="create-station-form">
        <div className="create-station-form-group">
          <label htmlFor="address">Địa chỉ:</label>
          <input
            type="text"
            id="address"
            value={address}
            onChange={(e) => setAddress(e.target.value)}
            placeholder="Nhập địa chỉ"
            required
          />
        </div>
        <div className="create-station-form-group">
          <label htmlFor="mapUrl">URL bản đồ:</label>
          <input
            type="text"
            id="mapUrl"
            value={mapUrl}
            onChange={(e) => setMapUrl(e.target.value)}
            placeholder="Nhập URL bản đồ"
            required
          />
        </div>
        <button type="submit" className="create-station-submit-btn">Tạo Bến Xe</button>
      </form>
    </div>
  );
};

export default CreateStation;
