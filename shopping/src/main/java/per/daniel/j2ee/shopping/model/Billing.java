package per.daniel.j2ee.shopping.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;


@Entity
@XmlRootElement
@Table(name = "Billing", uniqueConstraints = @UniqueConstraint(columnNames = "billing_id"))
public class Billing implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "billing_id")
    @GeneratedValue( strategy = GenerationType.IDENTITY ) 
    private long id;

    @Size(min = 1, max = 25)
    private String name;

    @Min(value=0)
    private float price;
    
    @ManyToMany(fetch=FetchType.LAZY, targetEntity=Goods.class, cascade=CascadeType.ALL)
    @JoinTable(name = "Billing_Goods", joinColumns = { 
			@JoinColumn(name = "billing_id")}, 
			inverseJoinColumns = { @JoinColumn(name = "goods_id" )})
    private Set<Goods> goodss;
    
    private int status;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public Set<Goods> getGoodss() {
		return goodss;
	}

	public void setGoodss(Set<Goods> goodss) {
		this.goodss = goodss;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}
	
	public String toString()
	{
		return "Billing ID:\t" + id + " Goodss\t " + goodss;
	}
    
}
