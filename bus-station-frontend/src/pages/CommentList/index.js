import React, { useEffect, useState, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import IconComment from './IconComment';
import { toast } from 'react-toastify';
import './styles.css'; 

const CommentList = ({ companyId, show, onClose }) => {
  const [comments, setComments] = useState([]);
  const { setLoading } = useContext(LoadingContext);

  const fetchComments = async () => {
    try {
      console.log('Fetching comments for company ID:', companyId);
      setLoading('flex');
      const response = await apis(null).get(endpoints.get_comments_by_company(companyId));
      setComments(response.data);
    } catch (ex) {
      console.error('Error fetching comments:', ex);
      toast.error('Không thể tải bình luận, vui lòng thử lại sau.');
    } finally {
      setLoading('none');
    }
  };  
  
  useEffect(() => {
    if (show) {
      console.log('Show is true, fetching comments...');
      fetchComments();
    }
  }, [show]);

  return (
    <div className={`comment-list ${show ? 'show' : ''}`}>
      <button onClick={onClose} className="btn btn-secondary">Đóng</button>
      <h5>Danh sách bình luận</h5>
      {comments.length > 0 ? (
        <div className="comments-container">
          {comments.map((comment) => (
            <IconComment key={comment.userId} comment={comment} />
          ))}
        </div>
      ) : (
        <p>Chưa có bình luận nào.</p>
      )}
    </div>
  );
};

export default CommentList;
