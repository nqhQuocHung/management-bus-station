import React, { useEffect, useState, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import './styles.css';
import { Modal, Button } from 'react-bootstrap';

const AdminManageCompany = () => {
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [error, setError] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');

  const fetchCompanies = async () => {
    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
      const response = await apiInstance.get(endpoints.company_list_admin);
      setCompanies(response.data);
    } catch (err) {
      setError(err.message);
      toast.error('Lỗi khi tải dữ liệu công ty.');
    } finally {
      setLoading('none');
    }
  };

  const fetchCompanyInfo = async (companyId) => {
    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
      const response = await apiInstance.get(endpoints.companyInfo(companyId));
      setSelectedCompany(response.data);
      setShowModal(true);
    } catch (err) {
      toast.error('Lỗi khi tải thông tin công ty.');
    } finally {
      setLoading('none');
    }
  };

  const toggleVerification = async (companyId) => {
    try {
      setLoading('flex');
      const apiInstance = apis(accessToken);
      await apiInstance.put(endpoints.verify_company(companyId));
      toast.success('Trạng thái xác thực đã được cập nhật.');
      fetchCompanies();
    } catch (err) {
      toast.error('Lỗi khi cập nhật trạng thái xác thực.');
    } finally {
      setLoading('none');
    }
  };

  useEffect(() => {
    fetchCompanies();
  }, [accessToken]);

  if (error) return <p>Error: {error}</p>;

  return (
    <div className="admin-container">
      <ToastContainer />
      <h1>Quản lý công ty</h1>
      <table className="company-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Tên công ty</th>
            <th>Số điện thoại</th>
            <th>Email</th>
            <th>Vận tải hàng hóa</th>
            <th>Hoạt động</th>
            <th>Xác thực</th>
          </tr>
        </thead>
        <tbody>
          {companies.map((company) => (
            <tr key={company.id} onClick={() => fetchCompanyInfo(company.id)}>
              <td>{company.id}</td>
              <td>{company.name}</td>
              <td>{company.phone}</td>
              <td>{company.email}</td>
              <td>
                <span className={`cargo-text ${company.isCargoTransport ? 'cargo-yes' : 'cargo-no'}`}>
                  {company.isCargoTransport ? 'Có' : 'Không'}
                </span>
              </td>
              <td>
                <button
                  className={`status-btn ${company.isActive ? 'active' : 'inactive'}`}
                >
                  {company.isActive ? 'Hoạt động' : 'Ngưng hoạt động'}
                </button>
              </td>
              <td>
                <button
                  className={`status-btn ${company.isVerified ? 'verified' : 'not-verified'}`}
                  onClick={() => toggleVerification(company.id)}
                >
                  {company.isVerified ? 'Đã xác thực' : 'Chưa xác thực'}
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>

      {selectedCompany && (
        <Modal show={showModal} onHide={() => setShowModal(false)}>
          <Modal.Header>
            <Modal.Title>Thông tin công ty</Modal.Title>
            <Button variant="close" onClick={() => setShowModal(false)} />
          </Modal.Header>
          <Modal.Body>
            <p>ID: {selectedCompany.id}</p>
            <p>Tên: {selectedCompany.name}</p>
            <p>Số điện thoại: {selectedCompany.phone}</p>
            <p>Email: {selectedCompany.email}</p>
            <p>Vận tải hàng hóa: {selectedCompany.isCargoTransport ? 'Có' : 'Không'}</p>
            <p>Hoạt động: {selectedCompany.isActive ? 'Hoạt động' : 'Ngưng hoạt động'}</p>
            <p>Xác thực: {selectedCompany.isVerified ? 'Đã xác thực' : 'Chưa xác thực'}</p>
          </Modal.Body>
          <Modal.Footer>
            <Button variant="secondary" onClick={() => setShowModal(false)}>
              Đóng
            </Button>
          </Modal.Footer>
        </Modal>
      )}
    </div>
  );
};

export default AdminManageCompany;
