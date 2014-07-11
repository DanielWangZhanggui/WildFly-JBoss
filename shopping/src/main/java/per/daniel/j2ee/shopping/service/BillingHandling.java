package per.daniel.j2ee.shopping.service;

import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.hibernate.Session;

import per.daniel.j2ee.shopping.model.Billing;

@Stateless
public class BillingHandling {
	@Inject
	private EntityManager em;
	
	@Inject
	private Event<Billing> billingEventSrc;
	
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
