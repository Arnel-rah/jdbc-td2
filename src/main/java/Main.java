import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        DataRetriever dr = new DataRetriever();

        Order commande = new Order();
        commande.setCreationDatetime(Instant.now());

        Dish plat = new Dish();
        plat.setId(1);
        plat.setDishIngredients(new ArrayList<>()); 

        DishOrder ligne = new DishOrder();
        ligne.setDish(plat);
        ligne.setQuantity(1);

        List<DishOrder> lignes = new ArrayList<>();
        lignes.add(ligne);
        commande.setDishOrderList(lignes);
        commande.setTypeOrder(TypeOrder.EAT_IN);
        commande.setOrderStatut(StatutEnum.CREATED);

        try {
            Order resultat = dr.saveOrder(commande);
            System.out.println("Nety ianyyyyyy : " + resultat.getReference());
        } catch (Exception e) {
            throw new RuntimeException("error sequence" + e);
        }

    }
}