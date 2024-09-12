import './App.css';
import {
  LoadingContext,
  AuthenticationContext,
  cartReducer,
  CartContext,
} from './config/context';

import {useEffect, useReducer, useRef, useState} from 'react';
import Loading from './components/loading';

import {ToastContainer} from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import {apis, endpoints} from './config/apis';
import CartIcon from './components/CartIcon';
import AppRouter from './routes';
import {GoogleOAuthProvider} from '@react-oauth/google';

function App() {
  const [loading, setLoading] = useState('none');
  const [user, setUser] = useState(null);
  const [cart, cartDispatcher] = useReducer(cartReducer, {
    key: '',
    data: [],
  });

  const fetchUserInfor = async (accessToken) => {
    try {
      setLoading('flex');
      const response = await apis(accessToken).get(endpoints.userInfor);
      if (response) {
        setUser(response['data']);
      } else {
        localStorage.removeItem('accessToken');
      }
    } catch (ex) {
      localStorage.removeItem('accessToken');
    } finally {
      setLoading('none');
    }
  };
  useEffect(() => {
    const accessToken = localStorage.getItem('accessToken');
    if (accessToken && !user) {
      fetchUserInfor(accessToken);
    }
  }, []);

  useEffect(() => {
    const localStorageItem = localStorage.getItem('cart');
    if (cart['key'] === '' && localStorageItem !== '' && !localStorageItem) {
      try {
        const temp = JSON.parse(localStorageItem);
        if (temp.length > 0) {
          cartDispatcher({
            type: 'ADD_TO_CART',
            payload: temp,
          });
        }
      } catch (ex) {
        console.error(ex);
      }
    } else {
      localStorage.setItem('cart', JSON.stringify(cart['data']));
    }
  }, [cart['key']]);

  return (
    <div className="container-fluid">
      <GoogleOAuthProvider clientId="74220990475-fqfc35bpnobkpphc31ik8mnuq1fm93og.apps.googleusercontent.com">
        <AuthenticationContext.Provider value={{user, setUser}}>
          <LoadingContext.Provider value={{loading, setLoading}}>
            <CartContext.Provider value={{cart, cartDispatcher}}>
              <Loading />
              <ToastContainer />
              <CartIcon />
              <AppRouter />
            </CartContext.Provider>
          </LoadingContext.Provider>
        </AuthenticationContext.Provider>
      </GoogleOAuthProvider>
    </div>
  );
}

export default App;
