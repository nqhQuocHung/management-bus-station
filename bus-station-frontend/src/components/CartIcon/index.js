import {FiShoppingCart} from 'react-icons/fi';
import './styles.css';
import {useContext} from 'react';
import {CartContext} from '../../config/context';

const CartIcon = () => {
  const {cart} = useContext(CartContext);
  return (
    <button
      className="cart-icon-container px-3"
      type="button"
      data-bs-toggle="offcanvas"
      data-bs-target="#offcanvasWithBothOptions"
      aria-controls="offcanvasWithBothOptions"
    >
      <FiShoppingCart size="2em" color="#00AEFF" />
      <span className="position-absolute top-0 start-0 translate-middle badge rounded-pill bg-danger">
        {cart['data'].length}
        <span className="visually-hidden">Total tickets</span>
      </span>
    </button>
  );
};

export default CartIcon;
