package com.busstation.repositories.impl;

import com.busstation.pojo.Route;
import com.busstation.repositories.RouteRepository;
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
import java.util.*;

@Repository
@Transactional
@PropertySource("classpath:configuration.properties")
public class RouteRepositoryImpl implements RouteRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;

    @Override
    public List<Route> list(Map<String, String> params) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Route> criteriaQueryResult = builder.createQuery(Route.class);
        Root root = criteriaQueryResult.from(Route.class);
        criteriaQueryResult.select(root);
        List<Predicate> predicates = createPredicates(builder, root, params);
        criteriaQueryResult.where(predicates.toArray(Predicate[]::new));
        Query queryResult = session.createQuery(criteriaQueryResult);
        String page = params.get("page");
        if (page != null && !page.isEmpty()) {
            int pageSize = Integer.parseInt(environment.getProperty("route.pageSize"));
            int start = (Integer.parseInt(page) - 1) * pageSize;
            queryResult.setFirstResult(start);
            queryResult.setMaxResults(pageSize);
        }
       return queryResult.getResultList();
    }

    @Override
    public Long count(Map<String, String> params) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root root = criteriaQuery.from(Route.class);
        criteriaQuery.select(builder.count(root));
        List<Predicate> predicates = createPredicates(builder, root, params);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        return session.createQuery(criteriaQuery).getSingleResult();
    }

    @Override
    public Route getById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Route route = session.get(Route.class, id);
        if (route == null) throw new IllegalArgumentException("Id not found");
        return route;
    }


    private List<Predicate> createPredicates(CriteriaBuilder builder , Root root , Map<String, String> params) {
        List<Predicate> results = new ArrayList<>();
        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate predicate = builder.like(root.get("name"), String.format("%%%s%%", kw));
            results.add(predicate);
        }
        return results;
    }

    @Override
    public void save(Route route) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        try {
            session.saveOrUpdate(route);
        } catch (Exception e) {
            throw new RuntimeException("Error saving or updating Route", e);
        }
    }

    @Override
    public List<Route> findByCompanyId(Long companyId) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        String hql = "SELECT r FROM Route r " +
                "JOIN FETCH r.company c " +
                "JOIN FETCH r.fromStation " +
                "JOIN FETCH r.toStation " +
                "WHERE c.id = :companyId";
        return session.createQuery(hql, Route.class)
                .setParameter("companyId", companyId)
                .getResultList();
    }

    @Override
    public Optional<Route> findById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        Route route = session.get(Route.class, id);
        return Optional.ofNullable(route);
    }
}
