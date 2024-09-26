import React, { useState, useContext } from 'react';
import './styles.css';
import { apis, endpoints } from '../../config/apis';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import { useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const CreateCompany = () => {
  const { user } = useContext(AuthenticationContext);
  const accessToken = localStorage.getItem('accessToken');
  const { setLoading } = useContext(LoadingContext);
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    name: '',
    phone: '',
    email: '',
    isCargoTransport: false,
    managerId: user ? user.id : ''
  });

  const [avatarPreview, setAvatarPreview] = useState(null);
  const [isSubmitted, setIsSubmitted] = useState(false);
  const [avatarFile, setAvatarFile] = useState(null);

  const handleChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFormData(prevState => ({
      ...prevState,
      [name]: type === 'checkbox' ? checked : value
    }));
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setAvatarFile(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        setAvatarPreview(e.target.result);
      };
      reader.readAsDataURL(file);
    } else {
      setAvatarPreview(null);
      setAvatarFile(null);
    }
  };

  const uploadAvatarAndGetUrl = async () => {
    if (!avatarFile) return null;
    const data = new FormData();
    data.append('file', avatarFile);
    
    try {
      setLoading('flex');
      const api = apis(accessToken);
      const response = await api.post(endpoints.upload_image, data, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      return response.data;
    } catch (error) {
      console.error("Failed to upload image:", error);
      return null;
    } finally {
      setLoading('none');
    }
  };

  const handleSubmit = async () => {
    setLoading('flex');
    try {
      const avatarUrl = await uploadAvatarAndGetUrl();
      
      if (avatarUrl || !avatarFile) {
        const newCompanyData = {
          ...formData,
          avatar: avatarUrl
        };

        const api = apis(accessToken);
        await api.post(endpoints.register_company, newCompanyData);
        setIsSubmitted(true);
        toast.success('Công ty đã được đăng ký thành công!');
      } else {
        toast.error("Failed to upload avatar.");
      }
    } catch (error) {
      console.error("Failed to create new company:", error);
      toast.error("Failed to create new company.");
    } finally {
      setLoading('none');
    }
  };

  return (
    <div className="custom-form-container">
      <ToastContainer />
      <h2>Đăng kí công ty mới</h2>
      {!isSubmitted ? (
        <form id="newCompanyForm" encType="multipart/form-data">
          <label htmlFor="name">Tên công ty:</label>
          <input type="text" id="name" name="name" value={formData.name} onChange={handleChange} required />

          <label htmlFor="phone">Số điện thoại:</label>
          <input type="text" id="phone" name="phone" value={formData.phone} onChange={handleChange} required />

          <label htmlFor="email">Email:</label>
          <input type="email" id="email" name="email" value={formData.email} onChange={handleChange} required />

          <label htmlFor="isCargoTransport">Vận chuyển hàng hóa:</label>
          <input type="checkbox" id="isCargoTransport" name="isCargoTransport" checked={formData.isCargoTransport} onChange={handleChange} />

          <label htmlFor="avatar">Ảnh đại diện:</label>
          <input type="file" id="avatar" name="avatar" accept="image/*" onChange={handleAvatarChange} />
          {avatarPreview && <img id="avatarPreview" src={avatarPreview} alt="Avatar Preview" style={{ maxWidth: '150px', marginTop: '10px' }} />}

          <div className="custom-button-group">
            <button type="button" className="custom-btn custom-btn-primary" onClick={handleSubmit}>Xác nhận</button>
            <button type="button" className="custom-btn custom-btn-secondary" onClick={() => navigate('/')}>Quay lại</button>
          </div>
        </form>
      ) : (
        <div className="custom-submission-message">
          <p>Bạn đã đăng kí công ty thành công. Xin hãy chờ quản trị viên xác nhận.</p>
          <button type="button" className="custom-btn custom-btn-primary" onClick={() => navigate('/')}>
            Về trang chủ
          </button>
        </div>
      )}
    </div>
  );
};

export default CreateCompany;
