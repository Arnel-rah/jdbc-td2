
import java.util.List;
import java.util.Objects;

public class Dish {

    private Integer id;
    private Double sellingPrice;
    private String name;
    private DishTypeEnum dishType;
    private List<Ingredient> ingredients;

    public Double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(Double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public Double getDishCost() {
        if (ingredients == null || ingredients.isEmpty()) {
            return 0.0;
        }
        return ingredients.stream()
                .mapToDouble(ing -> ing.getPrice() * ing.getQuantity())
                .sum();
    }

    public Dish() {
    }

    public Dish(Integer id, String name, DishTypeEnum dishType, List<Ingredient> ingredients, Double sellingPrice) {
        this.id = id;
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.sellingPrice = sellingPrice;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DishTypeEnum getDishType() {
        return dishType;
    }

    public void setDishType(DishTypeEnum dishType) {
        this.dishType = dishType;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        if (ingredients == null) {
            this.ingredients = null;
            return;
        }
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.get(i).setDish(this);
        }
        this.ingredients = ingredients;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Dish dish = (Dish) o;
        return Objects.equals(id, dish.id) && Objects.equals(name, dish.name) && dishType == dish.dishType && Objects.equals(ingredients, dish.ingredients);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dishType, ingredients);
    }

    @Override
    public String toString() {
        return "Dish{"
                + "id=" + id
                + ", price=" + sellingPrice
                + ", name='" + name + '\''
                + ", dishType=" + dishType
                + ", ingredients=" + ingredients
                + '}';
    }

    public Double getGrossMargin() {
        if (this.sellingPrice == null) {
            throw new RuntimeException("Exception (prix NULL)");
        }
        return this.sellingPrice - getDishCost();
    }
}
