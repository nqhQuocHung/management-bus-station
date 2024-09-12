package com.busstation.repositories.impl;

import com.busstation.pojo.Car;
import com.busstation.repositories.CarRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CarRepositoryImpl implements CarRepository {

    @Autowired
    LocalSessionFactoryBean sessionFactoryBean;

    @Override
    public List<Car> findAvailableCarsByDate(Date date) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        String hql = "SELECT c FROM Car c WHERE c.id NOT IN (SELECT t.car.id FROM Trip t WHERE DATE(t.departAt) = :date)";
        Query query = session.createQuery(hql, Car.class);
        query.setParameter("date", date);
        return query.getResultList();
    }

    @Override
    public Car getById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Car car = session.get(Car.class, id);
        if (car == null) throw new IllegalArgumentException("Id not found");
        return car;
    }

    @Override
    public Optional<Car> findById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Car car = session.get(Car.class, id);
        return Optional.ofNullable(car);
    }
}
