import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class DataRetriever {

    public Dish findDishById(Integer id) {
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection()) {
            String sql = "SELECT id, name, dish_type, selling_price FROM dish WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Dish dish = new Dish();
                        dish.setId(rs.getInt("id"));
                        dish.setName(rs.getString("name"));
                        dish.setDishType(DishTypeEnum.valueOf(rs.getString("dish_type")));
                        dish.setSellingPrice(rs.getObject("selling_price") == null
                                ? null : rs.getDouble("selling_price"));

                        dish.setIngredients(findIngredientsByDishId(id));
                        return dish;
                    }
                }
            }
            throw new RuntimeException("Dish not found with ID: " + id);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching dish", e);
        }
    }

    public List<Ingredient> findIngredientsByDishId(Integer idDish) {
        List<Ingredient> ingredients = new ArrayList<>();
        String sql = """
            SELECT i.id, i.name, i.price, i.category, di.quantity_required
            FROM ingredient i
            JOIN DishIngredient di ON i.id = di.id_ingredient
            WHERE di.id_dish = ?
        """;
        
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, idDish);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ing = new Ingredient();
                    ing.setId(rs.getInt("id"));
                    ing.setName(rs.getString("name"));
                    ing.setPrice(rs.getDouble("price"));
                    ing.setCategory(CategoryEnum.valueOf(rs.getString("category")));
                    ing.setQuantity(rs.getDouble("quantity_required"));
                    ingredients.add(ing);
                }
            }
            return ingredients;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching ingredients for dish " + idDish, e);
        }
    }

    public Dish saveDish(Dish toSave) {
        String upsertDishSql = """
                INSERT INTO dish (id, selling_price, name, dish_type)
                VALUES (?, ?, ?, ?::dish_type)
                ON CONFLICT (id) DO UPDATE
                SET name = EXCLUDED.name,
                    selling_price = EXCLUDED.selling_price,
                    dish_type = EXCLUDED.dish_type
                RETURNING id
                """;

        DBConnection dbConnection = new DBConnection();
        try (Connection conn = dbConnection.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int dishId;
                try (PreparedStatement ps = conn.prepareStatement(upsertDishSql)) {
                    int id = (toSave.getId() != null) ? toSave.getId() : getNextSerialValue(conn, "dish", "id");
                    ps.setInt(1, id);
                    if (toSave.getSellingPrice() != null) ps.setDouble(2, toSave.getSellingPrice());
                    else ps.setNull(2, Types.DOUBLE);
                    ps.setString(3, toSave.getName());
                    ps.setString(4, toSave.getDishType().name());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        dishId = rs.getInt(1);
                    }
                }

                updateDishIngredientsInTransaction(conn, dishId, toSave.getIngredients());
                conn.commit();
                return findDishById(dishId);
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Transaction failed. Changes rolled back.", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Connection error during save operation", e);
        }
    }

    private void updateDishIngredientsInTransaction(Connection conn, Integer dishId, List<Ingredient> ingredients) {
        try {
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM DishIngredient WHERE id_dish = ?")) {
                del.setInt(1, dishId);
                del.executeUpdate();
            }

            if (ingredients != null && !ingredients.isEmpty()) {
                String ins = "INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";
                try (PreparedStatement ps = conn.prepareStatement(ins)) {
                    for (Ingredient ing : ingredients) {
                        ps.setInt(1, dishId);
                        ps.setInt(2, ing.getId());
                        ps.setDouble(3, ing.getQuantity());
                        ps.setString(4, "KG");
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update junction table DishIngredient", e);
        }
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) {
        try {
            String seqSql = "SELECT pg_get_serial_sequence('" + tableName + "', '" + columnName + "')";
            String sequenceName;
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(seqSql)) {
                rs.next();
                sequenceName = rs.getString(1);
            }
            
            String syncSql = String.format("SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))", 
                             sequenceName, columnName, tableName);
            try (Statement st = conn.createStatement()) { st.executeQuery(syncSql); }

            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery("SELECT nextval('" + sequenceName + "')")) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating next ID for " + tableName, e);
        }
    }
}