
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
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
                        dish.setPrice(rs.getObject("selling_price") == null
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

    public List<DishIngredient> findDishIngredientsByDishId(Integer dishId) {
        if (dishId == null) {
            return Collections.emptyList();
        }

        List<DishIngredient> results = new ArrayList<>();

        String sql = """
        SELECT 
            i.id, i.name, i.price, i.category,
            di.quantity_required, di.unit
        FROM ingredient i
        JOIN DishIngredient di ON i.id = di.id_ingredient
        WHERE di.id_dish = ?
        """;

        try (Connection conn = new DBConnection().getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, dishId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ingredient ingredient = new Ingredient();
                    ingredient.setId(rs.getInt("id"));
                    ingredient.setName(rs.getString("name"));
                    ingredient.setPrice(rs.getDouble("price"));
                    String categoryStr = rs.getString("category");
                    CategoryEnum category = (categoryStr != null)
                            ? CategoryEnum.valueOf(categoryStr)
                            : null;
                    ingredient.setCategory(category);

                    DishIngredient dishIngredient = new DishIngredient();
                    dishIngredient.setIngredient(ingredient);
                    dishIngredient.setQuantity(rs.getDouble("quantity_required"));
                    String unitStr = rs.getString("unit");
                    Unit unit = (unitStr != null)
                            ? Unit.valueOf(unitStr.trim().toUpperCase())
                            : null;
                    dishIngredient.setUnit(unit);

                    results.add(dishIngredient);
                }
            }

            return results;

        } catch (SQLException e) {
            throw new RuntimeException("Error fetching ingredients for dish id " + dishId, e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid enum value in database for dish id " + dishId, e);
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
                    if (toSave.getPrice() != null) {
                        ps.setDouble(2, toSave.getPrice());
                    } else {
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
        if (diList != null && !diList.isEmpty()) {
            String sql = """
                             INSERT INTO DishIngredient
                             (id_dish, id_ingredient, quantity_required, unit)
                             VALUES (?, ?, ?, ?::unit_type)
                             """;
            try (PreparedStatement ps = conn.prepareStatement(sql)) {

                for (DishIngredient di : diList) {
                    Ingredient ing = di.getIngredient();
                    if (ing == null || ing.getId() == null) {
                        continue;
                    }
                    if (di.getQuantity() == null) {
                        continue;
                    }
                    if (di.getUnit() == null) {
                        throw new IllegalArgumentException(
                                "Unit is required for DishIngredient of dish id " + dishId
                                + " - ingredient: " + ing.getName()
                        );
                    }

                    ps.setInt(1, dishId);
                    ps.setInt(2, ing.getId());
                    ps.setDouble(3, di.getQuantity());
                    ps.setString(4, di.getUnit().name());

                    ps.addBatch();
                }

                ps.executeBatch();

            } catch (SQLException e) {
                throw new RuntimeException("Failed to insert DishIngredient for dish id " + dishId, e);
            }
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

// Annexe  ==>
    Order findOrderByReference(String reference) {
        DBConnection dbConnection = new DBConnection();
        try (Connection connection = dbConnection.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("""
                    select id, reference, creation_datetime, type_command, status from "order" where reference like ?""");
            preparedStatement.setString(1, reference);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                Order order = new Order();
                Integer idOrder = resultSet.getInt("id");
                order.setId(idOrder);
                order.setReference(resultSet.getString("reference"));
                order.setCreationDatetime(resultSet.getTimestamp("creation_datetime").toInstant());
                order.setTypeOrder(TypeOrder.valueOf(resultSet.getString("type_command")));
                order.setOrderStatut(StatutEnum.valueOf(resultSet.getString("status")));
                order.setDishOrderList(findDishOrderByIdOrder(idOrder));
                return order;
            }
            throw new RuntimeException("Order not found with reference " + reference);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private List<DishOrder> findDishOrderByIdOrder(Integer idOrder) {
        DBConnection dbConnection = new DBConnection();
        Connection connection = dbConnection.getConnection();
        List<DishOrder> dishOrders = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    """
                            select id, id_dish, quantity from dish_order where dish_order.id_order = ?
                            """);
            preparedStatement.setInt(1, idOrder);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                Dish dish = findDishById(resultSet.getInt("id_dish"));
                DishOrder dishOrder = new DishOrder();
                dishOrder.setId(resultSet.getInt("id"));
                dishOrder.setQuantity(resultSet.getInt("quantity"));
                dishOrder.setDish(dish);
                dishOrders.add(dishOrder);
            }
            dbConnection.closeConnection(connection);
            return dishOrders;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Mienregistrer an le commande
    public Order saveOrder(Order order) {
        if (order == null || order.getDishOrderList() == null || order.getDishOrderList().isEmpty()) {
            throw new RuntimeException("An order must contain at least one dish");
        }

        verifyIngredientStock(order);

        DBConnection db = new DBConnection();

        try (Connection conn = db.getConnection()) {

            conn.setAutoCommit(false);

            String reference = generateNextOrderReference(conn);
            order.setReference(reference);

            int orderId = insertOrder(conn, order);
            order.setId(orderId);
            order.setTypeOrder(order.getTypeOrder());
            order.setOrderStatut(order.getOrderStatut());

            insertDishOrders(conn, orderId, order);
            insertStockOutMovements(conn, order);

            conn.commit();

            return findOrderByReference(reference);

        } catch (Exception e) {
            throw new RuntimeException("Tsy mandeha", e);
        }
    }

    // fampidirana commande
    private int insertOrder(Connection conn, Order order) {
        String sql = """
    INSERT INTO "order" (reference, creation_datetime, type_command, "status")
        VALUES (?, ?, ?::command_type, ?::command_status)
        RETURNING id
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, order.getReference());

            Instant creationTime = order.getCreationDatetime() != null
                    ? order.getCreationDatetime()
                    : Instant.now();
            ps.setTimestamp(2, Timestamp.from(creationTime));
            ps.setString(3, order.getTypeOrder().toString());
            ps.setString(4, order.getOrderStatut().toString());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("id");
                }
                throw new RuntimeException("failed to insert order");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to insert order", e);
        }
    }

    // fampidirana dish_order
    private void insertDishOrders(Connection conn, int orderId, Order order) {
        String sql = """
        INSERT INTO dish_order (id_order, id_dish, quantity)
        VALUES (?, ?, ?)
        """;

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (DishOrder d : order.getDishOrderList()) {
                Dish dish = d.getDish();
                if (dish == null) {
                    throw new IllegalArgumentException(
                            "cannot insert dish order"
                    );
                }
                if (dish.getId() == null) {
                    throw new IllegalArgumentException(
                            "cannot insert dish order"
                    );
                }

                ps.setInt(1, orderId);
                ps.setInt(2, dish.getId());
                ps.setInt(3, d.getQuantity());
                ps.addBatch();
            }

            ps.executeBatch();

        } catch (SQLException e) {
            throw new RuntimeException(
                    "failed to insert dish orders for order #" + orderId, e);
        }
    }

    //  stock mivoaka
    private void insertStockOutMovements(Connection conn, Order order) {
        String sql = """
        INSERT INTO stock_movement
        (id_ingredient, quantity, "type", unit, creation_datetime)
        VALUES (?, ?, 'OUT', ?::unit_type, CURRENT_TIMESTAMP)
        """;
        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            for (DishOrder dishOrder : order.getDishOrderList()) {
                Dish dish = dishOrder.getDish();
                if (dish == null) {
                    throw new IllegalArgumentException(
                            "cannot process stock movement"
                    );
                }
                if (dish.getDishIngredients() == null) {
                    throw new IllegalArgumentException(
                            "cannot process stock movement"
                    );
                }

                for (DishIngredient di : dish.getDishIngredients()) {
                    Ingredient ing = di.getIngredient();
                    if (ing == null) {
                        throw new IllegalArgumentException(
                                "cannot process stock movement"
                        );
                    }
                    if (ing.getId() == null) {
                        throw new IllegalArgumentException(
                                "cannot process stock movement"
                        );
                    }

                    double qtyOut = dishOrder.getQuantity() * di.getQuantity();
                    if (qtyOut <= 0) {
                        throw new IllegalArgumentException(
                                "invalid quantity for stock movement"
                                + "(order qty=" + dishOrder.getQuantity() + ", ingredient qty=" + di.getQuantity() + ")"
                        );
                    }

                    ps.setInt(1, ing.getId());
                    ps.setDouble(2, qtyOut);
                    ps.setString(3, di.getUnit() != null ? di.getUnit().name() : "KG");
                    ps.executeUpdate();
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("failed to insert", e);
        }
    }

    // fanamboarana reference commande
    private String generateNextOrderReference(Connection conn) {
        final String sql = "SELECT nextval('order_ref_seq')";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            if (!rs.next()) {
                throw new RuntimeException("sequence  did not return any value");
            }

            long nextValue = rs.getLong(1);
            return String.format("ORD%06d", nextValue);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "failed to generate reference", e);
        }
    }

    // fijerena stock ingredient alohan ny commande
    private void verifyIngredientStock(Order order) {
        for (DishOrder d : order.getDishOrderList()) {

            Dish dish = d.getDish();
            if (dish == null || dish.getDishIngredients() == null) {
                continue;
            }

            for (DishIngredient di : dish.getDishIngredients()) {

                Ingredient ing = di.getIngredient();
                if (ing == null) {
                    continue;
                }

                double required = d.getQuantity() * di.getQuantity();

                if (ing.getTotalStockQuantity() < required) {
                    throw new RuntimeException(
                            "insufficient stock for ingredient: " + ing.getName()
                            + " | required=" + required
                            + " | available=" + ing.getTotalStockQuantity()
                    );
                }
            }
        }
    }

    // Manova commande - VERSION CORRIGÉE
    public Order updateOrder(Order orderToSave) {
        if (orderToSave == null || orderToSave.getId() == null) {
            throw new IllegalArgumentException("Tsy mandeha: ID ou Order null");
        }

        String ref = orderToSave.getReference();
        if (ref == null || ref.trim().isEmpty()) {
            throw new IllegalArgumentException("Order reference must not be null or empty");
        }

        Order existing = findOrderByReference(ref);
        if (existing == null) {
            throw new RuntimeException("Order not found with reference: " + ref);
        }
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection()) {
            conn.setAutoCommit(false);

            String sql = """
            UPDATE "order"
               SET type_command = ?::command_type,
                   "status"     = ?::command_status
             WHERE reference = ?
            RETURNING id, reference, creation_datetime, type_command, "status"
        """;

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, orderToSave.getTypeOrder().name());
                ps.setString(2, orderToSave.getOrderStatut().name());
                ps.setString(3, ref);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        Order updated = new Order();
                        updated.setId(rs.getInt("id"));
                        updated.setReference(rs.getString("reference"));
                        updated.setCreationDatetime(rs.getTimestamp("creation_datetime").toInstant());
                        updated.setTypeOrder(TypeOrder.valueOf(rs.getString("type_command")));
                        updated.setOrderStatut(StatutEnum.valueOf(rs.getString("status")));

                        conn.commit();
                        return updated;
                    } else {
                        throw new RuntimeException("Order not found during update: " + ref);
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw new RuntimeException("Error update", e);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error database connection", e);
        }
    }

    // Exercice TD5 : Java & PostgreSQL (JDBC) Codons autrement !
    StockValue getStockValue(Instant t, Integer ingredientIdentifier) {
        if (ingredientIdentifier == null || t == null) {
            throw new IllegalArgumentException("ingredientIdentifier et t sont obligatoires");
        }

        DBConnection db = new DBConnection();

        try (Connection conn = db.getConnection()) {

            String sumQuery = """
            SELECT id_ingredient,
                   SUM(quantity * CASE WHEN "type" = 'OUT' THEN -1 ELSE 1 END) AS actual_quantity
            FROM stock_movement
            WHERE creation_datetime <= ?
              AND id_ingredient = ?
            GROUP BY id_ingredient
        """;

            try (PreparedStatement ps = conn.prepareStatement(sumQuery)) {

                ps.setTimestamp(1, Timestamp.from(t));
                ps.setInt(2, ingredientIdentifier);

                try (ResultSet rs = ps.executeQuery()) {

                    if (rs.next()) {
                        StockValue stockValue = new StockValue();
                        stockValue.setQuantity(rs.getDouble("actual_quantity"));
                        return stockValue;
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching stock value", e);
        }

        return new StockValue();
    }

// 2
    Double getDishCost(Integer dishId) {
        if (dishId == null) {
            throw new IllegalArgumentException("dishId est obligatoire");
        }

        DBConnection db = new DBConnection();

        try (Connection conn = db.getConnection()) {

            String query = """
            SELECT SUM(di.quantity_required * i.price) AS total_cost
            FROM DishIngredient di
            JOIN ingredient i ON di.id_ingredient = i.id
            WHERE di.id_dish = ?
        """;

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, dishId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("total_cost");
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur de BD", e);
        }

        return 0.0;
    }

// --
    Double getGrossMargin(Integer dishId) {
        if (dishId == null) {
            throw new IllegalArgumentException("dishId est obligatoire");
        }
        DBConnection db = new DBConnection();
        try (Connection conn = db.getConnection()) {

            String query = """
            SELECT 
                d.selling_price - SUM(di.quantity_required * i.price) AS gross_margin
            FROM dish d
            JOIN DishIngredient di ON di.id_dish = d.id
            JOIN ingredient i ON di.id_ingredient = i.id
            WHERE d.id = ?
            GROUP BY d.id, d.selling_price
        """;

            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setInt(1, dishId);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getDouble("gross_margin");
                    }
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Database error while fetching gross margin", e);
        }

        return 0.0;
    }

}
