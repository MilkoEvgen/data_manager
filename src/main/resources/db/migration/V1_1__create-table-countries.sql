CREATE TABLE person.countries (
    id SERIAL PRIMARY KEY,
    created TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    name VARCHAR(32),
    alpha2 VARCHAR(2),
    alpha3 VARCHAR(3),
    status VARCHAR(32)
);

INSERT INTO person.countries ( name, alpha2, alpha3, status)
VALUES
    ('Australia', 'AU', 'AUS', 'ACTIVE'),
    ('Brazil', 'BR', 'BRA', 'ACTIVE'),
    ('Canada', 'CA', 'CAN', 'ACTIVE'),
    ('Denmark', 'DN', 'DNK', 'ACTIVE'),
    ('Egypt', 'EG', 'EGY', 'ACTIVE');
