CREATE TABLE preferences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    min_age SMALLINT NOT NULL DEFAULT 18 CHECK (min_age >= 18),
    max_age SMALLINT NOT NULL DEFAULT 99 CHECK (max_age >= min_age),
    max_distance_km INTEGER NOT NULL DEFAULT 50 CHECK (max_distance_km > 0),
    preferred_genders JSONB NOT NULL DEFAULT '[]'::jsonb,
    relationship_type VARCHAR(50),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);