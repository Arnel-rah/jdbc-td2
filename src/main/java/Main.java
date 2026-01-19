public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        Dish d1 = dr.findDishById(1);
        System.out.println(d1.getName() + " | Cost: " + d1.getDishCost() + " | Margin: " + d1.getGrossMargin());

        Dish d2 = dr.findDishById(2);
        System.out.println(d2.getName() + " | Cost: " + d2.getDishCost() + " | Margin: " + d2.getGrossMargin());

        Dish d4 = dr.findDishById(4);
        System.out.println(d4.getName() + " | Cost: " + d4.getDishCost() + " | Margin: " + d4.getGrossMargin());
    }
}