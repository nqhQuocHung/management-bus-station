import React, { useState, useEffect, useContext } from 'react';
import { apis, endpoints } from '../../config/apis';
import { AuthenticationContext, LoadingContext } from '../../config/context';
import './styles.css';

const ManageWork = () => {
  const { user } = useContext(AuthenticationContext);
  const { setLoading } = useContext(LoadingContext);
  const accessToken = localStorage.getItem('accessToken');
  const [trips, setTrips] = useState([]);

  useEffect(() => {
    const fetchTrips = async () => {
      try {
        setLoading('flex');
        const api = apis(accessToken);
        const response = await api.get(endpoints.get_trips_by_driver(user.id));
        setTrips(response.data);
      } catch (error) {
        console.error('Error fetching trips:', error);
        setTrips([]);
      } finally {
        setLoading('none');
      }
    };

    if (user && user.id) {
      fetchTrips();
    }
  }, [user, accessToken, setLoading]);

  const handleMarkComplete = async (tripId) => {
    try {
      setLoading('flex');
      const api = apis(accessToken);
      await api.patch(`${endpoints.update_trip_status}/${tripId}`, { status: true });
      setTrips((prevTrips) => prevTrips.map(trip => trip.id === tripId ? { ...trip, status: true } : trip));
    } catch (error) {
      console.error('Error marking trip as complete:', error);
    } finally {
      setLoading('none');
    }
  };

  return (
    <div className="manage-work-container">
      <h2>Quản lý công việc</h2>
      {trips.length > 0 ? (
        <table className="work-table">
          <thead>
            <tr>
              <th>Mã tuyến</th>
              <th>Mã xe</th>
              <th>Thời gian xuất phát</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            {trips.map((trip) => (
              <tr key={trip.id}>
                <td>{trip.routeName}</td>
                <td>{trip.carNumber}</td>
                <td>{new Date(trip.departAt).toLocaleString()}</td>
                <td>
                  {trip.status ? (
                    <span className="completed-status">Hoàn thành</span>
                  ) : (
                    <button
                      className="complete-button"
                      onClick={() => handleMarkComplete(trip.id)}
                    >
                      Hoàn thành
                    </button>
                  )}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>Không có công việc nào để hiển thị.</p>
      )}
    </div>
  );
};

export default ManageWork;
