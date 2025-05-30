CREATE TYPE application_status AS ENUM
    ('PENDING','APPROVED','REJECTED','CANCELED');


CREATE TYPE loan_status AS ENUM
    ('ACTIVE','PAID_OFF','DEFAULTED');

CREATE TYPE role AS ENUM
    ('ADMIN','LOAN_OFFICER','MANAGER');