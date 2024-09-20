import axios from 'axios';

// const BASE_URL = 'http://localhost:8080/busstation';
const BASE_URL = 'http://localhost:8080';
const endpoints = {
  login: '/api/auth/authenticate',
  register: '/api/v1/auth/register',

  upload_image : '/api/upload',
  update_user: (id) => `/api/users/${id}`,
  register_user : '/api/users/register',

  company_list: '/api/companies',
  // company_list: 'api/v1/transportation_company/list',
  company_list_idName: '/api/companies/name-id',
  // company_list_idName: '/api/v1/transportation_company/list/name-and-id',
  register_company: '/api/companies/register',
  get_company_managerid: (id) => `/api/companies/manager/${id}`,
  // get_company_managerid: (id) => `/api/v1/transportation_company/manager/${id}`,
  userInfor: '/api/v1/users/self',
  route_list: '/api/routes',
  // route_list: '/api/v1/route/list',
  add_cart: '/api/tickets/add-cart',
  cart_details: '/api/tickets/details',
  add_cargo: '/api/cargos',
   //Xong
  payment_method_list: '/api/payment-methods',
  create_route: '/api/routes/add',
  list_station: '/api/stations/list',
  get_available_seat: (tripId) => `/api/seats/available?tripId=${tripId}`,
  get_occupied_seat: (tripId) => `/api/seats/occupied/${tripId}`,
  //
  available_cars: '/api/cars/available',
  creat_trip: '/api/trips/create',
  self_ticket: '/api/v1/users/self/tickets',
  login_with_google: '/api/v1/auth/oauth2/google',
  //Xong
  statistics_ticket_year: (year) => `/api/statistics/annual/${year}`,
  statistics_ticket_quarterly: (year) => `/api/statistics/quarterly/${year}`,
  statistics_ticket_day: (year, month, day) =>
    `/api/statistics/daily/${year}/${month}/${day}`,
  //
  
  //xong
  register_cargo: (id) => `api/companies/cargo/${id}`,
  route_info: (id) => `/api/routes/${id}`,
  get_route_by_companyid: (id) => `/api/routes/company/${id}`,
  
  route_trip_list: (id) => `/api/trips/route/${id}`,
  trip_seat_details: (id) => `/api/v1/trip/${id}/seat-details`,
  checkout: (paymentMethodId) => `/api/v1/ticket/checkout/${paymentMethodId}`,
  // user: (id) => `/api/v1/users/${id}`,
  
  get_user_by_id: (id) => `/api/users/${id}`,
  get_user_by_role: (id) => `/admin/users/role/${id}`,
  // get_user_by_role: (id) => `/admin/users/role/${id}`,
  companyInfo: (id) => `/api/companies/${id}`,
  ticket: (id) => `/api/tickets/${id}`,
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
