package per.daniel.j2ee.shopping.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@XmlRootElement
@Table(name = "Goods", uniqueConstraints = @UniqueConstraint(columnNames = "goods_id"))
public class Goods implements Serializable {
    /** Default value included to remove warning. Remove or modify at will. **/
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "goods_id")
    private long id;

    @NotNull
    @Size(min = 1, max = 25)
    private String name;

    @NotNull
    @Min(value=0)
    private float price;
    
    @ManyToMany(fetch = FetchType.LAZY, mappedBy = "goodss")
    private Set<Billing> billings = new HashSet<Billing>(0);
    
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
	
	
	public Set<Billing> getBillings() {
		return billings;
	}

	public void setBillings(Set<Billing> billings) {
		this.billings = billings;
	}

	public String toString()
	{
		return "Goods ID: " + id + "\t Name:" + name + "\t Price: " + price;
	}
}
