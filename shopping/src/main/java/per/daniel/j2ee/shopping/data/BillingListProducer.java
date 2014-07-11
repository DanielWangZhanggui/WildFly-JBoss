package per.daniel.j2ee.shopping.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import per.daniel.j2ee.shopping.model.Billing;
import per.daniel.j2ee.shopping.model.Goods;

@RequestScoped
public class BillingListProducer {
    @Inject
    private BillingRepository billingRepository;

    private List<Billing> billings;

    @Produces
    @Named
    public List<Billing> getBillings() {
        return billings;
    }

    public void onBillingListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Billing billing) {
    	System.out.println("============");
        retrieveAllBillingOrderedById();
    }

    @PostConstruct
    public void retrieveAllBillingOrderedById() {
    	billings = billingRepository.findAllOrderedById();
    }
}
