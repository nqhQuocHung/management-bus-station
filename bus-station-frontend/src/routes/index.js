import Cart from '../components/Cart';
import {Routes, Route, BrowserRouter} from 'react-router-dom';
import Grid from '../components/Grid';
import RouteInfo from '../pages/RouteInfo';
import AuthenticatedRoute from './AuthenticatedRoute';
import Checkout from '../pages/Checkout';
import Login from '../pages/login';
import Register from '../pages/register';
import CreateCompany from '../pages/createCompany';
import ManageCompany from '../pages/ManageCompany';
import CreateRoute from '../pages/CreateRoute';
import CreateTrip from '../pages/CreateTrip';
import Home from '../pages/Home';
import {endpoints} from '../config/apis';
import PaymentResult from '../pages/PaymentResult';
import ManagerRoute from './ManagerRoute';
import Profile from '../pages/Profile';
import CompanyInfo from '../pages/CompanyInfo';
import CustomerTicket from '../pages/CustomerTicket';
import VnpayPaymentResult from '../pages/VnpayPaymentResult';
import DriverList from '../pages/DriverList';
import ManageWork from '../pages/ManageWork';
import ChangePassword from '../pages/ChangePassword';
import AdminDashboard from '../pages/AdminDashboard';
import AdminManageCompany from '../pages/AdminManageCompany';
import CreateStation from '../pages/CreateStation';
import StationList from '../pages/StationList/StationList';
const AppRouter = () => {
  return (
    <BrowserRouter>
      <Cart />
      <Routes>
        <Route path="/" element={<Home />}>
          <Route
            index={true}
            element={
              <>
                <Grid
                  title="Công ty"
                  dataEndpoint={endpoints.company_list}
                  breadcrumb={['Trang chủ', 'Công ty']}
                />

                <Grid
                  title="Tuyến xe"
                  dataEndpoint={endpoints.route_list}
                  breadcrumb={['Trang chủ', 'Tuyến xe']}
                />
              </>
            }
          />
          <Route path="/route/:id" element={<RouteInfo />} />
          <Route path="/company/:id" element={<CompanyInfo />} />
          <Route
            path="/checkout"
            element={
              <AuthenticatedRoute>
                <Checkout />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/ticket"
            element={
              <AuthenticatedRoute>
                <CustomerTicket />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/profile"
            element={
              <AuthenticatedRoute>
                <Profile />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/create-company"
            element={
              <AuthenticatedRoute>
                <CreateCompany />
              </AuthenticatedRoute>
            }
          />
           <Route
            path="/driver-work"
            element={
              <AuthenticatedRoute>
                <ManageWork />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/admin"
            element={
              <AuthenticatedRoute>
                <AdminDashboard/>
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/admin-company"
            element={
              <AuthenticatedRoute>
                <AdminManageCompany/>
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/admin-create-station"
            element={
              <AuthenticatedRoute>
                <CreateStation/>
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/admin-station"
            element={
              <AuthenticatedRoute>
                <StationList/>
              </AuthenticatedRoute>
            }
          />
           <Route
            path="/change-password"
            element={
              <AuthenticatedRoute>
                <ChangePassword />
              </AuthenticatedRoute>
            }
          />
          <Route
            path="/manage-company"
            element={
              <ManagerRoute>
                <ManageCompany />
              </ManagerRoute>
            }
          />
          <Route
            path="/create-route"
            element={
              <ManagerRoute>
                <CreateRoute />
              </ManagerRoute>
            }
          />
          <Route
            path="/register-trip"
            element={
              <ManagerRoute>
                <CreateTrip />
              </ManagerRoute>
            }
          />
           <Route
            path="/company_drivers"
            element={
              <ManagerRoute>
                <DriverList/>
              </ManagerRoute>
            }
          />
        </Route>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/vnpay_result" element={<VnpayPaymentResult />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;
