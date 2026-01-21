public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        Dish d1 = dr.findDishById(1);
        System.out.println(d1.getName() + " " + d1.getGrossMargin());

        Dish d2 = dr.findDishById(2);
        System.out.println(d2.getName() + " " + d2.getGrossMargin());

        Dish d3 = dr.findDishById(3);
        System.out.println(d3.getName() + " " + d3.getGrossMargin());
    }
}