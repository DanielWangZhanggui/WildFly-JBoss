package per.daniel.j2ee.shopping.service;

import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Remote;

@Remote
public interface ShoppingCart {

	public void addToShoppingCart(String goods, int quantity);

    public void commit();

    public ConcurrentHashMap<String, Integer> getCartContents();
}
