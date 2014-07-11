package per.daniel.j2ee.shopping.data;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;

import per.daniel.j2ee.shopping.model.Goods;

@ApplicationScoped
public class GoodsRepository {

    @Inject
    private EntityManager em;

    public Goods findById(Long id) {
        return em.find(Goods.class, id);
    }

    public Goods findByName(String name)
    {
    	Query query = em.createQuery("select g from Goods g where g.name like ?1");
    	query.setParameter(1, name);
    	return (Goods) query.getResultList().get(0);
    }
    
    @SuppressWarnings("unchecked")
    public List<Goods> findAllOrderedById() {
        Session session = (Session) em.getDelegate();
        Criteria cb = session.createCriteria(Goods.class);
        cb.addOrder(Order.asc("id"));
        return (List<Goods>) cb.list();
    }
   
    public void updateGoods(Goods goods) {
    	Session session = (Session) em.getDelegate();
        session.update(goods);
    }
    
    public void addGoods(Goods goods) {
    	Session session = (Session) em.getDelegate();
        session.save(goods);
    }
    
}
