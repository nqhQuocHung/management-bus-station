import { useContext, useEffect, useRef, useState } from 'react';
import './styles.css';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import { toast } from 'react-toastify';

const Profile = () => {
  const { user, setUser } = useContext(AuthenticationContext);
  const { setLoading } = useContext(LoadingContext);
  const [userInfo, setUserInfo] = useState({});
  const [isChanged, setIsChanged] = useState(false);
  const [avatarFile, setAvatarFile] = useState(null);
  const avatar = useRef(null);
  const [currentAvatar, setCurrentAvatar] = useState(user['avatar']);

  const updateState = (event, field) => {
    setIsChanged(true);
    setUserInfo((prevState) => ({
      ...prevState,
      [field]: event.target.value,
    }));
  };

  const resetUserInfo = () => {
    setIsChanged(false);
    setUserInfo({
      firstName: user['firstName'] || user['firstname'] || '',
      lastName: user['lastName'] || user['lastname'] || '',
      email: user['email'] || '',
      username: user['username'] || '',
      phone: user['phone'] || ''
    });
    setCurrentAvatar(user['avatar']);
    setAvatarFile(null);
    avatar.current.value = null;
  };

  useEffect(() => {
    resetUserInfo();
  }, [user]);

  const uploadAvatarAndGetUrl = async () => {
    if (!avatarFile) return null;
    const data = new FormData();
    data.append('file', avatarFile);

    try {
      setLoading('flex');
      const api = apis(localStorage.getItem('accessToken'));
      const response = await api.post(endpoints.upload_image, data, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      return response.data;
    } catch (error) {
      toast.error('Không thể tải lên ảnh đại diện!');
      return null;
    } finally {
      setLoading('none');
    }
  };

  const handleUpdateUserInfo = async () => {
    const isConfirmed = window.confirm("Bạn có muốn lưu thông tin này không?");
    if (!isConfirmed) {
      return;
    }

    setLoading('flex');
    try {
      const avatarUrl = await uploadAvatarAndGetUrl() || currentAvatar;

      const formData = {
        firstName: userInfo.firstName,
        lastName: userInfo.lastName,
        email: userInfo.email,
        username: userInfo.username,
        phone: userInfo.phone,
        avatar: avatarUrl,
      };

      const accessToken = localStorage.getItem('accessToken');

      const response = await apis(accessToken).patch(
        endpoints.update_user(user['id']),
        formData,
        {
          headers: {
            'Content-Type': 'application/json',
          },
        }
      );

      setUser(response.data);
      toast.success('Cập nhật thông tin thành công!');
      
    } catch (ex) {
      toast.error('Cập nhật thông tin thất bại!');
      console.error('Failed to update user:', ex);
    } finally {
      setIsChanged(false);
      setLoading('none');
    }
  };

  return (
    <div className="container mt-5 shadow p-3 mb-5 bg-body rounded">
      <div className="row mt-2">
        <h2>Thông tin của bạn</h2>
      </div>
      <div className="row mt-2 gx-5">
        <div className="col-md-6 fields">
          {['firstName', 'lastName', 'email', 'username', 'phone'].map((field) => {
            let type = field === 'phone' ? 'tel' : 'text';
            let disabled = (field === 'email' || field === 'username');
            return (
              <div key={field}>
                <label className="form-label text-capitalize" htmlFor={field}>
                  {field}
                </label>
                <input
                  type={type}
                  id={field}
                  className="form-control"
                  disabled={disabled}
                  value={userInfo[field] || ''}
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
            src={currentAvatar}
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
                  setAvatarFile(element.files[0]);
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
