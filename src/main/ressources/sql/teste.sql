
SELECT id_ingredient, SUM(quantity * CASE WHEN "type" = 'OUT' THEN -1 ELSE 1 END
    ) AS actual_quantity
FROM stock_movement
WHERE id_ingredient = 1 AND creation_datetime = '2024-01-05 08:00:00'
GROUP BY id_ingredient;

SELECT * FROM stock_movement;







