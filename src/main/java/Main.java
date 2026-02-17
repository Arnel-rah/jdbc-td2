import java.time.Instant;

public class Main {

 public static void main(String[] args) {
    DataRetriever retriever = new DataRetriever();

    Instant t = Instant.parse("2024-02-01T00:00:00Z");
    Integer ingredientId = 1;

    StockValue result = retriever.getStockValue(t, ingredientId);

    System.out.println("quantity actuel: " + result.getQuantity());
}

}
