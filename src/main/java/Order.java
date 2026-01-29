
import java.time.Instant;
import java.util.List;

public class Order {

    private Integer id;
    private String reference;
    private Instant creationDatetime;
    private List<DishOrder> dishOrderList;
    private TypeOrder typeOrder;
    private StatutEnum orderStatut;

    Double getTotalAmountWithoutVat() {
        throw new RuntimeException("Not implemented");
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public List<DishOrder> getDishOrderList() {
        return dishOrderList;
    }

    public void setDishOrderList(List<DishOrder> dishOrderList) {
        this.dishOrderList = dishOrderList;
    }

    public TypeOrder getTypeOrder() {
        return typeOrder;
    }

    public void setTypeOrder(TypeOrder typeOrder) {
        this.typeOrder = typeOrder;
    }

    public StatutEnum getOrderStatut() {
        return orderStatut;
    }

    public void setOrderStatut(StatutEnum orderStatut) {
        this.orderStatut = orderStatut;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((reference == null) ? 0 : reference.hashCode());
        result = prime * result + ((creationDatetime == null) ? 0 : creationDatetime.hashCode());
        result = prime * result + ((dishOrderList == null) ? 0 : dishOrderList.hashCode());
        result = prime * result + ((typeOrder == null) ? 0 : typeOrder.hashCode());
        result = prime * result + ((orderStatut == null) ? 0 : orderStatut.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Order other = (Order) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        if (reference == null) {
            if (other.reference != null) {
                return false;
            }
        } else if (!reference.equals(other.reference)) {
            return false;
        }
        if (creationDatetime == null) {
            if (other.creationDatetime != null) {
                return false;
            }
        } else if (!creationDatetime.equals(other.creationDatetime)) {
            return false;
        }
        if (dishOrderList == null) {
            if (other.dishOrderList != null) {
                return false;
            }
        } else if (!dishOrderList.equals(other.dishOrderList)) {
            return false;
        }
        if (typeOrder != other.typeOrder) {
            return false;
        }
        if (orderStatut != other.orderStatut) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", reference=" + reference + ", creationDatetime=" + creationDatetime
                + ", dishOrderList=" + dishOrderList + ", typeOrder=" + typeOrder + ", orderStatut=" + orderStatut
                + "]";
    }

}

/* 
hashcode, equals, toString, getter and setter methods

 */
