CREATE TABLE person.merchant_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE REFERENCES person.users(id),
    created TIMESTAMP NOT NULL,
    updated TIMESTAMP NOT NULL,
    merchant_id UUID REFERENCES person.merchants(id),
    member_role VARCHAR(32),
    status VARCHAR(32)
);

