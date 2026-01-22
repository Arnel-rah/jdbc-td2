
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
                        dish.setDishIngredients(findDishIngredientsByDishId(id));
                        return dish;
                    }
                }
            }
            throw new RuntimeException("Dish not found with ID: " + id);
        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching dish", e);
        }
    }

    public List<DishIngredient> findDishIngredientsByDishId(Integer idDish) {
        List<DishIngredient> results = new ArrayList<>();
        String sql = """
            SELECT i.id, i.name, i.price, i.category, di.quantity_required, di.unit
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

                    DishIngredient di = new DishIngredient();
                    di.setIngredient(ing);
                    di.setQuantityRequired(rs.getDouble("quantity_required"));
                    di.setUnit(rs.getString("unit"));

                    results.add(di);
                }
            }
            return results;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching dish ingredients for dish " + idDish, e);
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
                    if (toSave.getSellingPrice() != null) {
                        ps.setDouble(2, toSave.getSellingPrice()); 
                    }else {
                        ps.setNull(2, Types.DOUBLE);
                    }
                    ps.setString(3, toSave.getName());
                    ps.setString(4, toSave.getDishType().name());

                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        dishId = rs.getInt(1);
                    }
                }

                updateDishIngredientsInTransaction(conn, dishId, toSave.getDishIngredients());
                conn.commit();
                return findDishById(dishId);
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("transaction failed ", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("connection error ", e);
        }
    }

    private void updateDishIngredientsInTransaction(Connection conn, Integer dishId, List<DishIngredient> diList) {
        try {
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM DishIngredient WHERE id_dish = ?")) {
                del.setInt(1, dishId);
                del.executeUpdate();
            }

            if (diList != null && !diList.isEmpty()) {
                String ins = "INSERT INTO DishIngredient (id_dish, id_ingredient, quantity_required, unit) VALUES (?, ?, ?, ?::unit_type)";
                try (PreparedStatement ps = conn.prepareStatement(ins)) {
                    for (DishIngredient di : diList) {
                        ps.setInt(1, dishId);
                        ps.setInt(2, di.getIngredient().getId());
                        ps.setDouble(3, di.getQuantityRequired());
                        ps.setString(4, di.getUnit() != null ? di.getUnit() : "KG");
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("failed to update table", e);
        }
    }

    private int getNextSerialValue(Connection conn, String tableName, String columnName) {
        try {
            String seqSql = "SELECT pg_get_serial_sequence('" + tableName + "', '" + columnName + "')";
            String sequenceName;
            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery(seqSql)) {
                if (rs.next()) {
                    sequenceName = rs.getString(1);
                } else {
                    throw new RuntimeException("No sequence found for " + tableName);
                }
            }

            String syncSql = String.format("SELECT setval('%s', (SELECT COALESCE(MAX(%s), 0) FROM %s))",
                    sequenceName, columnName, tableName);
            try (Statement st = conn.createStatement()) {
                st.executeQuery(syncSql);
            }

            try (Statement st = conn.createStatement(); ResultSet rs = st.executeQuery("SELECT nextval('" + sequenceName + "')")) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error generating next ID for " + tableName, e);
        }
    }
}
