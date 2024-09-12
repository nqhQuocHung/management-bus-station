import {Link, useParams, useLocation} from 'react-router-dom';
import './styles.css';
import {useState, useContext, useEffect, useRef} from 'react';
import {AuthenticationContext, LoadingContext} from '../../config/context';
import {apis, endpoints} from '../../config/apis';
import moment from 'moment';
import Chat from '../../components/Chat';
import databaseRef from '../../config/firebase';
const CompanyInfo = () => {
  let {pathname} = useLocation();
  const {user} = useContext(AuthenticationContext);
  const {id} = useParams();
  const {setLoading} = useContext(LoadingContext);
  const [company, setCompany] = useState(null);
  const [routes, setRoutes] = useState([]);
  const [startChat, setStartChat] = useState(false);
  const conversationKey = useRef(null);
  const fetchComanyInfo = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(endpoints.companyInfo(id));
      setCompany(response['data']);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };
  const fetchRoutes = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(
        endpoints.get_route_by_companyid(id),
      );
      setRoutes(response['data']);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };
  const startConversation = async () => {
    let key = null;
    await databaseRef
      .child('/users_keys/' + user['id'])
      .once('value')
      .then((snapshot) => {
        snapshot.forEach((child) => {
          const data = child.val();
          if (data['opponentId'] === company['id']) {
            key = child.key;
          }
        });
        if (!key) {
          key = databaseRef.child('/users_keys/' + user['id']).push().key;
          databaseRef.child('/users_keys/' + user['id'] + `/${key}`).set({
            opponentId: company['id'],
            unread: 0,
          });
          databaseRef
            .child('/companies_keys/' + company['id'] + `/${key}`)
            .set({
              opponentId: user['id'],
              unread: 0,
            });
        }
        conversationKey.current = key;
      });

    setStartChat(true);
  };

  useEffect(() => {
    fetchRoutes();
    fetchComanyInfo();
  }, []);
  return (
    <div className="container mt-5">
      {company && (
        <div className="row">
          <div className="col-md-6">
            <img
              alt="company avatar"
              className="img-thumbnail"
              src={company['avatar']}
            ></img>
          </div>
          <div className="col-md-6">
            <div className="row">
              <p className="fs-5 fw-bold">Thông tin giới thiệu</p>
              <ul className="company-list">
                <li>
                  <p>{company['name']}</p>
                </li>
                <li>
                  <p>
                    Ngày tham gia:{' '}
                    <span>{moment(company['createdAt']).format('ll')}</span>
                  </p>
                </li>
                <li>
                  <p>
                    Vận chuyển hàng hóa:{' '}
                    <span>{company['isCargoTransport'] ? 'Có' : 'Không'}</span>
                  </p>
                </li>
              </ul>
            </div>
            <div className="row">
              <p className="fs-5 fw-bold">Liên hệ</p>
              <ul className="company-list">
                <li>
                  <p>
                    Email: <span>{company['email']}</span>
                  </p>
                </li>
                <li>
                  <p>
                    Điện thoại: <span>{company['phone']}</span>
                  </p>
                </li>
                <li>
                  {user ? (
                    <button
                      disabled={startChat}
                      className="btn btn-primary"
                      onClick={startConversation}
                    >
                      Tư vấn
                    </button>
                  ) : (
                    <Link to={'/login'} state={{from: pathname}}>
                      Đăng nhập để chat với nhân viên nhà xe
                    </Link>
                  )}
                </li>
              </ul>
            </div>
            <div className="row">
              <p className="fs-5 fw-bold">Tuyến xe</p>
              <div className="d-flex flex-wrap">
                {routes.map((route) => (
                  <Link
                    className="me-3 mb-3"
                    key={route['id']}
                    to={`/route/${id}`}
                  >
                    {route['name']}
                  </Link>
                ))}
              </div>
            </div>
          </div>
        </div>
      )}
      <div className="row">
        <div className="col-md-4">
          {startChat && (
            <Chat
              key={conversationKey.current}
              conversationKey={conversationKey.current}
              senderId={user['id']}
              receiverId={company['id']}
              avatar={company['avatar']}
              subtitle={`Trò chuyện với nhân viên của ${company['name']}`}
              isCompany={false}
            />
          )}
        </div>
      </div>
    </div>
  );
};

export default CompanyInfo;
