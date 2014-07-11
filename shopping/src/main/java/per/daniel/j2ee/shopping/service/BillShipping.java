package per.daniel.j2ee.shopping.service;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;

import per.daniel.j2ee.shopping.data.BillingRepository;
import per.daniel.j2ee.shopping.model.Billing;

@Stateless
public class BillShipping {

    @Inject
    private Logger log;

    @Inject
    private BillingHandling billingHandling;
    
    @Resource(mappedName = "java:/ConnectionFactory")
    private ConnectionFactory connectionFactory;

    @Resource(mappedName = "java:/queue/Shipping")
    private Queue queue;

    public void shippingBill(Billing billing) throws Exception {
        log.info("Shipping... " + billing.getId());
        billing.setStatus(1);
        billingHandling.updateBilling(billing);
        
        sendShippingMSG(billing);
    }
    
    private void sendShippingMSG(Billing billing)
    {
    	 Connection connection = null;
         try {
             Destination destination = queue;
             
             connection = connectionFactory.createConnection();
             javax.jms.Session session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
             MessageProducer messageProducer = session.createProducer(destination);
             connection.start();
             ObjectMessage billingMsg = session.createObjectMessage();
             billingMsg.setObject(billing);
             
             messageProducer.send(billingMsg);
         } catch (JMSException e) {
             e.printStackTrace();
         } finally {
             if (connection != null) {
                 try {
                     connection.close();
                 } catch (JMSException e) {
                     e.printStackTrace();
                 }
             }
         }
    }
}
