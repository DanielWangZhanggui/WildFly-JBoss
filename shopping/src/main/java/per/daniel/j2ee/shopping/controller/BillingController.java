package per.daniel.j2ee.shopping.controller;

import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;

import per.daniel.j2ee.shopping.model.Goods;
import per.daniel.j2ee.shopping.model.Member;
import per.daniel.j2ee.shopping.service.ShoppingCart;

@Model
public class BillingController {
	 private final static Logger LOGGER = Logger.getLogger(BillingController.class.toString());
	
    @Inject
    private FacesContext facesContext;
    
    private int quantity;

    @Produces
    @Named
    public int getQuantity() {
        return quantity;
    }
    
    @PostConstruct
    public void initQuantity() {
    	quantity = 1;
    }
    
    @EJB(lookup="java:global/shopping/ShoppingCartBean")
    private ShoppingCart shoppingCart;
    
    public void addToShoppingCart(Goods goods) {
    	shoppingCart.addToShoppingCart(goods.getName(), quantity);
    }

    public void commitBilling()
    {
    	shoppingCart.commit();
    }

}

