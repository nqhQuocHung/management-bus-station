package com.busstation.repositories.impl;

import com.busstation.dtos.StatisticsDTO;
import com.busstation.pojo.OnlinePaymentResult;
import com.busstation.pojo.Ticket;
import com.busstation.pojo.User;
import com.busstation.repositories.TicketRepository;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Transactional
public class TicketRepositoryImpl implements TicketRepository {

    @Autowired
    private LocalSessionFactoryBean localSessionFactoryBean;

    @Override
    public void saveAll(List<Ticket> tickets) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        tickets.forEach(session::save);
    }

    @Override
    public void updateAll(List<Ticket> tickets) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        tickets.forEach(session::update);
    }


    @Override
    public Map<Integer, StatisticsDTO> calculateAnnualRevenue(int year, Long companyId) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        String hql = "SELECT MONTH(t.paidAt), SUM(t.seatPrice), COALESCE(SUM(c.cargoPrice), 0) " +
                "FROM Ticket t " +
                "LEFT JOIN t.cargo c " +
                "JOIN t.trip tr " +
                "JOIN tr.route r " +
                "JOIN r.company comp " +
                "WHERE YEAR(t.paidAt) = :year AND comp.id = :companyId " +
                "GROUP BY MONTH(t.paidAt) " +
                "ORDER BY MONTH(t.paidAt)";
        Query<Object[]> query = session.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("companyId", companyId);
        List<Object[]> results = query.list();

        System.out.println("Results: " + results.size());

        for (Object[] result : results) {
            System.out.println("Month: " + result[0] + ", TotalTicket: " + result[1] + ", TotalCargo: " + result[2]);
        }

        Map<Integer, StatisticsDTO> revenueMap = new HashMap<>();
        for (Object[] result : results) {
            Integer month = (Integer) result[0];
            Double totalTicket = (Double) result[1];
            Double totalCargo = (Double) result[2];
            StatisticsDTO statisticsDTO = new StatisticsDTO(totalTicket, totalCargo);
            revenueMap.put(month, statisticsDTO);
        }
        return revenueMap;
    }


    @Override
    public Map<Integer, StatisticsDTO> calculateQuarterlyRevenue(int year, Long companyId) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        String hql = "SELECT QUARTER(t.paidAt), SUM(t.seatPrice), COALESCE(SUM(c.cargoPrice), 0) " +
                "FROM Ticket t " +
                "LEFT JOIN t.cargo c " +
                "JOIN t.trip tr " +
                "JOIN tr.route r " +
                "JOIN r.company comp " +
                "WHERE YEAR(t.paidAt) = :year AND comp.id = :companyId " +
                "GROUP BY QUARTER(t.paidAt) " +
                "ORDER BY QUARTER(t.paidAt)";
        Query<Object[]> query = session.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("companyId", companyId);
        List<Object[]> results = query.list();

        Map<Integer, StatisticsDTO> revenueMap = new HashMap<>();
        for (Object[] result : results) {
            Integer quarter = (Integer) result[0];
            Double totalTicket = (Double) result[1];
            Double totalCargo = (Double) result[2];
            StatisticsDTO statisticsDTO = new StatisticsDTO(totalTicket, totalCargo);
            revenueMap.put(quarter, statisticsDTO);
        }
        return revenueMap;
    }


    @Override
    public Map<Integer, StatisticsDTO> calculateDailyRevenue(int year, int month, int day, Long companyId) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        String hql = "SELECT DAY(t.paidAt), SUM(t.seatPrice), COALESCE(SUM(c.cargoPrice), 0) " +
                "FROM Ticket t " +
                "LEFT JOIN t.cargo c " +
                "JOIN t.trip tr " +
                "JOIN tr.route r " +
                "JOIN r.company comp " +
                "WHERE YEAR(t.paidAt) = :year AND MONTH(t.paidAt) = :month AND DAY(t.paidAt) = :day AND comp.id = :companyId " +
                "GROUP BY DAY(t.paidAt) " +
                "ORDER BY DAY(t.paidAt)";
        Query<Object[]> query = session.createQuery(hql, Object[].class);
        query.setParameter("year", year);
        query.setParameter("month", month);
        query.setParameter("day", day);
        query.setParameter("companyId", companyId);
        List<Object[]> results = query.list();

        Map<Integer, StatisticsDTO> revenueMap = new HashMap<>();
        for (Object[] result : results) {
            Integer dayOfMonth = (Integer) result[0];
            Double totalTicket = (Double) result[1];
            Double totalCargo = (Double) result[2];
            StatisticsDTO statisticsDTO = new StatisticsDTO(totalTicket, totalCargo);
            revenueMap.put(dayOfMonth, statisticsDTO);
        }
        return revenueMap;
    }

    @Override
    public List<Ticket> findTicketsByUserId(Long userId) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Ticket> criteriaQuery = builder.createQuery(Ticket.class);
        Root<Ticket> ticketRoot = criteriaQuery.from(Ticket.class);
        Predicate userIdPredicate = builder.equal(ticketRoot.get("customer"), userId);
        criteriaQuery.select(ticketRoot);
        criteriaQuery.where(userIdPredicate);
        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public void delete(Long id) {
        Session session = localSessionFactoryBean.getObject().getCurrentSession();
        String hql = "DELETE Ticket t where t.id =:id";
        Query query = session.createQuery(hql);
        query.setParameter("id", id);
        query.executeUpdate();

    }

    @Override
    public Ticket getById(Long id) {
        Session session  = localSessionFactoryBean.getObject().getCurrentSession();
        Ticket ticket = session.get(Ticket.class, id);
        if (ticket == null) throw  new IllegalArgumentException("Ticket id is not exist");
        return ticket;
    }
}
