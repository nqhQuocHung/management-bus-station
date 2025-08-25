import { Link, useParams, useLocation } from 'react-router-dom';
import './styles.css';
import { useState, useContext, useEffect, useRef } from 'react';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import moment from 'moment';
// import Chat from '../../components/Chat';
import databaseRef from '../../config/firebase';
import { toast } from 'react-toastify';
import { Modal, Button } from 'react-bootstrap';
import IconComment from '../CommentList/IconComment';
import ChatSocket from '../../components/ChatSocket';

const CompanyInfo = () => {
  let { pathname } = useLocation();
  const { user } = useContext(AuthenticationContext);
  const { id } = useParams();
  const { setLoading } = useContext(LoadingContext);
  const [company, setCompany] = useState(null);
  const [routes, setRoutes] = useState([]);
  const [rating, setRating] = useState({ averageRating: 0, totalReviews: 0 });
  const [comments, setComments] = useState([]);
  const [showComments, setShowComments] = useState(false);
  const [startChat, setStartChat] = useState(false);
  const conversationKey = useRef(null);
  const [showRatingDialog, setShowRatingDialog] = useState(false);
  const [newRating, setNewRating] = useState(0);
  const [newComment, setNewComment] = useState('');
  const accessToken = localStorage.getItem('accessToken');

  const fetchCompanyInfo = async () => {
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
      const response = await apis(null).get(endpoints.get_route_by_companyid(id));
      setRoutes(response['data']);
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };

  const fetchAverageRating = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(endpoints.get_average_rating(id));
      setRating(response.data);
    } catch (ex) {
      console.error('Error fetching average rating:', ex);
    } finally {
      setLoading('none');
    }
  };

  const fetchComments = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(endpoints.get_comments_by_company(id));
      setComments(response.data);
    } catch (ex) {
      console.error('Error fetching comments:', ex);
      toast.error('Không thể tải bình luận, vui lòng thử lại sau.');
    } finally {
      setLoading('none');
    }
  };

  const startConversation = async () => {
    let key = null;
    try {
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
    } catch (error) {
      console.error('Error starting conversation:', error);
    }
  };

  const handleAddComment = async () => {
    if (!newRating || newRating < 1 || newRating > 5) {
      toast.error('Vui lòng nhập số sao hợp lệ (1-5).');
      return;
    }

    if (!user) {
      toast.error('Bạn cần đăng nhập để thực hiện đánh giá.');
      return;
    }

    try {
      setLoading('flex');

      const payload = {
        content: newComment,
        rating: newRating,
        userId: user.id,
        companyId: company.id
      };

      console.log('Form Data:', payload);

      await apis(accessToken).post(endpoints.create_comment(id), payload);

      toast.success('Đánh giá thành công!');
      setShowRatingDialog(false);
      setNewRating(0);
      setNewComment('');
      fetchAverageRating();
      fetchComments();
    } catch (ex) {
      console.error('Error creating comment:', ex);
      toast.error('Không thể gửi đánh giá, vui lòng thử lại sau.');
    } finally {
      setLoading('none');
    }
  };



  const registerDriver = async () => {
    if (!user) {
      toast.error('Bạn cần đăng nhập để đăng ký làm tài xế.');
      return;
    }

    try {
      setLoading('flex');
      const api = apis(accessToken);
      const response = await api.post(endpoints.driver_create, null, {
        params: {
          userId: user.id,
          companyId: company.id,
        },
      });
      toast.success('Đăng ký tài xế thành công!');
    } catch (ex) {
      console.error('Error registering driver:', ex);
      if (ex.response && ex.response.data && ex.response.data.message) {
        if (ex.response.data.message.includes('Tài xế đã đăng ký làm việc với công ty này')) {
          toast.error('Tài xế đã đăng ký với công ty này. Vui lòng không đăng ký trùng.');
        } else {
          toast.error('Không thể đăng ký tài xế, vui lòng thử lại sau.');
        }
      } else {
        toast.error('Không thể đăng ký tài xế, vui lòng thử lại sau.');
      }
    } finally {
      setLoading('none');
    }
  };

  useEffect(() => {
    fetchRoutes();
    fetchCompanyInfo();
    fetchAverageRating();
    fetchComments();
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
            <div className="mt-3 d-flex align-items-center">
              <div className="me-3">
                <p className="mb-0">
                  Số sao trung bình: <span className="fw-bold">{rating.averageRating.toFixed(1)}</span> / 5
                  <img
                    src="/images/rating.png"
                    alt="Rating"
                    style={{ width: '20px', height: '20px', marginRight: '8px' }}
                  />
                </p>
                <p>
                  Tổng lượt đánh giá: <span className="fw-bold">{rating.totalReviews}</span>
                </p>
              </div>
              <Button variant="primary" onClick={() => setShowRatingDialog(true)}>Đánh giá</Button>
              <Button
                variant="secondary"
                className="ms-2"
                onClick={() => setShowComments(!showComments)}
              >
                {showComments ? 'Đóng' : 'Xem đánh giá chi tiết'}
              </Button>
            </div>
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
                    Ngày tham gia: <span>{moment(company['createdAt']).format('ll')}</span>
                  </p>
                </li>
                <li>
                  <p>
                    Vận chuyển hàng hóa: <span>{company['isCargoTransport'] ? 'Có' : 'Không'}</span>
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
                    <div className="d-flex">
                      <button
                        disabled={startChat}
                        className="btn btn-primary me-2"
                        onClick={startConversation}
                      >
                        Tư vấn
                      </button>
                      <button
                        className="btn btn-success"
                        onClick={registerDriver}
                      >
                        Đăng ký tài xế
                      </button>
                    </div>
                  ) : (
                    <Link to={'/login'} state={{ from: pathname }}>
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
                    to={`/route/${route['id']}`}
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
            // <Chat
            //   key={conversationKey.current}
            //   conversationKey={conversationKey.current}
            //   senderId={user['id']}
            //   receiverId={company['id']}
            //   avatar={company['avatar']}
            //   subtitle={`Trò chuyện với nhân viên của ${company['name']}`}
            //   isCompany={false}
            // />
            <ChatSocket
              senderId={user.id}
              receiverId={company.id}
              avatar={company.avatar}
              subtitle={`Trò chuyện với nhân viên của ${company.name}`}
              isCompany={false}
              onClose={() => setStartChat(false)}
            />


          )}
        </div>
      </div>

      {showComments && (
        <div className="comments-section mt-4">
          <h5>Danh sách bình luận</h5>
          {comments.length > 0 ? (
            comments.map((comment) => (
              <IconComment key={comment.userId} comment={comment} />
            ))
          ) : (
            <p>Chưa có bình luận nào.</p>
          )}
        </div>
      )}

      <Modal show={showRatingDialog} onHide={() => setShowRatingDialog(false)}>
        <Modal.Header closeButton>
          <Modal.Title>Đánh giá công ty</Modal.Title>
        </Modal.Header>
        <Modal.Body>
          <div className="mb-3">
            <label>Đánh giá sao:</label>
            <div className="rating-stars">
              {[...Array(5)].map((_, index) => (
                <img
                  key={index}
                  src="/images/rating.png"
                  alt={`${index + 1} Star`}
                  className={`star ${index < newRating ? 'selected' : ''}`}
                  onClick={() => setNewRating(index + 1)}
                  style={{
                    width: '30px',
                    height: '30px',
                    cursor: 'pointer',
                    marginRight: '5px',
                    opacity: index < newRating ? 1 : 0.5,
                  }}
                />
              ))}
            </div>
          </div>
          <div className="mb-3">
            <label>Bình luận:</label>
            <textarea
              value={newComment}
              onChange={(e) => setNewComment(e.target.value)}
              className="form-control"
              rows="3"
            ></textarea>
          </div>
        </Modal.Body>
        <Modal.Footer>
          <Button variant="secondary" onClick={() => setShowRatingDialog(false)}>
            Hủy
          </Button>
          <Button variant="primary" onClick={handleAddComment}>
            Gửi đánh giá
          </Button>
        </Modal.Footer>
      </Modal>
    </div>
  );
};

export default CompanyInfo;
