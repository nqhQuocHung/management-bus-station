package com.busstation.repositories.impl;

import com.busstation.dtos.TripDTO;
import com.busstation.pojo.Car;
import com.busstation.pojo.Seat;
import com.busstation.pojo.Ticket;
import com.busstation.pojo.Trip;
import com.busstation.repositories.TripRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional
public class TripRepositoryImpl implements TripRepository {

    @Autowired
    LocalSessionFactoryBean sessionFactoryBean;


    @Override
    public List<Seat> getAvailableSeats(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Seat> seatCriteriaQuery = builder.createQuery(Seat.class);
        Root<Trip> tripRoot = seatCriteriaQuery.from(Trip.class);
        Join<Trip, Car> tripCarJoin = tripRoot.join("car");
        Join<Car, Seat> carSeatJoin = tripCarJoin.join("seats");

        Predicate tripIdPredicate = builder.equal(tripRoot.get("id"), id);
        seatCriteriaQuery.select(carSeatJoin);


        Subquery<Seat> seatSubquery = seatCriteriaQuery.subquery(Seat.class);
        Root<Trip> subQueryTripRoot = seatSubquery.from(Trip.class);
        Join<Trip, Ticket> subQueryTripTicketJoin = subQueryTripRoot.join("tickets");
        Join<Ticket, Seat> subQueryTicketSeatJoin = subQueryTripTicketJoin.join("seat");
        Predicate subQueryTripIdPredicate = builder.equal(subQueryTripRoot.get("id"), id);
        seatSubquery.select(subQueryTicketSeatJoin.get("code"));
        seatSubquery.where(subQueryTripIdPredicate);

        seatCriteriaQuery.where(builder.and(builder.not(carSeatJoin.get("code").in(seatSubquery)), tripIdPredicate));

        Query query = session.createQuery(seatCriteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<Seat> getUnAvailableSeats(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Seat> criteriaQuery = builder.createQuery(Seat.class);
        Root<Trip> tripRoot = criteriaQuery.from(Trip.class);
        Join<Trip, Ticket> tripTicketJoin = tripRoot.join("tickets");
        Join<Ticket, Seat> ticketSeatJoin = tripTicketJoin.join("seat");
        Predicate predicate = builder.equal(tripRoot.get("id"), id);
        criteriaQuery.select(ticketSeatJoin);
        criteriaQuery.where(predicate);
        Query query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public Seat availableSeat(Long tripId, Long seatId) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Seat> criteriaQuery = builder.createQuery(Seat.class);
        Subquery<Seat> seatSubquery = criteriaQuery.subquery(Seat.class);
        Root<Seat> seatRoot = criteriaQuery.from(Seat.class);
        criteriaQuery.select(seatRoot);

        // subquery
        Root<Trip> tripSubRoot = seatSubquery.from(Trip.class);
        Join<Trip, Ticket> tripTicketJoin = tripSubRoot.join("tickets");
        Join<Ticket, Seat> ticketSeatJoin = tripTicketJoin.join(("seat"));
        seatSubquery.select(ticketSeatJoin);
        Predicate subPredicateTripId = builder.equal(tripSubRoot.get("id"), tripId);

        seatSubquery.where(subPredicateTripId);

        criteriaQuery.where(builder.and(builder.not(seatRoot.get("id").in(seatSubquery)), builder.equal(seatRoot.get("id"), seatId)));

        Query query = session.createQuery(criteriaQuery);
        Seat seat = (Seat) query.getResultList().stream().findFirst().orElse(null);
        if (seat == null) throw new IllegalArgumentException(String.format("Seat with id %s is unavailable", seatId));
        return seat;

    }

    @Override
    public Trip getById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Trip trip = session.get(Trip.class, id);
        if (trip == null) throw new IllegalArgumentException("Trip id is not exist");
        return trip;
    }

    @Override
    public Trip save(Trip trip) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        session.saveOrUpdate(trip);
        return trip;
    }
}
