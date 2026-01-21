
public class DishIngredient {

    private Ingredient ingredient;
    private Double quantityRequired;
    private String unit;

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public Double getQuantityRequired() {
        return quantityRequired;
    }

    public void setQuantityRequired(Double quantityRequired) {
        this.quantityRequired = quantityRequired;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((ingredient == null) ? 0 : ingredient.hashCode());
        result = prime * result + ((quantityRequired == null) ? 0 : quantityRequired.hashCode());
        result = prime * result + ((unit == null) ? 0 : unit.hashCode());
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
        DishIngredient other = (DishIngredient) obj;
        if (ingredient == null) {
            if (other.ingredient != null) {
                return false;
            }
        } else if (!ingredient.equals(other.ingredient)) {
            return false;
        }
        if (quantityRequired == null) {
            if (other.quantityRequired != null) {
                return false;
            }
        } else if (!quantityRequired.equals(other.quantityRequired)) {
            return false;
        }
        if (unit == null) {
            if (other.unit != null) {
                return false;
            }
        } else if (!unit.equals(other.unit)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "DishIngredient [ingredient=" + ingredient + ", quantityRequired=" + quantityRequired + ", unit=" + unit
         + "]";
    }

}
