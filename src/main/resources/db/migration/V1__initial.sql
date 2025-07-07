/* ================================================================
   V1__initial.sql  –  Central-Vet schema (PostgreSQL 14+)
   ================================================================ */

-- Helpers --------------------------------------------------------
CREATE EXTENSION IF NOT EXISTS pgcrypto;          -- gen_random_uuid()
-- CREATE EXTENSION IF NOT EXISTS pgvector;

/* ---------- ENUM TYPES ----------------------------------------- */
CREATE TYPE sex_enum            AS ENUM ('MALE','FEMALE','UNKNOWN');
CREATE TYPE pet_status_enum     AS ENUM ('LOST', 'OK', 'SICK', 'DECEASED');
-- fix
CREATE TYPE visibility_enum     AS ENUM ('PUBLIC','CLINIC_ONLY','PRIVATE');
CREATE TYPE access_level_enum   AS ENUM ('READ','WRITE','FULL');
CREATE TYPE log_action_enum     AS ENUM ('view','request_write','granted','edit');

/* ---------- CORE ROLES ----------------------------------------- */
CREATE TABLE roles (
                       id          BIGSERIAL  PRIMARY KEY,
                       name        VARCHAR UNIQUE NOT NULL,           -- ADMIN | VET | PET_OWNER
                       created_at  TIMESTAMPTZ DEFAULT now(),
                       updated_at  TIMESTAMPTZ DEFAULT now()
);

INSERT INTO roles (name) VALUES ('ADMIN'), ('VET'), ('PET_OWNER');

/* ---------- USERS & SUB-TYPES ---------------------------------- */
CREATE TABLE users (
                       id          BIGSERIAL     PRIMARY KEY,
                       username    VARCHAR       UNIQUE NOT NULL,
                       password_hash    VARCHAR       NOT NULL,           -- bcrypt / argon2 hash
                       role_id     BIGINT        NOT NULL REFERENCES roles(id),
                       created_at  TIMESTAMPTZ   DEFAULT now(),
                       updated_at  TIMESTAMPTZ   DEFAULT now()
);

/* ---------- CLINICS -------------------------------------------- */
CREATE TABLE clinic (
                        id          BIGSERIAL     PRIMARY KEY,
                        name        VARCHAR,
                        address     VARCHAR,
                        latitude    DECIMAL(9,6),
                        longitude   DECIMAL(9,6),
                        email       VARCHAR,
                        created_at  TIMESTAMPTZ   DEFAULT now(),
                        updated_at  TIMESTAMPTZ   DEFAULT now()
);

/* ---- Vet profile (1-to-1 with users) --------------------------- */
CREATE TABLE vet_user (
                          id          BIGINT  PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
--                           clinic_id   BIGINT  NOT NULL UNIQUE REFERENCES clinic(id),
                          clinic_id   BIGINT  NOT NULL REFERENCES clinic(id),
                          email       VARCHAR,
                          cel_num     VARCHAR,
                          face_embedding   BYTEA,
                          created_at  TIMESTAMPTZ DEFAULT now(),
                          updated_at  TIMESTAMPTZ DEFAULT now()
);

/* ---- Pet-owner profile ---------------------------------------- */
CREATE TABLE pet_owner_user (
                                id          BIGINT  PRIMARY KEY REFERENCES users(id) ON DELETE CASCADE,
                                email       VARCHAR,
                                cel_num     VARCHAR,
                                created_at  TIMESTAMPTZ DEFAULT now(),
                                updated_at  TIMESTAMPTZ DEFAULT now()
);

/* ---------- PETS & SHARED RECORDS ------------------------------ */
CREATE TABLE pet (
                     id              BIGSERIAL        PRIMARY KEY,
                     name            VARCHAR,
                     species         VARCHAR,
                     breed           VARCHAR,
                     sex             sex_enum ,
                     birthdate       DATE,
                     status          pet_status_enum,
--                      home_clinic_id  BIGINT           NOT NULL REFERENCES clinic(id) ,
--                      home_clinic_id  BIGINT           NOT NULL DEFAULT 0 REFERENCES clinic(id),
                     home_clinic_id  BIGINT NOT NULL DEFAULT 1 REFERENCES clinic(id),
                     owner_name      VARCHAR,
                     owner_contact   VARCHAR,
                     owner_user_id   BIGINT           REFERENCES users(id) ON DELETE SET NULL,
                     qr_code_token   UUID             UNIQUE NOT NULL DEFAULT gen_random_uuid(),
                     edit_code       VARCHAR          NOT NULL,            -- store salted hash
                     visibility      visibility_enum  NOT NULL DEFAULT 'CLINIC_ONLY',
                     created_at      TIMESTAMPTZ      DEFAULT now(),
                     updated_at      TIMESTAMPTZ      DEFAULT now()
);

CREATE TABLE pet_historical_record (
                                       id          BIGSERIAL  PRIMARY KEY,
                                       pet_id      BIGINT     NOT NULL UNIQUE REFERENCES pet(id) ON DELETE CASCADE,
                                       created_by  BIGINT     REFERENCES users(id),
                                       created_at  TIMESTAMPTZ DEFAULT now(),
                                       updated_at  TIMESTAMPTZ DEFAULT now()
);

/* ---------- RELATIONSHIPS & ACL -------------------------------- */
CREATE TABLE pet_clinic (
                            pet_id      BIGINT NOT NULL REFERENCES pet(id)    ON DELETE CASCADE,
                            clinic_id   BIGINT NOT NULL REFERENCES clinic(id) ON DELETE CASCADE,
                            linked_at   TIMESTAMPTZ DEFAULT now(),
                            PRIMARY KEY (pet_id, clinic_id)
);

CREATE TABLE historical_record_clinic (
                                          record_id     BIGINT NOT NULL REFERENCES pet_historical_record(id) ON DELETE CASCADE,
                                          clinic_id     BIGINT NOT NULL REFERENCES clinic(id)                ON DELETE CASCADE,
                                          access_level  access_level_enum NOT NULL DEFAULT 'READ',
                                          authorized_at TIMESTAMPTZ DEFAULT now(),
                                          PRIMARY KEY (record_id, clinic_id)
);

/* ---------- APPOINTMENTS & SYMPTOMS ---------------------------- */
CREATE TABLE appointment (
                             id           BIGSERIAL PRIMARY KEY,
                             record_id    BIGINT     NOT NULL REFERENCES pet_historical_record(id) ON DELETE CASCADE,
                             date         TIMESTAMPTZ,
                             weight       NUMERIC(5,2),
                             temperature  NUMERIC(4,1),
                             heart_rate   SMALLINT,
                             description  TEXT,
                             treatments   TEXT,
                             diagnosis    TEXT,
                             notes        TEXT,
                             created_by   BIGINT REFERENCES users(id),
                             created_at   TIMESTAMPTZ DEFAULT now(),
                             updated_at   TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE symptoms (
                          id          BIGSERIAL  PRIMARY KEY,
                          name        VARCHAR UNIQUE,
                          created_at  TIMESTAMPTZ DEFAULT now(),
                          updated_at  TIMESTAMPTZ DEFAULT now()
);

CREATE TABLE appointment_symptoms (
                                      appointment_id BIGINT NOT NULL REFERENCES appointment(id) ON DELETE CASCADE,
                                      symptom_id     BIGINT NOT NULL REFERENCES symptoms(id)    ON DELETE CASCADE,
                                      PRIMARY KEY (appointment_id, symptom_id),
                                      created_at TIMESTAMPTZ DEFAULT now(),
                                      updated_at TIMESTAMPTZ DEFAULT now()
);

/* ---------- AUDIT LOG ----------------------------------------- */
CREATE TABLE access_log (
                            id           BIGSERIAL        PRIMARY KEY,
                            vet_user_id  BIGINT           REFERENCES vet_user(id),
                            record_id    BIGINT           REFERENCES pet_historical_record(id),
                            action       log_action_enum  NOT NULL,
                            ts           TIMESTAMPTZ      DEFAULT now()
);

/* ---------- INDEXES ------------------------------------------- */
CREATE UNIQUE INDEX pet_qr_idx          ON pet(qr_code_token);
CREATE        INDEX vet_clinic_idx      ON vet_user(clinic_id);
CREATE        INDEX hist_clinic_idx     ON historical_record_clinic(clinic_id);
CREATE        INDEX accesslog_rec_ts    ON access_log(record_id, ts DESC);
CREATE        INDEX appointment_tl_idx  ON appointment(record_id, date);

-- done
