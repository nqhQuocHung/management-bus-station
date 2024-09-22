import './styles.css';
import '../login/styles.css';
import { useContext, useState } from 'react';
import * as validator from '../../config/validator';
import { LoadingContext, AuthenticationContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import { toast } from 'react-toastify';
import { Link } from 'react-router-dom';

const Register = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [rePassword, setRePassword] = useState('');
  const [email, setEmail] = useState('');
  const [firstName, setFirstName] = useState('');
  const [lastName, setLastName] = useState('');
  const [phone, setPhone] = useState('');
  const [avatar, setAvatar] = useState(null);
  const [avatarPreview, setAvatarPreview] = useState(null);
  const { setLoading } = useContext(LoadingContext);
  const { setUser } = useContext(AuthenticationContext);

  const validate = () => {
    if (password !== rePassword) {
      return 'Mật khẩu không khớp';
    }
    const msgs = [];
    msgs.push(validator.validateEmail(email));
    msgs.push(validator.validateUsername(username));
    msgs.push(validator.validatePassword(password));
    for (let msg of msgs) {
      if (msg) return msg;
    }
  };

  const handleAvatarChange = (e) => {
    const file = e.target.files[0];
    setAvatar(file);
    const reader = new FileReader();
    reader.onloadend = () => {
      setAvatarPreview(reader.result);
    };
    reader.readAsDataURL(file);
  };

  const uploadAvatar = async (file) => {
    const formData = new FormData();
    formData.append('file', file);

    try {
      const response = await apis(null).post(endpoints.upload_image, formData);
      if (response.status === 200) {
        return response.data;
      }
    } catch (error) {
      console.error('Error uploading avatar:', error);
      return null;
    }
  };

  const callRegister = async () => {
    const validateMsg = validate();
  
    if (validateMsg) {
      toast.error(validateMsg, {
        position: 'top-center',
        autoClose: 4000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
      return;
    } else {
      try {
        setLoading('flex');
        let avatarUrl = null;
  
        if (avatar) {
          avatarUrl = await uploadAvatar(avatar);
          if (!avatarUrl) {
            toast.error('Upload ảnh thất bại!', {
              position: 'top-center',
              autoClose: 4000,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
              theme: 'colored',
            });
            setLoading('none');
            return;
          }
        }
  
        const formData = new FormData();
        formData.append('username', username);
        formData.append('password', password);
        formData.append('email', email);
        formData.append('firstName', firstName);
        formData.append('lastName', lastName);
        formData.append('phone', phone);
  
        if (avatarUrl) {
          formData.append('avatar', avatarUrl);
        }
  
        const response = await apis(null).post(endpoints.register_user, formData);
        if (response && response.status === 201) {
          toast.success('Đăng ký thành công!', {
            position: 'top-center',
            autoClose: 4000,
            closeOnClick: true,
            pauseOnHover: true,
            draggable: true,
            progress: undefined,
            theme: 'colored',
          });
  
          // Reset tất cả các input về trạng thái trống
          setUsername('');
          setPassword('');
          setRePassword('');
          setEmail('');
          setFirstName('');
          setLastName('');
          setPhone('');
          setAvatar(null);
          setAvatarPreview(null);
        }
      } catch (error) {
        console.log(error);
        toast.error('Đăng ký thất bại, vui lòng thử lại!', {
          position: 'top-center',
          autoClose: 4000,
          closeOnClick: true,
          pauseOnHover: true,
          draggable: true,
          progress: undefined,
          theme: 'colored',
        });
      } finally {
        setLoading('none');
      }
    }
  };
  

  return (
    <div className="row" style={{ height: '100vh' }}>
      <div className="col-md-6">
        <div className="container" style={{ height: '100%' }}>
          <div
            className="row align-items-center justify-content-center"
            style={{ height: '100%' }}
          >
            <div className="col-md-7">
              <div>
                <h3>Đăng ký</h3>
                <p className="text-muted">Chào mừng đến với OU BUS</p>
              </div>

              <form>
                <div className="mb-3">
                  <label htmlFor="username" className="form-label">
                    Tên người dùng
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="username"
                    name="username"
                    type="text"
                    aria-describedby="username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                  ></input>
                </div>
                <div className="mb-3">
                  <label htmlFor="email" className="form-label">
                    Email
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="email"
                    name="email"
                    type="email"
                    aria-describedby="email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                  ></input>
                </div>
                <div className="mb-3">
                  <label htmlFor="firstName" className="form-label">
                    Tên
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="firstName"
                    name="firstName"
                    type="text"
                    aria-describedby="firstName"
                    value={firstName}
                    onChange={(e) => setFirstName(e.target.value)}
                  ></input>
                </div>
                <div className="mb-3">
                  <label htmlFor="lastName" className="form-label">
                    Họ và tên đệm
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="lastName"
                    name="lastName"
                    type="text"
                    aria-describedby="lastName"
                    value={lastName}
                    onChange={(e) => setLastName(e.target.value)}
                  ></input>
                </div>
                <div className="mb-3">
                  <label htmlFor="phone" className="form-label">
                    Số điện thoại
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="phone"
                    name="phone"
                    type="text"
                    aria-describedby="phone"
                    value={phone}
                    onChange={(e) => setPhone(e.target.value)}
                  ></input>
                </div>
                <div className="mb-3">
                  <label htmlFor="avatar" className="form-label">
                    Ảnh đại diện
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="avatar"
                    name="avatar"
                    type="file"
                    aria-describedby="avatar"
                    onChange={handleAvatarChange}
                  ></input>
                  {avatarPreview && (
                    <div className="mt-3">
                      <p>Xem trước ảnh đại diện:</p>
                      <img
                        src={avatarPreview}
                        alt="Avatar Preview"
                        style={{ width: '100%', maxWidth: '200px' }}
                      />
                    </div>
                  )}
                </div>
                <div className="mb-3">
                  <label htmlFor="password" className="form-label">
                    Mật khẩu
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="password"
                    name="password"
                    type="password"
                    aria-describedby="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                  ></input>
                </div>
                <div className="mb-3">
                  <label htmlFor="re-password" className="form-label">
                    Xác nhận mật khẩu
                  </label>
                  <input
                    className="form-control form-control-lg"
                    id="re-password"
                    name="re-password"
                    type="password"
                    aria-describedby="re-password"
                    value={rePassword}
                    onChange={(e) => setRePassword(e.target.value)}
                  ></input>
                </div>
                <button
                  onClick={callRegister}
                  type="button"
                  className="btn btn-primary btn-lg"
                  style={{ width: '100%' }}
                >
                  Đăng ký
                </button>
                <Link
                  to="/login"
                  className="d-block text-center my-4 text-muted btn"
                >
                  Bạn đã có tài khoản?
                </Link>
              </form>
            </div>
          </div>
        </div>
      </div>
      <div className="col-md-6 rightImage"></div>
    </div>
  );
};

export default Register;
