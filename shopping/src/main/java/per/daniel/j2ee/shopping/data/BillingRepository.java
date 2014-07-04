package per.daniel.j2ee.shopping.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import per.daniel.j2ee.shopping.model.Billing;
import per.daniel.j2ee.shopping.model.Goods;
import per.daniel.j2ee.shopping.model.Member;

@ApplicationScoped
public class BillingRepository {

    @Inject
    private EntityManager em;
    
    @Inject
    private Event<Billing> billingEventSrc;

    public Billing findById(Long id) {
        return em.find(Billing.class, id);
    }

    @SuppressWarnings("unchecked")
    public List<Billing> findAllOrderedById() {
        Session session = (Session) em.getDelegate();
        Criteria cb = session.createCriteria(Billing.class);
        cb.addOrder(Order.asc("id"));
        return (List<Billing>) cb.list();
    }
    
    public void updateBilling(Billing billing) {
    	Session session = (Session) em.getDelegate();
        session.update(billing);
        billingEventSrc.fire(billing);
    }
    
    public void commitBilling(Billing billing) {

        Session session = (Session) em.getDelegate();
        session.persist(billing);
        billingEventSrc.fire(billing);
    }
}
