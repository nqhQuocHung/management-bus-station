package com.busstation.repositories.impl;

import com.busstation.pojo.Role;
import com.busstation.repositories.RoleRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
@Transactional
public class RoleRepositoryImpl implements RoleRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public Role getRoleByName(String name) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Role> criteriaQuery = builder.createQuery(Role.class);
        Root root = criteriaQuery.from(Role.class);
        criteriaQuery.select(root);
        criteriaQuery.where(builder.equal(root.get("name"), name));
        Query query = session.createQuery(criteriaQuery);
        return (Role) query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public Role findById(Long id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Role.class, id);
    }
}
