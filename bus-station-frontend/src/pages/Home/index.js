import './styles.css';
import Navbar from '../../components/navbar';
import Footer from '../../components/footer';
import {Outlet} from 'react-router-dom';
const Home = () => {
  return (
    <>
      <Navbar />

      <Outlet />

      <Footer />
    </>
  );
};

export default Home;
