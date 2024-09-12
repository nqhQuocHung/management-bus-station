import React, {useState, useEffect, useContext} from 'react';
import './styles.css';
import {Link} from 'react-router-dom';
import {AuthenticationContext, LoadingContext} from '../../config/context';
import {apis, endpoints} from '../../config/apis';
import {Bar} from 'react-chartjs-2';
import ChatIcon from '../../components/ChatIcon';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
);

const ManageCompany = () => {
  const [selectedOption, setSelectedOption] = useState('month');
  const [date, setDate] = useState('');
  const [showConfirmation, setShowConfirmation] = useState(false);
  const [companyId, setCompanyId] = useState(null);
  const [stats, setStats] = useState(null);
  const { setLoading } = useContext(LoadingContext);
  const { user } = useContext(AuthenticationContext);
  const [companyName, setCompanyName] = useState(null);
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
    const fetchCompanyId = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(
          endpoints.get_company_managerid(user.id),
        );
        setCompanyId(response.data.id);
        setCompanyName(response.data.name);
        console.log('Fetched Company ID:', response.data.id);
      } catch (error) {
        console.error('Error fetching company ID:', error);
      } finally {
        setLoading('none');
      }
    };

    if (user && user.id) {
      fetchCompanyId();
    }
  }, [user, accessToken, setLoading]);

  const fetchStats = async (type) => {
    if (!companyId) return;

    try {
      setLoading('flex');
      const api = apis(accessToken);
      const dateObj = new Date(date);
      const year = dateObj.getFullYear();
      const month = dateObj.getMonth() + 1;
      const day = dateObj.getDate();
      let endpoint;

      if (type === 'month') {
        endpoint = `${endpoints.statistics_ticket_year(
          year,
        )}?companyId=${companyId}`;
      } else if (type === 'quarter') {
        endpoint = `${endpoints.statistics_ticket_quarterly(
          year,
        )}?companyId=${companyId}`;
      } else if (type === 'day') {
        endpoint = `${endpoints.statistics_ticket_day(
          year,
          month,
          day,
        )}?companyId=${companyId}`;
      }

      console.log('API Request URL:', endpoint);
      const response = await api.get(endpoint, accessToken);
      console.log('API Response Data:', response.data);
      setStats(response.data);
    } catch (error) {
      console.error(`Error fetching ${type} statistics:`, error);
    } finally {
      setLoading('none');
    }
  };

  const handleRegisterCargo = async () => {
    setShowConfirmation(false);
    try {
      setLoading('flex');
      const api = apis(accessToken);
      const payload = { date };
      console.log('Register Cargo Payload:', payload);
      const response = await api.put(
        endpoints.register_cargo(companyId),
        payload,
      );
      if (response.status === 200) {
        alert('Registration successful!');
      } else {
        alert('Registration failed!');
      }
    } catch (error) {
      console.error('Error registering cargo:', error);
      alert('An error occurred during registration.');
    } finally {
      setLoading('none');
    }
  };

  const showConfirmationDialog = () => {
    setShowConfirmation(true);
  };

  const hideConfirmationDialog = () => {
    setShowConfirmation(false);
  };

  const handleFetchStats = (type) => {
    if (!date) {
      alert('Please select a date before fetching statistics.');
      return;
    }
    fetchStats(type);
  };

  const renderChart = () => {
    if (!stats || Object.keys(stats).length === 0) {
      return <p>Dữ liệu chưa được thiết lập!</p>;
    }

    const labels = Object.keys(stats);
    const ticketData = Object.values(stats).map((item) => item.totalTicket);
    const cargoData = Object.values(stats).map((item) => item.totalCargo);

    const data = {
      labels,
      datasets: [
        {
          label: 'Total Ticket Revenue',
          data: ticketData,
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1,
        },
        {
          label: 'Total Cargo Revenue',
          data: cargoData,
          backgroundColor: 'rgba(153, 102, 255, 0.2)',
          borderColor: 'rgba(153, 102, 255, 1)',
          borderWidth: 1,
        },
      ],
    };

    const options = {
      scales: {
        y: {
          beginAtZero: true,
        },
      },
    };

    return <Bar data={data} options={options} />;
  };

  return (
    <>
      <div className="custom-button-container">
        <Link to="/create-route">
          <button className="custom-button">Đăng ký tuyến</button>
        </Link>
        <Link to="/register-trip">
          <button className="custom-button">Đăng kí chuyến</button>
        </Link>
        <button className="custom-button" onClick={showConfirmationDialog}>
          Đăng kí chuyển hàng
        </button>
        <ChatIcon/>
      </div>
      {showConfirmation && (
        <div className="custom-confirmation-overlay">
          <div className="custom-confirmation-dialog">
            <p>Bạn chắc chắn muốn đăng kí vận chuyển hàng hóa không?</p>
            <button className="custom-button" onClick={handleRegisterCargo}>
              OK
            </button>
            <button className="custom-button" onClick={hideConfirmationDialog}>
              Cancel
            </button>
          </div>
        </div>
      )}
      <div className="custom-stats-chart-container">
        <div className="custom-stats-button-container">
          <button
            className="custom-button"
            onClick={() => {
              setSelectedOption('month');
              handleFetchStats('month');
            }}
          >
            Thống kê theo tháng
          </button>
          <button
            className="custom-button"
            onClick={() => {
              setSelectedOption('quarter');
              handleFetchStats('quarter');
            }}
          >
            Thống kê theo quý
          </button>
          <button
            className="custom-button"
            onClick={() => {
              setSelectedOption('day');
              handleFetchStats('day');
            }}
          >
            Thống kê theo ngày
          </button>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="custom-date-input"
          />
        </div>
        <div className="custom-chart-and-heading-container">
          <h2 className="custom-heading">
            Báo cáo doanh thu của công ty <span className="company-name">{companyName}</span>
          </h2>
          <div className="custom-chart-container">{renderChart()}</div>
        </div>
      </div>
    </>
  );
};

export default ManageCompany;