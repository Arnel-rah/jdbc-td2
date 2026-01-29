

public class Main {

    public static void main(String[] args) {
    DataRetriever data = new DataRetriever();

    try {
        Order order = data.findOrderByReference("ORD102");
        if (order == null) {
            System.out.println("non trouve");
            return;
        }

        System.out.println("Avant : " + order.getTypeOrder());

        order.setTypeOrder(TypeOrder.EAT_IN);
        Order updated = data.updateOrder(order);

        System.out.println("Après modification : " + updated.getTypeOrder());

    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}
}
