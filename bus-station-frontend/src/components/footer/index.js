import './styles.css';
import {FaPhone, FaFacebook} from 'react-icons/fa';
import {IoMdMail} from 'react-icons/io';
const Footer = () => {
  return (
    <footer className="footer mt-5 p-3">
      <div className="container">
        <div className="row ">
          <div className="col-md-4  px-5">
            <div className="border-bottom mb-4 d-flex justify-content-center">
              <h3>OU BUS</h3>
            </div>
            <p>Chúng tôi mang đến cho bạn dịch vụ tuyệt vời nhất</p>
          </div>
          <div className="col-md-4 px-5">
            <div className="border-bottom mb-4  d-flex justify-content-center">
              <h3>Liên hệ với chúng tôi</h3>
            </div>
            <ul className="list-unstyled mt-5">
              <li className="d-flex  my-2">
                <FaPhone color="#34A853" />
                <p className="px-3">0329183328</p>
              </li>
              <li className="d-flex my-2">
                <IoMdMail color="#E74033" />
                <p className="px-3">2151013034hung@ou.edu.vn</p>
              </li>
              <li className="d-flex my-2">
                <FaFacebook color="#0866FF" />
                <a className="px-3" href="https://www.facebook.com/hung.nguyenquoc.16503323">
                  facebook.com
                </a>
              </li>
            </ul>
          </div>
          <div className="col-md-4 px-5">
            <div className="border-bottom mb-4 d-flex justify-content-center">
              <h3>Địa chỉ trụ sở</h3>
            </div>
            <iframe
              src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3920.7641005386286!2d106.68809457589775!3d10.675409961047086!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x31753100099ce9ed%3A0xdb6079801f0735ea!2zVHLGsOG7nW5nIMSQ4bqhaSBo4buNYyBN4bufIFRQLiBI4buTIENow60gTWluaCAoY8ahIHPhu58gMyk!5e0!3m2!1svi!2s!4v1715964384907!5m2!1svi!2s"
              width="350"
              height="300"
              allowFullScreen={true}
              loading="lazy"
              referrerPolicy="no-referrer-when-downgrade"
            ></iframe>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
