package com.busstation.repositories.impl;

import com.busstation.pojo.User;
import com.busstation.repositories.UserRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
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


@Repository
@Transactional
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private Environment environment;

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public User getUserByUserName(String username) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        criteriaQuery.where(builder.equal(root.get("username"), username));
        Query query = session.createQuery(criteriaQuery);

        return (User) query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public void saveUser(User newUser) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.save(newUser);
    }

    @Override
    public boolean isEmailExist(String email) {
        Session session = sessionFactory.getObject().getCurrentSession();
        Query query = session.createQuery("from User u where u.email = :email", User.class)
                .setParameter("email", email);
        return query.getResultList().size() > 0;
    }

    @Override
    public List<User> listActiveUsers() {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);

        // Add predicate for isActive = true
        Predicate activePredicate = builder.isTrue(root.get("isActive"));
        criteriaQuery.where(activePredicate);

        Query query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<User> findActiveUsersByRoleId(Long roleId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);

        Predicate activePredicate = builder.isTrue(root.get("isActive"));
        Predicate rolePredicate = builder.equal(root.get("role").get("id"), roleId);
        criteriaQuery.where(builder.and(activePredicate, rolePredicate));

        Query query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public User getUserById(Long userId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        User user = session.get(User.class, userId);
        if (user == null) throw new IllegalArgumentException("User id is not exist");
        return user;
    }

    @Override
    public void changeRole(User user) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.saveOrUpdate(user);
    }

    @Override
    public User update(User user) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.update(user);
        return user;
    }

    @Override
    public long countUsersByRoleId(Long roleId) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<User> root = criteriaQuery.from(User.class);

        Predicate rolePredicate = builder.equal(root.get("role").get("id"), roleId);
        Predicate activePredicate = builder.isTrue(root.get("isActive"));

        criteriaQuery.select(builder.count(root));
        criteriaQuery.where(builder.and(rolePredicate, activePredicate));

        Query query = session.createQuery(criteriaQuery);
        return (Long) query.getSingleResult();
    }

    @Override
    public User getUserByEmail(String email) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = builder.createQuery(User.class);
        Root root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);
        criteriaQuery.where(builder.equal(root.get("email"), email));
        Query query = session.createQuery(criteriaQuery);

        return (User) query.getResultList().stream().findFirst().orElse(null);

    }

}

