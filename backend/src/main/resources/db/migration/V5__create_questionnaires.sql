CREATE TABLE questionnaires (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(150) NOT NULL,
    description VARCHAR(500),
    version INTEGER NOT NULL DEFAULT 1,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE questionnaire_questions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    questionnaire_id UUID NOT NULL
        REFERENCES questionnaires(id) ON DELETE CASCADE,
    question_text VARCHAR(1000) NOT NULL,
    question_type VARCHAR(30) NOT NULL,
    options JSONB NOT NULL DEFAULT '[]'::jsonb,
    display_order INTEGER NOT NULL DEFAULT 0,
    required BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE questionnaire_answers (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    question_id UUID NOT NULL
        REFERENCES questionnaire_questions(id) ON DELETE CASCADE,
    answer JSONB NOT NULL,
    answered_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, question_id)
);

CREATE INDEX idx_questionnaire_questions
    ON questionnaire_questions(questionnaire_id, display_order);

CREATE INDEX idx_questionnaire_answers_user
    ON questionnaire_answers(user_id);