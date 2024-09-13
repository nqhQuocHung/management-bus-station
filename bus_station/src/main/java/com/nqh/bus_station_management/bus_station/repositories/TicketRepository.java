package com.nqh.bus_station_management.bus_station.repositories;

import com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO;
import com.nqh.bus_station_management.bus_station.pojo.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Tìm vé dựa trên ID người dùng
    @Query("SELECT t FROM Ticket t WHERE t.customer.id = :userId")
    List<Ticket> findTicketsByUserId(@Param("userId") Long userId);

    // Lấy vé dựa trên ID
    @Query("SELECT t FROM Ticket t WHERE t.id = :id")
    Ticket getById(@Param("id") Long id);

    // Tính doanh thu hàng năm theo công ty
    @Query("SELECT new com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO(SUM(t.seatPrice), COUNT(t)) " +
            "FROM Ticket t WHERE YEAR(t.paidAt) = :year " +
            "AND t.trip.route.company.id = :companyId " +
            "GROUP BY MONTH(t.paidAt)")
    List<StatisticsDTO> calculateAnnualRevenue(@Param("year") int year, @Param("companyId") Long companyId);

    // Tính doanh thu hàng quý theo công ty
    @Query("SELECT new com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO(SUM(t.seatPrice), COALESCE(SUM(c.cargoPrice), 0)) " +
            "FROM Ticket t " +
            "LEFT JOIN t.cargo c " +
            "JOIN t.trip.route.company comp " +
            "WHERE YEAR(t.paidAt) = :year AND comp.id = :companyId " +
            "GROUP BY QUARTER(t.paidAt) " +
            "ORDER BY QUARTER(t.paidAt)")
    List<StatisticsDTO> calculateQuarterlyRevenue(@Param("year") int year, @Param("companyId") Long companyId);

    // Tính doanh thu hàng ngày theo công ty
    @Query("SELECT new com.nqh.bus_station_management.bus_station.dtos.StatisticsDTO(SUM(t.seatPrice), COUNT(t)) " +
            "FROM Ticket t WHERE YEAR(t.paidAt) = :year AND MONTH(t.paidAt) = :month AND DAY(t.paidAt) = :day AND t.trip.route.company.id = :companyId")
    List<StatisticsDTO> calculateDailyRevenue(@Param("year") int year, @Param("month") int month, @Param("day") int day, @Param("companyId") Long companyId);

    // Cập nhật thông tin vé
    @Modifying
    @Transactional
    @Query("UPDATE Ticket t SET t.seatPrice = :seatPrice WHERE t.id = :ticketId")
    void updateSeatPrice(@Param("ticketId") Long ticketId, @Param("seatPrice") Double seatPrice);

    // Xóa vé dựa trên ID
    @Modifying
    @Transactional
    @Query("DELETE FROM Ticket t WHERE t.id = :id")
    void deleteById(@Param("id") Long id);
}
