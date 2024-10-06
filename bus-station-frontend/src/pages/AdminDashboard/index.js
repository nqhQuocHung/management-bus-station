import React, { useState, useEffect, useContext } from 'react';
import { Bar, Line, Pie } from 'react-chartjs-2';
import { useNavigate } from 'react-router-dom';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  LineElement,
  PointElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';
import jsPDF from 'jspdf';
import html2canvas from 'html2canvas';
import './styles.css';
import { apis, endpoints } from '../../config/apis';
import { LoadingContext } from '../../config/context';
const accessToken = localStorage.getItem('accessToken');

ChartJS.register(CategoryScale, LinearScale, BarElement, LineElement, PointElement, ArcElement, Title, Tooltip, Legend);

const AdminDashboard = () => {
  const [chartType, setChartType] = useState('bar');
  const [revenueData, setRevenueData] = useState({
    labels: [],
    datasets: [],
  });
  const [barData, setBarData] = useState({
    labels: [],
    datasets: [],
  });
  const [userStats, setUserStats] = useState({
    labels: [],
    datasets: [],
  });
  const [year, setYear] = useState(2024);
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');
  const currentYear = new Date().getFullYear();
  const navigate = useNavigate();

  const generateYears = (startYear, range) => {
    const years = [];
    for (let i = startYear - range; i <= startYear + range; i++) {
      years.push(i);
    }
    return years;
  };

  const years = generateYears(currentYear, 5);

  useEffect(() => {
    const fetchBarStats = async () => {
      if (chartType === 'bar') {
        setLoading('flex');
        try {
          const api = apis(accessToken);
          const response = await api.get(endpoints.statistics_bar);
          const apiData = response.data;

          const labels = apiData.map((stat) => stat.name);
          const counts = apiData.map((stat) => stat.count);

          setBarData({
            labels,
            datasets: [
              {
                label: 'Số lượng',
                data: counts,
                backgroundColor: [
                  'rgba(75, 192, 192, 0.2)',
                  'rgba(153, 102, 255, 0.2)',
                  'rgba(255, 159, 64, 0.2)',
                  'rgba(54, 162, 235, 0.2)',
                ],
                borderColor: [
                  'rgba(75, 192, 192, 1)',
                  'rgba(153, 102, 255, 1)',
                  'rgba(255, 159, 64, 1)',
                  'rgba(54, 162, 235, 1)',
                ],
                borderWidth: 1,
              },
            ],
          });
        } catch (error) {
          console.error('Error fetching bar statistics:', error);
        } finally {
          setLoading('none');
        }
      }
    };

    fetchBarStats();
  }, [chartType, accessToken, setLoading]);

  useEffect(() => {
    const fetchRevenueStats = async () => {
      if (chartType === 'line') {
        setLoading('flex');
        try {
          const api = apis(accessToken);
          const response = await api.get(`${endpoints.statistics_ticket}?year=${year}`);
          const apiData = response.data;

          const labels = apiData.map((stat) => `Tháng ${stat.month}`);
          const totalTicketData = apiData.map((stat) => stat.totalTicket);
          const totalCargoData = apiData.map((stat) => stat.totalCargo);

          setRevenueData({
            labels,
            datasets: [
              {
                label: 'Doanh thu vé',
                data: totalTicketData,
                backgroundColor: 'rgba(75, 192, 192, 0.2)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1,
              },
              {
                label: 'Doanh thu hàng hóa',
                data: totalCargoData,
                backgroundColor: 'rgba(153, 102, 255, 0.2)',
                borderColor: 'rgba(153, 102, 255, 1)',
                borderWidth: 1,
              },
            ],
          });
        } catch (error) {
          console.error('Error fetching revenue statistics:', error);
        } finally {
          setLoading('none');
        }
      }
    };

    fetchRevenueStats();
  }, [chartType, year, accessToken, setLoading]);

  useEffect(() => {
    const fetchUserStats = async () => {
      if (chartType === 'pie') {
        setLoading('flex');
        try {
          const api = apis(accessToken);
          const response = await api.get(endpoints.statistics_user);
          const apiData = response.data;

          const labels = apiData.map((stat) => stat.roleName);
          const data = apiData.map((stat) => stat.userCount);

          setUserStats({
            labels,
            datasets: [
              {
                data,
                backgroundColor: ['#FF6384', '#36A2EB', '#FFCE56', '#4BC0C0', '#9966FF', '#FF9F40'],
                hoverOffset: 4,
              },
            ],
          });
        } catch (error) {
          console.error('Error fetching user statistics:', error);
        } finally {
          setLoading('none');
        }
      }
    };

    fetchUserStats();
  }, [chartType, accessToken, setLoading]);

  const handleChartTypeChange = (e) => {
    setChartType(e.target.value);
  };

  const handleYearChange = (e) => {
    setYear(e.target.value);
  };

  const renderChart = () => {
    if (chartType === 'bar') {
      return <Bar data={barData} />;
    } else if (chartType === 'line') {
      return <Line data={revenueData} />;
    } else if (chartType === 'pie') {
      return <Pie data={userStats} />;
    }
    return <Bar data={barData} />;
  };

  const exportReportToPDF = async (existingFile) => {
    let pdf;
    const currentYear = new Date().getFullYear();
    
    if (!existingFile) {
      pdf = new jsPDF();
      pdf.setFont('helvetica', 'normal');
      pdf.setFontSize(16); 
      pdf.text('Bus Station Data Statistics', 105, 20, null, null, 'center');
    } else {
      pdf = existingFile;
      pdf.addPage();
    }
  
    const waitForRender = async (time = 1000) => {
      return new Promise((resolve) => setTimeout(resolve, time));
    };
  
    setChartType('bar');
    await waitForRender();
    const barChartElement = document.querySelector('.admin-chart canvas');
    if (barChartElement) {
      const barCanvas = await html2canvas(barChartElement, { scale: 3 });
      const barImage = barCanvas.toDataURL('image/png');
      const imageProps = pdf.getImageProperties(barImage);
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (imageProps.height * pdfWidth) / imageProps.width;
      pdf.addImage(barImage, 'PNG', 5, 40, pdfWidth - 10, pdfHeight - 20);
    }
  
    pdf.addPage();
    pdf.text(`Revenue Statistics (${currentYear})`, 105, 20, null, null, 'center');
    setChartType('line');
    setYear(currentYear);
    await waitForRender();
    const lineChartElement = document.querySelector('.admin-chart canvas');
    if (lineChartElement) {
      const lineCanvas = await html2canvas(lineChartElement, { scale: 3 });
      const lineImage = lineCanvas.toDataURL('image/png');
      const imageProps = pdf.getImageProperties(lineImage);
      const pdfWidth = pdf.internal.pageSize.getWidth();
      const pdfHeight = (imageProps.height * pdfWidth) / imageProps.width;
      pdf.addImage(lineImage, 'PNG', 5, 40, pdfWidth - 10, pdfHeight - 20);
    }
  
    pdf.addPage();
    pdf.text('User Statistics', 105, 20, null, null, 'center');
    setChartType('pie');
    await waitForRender();
    const pieChartElement = document.querySelector('.admin-chart canvas');
    if (pieChartElement) {
      const pieCanvas = await html2canvas(pieChartElement, { scale: 3 });
      const pieImage = pieCanvas.toDataURL('image/png');
      const pdfWidth = pdf.internal.pageSize.getWidth();
      
      const pdfHeight = 100;
      const imageWidth = 100;
      
      pdf.addImage(pieImage, 'PNG', 55, 40, imageWidth, pdfHeight);
    }
  
    const currentDate = new Date();
    const day = String(currentDate.getDate()).padStart(2, '0');
    const month = String(currentDate.getMonth() + 1).padStart(2, '0');
    const year = currentDate.getFullYear();
    const fileName = `report_${day}-${month}-${year}.pdf`;
  
    pdf.save(fileName);
  };
  
  return (
    <div className="admin-dashboard">
      <div className="admin-content">
        <div className="admin-sidebar">
          <button className="admin-btn" onClick={() => navigate('/admin-company')}>
            Quản lý công ty
          </button>
          <button className="admin-btn" onClick={() => navigate('/admin-station')}>
            Danh sách trạm xe
          </button>
          <button className="admin-btn" onClick={() => navigate('/admin-create-station')}>
            Thêm trạm xe
          </button>
          <button className="admin-btn export-btn" onClick={() => exportReportToPDF(null)}>
            Xuất báo cáo PDF
          </button>
          <div>
            <label htmlFor="report-type">Báo cáo:</label>
            <select id="report-type" value={chartType} onChange={handleChartTypeChange}>
              <option value="bar">Biểu đồ cột</option>
              <option value="line">Doanh thu</option>
              <option value="pie">Người dùng</option>
            </select>
          </div>
          {chartType === 'line' && (
            <div>
              <label htmlFor="year-select">Chọn năm:</label>
              <select id="year-select" value={year} onChange={handleYearChange}>
                {years.map((yr) => (
                  <option key={yr} value={yr}>{yr}</option>
                ))}
              </select>
            </div>
          )}
        </div>

        <div className="admin-main">
          <h2>
            {chartType === 'pie'
              ? 'Thống kê người dùng'
              : chartType === 'line'
              ? 'Thống kê doanh thu'
              : 'Thống kê Số liệu'}
          </h2>
          <div className="admin-chart">{renderChart()}</div>
        </div>
      </div>
    </div>
  );
};

export default AdminDashboard;
