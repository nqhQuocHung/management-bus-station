import {useContext} from 'react';

import {Navigate, useLocation} from 'react-router-dom';
import {AuthenticationContext} from '../config/context';
import LoadingPrototype from '../components/LoadingPrototype';

const AuthenticatedRoute = ({children}) => {
  const {user} = useContext(AuthenticationContext);

  let {pathname} = useLocation();
  const accessToken = localStorage.getItem('accessToken');

  if (!user) {
    if (accessToken) {
      return <LoadingPrototype />;
    } else {
      return <Navigate to={'/login'} state={{from: pathname}} />;
    }
  } else {
    if (user['role'] === 'COMPANY_MANAGER') {
      return children;
    } else {
      return <Navigate to={'/create-company'} />;
    }
  }
};

export default AuthenticatedRoute;
