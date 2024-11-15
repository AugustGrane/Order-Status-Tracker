-- First add the column as nullable
ALTER TABLE product_type_steps ADD COLUMN step_order INTEGER;

-- Update existing rows with sequential numbers based on insertion order
WITH indexed_steps AS (
    SELECT product_type_id, step_id, ROW_NUMBER() OVER (PARTITION BY product_type_id ORDER BY step_id) - 1 as new_order
    FROM product_type_steps
)
UPDATE product_type_steps ps
SET step_order = i.new_order
FROM indexed_steps i
WHERE ps.product_type_id = i.product_type_id AND ps.step_id = i.step_id;

-- Now make the column not null
ALTER TABLE product_type_steps ALTER COLUMN step_order SET NOT NULL;
