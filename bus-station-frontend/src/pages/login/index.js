import {useContext, useState} from 'react';
import './styles.css';

import {LoadingContext, AuthenticationContext} from '../../config/context';
import {apis, endpoints} from '../../config/apis';
import {toast} from 'react-toastify';
import {Link, useLocation, useNavigate} from 'react-router-dom';
import * as validator from '../../config/validator';
import {GoogleLogin} from '@react-oauth/google';
import {jwtDecode} from 'jwt-decode';
const Login = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const {setLoading} = useContext(LoadingContext);
  const {setUser} = useContext(AuthenticationContext);
  const location = useLocation();
  const {from} = location['state'] || {from: '/'};
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
  const handleLoginWithGoogle = async ({credential}) => {
    try {
      setLoading('flex');
      const {email, family_name, given_name, name, picture} =
        jwtDecode(credential);
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
      toast.error('Error when process your request', {
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
    <div className="row" style={{height: '100vh'}}>
      <div className="col-md-6">
        <div className="container" style={{height: '100%'}}>
          <div
            className="row align-items-center justify-content-center"
            style={{height: '100%'}}
          >
            <div className="col-md-7">
              <div>
                <h3>Đăng nhập</h3>
                <p className="text-muted">Chào mừng đến OU BUS</p>
              </div>

              <form>
                <div className="mb-3">
                  <label htmlFor="username" className="form-label">
                    Tên tài khoản
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
                <div className="d-flex justify-content-between mb-3">
                  <p className="text-decoration-underline text-muted btn">
                    Quên mật khẩu?
                  </p>
                  <Link
                    to="/register"
                    className="text-decoration-underline text-muted btn"
                  >
                    Bạn chưa có tài khoản?
                  </Link>
                </div>
                <button
                  onClick={callLogin}
                  type="button"
                  className="btn btn-primary btn-lg"
                  style={{width: '100%'}}
                >
                  Đăng nhập
                </button>
                <span className="d-block text-center my-4 text-muted">
                  -- hoặc --
                </span>

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
                ></GoogleLogin>
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
