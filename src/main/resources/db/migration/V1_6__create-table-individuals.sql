CREATE TABLE person.individuals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES person.users(id),
    created TIMESTAMP NOT NULL,
    updated TIMESTAMP NOT NULL,
    passport_number VARCHAR(32),
    phone_number VARCHAR(32),
    email VARCHAR(32),
    verified_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL,
    status VARCHAR(32)
);

