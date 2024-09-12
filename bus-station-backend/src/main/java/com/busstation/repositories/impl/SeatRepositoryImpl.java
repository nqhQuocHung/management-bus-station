package com.busstation.repositories.impl;

import com.busstation.pojo.Seat;
import com.busstation.repositories.SeatRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Repository
public class SeatRepositoryImpl implements SeatRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;


    @Override
    public Seat getById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Seat seat = session.get(Seat.class, id);
        if (seat == null) throw new IllegalArgumentException("Seat id is not exist");
        return seat;
    }
}
