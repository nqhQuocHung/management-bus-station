import {Link, useNavigate, useSearchParams} from 'react-router-dom';
import './styles.css';
import {FaCheck} from 'react-icons/fa';
import {VscError} from 'react-icons/vsc';
const PaymentResult = () => {
  const [searchParams] = useSearchParams();

  const resultCode = searchParams.get('code');
  const content = {
    title: 'Fail',
    icon: () => <VscError color={'#E74033'} size={180} />,
    message: 'An error occur!',
  };
  if (resultCode === '00') {
    content['title'] = 'Success';
    content['icon'] = () => <FaCheck size={180} color="#34A853" />;
    content['message'] = 'Your payment is successfull';
  }
  return (
    <div className="container d-flex flex-column align-items-center my-5">
      <div className="">
        <h1 className="title text-uppercase fw-bold ">{content['title']}</h1>
      </div>
      <div className="my-5">{content['icon']()}</div>
      <div>
        <p className="fs-1">{content['message']}</p>
      </div>
      <div className="row">
        <Link to={'/'} className="btn btn-primary my-2">
          Home
        </Link>
        <Link to={'#'} className="btn btn-outline-success my-2">
          Your tickets
        </Link>
      </div>
    </div>
  );
};

export default PaymentResult;
