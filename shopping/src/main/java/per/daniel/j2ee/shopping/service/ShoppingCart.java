package per.daniel.j2ee.shopping.service;

import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.Local;

import per.daniel.j2ee.shopping.model.Goods;

@Local
public interface ShoppingCart {

	public void addToShoppingCart(String goods, int quantity);

    public void commit();

    public ConcurrentHashMap<String, Integer> getCartContents();
}
