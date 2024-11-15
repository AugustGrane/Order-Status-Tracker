-- Add index for id lookups
CREATE INDEX IF NOT EXISTS idx_status_definitions_id ON status_definitions(id);

-- Add index for name searches since we have a findByNameContainingIgnoreCase query
CREATE INDEX IF NOT EXISTS idx_status_definitions_name ON status_definitions(name);
