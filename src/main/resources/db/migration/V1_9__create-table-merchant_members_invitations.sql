CREATE TABLE person.merchant_members_invitations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created TIMESTAMP NOT NULL,
    expires TIMESTAMP NOT NULL,
    merchant_id UUID REFERENCES person.merchants(id),
    first_name VARCHAR(32),
    last_name VARCHAR(32),
    email VARCHAR(32),
    status VARCHAR(32)
);

