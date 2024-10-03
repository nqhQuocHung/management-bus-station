import React, { useState, useEffect, useContext } from 'react';
import './styles.css';
import { Link } from 'react-router-dom';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import { apis, endpoints } from '../../config/apis';
import { Bar } from 'react-chartjs-2';
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
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

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
  const [isCargoTransport, setIsCargoTransport] = useState(false);
  const accessToken = localStorage.getItem('accessToken');

  useEffect(() => {
    const fetchCompanyId = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.get_company_managerid(user.id));
        setCompanyId(response.data.id);
        setCompanyName(response.data.name);
        setIsCargoTransport(response.data.is_cargo_transport);
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
    if (!companyId || !date) return;

    try {
      setLoading('flex');
      const api = apis(accessToken);
      const dateObj = new Date(date);

      if (isNaN(dateObj.getTime())) {
        toast.error('Ngày không hợp lệ. Vui lòng chọn lại.');
        return;
      }

      const year = dateObj.getFullYear();
      const month = dateObj.getMonth() + 1;
      const day = dateObj.getDate();
      let endpoint;

      if (type === 'month') {
        endpoint = `${endpoints.statistics_ticket_year(year)}?companyId=${companyId}`;
      } else if (type === 'quarter') {
        endpoint = `${endpoints.statistics_ticket_quarterly(year)}?companyId=${companyId}`;
      } else if (type === 'day') {
        endpoint = `${endpoints.statistics_ticket_day(year, month, day)}?companyId=${companyId}`;
      }

      const response = await api.get(endpoint, accessToken);
      setStats(response.data);
    } catch (error) {
      console.error(`Error fetching ${type} statistics:`, error);
      setStats(null);
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
      const response = await api.patch(endpoints.register_cargo(companyId), payload);
      if (response.status === 200) {
        setIsCargoTransport(!isCargoTransport);
        toast.success(isCargoTransport ? 'Hủy đăng kí thành công!' : 'Đăng kí thành công!');
      } else {
        toast.error('Thao tác không thành công!');
      }
    } catch (error) {
      console.error('Error registering cargo:', error);
      toast.error('Có lỗi xảy ra trong quá trình thao tác.');
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
      toast.error('Vui lòng chọn ngày trước khi lấy thống kê.');
      return;
    }
    fetchStats(type);
  };

  const renderChart = () => {
    if (!stats || Object.keys(stats).length === 0) {
      return <p>Không có dữ liệu để hiển thị.</p>;
    }

    let labels;

    if (selectedOption === 'day') {
      const dateObj = new Date(date);
      const formattedDate = `${dateObj.getDate()}/${dateObj.getMonth() + 1}/${dateObj.getFullYear()}`;
      labels = [formattedDate];
    } else {
      labels = Object.keys(stats).map((label) => {
        if (selectedOption === 'quarter') {
          return parseInt(label) + 1;
        } else if (selectedOption === 'month') {
          return parseInt(label) + 1;
        } else {
          return label;
        }
      });
    }

    const ticketData = Object.values(stats).map((item) => item.totalTicket);
    const cargoData = Object.values(stats).map((item) => item.totalCargo);

    if (ticketData.length === 0 || cargoData.length === 0) {
      return <p>Không có dữ liệu để hiển thị.</p>;
    }

    const data = {
      labels,
      datasets: [
        {
          label: 'Doanh thu vé',
          data: ticketData,
          backgroundColor: 'rgba(75, 192, 192, 0.2)',
          borderColor: 'rgba(75, 192, 192, 1)',
          borderWidth: 1,
        },
        {
          label: 'Doanh thu hàng hóa',
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

  const exportReportToPDF = async () => {
    const chartElement = document.querySelector('.mc-chart-container canvas');
    if (!chartElement || !companyName) return;

    const canvas = await html2canvas(chartElement);
    const chartImage = canvas.toDataURL('image/png');

    const pdf = new jsPDF();
    pdf.setFont('helvetica', 'normal');
    pdf.setFontSize(16);
    pdf.text(`Bus Station Revenue Report - ${companyName}`, 105, 20, null, null, 'center');

    pdf.addImage(chartImage, 'PNG', 15, 30, 180, 100);

    const currentDate = new Date();
    const day = String(currentDate.getDate()).padStart(2, '0');
    const month = String(currentDate.getMonth() + 1).padStart(2, '0');
    const year = currentDate.getFullYear();
    const fileName = `${companyName}_report_${day}-${month}-${year}.pdf`;

    pdf.save(fileName);
  };

  return (
    <>
      <ToastContainer />
      <div className="mc-button-container">
        <Link to="/create-route">
          <button className="mc-button">Đăng ký tuyến</button>
        </Link>
        <Link to="/register-trip">
          <button className="mc-button">Đăng kí chuyến</button>
        </Link>
        <Link to="/create-car">
          <button className="mc-button">Đăng kí xe</button>
        </Link>
        <Link
          to={{
            pathname: '/company_drivers',
            state: { companyId },
          }}
        >
          <button className="mc-button">Quản lý tài xế</button>
        </Link>
        <button className="mc-button" onClick={showConfirmationDialog}>
          {isCargoTransport ? 'Hủy chuyển hàng' : 'Đăng kí chuyển hàng'}
        </button>
        <ChatIcon />
      </div>
      {showConfirmation && (
        <div className="mc-confirmation-overlay">
          <div className="mc-confirmation-dialog">
            <p>
              {isCargoTransport
                ? 'Bạn chắc chắn muốn hủy đăng kí vận chuyển hàng hóa không?'
                : 'Bạn chắc chắn muốn đăng kí vận chuyển hàng hóa không?'}
            </p>
            <button className="mc-button" onClick={handleRegisterCargo}>
              OK
            </button>
            <button className="mc-button" onClick={hideConfirmationDialog}>
              Hủy
            </button>
          </div>
        </div>
      )}
      <div className="mc-stats-chart-container">
        <div className="mc-stats-button-container">
          <button className="mc-button export-button" onClick={exportReportToPDF}>
            Xuất báo cáo PDF
          </button>
          <button
            className="mc-button"
            onClick={() => {
              setSelectedOption('month');
              handleFetchStats('month');
            }}
          >
            Thống kê theo tháng
          </button>
          <button
            className="mc-button"
            onClick={() => {
              setSelectedOption('quarter');
              handleFetchStats('quarter');
            }}
          >
            Thống kê theo quý
          </button>
          <button
            className="mc-button"
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
            className="mc-date-input"
          />
        </div>
        <div className="mc-chart-and-heading-container">
          <h2 className="mc-heading">
            Báo cáo doanh thu của công ty <span className="mc-company-name">{companyName}</span>
          </h2>
          <div className="mc-chart-container">{renderChart()}</div>
        </div>
      </div>
    </>
  );
};

export default ManageCompany;
