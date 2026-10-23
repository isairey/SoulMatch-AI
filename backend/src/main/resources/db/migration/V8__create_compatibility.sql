CREATE TABLE compatibility_scores (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_one_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    user_two_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    score NUMERIC(5,2) NOT NULL CHECK (score >= 0 AND score <= 100),
    factors JSONB NOT NULL DEFAULT '{}'::jsonb,
    model_version VARCHAR(50),
    calculated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CHECK (user_one_id < user_two_id),
    UNIQUE (user_one_id, user_two_id)
);

CREATE INDEX idx_compatibility_score
    ON compatibility_scores(user_one_id, score DESC);