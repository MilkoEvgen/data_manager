CREATE TABLE person.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    auth_service_id UUID,
    created TIMESTAMP NOT NULL,
    updated TIMESTAMP NOT NULL,
    first_name VARCHAR(32),
    last_name VARCHAR(32),
    verified_at TIMESTAMP NOT NULL,
    archived_at TIMESTAMP NOT NULL,
    status VARCHAR(64),
    address_id UUID REFERENCES person.addresses(id)
);

