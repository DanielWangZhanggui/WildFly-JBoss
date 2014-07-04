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

    private List<Billing> billing;

    @Produces
    @Named
    public List<Billing> getBilling() {
        return billing;
    }

    public void onGoodsListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final BillingRepository billingRepository) {
        retrieveAllBillingOrderedById();
    }

    @PostConstruct
    public void retrieveAllBillingOrderedById() {
    	billing = billingRepository.findAllOrderedById();
    }
}
