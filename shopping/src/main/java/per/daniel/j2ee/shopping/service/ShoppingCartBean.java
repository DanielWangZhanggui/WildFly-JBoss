package per.daniel.j2ee.shopping.service;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.inject.Inject;

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
	private BillingRepository billRepository;
	
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
		Set<Goods> goodsSet = new LinkedHashSet<Goods>();
		float totalPrice = 0;
		
		Iterator<Map.Entry<String, Integer>> itr = cart.entrySet().iterator();
		Billing billing = new Billing();
		billing.setId(new Long(1));
		billing.setStatus(0);
		while(itr.hasNext())
		{
			Map.Entry<String, Integer> goodsEntry = itr.next(); 
			Goods goods = goodsRepository.findByName(goodsEntry.getKey());
			goodsSet.add(goods);
			totalPrice = goods.getPrice() * goodsEntry.getValue() + totalPrice;
			billing.setPrice(totalPrice);
			goods.setBilling(billing);
			goodsRepository.updateGoods(goods);
		}
		
//		try {
//			billShipping.shippingBill(billing);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}
}
