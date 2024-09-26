import React, { useState, useContext } from 'react';
import { toast } from 'react-toastify';
import { useNavigate } from 'react-router-dom';
import './styles.css';
import { AuthenticationContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';

const PasswordChange = () => {
  const [oldPassword, setOldPassword] = useState('');
  const [newPass, setNewPass] = useState('');
  const [confirmPass, setConfirmPass] = useState('');
  const [showPass, setShowPass] = useState(false);

  const { user } = useContext(AuthenticationContext);
  const accessToken = localStorage.getItem('accessToken');
  const navigate = useNavigate();

  const toggleVisibility = () => {
    setShowPass(!showPass);
  };

  const handlePasswordChange = async (e) => {
    e.preventDefault();

    if (newPass !== confirmPass) {
      toast.error('Mật khẩu mới và xác nhận không khớp', {
        position: 'top-center',
        autoClose: 3000,
        theme: 'colored',
      });
      return;
    }

    try {
      const api = apis(accessToken);
      await api.post(endpoints.change_password, {
        currentPassword: oldPassword,
        newPassword: newPass,
      });

      toast.success('Mật khẩu đã thay đổi thành công', {
        position: 'top-center',
        autoClose: 3000,
        theme: 'colored',
      });

      setOldPassword('');
      setNewPass('');
      setConfirmPass('');
      navigate('/');
    } catch (error) {
      toast.error(error.response?.data || 'Đã xảy ra lỗi', {
        position: 'top-center',
        autoClose: 3000,
        theme: 'colored',
      });
    }
  };

  return (
    <div className="password-change-container">
      <h2>Thay đổi mật khẩu</h2>
      <form onSubmit={handlePasswordChange}>
        <div className="input-group">
          <label htmlFor="oldPassword">Mật khẩu hiện tại</label>
          <input
            type={showPass ? 'text' : 'password'}
            id="oldPassword"
            className="custom-input"
            value={oldPassword}
            onChange={(e) => setOldPassword(e.target.value)}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="newPass">Mật khẩu mới</label>
          <input
            type={showPass ? 'text' : 'password'}
            id="newPass"
            className="custom-input"
            value={newPass}
            onChange={(e) => setNewPass(e.target.value)}
            required
          />
        </div>
        <div className="input-group">
          <label htmlFor="confirmPass">Xác nhận mật khẩu mới</label>
          <input
            type={showPass ? 'text' : 'password'}
            id="confirmPass"
            className="custom-input"
            value={confirmPass}
            onChange={(e) => setConfirmPass(e.target.value)}
            required
          />
        </div>
        <div className="input-group">
          <input
            type="checkbox"
            id="showPass"
            checked={showPass}
            onChange={toggleVisibility}
          />
          <label htmlFor="showPass">Hiển thị mật khẩu</label>
        </div>
        <button type="submit" className="custom-button">
          Thay đổi mật khẩu
        </button>
      </form>
    </div>
  );
};

export default PasswordChange;
