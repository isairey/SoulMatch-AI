CREATE TABLE profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    display_name VARCHAR(80) NOT NULL,
    birth_date DATE,
    biography VARCHAR(500),
    gender VARCHAR(40),
    city VARCHAR(120),
    country VARCHAR(120) NOT NULL DEFAULT 'Mexico',
    occupation VARCHAR(120),
    education VARCHAR(120),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_profiles_city_country ON profiles(city, country);