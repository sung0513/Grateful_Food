package com.example.demo.repository;


import com.example.demo.domain.Member;
import com.example.demo.domain.Order;
import com.example.demo.domain.OrderSearch;
import com.example.demo.domain.Store;
import com.example.demo.web.Request.OrderSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;


import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {


    private final EntityManager em;

    @Transactional
    public Order save(Order order) {
        em.persist(order);
        return order;
    }

    @Transactional
    public List<Order> findAll() {
        return em.createQuery("select o from Order o", Order.class)
                .getResultList();
    }

//    @Transactional
//    public void delete(Order order){
//        Assert.notNull(order, "Entity must not be null!");
//        if(entityInformation.isNew(order)){
//            return;
//        }
//    }
//
//    @Transactional
//    public void deleteAll(){
//        for(Order element : findAll()){
//            delete(element);
//        }
//    }




    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }


    public List<Order> findOrderSearch(OrderSearch orderSearch) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Order> cq = cb.createQuery(Order.class);
        Root<Order> o = cq.from(Order.class);

        List<Predicate> criteria = new ArrayList<Predicate>();

        //?
        if (orderSearch.getDeliveryStatus() != null) {
            Predicate status =
                    cb.equal(o.get("status"), orderSearch.getDeliveryStatus());
            criteria.add(status);
        }

        if (StringUtils.hasText(orderSearch.getMemberName())) {

            Join<Order, Member> m = o.join("member", JoinType.INNER);
            Predicate name =
                    cb.like(m.<String>get("name"), "%" +
                            orderSearch.getMemberName()
                            + "%");
        }

        cq.where(cb.and(criteria.toArray(new Predicate[criteria.size()])));
        TypedQuery<Order> query =
                em.createQuery(cq).setMaxResults(100);
        return query.getResultList();

    }


//        public  List<Order> findAll(OrderSearch orderSearch)
//        {
//            String jpql = "select o from ";
//            em.createQuery("select  o from Order o join o.member m " +
//                    "where o.status = :status " +
//                    "and m.name like :name", Order.class)
//                    .setParameter("status", orderSearch.getDeliveryStatus())
//                    .setParameter("name", orderSearch.getMemberName())
//                    .getResultList();
//
//
//        }
}
