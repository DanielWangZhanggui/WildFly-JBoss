package per.daniel.j2ee.shopping.service;

import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.inject.Inject;

import per.daniel.j2ee.shopping.controller.BillingController;
import per.daniel.j2ee.shopping.data.BillingRepository;
import per.daniel.j2ee.shopping.data.GoodsRepository;
import per.daniel.j2ee.shopping.model.Billing;
import per.daniel.j2ee.shopping.model.Goods;

@Stateful
public class ShoppingCartBean implements ShoppingCart{
	
	private ConcurrentHashMap<String, Integer> cart = new ConcurrentHashMap<String, Integer>();
	@Inject
	private GoodsRepository goodsRepository;
	
	@Inject
	private BillingHandling billingHandling;
	
	@Inject
	private BillShipping billShipping;
	
	private final static Logger LOGGER = Logger.getLogger(ShoppingCartBean.class.toString());

	public void addToShoppingCart(String product, int quantity) {
		if (cart.containsKey(product)) {
            int currentQuantity = cart.get(product);
            currentQuantity += quantity;
            cart.put(product, currentQuantity);
        } else {
            cart.put(product, quantity);
        }
		LOGGER.info("ShoppingCart: \t" + cart);
	}

	public ConcurrentHashMap<String, Integer> getCartContents() {
		return cart;
	}
	
	@Remove
	public void commit()
	{
		float totalPrice = 0;
		
		Iterator<Map.Entry<String, Integer>> itr = cart.entrySet().iterator();
		Billing billing = new Billing();
		billing.setGoodss(new HashSet<Goods>());
		
		while(itr.hasNext())
		{
			Map.Entry<String, Integer> goodsEntry = itr.next(); 
			Goods goods = goodsRepository.findByName(goodsEntry.getKey());
			billing.getGoodss().add(goods);
			totalPrice = goods.getPrice() * goodsEntry.getValue() + totalPrice;
		}
		
		billing.setPrice(totalPrice);		
		billingHandling.commitBilling(billing);
		LOGGER.info("Commit Billing" + billing.getId());
		
		try {
			billShipping.shippingBill(billing);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
