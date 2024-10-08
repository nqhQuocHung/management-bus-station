import { useContext, useEffect, useState } from 'react';
import './styles.css';
import { apis } from '../../config/apis';
import { LoadingContext } from '../../config/context';
import CompanyItem from './CompanyItem';
import RouteItem from './RouteItem';

const Grid = ({ title, breadcrumb, dataEndpoint }) => {
  const [page, setPage] = useState(1);
  const [data, setData] = useState([]);
  const { setLoading } = useContext(LoadingContext);
  const [pageTotal, setPageTotal] = useState(0);
  const [kw, setKw] = useState('');

  const fetchData = async () => {
    try {
      setLoading('flex');
      const response = await apis(null).get(
        `${dataEndpoint}?page=${page}&name=${kw}`,
      );
      if (response) {
        setData(response.data.results);
        setPageTotal(response.data.pageTotal);
      }
    } catch (ex) {
      console.error(ex);
    } finally {
      setLoading('none');
    }
  };

  useEffect(() => {
    fetchData();
  }, [page, kw]);

  const handlePrevPage = () => {
    if (page > 1) {
      setPage(page - 1);
    }
  };

  const handleNextPage = () => {
    if (page < pageTotal) {
      setPage(page + 1);
    }
  };

  return (
    <div className="container-fluid px-5 mt-5 border-bottom">
      <nav aria-label="breadcrumb" className="row d-flex justify-content-between px-5">
        <div className="col-md-6">
          <h2 className="px-3 fw-bolder">{title}</h2>
          <ol className="breadcrumb px-3">
            {breadcrumb.map((b, index) => (
              <li key={index} className="breadcrumb-item">
                {b}
              </li>
            ))}
            <li className="breadcrumb-item active">{page}</li>
          </ol>
        </div>
        <div className="col-3 d-flex align-items-center">
          <form className="w-100">
            <input
              className="form-control me-2"
              type="search"
              value={kw}
              onChange={(e) => setKw(e.target.value)}
              placeholder="Search"
              aria-label="Search"
            />
          </form>
        </div>
      </nav>

      <div className="row">
        <div className="grid-data">
          {data.map((value) => {
            if (title === 'Công ty') {
              return <CompanyItem key={value.id} value={value} />;
            } else if (title === 'Tuyến xe') {
              return <RouteItem key={value.id} value={value} />;
            }
          })}
        </div>
      </div>

      <div className="row mt-5" aria-label="page">
        <ul className="pagination d-flex justify-content-center">
          <li className={`page-item ${page === 1 ? 'disabled' : ''}`}>
            <button
              className="page-link"
              onClick={handlePrevPage}
              aria-label="Previous"
              disabled={page === 1}
            >
              &laquo;
            </button>
          </li>
          {[...Array(pageTotal)].map((_, index) => (
            <li
              key={index}
              className={`page-item ${page === index + 1 ? 'active' : ''}`}
            >
              <button
                className="page-link"
                onClick={() => setPage(index + 1)}
              >
                {index + 1}
              </button>
            </li>
          ))}
          <li className={`page-item ${page === pageTotal ? 'disabled' : ''}`}>
            <button
              className="page-link"
              onClick={handleNextPage}
              aria-label="Next"
              disabled={page === pageTotal}
            >
              &raquo;
            </button>
          </li>
        </ul>
      </div>
    </div>
  );
};

export default Grid;
