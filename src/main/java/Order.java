import java.time.Instant;
import java.util.List;

public class Order {
    
    private int id;
    private String refence;
    private Instant creationDatetime;
    private List<DishOrder> dishOrders;


    public Order(){

    }
    
    @Override
    public String toString() {
        return "Order [id=" + id + ", refence=" + refence + ", creationDatetime=" + creationDatetime + ", dishOrders="
                + dishOrders + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((refence == null) ? 0 : refence.hashCode());
        result = prime * result + ((creationDatetime == null) ? 0 : creationDatetime.hashCode());
        result = prime * result + ((dishOrders == null) ? 0 : dishOrders.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Order other = (Order) obj;
        if (id != other.id)
            return false;
        if (refence == null) {
            if (other.refence != null)
                return false;
        } else if (!refence.equals(other.refence))
            return false;
        if (creationDatetime == null) {
            if (other.creationDatetime != null)
                return false;
        } else if (!creationDatetime.equals(other.creationDatetime))
            return false;
        if (dishOrders == null) {
            if (other.dishOrders != null)
                return false;
        } else if (!dishOrders.equals(other.dishOrders))
            return false;
        return true;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRefence() {
        return refence;
    }

    public void setRefence(String refence) {
        this.refence = refence;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public List<DishOrder> getDishOrders() {
        return dishOrders;
    }

    public void setDishOrders(List<DishOrder> dishOrders) {
        this.dishOrders = dishOrders;
    }
}
