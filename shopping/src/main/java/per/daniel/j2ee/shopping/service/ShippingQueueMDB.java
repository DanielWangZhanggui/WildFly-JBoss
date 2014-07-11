package per.daniel.j2ee.shopping.service;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import per.daniel.j2ee.shopping.data.BillingRepository;
import per.daniel.j2ee.shopping.model.Billing;

@MessageDriven(name = "HelloWorldQueueMDB", activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
        @ActivationConfigProperty(propertyName = "destination", propertyValue = "queue/Shipping"),
        @ActivationConfigProperty(propertyName = "acknowledgeMode", propertyValue = "Auto-acknowledge") })
public class ShippingQueueMDB implements MessageListener{

    private final static Logger LOGGER = Logger.getLogger(ShippingQueueMDB.class.toString());

    @Inject
    private BillingHandling billingHandling;
    /**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message rcvMessage) {
    	ObjectMessage msg = null;
        try {
            if (rcvMessage instanceof ObjectMessage) {
                msg = (ObjectMessage) rcvMessage;
                Billing billing = (Billing) msg.getObject();
                billing.setStatus(2);
                billingHandling.updateBilling(billing);
                LOGGER.info("Shipping Status ... " + billing.getId());
            } 
            else {
                LOGGER.warning("Message of wrong type: " + rcvMessage.getClass().getName());
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
