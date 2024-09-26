import { useContext, useState } from 'react';
import './styles.css';

import { LoadingContext, AuthenticationContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import { toast } from 'react-toastify';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import * as validator from '../../config/validator';
import { GoogleLogin } from '@react-oauth/google';
import { jwtDecode } from 'jwt-decode';

const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const [isForgotPassword, setIsForgotPassword] = useState(false);
  const { setLoading } = useContext(LoadingContext);
  const { setUser } = useContext(AuthenticationContext);
  const location = useLocation();
  const { from } = location['state'] || { from: '/' };
  const navigator = useNavigate();

  const validate = () => {
    const msgs = [];
    msgs.push(validator.validateUsername(username));
    msgs.push(validator.validatePassword(password));
    for (let msg of msgs) {
      if (msg) return msg;
    }
  };

  const callLogin = async () => {
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
    }

    try {
      setLoading('flex');
      const response = await apis(null)
        .post(endpoints.login, {
          username: username,
          password: password,
        })
        .catch((error) => {
          if (error.response.status === 400) {
            toast.error(error.response.data.errors[0], {
              position: 'top-center',
              autoClose: 4000,
              closeOnClick: true,
              pauseOnHover: true,
              draggable: true,
              progress: undefined,
              theme: 'colored',
            });
          }
        });
      const data = response.data;

      localStorage.setItem('accessToken', data.accessToken);
      setUser(data['userDetails']);
      navigator(from);
    } catch (error) {
    } finally {
      setLoading('none');
    }
  };

  const handleLoginWithGoogle = async ({ credential }) => {
    try {
      setLoading('flex');
      const { email, family_name, given_name, name, picture } = jwtDecode(credential);
      const response = await apis(null).post(endpoints['login_with_google'], {
        firstName: family_name,
        lastName: given_name,
        username: email,
        email: email,
        avatar: picture,
      });
      const data = response['data'];
      localStorage.setItem('accessToken', data['accessToken']);
      setUser(data['userDetails']);
      navigator(from);
    } catch (ex) {
      console.error(ex);
      toast.error('Error when processing your request', {
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
  };
  const handleForgotPassword = async () => {
    try {
      if (!email) {
        toast.error('Vui lòng nhập email của bạn', {
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
      setLoading('flex');
      const response = await apis(null).post(endpoints.forgot_password, {
        username: username,
        email: email,
      });
      toast.success('Mật khẩu mới đã được gửi tới email của bạn', {
        position: 'top-center',
        autoClose: 4000,
        closeOnClick: true,
        pauseOnHover: true,
        draggable: true,
        progress: undefined,
        theme: 'colored',
      });
    } catch (error) {
      toast.error('Lỗi khi xử lý yêu cầu của bạn', {
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
  };

  return (
    <div className="row" style={{ height: '100vh' }}>
      <div className="col-md-6">
        <div className="container" style={{ height: '100%' }}>
          <div className="row align-items-center justify-content-center" style={{ height: '100%' }}>
            <div className="col-md-7">
              <div>
                <h3>{isForgotPassword ? 'Quên mật khẩu' : 'Đăng nhập'}</h3>
                <p className="text-muted">{isForgotPassword ? 'Vui lòng nhập email và tên tài khoản của bạn' : 'Chào mừng đến OU BUS'}</p>
              </div>

              <form>
                {!isForgotPassword ? (
                  <>
                    <div className="mb-3">
                      <label htmlFor="username" className="form-label">
                        Tên tài khoản
                      </label>
                      <input
                        className="form-control form-control-lg"
                        id="username"
                        name="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                      />
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
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                      />
                    </div>
                  </>
                ) : (
                  <>
                    <div className="mb-3">
                      <label htmlFor="username" className="form-label">
                        Tên tài khoản
                      </label>
                      <input
                        className="form-control form-control-lg"
                        id="username"
                        name="username"
                        type="text"
                        value={username}
                        onChange={(e) => setUsername(e.target.value)}
                      />
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
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                      />
                    </div>
                  </>
                )}

                <div className="d-flex justify-content-between mb-3">
                  {!isForgotPassword ? (
                    <>
                      <p
                        className="text-decoration-underline text-muted btn"
                        onClick={() => setIsForgotPassword(true)}
                      >
                        Quên mật khẩu?
                      </p>
                      <Link to="/register" className="text-decoration-underline text-muted btn">
                        Bạn chưa có tài khoản?
                      </Link>
                    </>
                  ) : (
                    <p
                      className="text-decoration-underline text-muted btn"
                      onClick={() => setIsForgotPassword(false)}
                    >
                      Quay lại đăng nhập
                    </p>
                  )}
                </div>

                {!isForgotPassword ? (
                  <button
                    onClick={callLogin}
                    type="button"
                    className="btn btn-primary btn-lg"
                    style={{ width: '100%' }}
                  >
                    Đăng nhập
                  </button>
                ) : (
                  <button
                    onClick={handleForgotPassword}
                    type="button"
                    className="btn btn-primary btn-lg"
                    style={{ width: '100%' }}
                  >
                    Xác nhận
                  </button>
                )}

                {!isForgotPassword && (
                  <>
                    <span className="d-block text-center my-4 text-muted">-- hoặc --</span>

                    <GoogleLogin
                      onSuccess={(credentialResponse) => {
                        handleLoginWithGoogle(credentialResponse);
                      }}
                      onError={() => {
                        toast.error('Access denied when login with Google', {
                          position: 'top-center',
                          autoClose: 4000,
                          closeOnClick: true,
                          pauseOnHover: true,
                          draggable: true,
                          progress: undefined,
                          theme: 'colored',
                        });
                      }}
                    />
                  </>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>
      <div className="col-md-6 rightImage"></div>
    </div>
  );
};

export default Login;
