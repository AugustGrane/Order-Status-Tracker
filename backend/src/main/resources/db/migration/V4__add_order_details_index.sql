-- Add index for order details to optimize initial query
CREATE INDEX IF NOT EXISTS idx_order_details_order_id ON order_details(order_id);
