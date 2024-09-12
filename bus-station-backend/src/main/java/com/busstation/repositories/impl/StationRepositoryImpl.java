
package com.busstation.repositories.impl;

import com.busstation.pojo.Station;
import com.busstation.repositories.StationRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@Transactional
@PropertySource("classpath:configuration.properties")
public class StationRepositoryImpl implements StationRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;

    @Autowired
    private Environment environment;

    @Override
    public List<Station> getAll() {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        return session.createQuery("FROM Station", Station.class).list();
    }


    @Override
    public Station findById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        return session.get(Station.class, id);
    }

    @Override
    public void save(Station station) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        session.saveOrUpdate(station);
    }

    @Override
    public void update(Station station) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Station existingStation = session.byId(Station.class).load(station.getId());
        if (existingStation != null) {
            existingStation.setAddress(station.getAddress());
            existingStation.setMapUrl(station.getMapUrl());
            session.flush();
        }
    }

    @Override
    public void deleteById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Station station = session.byId(Station.class).load(id);
        if (station != null) {
            session.delete(station);
        }
    }

    @Override
    public Optional<Station> getStationById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Station station = session.get(Station.class, id);
        return Optional.ofNullable(station);
    }
}
