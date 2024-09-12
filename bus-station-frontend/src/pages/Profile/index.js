import {useContext, useEffect, useRef, useState} from 'react';
import './styles.css';
import {AuthenticationContext, LoadingContext} from '../../config/context';
import {apis, endpoints} from '../../config/apis';

const Profile = () => {
  const {user, setUser} = useContext(AuthenticationContext);
  const {setLoading} = useContext(LoadingContext);
  const [userInfo, setUserInfo] = useState({});
  const [isChanged, setIsChanged] = useState(false);
  const updateState = (event, field) => {
    setIsChanged(true);
    setUserInfo((userInfo) => {
      return {
        ...userInfo,
        [field]: event.target.value,
      };
    });
  };
  const resetUserInfo = () => {
    setIsChanged(false);
    setUserInfo({
      username: user['username'],
      email: user['email'],
      firstname: user['firstname'],
      lastname: user['lastname'],
      phone: user['phone'],
    });
    setCurrentAvatar(user['avatar']);
    avatar.current.value = null;
  };
  useEffect(() => {
    resetUserInfo();
  }, []);
  const handleUpdateUserInfo = async () => {
    setLoading('flex');
    try {
      const formData = new FormData();
      for (let key in userInfo) {
        formData.append([key], userInfo[key]);
      }
      if (avatar.current.files.length > 0) {
        formData.append('file', avatar.current.files[0]);
      }
      const accessToken = localStorage.getItem('accessToken');
      const response = await apis(accessToken).patch(
        endpoints.user(user['id']),
        formData,
        {
          headers: {
            'Content-Type': 'multipart/form-data',
          },
        },
      );
      setUser(response['data']);
    } catch (ex) {
      console.error(ex);
    } finally {
      setIsChanged(false);
      setLoading('none');
    }
  };
  const avatar = useRef(null);
  const [currentAvart, setCurrentAvatar] = useState(user['avatar']);
  return (
    <div className="container mt-5 shadow p-3 mb-5 bg-body rounded">
      <div className="row mt-2">
        <h2>Thông tin của bạn</h2>
      </div>
      <div className="row mt-2 gx-5">
        <div className="col-md-6 fields">
          {Object.keys(user).map((field) => {
            if (field === 'id' || field === 'avatar' || field === 'role') {
              return null;
            }
            let disable = false;
            if (field === 'username' || field === 'email') {
              disable = true;
            }
            let type = 'text';
            if (field === 'phone') {
              type = 'tel';
            }
            return (
              <div key={field}>
                <label className="form-label text-capitalize" htmlFor={field}>
                  {field}
                </label>
                <input
                  type={type}
                  disabled={disable}
                  id={field}
                  className="form-control"
                  value={userInfo[field]}
                  onChange={(event) => updateState(event, field)}
                />
              </div>
            );
          })}
        </div>
        <div className="col-md-6">
          <img
            className="img-thumbnail mx-auto d-block"
            alt="Ảnh đại diện của bạn"
            src={currentAvart}
            width={300}
            height={300}
          />
          <div className="mb-3">
            <label htmlFor="avatar">Thay đổi ảnh đại diện</label>
            <input
              ref={avatar}
              className="form-control"
              type="file"
              id="avatar"
              accept=".png,.jpeg"
              onChange={(event) => {
                const element = event.target;
                setIsChanged(true);
                if (element.files.length > 0) {
                  setCurrentAvatar(URL.createObjectURL(element.files[0]));
                }
              }}
            />
          </div>
          <div className="d-flex">
            <div className="profile-btn me-2">
              <button
                disabled={!isChanged}
                onClick={handleUpdateUserInfo}
                className="btn btn-primary"
              >
                Lưu
              </button>
            </div>
            <div className="profile-btn">
              <button
                disabled={!isChanged}
                onClick={resetUserInfo}
                className="btn btn-primary"
              >
                Hủy bỏ
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;
