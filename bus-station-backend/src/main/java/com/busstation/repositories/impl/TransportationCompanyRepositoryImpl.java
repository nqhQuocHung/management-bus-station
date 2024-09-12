package com.busstation.repositories.impl;

import com.busstation.dtos.TransportationCompanyDTO;
import com.busstation.pojo.TransportationCompany;
import com.busstation.repositories.TransportationCompanyRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
public class TransportationCompanyRepositoryImpl implements TransportationCompanyRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;

    @Autowired
    Environment environment;

    @Override
    public List<TransportationCompany> list(Map<String, String> params) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<TransportationCompany> criteriaQuery = builder.createQuery(TransportationCompany.class);
        Root root = criteriaQuery.from(TransportationCompany.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = createPredicates(builder, root, params);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));

        Query query = session.createQuery(criteriaQuery);

        String page = params.get("page");
        if (page != null && !page.isEmpty()) {
            int pageSize = Integer.parseInt(environment.getProperty("transportationCompany.pageSize").toString());

            int start = (Integer.parseInt(page) -1 ) * pageSize;
            query.setFirstResult(start);
            query.setMaxResults(pageSize);

        }
        return query.getResultList();
    }

    @Override

    public Long count(Map<String, String> params) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root root = criteriaQuery.from(TransportationCompany.class);
        criteriaQuery.select(builder.count(root));
        List<Predicate> predicates = createPredicates(builder, root, params);
        criteriaQuery.where(predicates.toArray(Predicate[]::new));
        return session.createQuery(criteriaQuery).getSingleResult();
    }

    private List<Predicate> createPredicates(CriteriaBuilder builder , Root root , Map<String, String> params) {
        List<Predicate> results = new ArrayList<>();
        String kw = params.get("kw");
        if (kw != null && !kw.isEmpty()) {
            Predicate predicate = builder.like(root.get("name"), String.format("%%%s%%", kw));
            results.add(predicate);
        }
        Predicate isActivePredicate = builder.isTrue(root.get("isActive"));
        results.add(isActivePredicate);
        Predicate isVerifiedPredicate = builder.isTrue((root.get("isVerified")));
        results.add(isVerifiedPredicate);
        return results;
    }


    public TransportationCompany getTransportationCompanyById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TransportationCompany> criteria = builder.createQuery(TransportationCompany.class);
        Root root = criteria.from(TransportationCompany.class);
        criteria.select(root);
        criteria.where(builder.equal(root.get("id"), id));
        Query query = session.createQuery(criteria);
        return (TransportationCompany) query.getResultList().stream().findFirst().orElse(null);
    }

    @Override
    public void saveTransportationCompany(TransportationCompany newtransportationCompany) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        session.persist(newtransportationCompany);
    }

    @Override
    public void updateTransportationCompany(TransportationCompany transportationCompany) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        session.saveOrUpdate(transportationCompany);
    }

    @Override
    public void deleteById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        TransportationCompany company = session.get(TransportationCompany.class, id);
        if (company != null) {
            session.delete(company);
        }
    }

    @Override
    public Optional<TransportationCompany> findById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        TransportationCompany company = session.get(TransportationCompany.class, id);
        return Optional.ofNullable(company);
    }

    @Override
    public void save(TransportationCompany company) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        session.saveOrUpdate(company);
    }

    @Override
    public List<TransportationCompany> findByIsVerifiedFalse() {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<TransportationCompany> criteriaQuery = builder.createQuery(TransportationCompany.class);
        Root<TransportationCompany> root = criteriaQuery.from(TransportationCompany.class);
        criteriaQuery.select(root);

        Predicate verifiedPredicate = builder.isFalse(root.get("isVerified"));
        criteriaQuery.where(verifiedPredicate);

        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public void verifyCompany(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        TransportationCompany company = session.get(TransportationCompany.class, id);
        if (company != null) {
            company.setIsVerified(true);
            session.update(company);
        }
    }

    @Override
    public void cargo(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        TransportationCompany company = session.get(TransportationCompany.class, id);
        if (company != null) {
            company.setIsCargoTransport(true);
            session.update(company);
        }
    }

    @Override
    public TransportationCompanyDTO getCompanyAndManager(Long companyId) {
        try (Session session = sessionFactoryBean.getObject().openSession()) {
            String jpql = "SELECT new com.busstation.dtos.TransportationCompanyDTO(" +
                    "c.id, c.name, c.avatar, c.phone, c.email, c.isVerified, c.isActive, c.isCargoTransport, m.id) " +
                    "FROM TransportationCompany c " +
                    "LEFT JOIN c.manager m " +
                    "WHERE c.id = :companyId";
            TypedQuery<TransportationCompanyDTO> query = session.createQuery(jpql, TransportationCompanyDTO.class);
            query.setParameter("companyId", companyId);
            return query.getSingleResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public TransportationCompany findByManagerId(Long managerId) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        String hql = "FROM TransportationCompany WHERE manager.id = :managerId";
        return session.createQuery(hql, TransportationCompany.class)
                .setParameter("managerId", managerId)
                .uniqueResult();
    }

    @Override
    public long countAllCompanies() {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = builder.createQuery(Long.class);
        Root<TransportationCompany> root = criteriaQuery.from(TransportationCompany.class);

        criteriaQuery.select(builder.count(root));

        return session.createQuery(criteriaQuery).getSingleResult();
    }
}