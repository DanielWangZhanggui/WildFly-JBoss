package per.daniel.j2ee.shopping.data;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import per.daniel.j2ee.shopping.model.Goods;

@RequestScoped
public class GoodsListProducer {
    @Inject
    private GoodsRepository goodsRepository;

    private List<Goods> goods;

    // @Named provides access the return value via the EL variable name "members" in the UI (e.g.,
    // Facelets or JSP view)
    @Produces
    @Named
    public List<Goods> getGoods() {
        return goods;
    }

    public void onGoodsListChanged(@Observes(notifyObserver = Reception.IF_EXISTS) final Goods goods) {
        retrieveAllGoodsOrderedById();
    }

    @PostConstruct
    public void retrieveAllGoodsOrderedById() {
        goods = goodsRepository.findAllOrderedById();
    }
}
