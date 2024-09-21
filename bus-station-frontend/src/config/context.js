import {createContext} from 'react';

const LoadingContext = createContext('none');
const AuthenticationContext = createContext(null);
const CartContext = createContext(null);
const CompanyContext = createContext(null);




export {
  CompanyContext,
  LoadingContext,
  AuthenticationContext,
  CartContext,
};
