CREATE TABLE person.merchants (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    creator_id UUID REFERENCES person.users(id),
    created TIMESTAMP NOT NULL,
    updated TIMESTAMP NOT NULL,
    company_name VARCHAR(32),
    company_id VARCHAR(32),
    email VARCHAR(32),
    phone_number VARCHAR(32),
    verified_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL,
    status VARCHAR(32),
    filled BOOLEAN
);

