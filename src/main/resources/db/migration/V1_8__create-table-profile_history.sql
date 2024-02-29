CREATE TABLE person.profile_history (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created TIMESTAMP NOT NULL,
    user_id UUID REFERENCES person.users(id),
    profile_type VARCHAR(32),
    reason VARCHAR(255),
    comment VARCHAR(255),
    changed_values VARCHAR(1024)
);

