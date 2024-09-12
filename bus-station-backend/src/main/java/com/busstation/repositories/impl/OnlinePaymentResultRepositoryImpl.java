package com.busstation.repositories.impl;


import com.busstation.pojo.OnlinePaymentResult;
import com.busstation.repositories.OnlinePaymentResultRepository;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Repository
@Transactional
public class OnlinePaymentResultRepositoryImpl implements OnlinePaymentResultRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactoryBean;

    @Override
    public OnlinePaymentResult getById(Long id) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        OnlinePaymentResult paymentResult = session.get(OnlinePaymentResult.class, id);
        if (paymentResult == null)  throw  new IllegalArgumentException("Invalid online payment result id");
        return paymentResult;
    }

    @Override
    public OnlinePaymentResult getByPaymentCode(String paymentCode) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<OnlinePaymentResult> criteriaQuery = builder.createQuery(OnlinePaymentResult.class);
        Root<OnlinePaymentResult> root = criteriaQuery.from(OnlinePaymentResult.class);
        criteriaQuery.select(root);
        criteriaQuery.where(builder.equal(root.get("paymentCode"), paymentCode));
        return  session.createQuery(criteriaQuery).getResultList().stream().findFirst().orElseThrow();
    }

    @Override
    public void update(OnlinePaymentResult paymentResult) {
        Session session = sessionFactoryBean.getObject().getCurrentSession();
        session.update(paymentResult);
    }
}
