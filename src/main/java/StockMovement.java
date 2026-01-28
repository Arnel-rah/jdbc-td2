
import java.time.Instant;


public class StockMovement {
    private int id;
    private StockValue value;
    private MovementTypeEnum typeEnum;
    private Instant creationDatetime;
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public StockValue getValue() {
        return value;
    }
    public void setValue(StockValue value) {
        this.value = value;
    }
    public MovementTypeEnum getTypeEnum() {
        return typeEnum;
    }
    public void setTypeEnum(MovementTypeEnum typeEnum) {
        this.typeEnum = typeEnum;
    }
    public Instant getCreationDatetime() {
        return creationDatetime;
    }
    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }
    
    @Override
    public String toString() {
        return "StockMovement [id=" + id + ", value=" + value + ", typeEnum=" + typeEnum + ", creationDatetime="
                + creationDatetime + "]";
    }
    
    
    
}
