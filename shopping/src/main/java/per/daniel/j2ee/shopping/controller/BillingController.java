package per.daniel.j2ee.shopping.controller;

import java.util.Hashtable;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Model;
import javax.enterprise.inject.Produces;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import per.daniel.j2ee.shopping.model.Goods;
import per.daniel.j2ee.shopping.service.ShoppingCart;

@Model
@ApplicationScoped
public class BillingController {
	 private final static Logger LOGGER = Logger.getLogger(BillingController.class.toString());
	 
    @Inject
    private FacesContext facesContext;
    
    private boolean isNewBilling = true;
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
    
    private ShoppingCart shoppingCart;
    
    public void addToShoppingCart(Goods goods) throws NamingException {
    	if(isNewBilling == true)
    	{
    		Hashtable<String, String> jndiProperties = new Hashtable<String, String>();
			jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			final Context context = new InitialContext(jndiProperties);
			shoppingCart = (ShoppingCart) context.lookup("java:global/shopping/ShoppingCartBean");
			isNewBilling = false;
    	}
    	shoppingCart.addToShoppingCart(goods.getName(), quantity);
    }

    public void commitBilling()
    {
    	shoppingCart.commit();
    	isNewBilling = true;
    }

}

