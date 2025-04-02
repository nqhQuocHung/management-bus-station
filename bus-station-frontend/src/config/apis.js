import axios from 'axios';

// const BASE_URL = 'http://localhost:8080/busstation';
// const BASE_URL = 'http://localhost:8080';
const BASE_URL = 'http://54.82.60.156:8080/';
const endpoints = {
  // Authentication
  login: '/api/auth/authenticate',
  login_with_google: '/api/v1/auth/oauth2/google',
  forgot_password: '/api/auth/forgot-password',
  change_password: '/api/auth/change-password',
  login_with_otp: '/api/auth/login-otp',
  get_otp: '/api/auth/send-otp',

  // User Management
  update_user: (id) => `/api/users/${id}`,
  register_user: '/api/users/register',
  // userInfor: '/api/v1/users/self',
  // self_ticket: '/api/v1/users/self/tickets',
  get_user_by_id: (id) => `/api/users/${id}`,
  get_user_by_role: (id) => `/admin/users/role/${id}`,
  get_user_by_username:(username) => `/api/users/username/${username}`,

  // Company Management
  verified_company_list: '/api/companies',
  company_list: '/api/companies/verified',
  company_list_idName: '/api/companies/name-id',
  register_company: '/api/companies/register',
  company_list_admin: '/api/companies/list',
  verify_company: (id) => `/api/companies/verify/${id}`,
  get_company_managerid: (id) => `/api/companies/manager/${id}`,
  companyInfo: (id) => `/api/companies/${id}`,

  // Driver Management
  driver_list_by_company: (companyId) => `/api/drivers/company/${companyId}`,
  driver_create: '/api/drivers/create',
  driver_verify: (id) => `/api/drivers/verify/${id}`,
  verified_driver_list_by_company: (companyId) => `/api/drivers/verified/company/${companyId}`,
  driver_available: '/api/drivers/available',

  // Trip Management
  creat_trip: '/api/trips/create',
  update_trip_status: (id) => `/api/trips/${id}/status`,
  get_trips_by_driver: (id) => `/api/trips/driver/${id}`,
  route_trip_list: (id) => `/api/trips/route/${id}`,
  get_list_passengers: (id) => `/api/trips/${id}/passengers`,
  
  // Route Management
  route_list: '/api/routes',
  create_route: '/api/routes/add',
  route_info: (id) => `/api/routes/${id}`,
  get_route_by_companyid: (id) => `/api/routes/company/${id}`,

  // Ticket Management
  add_cart: '/api/tickets/add-cart',
  cart_details: '/api/tickets/details',
  get_cart_details: '/api/tickets/cart/details',
  ticket: (id) => `/api/tickets/${id}`,
  update_payment_ticket: '/api/tickets/payment',
  get_ticket_of_user: (id) => `/api/tickets/user/${id}/paid`,

  // Payment Management
  payment_method_list: '/api/payment-methods',
  payment_url: '/api/payment/create',
  payment_result: '/api/payment/vnpay_return',
  payment_bill: '/api/payment/bill',
  // checkout: (paymentMethodId) => `/api/v1/ticket/checkout/${paymentMethodId}`,

  // Seat Management
  get_available_seat: (tripId) => `/api/seats/available?tripId=${tripId}`,
  get_occupied_seat: (tripId) => `/api/seats/occupied/${tripId}`,

  // Car Management
  available_cars: '/api/cars/available',
  create_car: '/api/cars/create',
  get_car_by_company:(id) => `api/cars/company/${id}`,

  // Cargo Management
  add_cargo: '/api/cargos',
  register_cargo: (id) => `/api/companies/cargo/${id}`,
  
  // Station Management
  list_station: '/api/stations/list',
  create_station: '/api/stations',

  // Statistics
  statistics_ticket_year: (year) => `/api/statistics/annual/${year}`,
  statistics_ticket_quarterly: (year) => `/api/statistics/quarterly/${year}`,
  statistics_ticket_day: (year, month, day) => `/api/statistics/daily/${year}/${month}/${day}`,
  statistics_user: "/api/statistics/user-statistics",
  statistics_ticket: "/api/statistics/annual-revenue",
  statistics_bar: "/api/statistics/bar-data",

  // Comments and Ratings
  get_comments_by_company: (companyId) => `/api/comments/company/${companyId}`,
  get_average_rating: (companyId) => `/api/comments/rating/${companyId}`,
  create_comment: () => `/api/comments/create`,

  // Image Upload
  upload_image: '/api/upload',
};


const apis = (accessToken) => {
  const instance = axios.create({
    baseURL: BASE_URL,
    headers: accessToken ? { Authorization: `Bearer ${accessToken}` } : {},
  });

  instance.interceptors.response.use(
    (response) => response,
    (error) => {
      if (error.response) {
        console.error("Lỗi từ server:", error.response);
      } else if (error.request) {
        console.error("Không nhận được phản hồi từ server:", error.request);
      } else {
        console.error("Lỗi không xác định:", error.message);
      }
      return Promise.reject(error);
    }
  );

  return instance;
};

export { apis, endpoints };
