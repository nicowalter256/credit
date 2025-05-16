--
-- PostgreSQL database dump
--

-- Dumped from database version 12.19 (Debian 12.19-1.pgdg120+1)
-- Dumped by pg_dump version 16.1

-- Started on 2025-05-16 09:36:16 EAT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 6 (class 2615 OID 2200)
-- Name: public; Type: SCHEMA; Schema: -; Owner: postgres
--

-- *not* creating schema, since initdb creates it


ALTER SCHEMA public OWNER TO postgres;

--
-- TOC entry 638 (class 1247 OID 28666)
-- Name: application_status; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.application_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED',
    'CANCELED'
);


ALTER TYPE public.application_status OWNER TO postgres;

--
-- TOC entry 547 (class 1247 OID 28650)
-- Name: loan_status; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.loan_status AS ENUM (
    'ACTIVE',
    'PAID_OFF',
    'DEFAULTED'
);


ALTER TYPE public.loan_status OWNER TO postgres;

--
-- TOC entry 550 (class 1247 OID 28658)
-- Name: role; Type: TYPE; Schema: public; Owner: postgres
--

CREATE TYPE public.role AS ENUM (
    'ADMIN',
    'LOAN_OFFICER',
    'MANAGER'
);


ALTER TYPE public.role OWNER TO postgres;

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 202 (class 1259 OID 28675)
-- Name: credit_profiles; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.credit_profiles (
    id bigint NOT NULL,
    created_at timestamp with time zone,
    credit_score integer,
    current_debt numeric(19,2),
    last_assessment_date timestamp with time zone,
    max_loan_amount numeric(19,2),
    updated_at timestamp with time zone,
    customer_id bigint
);


ALTER TABLE public.credit_profiles OWNER TO postgres;

--
-- TOC entry 203 (class 1259 OID 28680)
-- Name: customers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.customers (
    id bigint NOT NULL,
    created_at timestamp with time zone,
    email character varying(255),
    first_name character varying(255),
    last_name character varying(255),
    password character varying(255),
    phone_number character varying(255),
    salt character varying(255),
    sme_name character varying(255),
    sme_registration_number character varying(255),
    updated_at timestamp with time zone,
    address character varying(255),
    date_of_birth date,
    is_active boolean
);


ALTER TABLE public.customers OWNER TO postgres;

--
-- TOC entry 204 (class 1259 OID 28688)
-- Name: loan_applications; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.loan_applications (
    id bigint NOT NULL,
    amount_requested numeric(19,2),
    application_date timestamp without time zone,
    created_at timestamp with time zone,
    decision_date timestamp without time zone,
    purpose character varying(255),
    rejection_reason character varying(255),
    status public.application_status,
    updated_at timestamp with time zone,
    customer_id bigint NOT NULL,
    term_in_months integer
);


ALTER TABLE public.loan_applications OWNER TO postgres;

--
-- TOC entry 205 (class 1259 OID 28696)
-- Name: loan_repayments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.loan_repayments (
    id bigint NOT NULL,
    amount_paid numeric(19,2),
    created_at timestamp with time zone,
    payment_date timestamp without time zone,
    payment_method character varying(255),
    updated_at timestamp with time zone,
    loan_id bigint NOT NULL
);


ALTER TABLE public.loan_repayments OWNER TO postgres;

--
-- TOC entry 206 (class 1259 OID 28701)
-- Name: loans; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.loans (
    id bigint NOT NULL,
    amount_approved numeric(19,2),
    created_at timestamp with time zone,
    end_date date,
    interest_rate numeric(19,2),
    outstanding_balance numeric(19,2),
    start_date date,
    status public.loan_status,
    term_in_months integer,
    updated_at timestamp with time zone,
    loan_application_id bigint
);


ALTER TABLE public.loans OWNER TO postgres;

--
-- TOC entry 207 (class 1259 OID 28706)
-- Name: system_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE public.system_users (
    id bigint NOT NULL,
    created_at timestamp with time zone,
    email character varying(255),
    first_name character varying(255),
    is_active boolean,
    last_name character varying(255),
    password character varying(255),
    role public.role,
    salt character varying(255),
    updated_at timestamp with time zone
);


ALTER TABLE public.system_users OWNER TO postgres;

--
-- TOC entry 208 (class 1259 OID 28718)
-- Name: tbl_credit_profiles_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tbl_credit_profiles_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tbl_credit_profiles_seq OWNER TO postgres;

--
-- TOC entry 209 (class 1259 OID 28720)
-- Name: tbl_customers_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tbl_customers_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tbl_customers_seq OWNER TO postgres;

--
-- TOC entry 210 (class 1259 OID 28722)
-- Name: tbl_loan_applications_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tbl_loan_applications_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tbl_loan_applications_seq OWNER TO postgres;

--
-- TOC entry 211 (class 1259 OID 28724)
-- Name: tbl_loan_repayments_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tbl_loan_repayments_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tbl_loan_repayments_seq OWNER TO postgres;

--
-- TOC entry 212 (class 1259 OID 28726)
-- Name: tbl_loans_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tbl_loans_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tbl_loans_seq OWNER TO postgres;

--
-- TOC entry 213 (class 1259 OID 28728)
-- Name: tbl_system_users_seq; Type: SEQUENCE; Schema: public; Owner: postgres
--

CREATE SEQUENCE public.tbl_system_users_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE public.tbl_system_users_seq OWNER TO postgres;

--
-- TOC entry 2893 (class 2606 OID 28679)
-- Name: credit_profiles credit_profiles_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.credit_profiles
    ADD CONSTRAINT credit_profiles_pkey PRIMARY KEY (id);


--
-- TOC entry 2896 (class 2606 OID 28687)
-- Name: customers customers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT customers_pkey PRIMARY KEY (id);


--
-- TOC entry 2903 (class 2606 OID 28695)
-- Name: loan_applications loan_applications_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loan_applications
    ADD CONSTRAINT loan_applications_pkey PRIMARY KEY (id);


--
-- TOC entry 2907 (class 2606 OID 28700)
-- Name: loan_repayments loan_repayments_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loan_repayments
    ADD CONSTRAINT loan_repayments_pkey PRIMARY KEY (id);


--
-- TOC entry 2912 (class 2606 OID 28705)
-- Name: loans loans_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT loans_pkey PRIMARY KEY (id);


--
-- TOC entry 2914 (class 2606 OID 28713)
-- Name: system_users system_users_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_users
    ADD CONSTRAINT system_users_pkey PRIMARY KEY (id);


--
-- TOC entry 2916 (class 2606 OID 28717)
-- Name: system_users ukdxy6tf9nvg7o3kd7yfd5j7qiu; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.system_users
    ADD CONSTRAINT ukdxy6tf9nvg7o3kd7yfd5j7qiu UNIQUE (email);


--
-- TOC entry 2898 (class 2606 OID 28715)
-- Name: customers ukrfbvkrffamfql7cjmen8v976v; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.customers
    ADD CONSTRAINT ukrfbvkrffamfql7cjmen8v976v UNIQUE (email);


--
-- TOC entry 2899 (class 1259 OID 28752)
-- Name: idx22uv1dtnrs8v2yrv62wr22aqt; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idx22uv1dtnrs8v2yrv62wr22aqt ON public.loan_applications USING btree (status);


--
-- TOC entry 2894 (class 1259 OID 28750)
-- Name: idxbv0j1lme8lk78ku0hdsa6mnl3; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxbv0j1lme8lk78ku0hdsa6mnl3 ON public.credit_profiles USING btree (customer_id);


--
-- TOC entry 2904 (class 1259 OID 28755)
-- Name: idxf44eyhmpqyerbqxwgkpqjrq8l; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxf44eyhmpqyerbqxwgkpqjrq8l ON public.loan_repayments USING btree (payment_date);


--
-- TOC entry 2905 (class 1259 OID 28754)
-- Name: idxfxp0vjp5akt6ven0at5wrx5p0; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxfxp0vjp5akt6ven0at5wrx5p0 ON public.loan_repayments USING btree (loan_id);


--
-- TOC entry 2908 (class 1259 OID 28756)
-- Name: idxip3rigtpcnedy7jqluhtub0ng; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxip3rigtpcnedy7jqluhtub0ng ON public.loans USING btree (status);


--
-- TOC entry 2909 (class 1259 OID 28757)
-- Name: idxj7kw9g6hawml65u8g3ed2r4yu; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxj7kw9g6hawml65u8g3ed2r4yu ON public.loans USING btree (end_date);


--
-- TOC entry 2900 (class 1259 OID 28753)
-- Name: idxmpbf8gvmlp46xhghtl70c45jo; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxmpbf8gvmlp46xhghtl70c45jo ON public.loan_applications USING btree (application_date);


--
-- TOC entry 2910 (class 1259 OID 28758)
-- Name: idxobys70kv2uc9b3w96xe0hv5le; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxobys70kv2uc9b3w96xe0hv5le ON public.loans USING btree (status, end_date);


--
-- TOC entry 2901 (class 1259 OID 28751)
-- Name: idxt9fv8ccdnlle7656bbrysmo3k; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX idxt9fv8ccdnlle7656bbrysmo3k ON public.loan_applications USING btree (customer_id);


--
-- TOC entry 2920 (class 2606 OID 28745)
-- Name: loans fk48lba6mtncmcu7jiuoh041dr7; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loans
    ADD CONSTRAINT fk48lba6mtncmcu7jiuoh041dr7 FOREIGN KEY (loan_application_id) REFERENCES public.loan_applications(id);


--
-- TOC entry 2917 (class 2606 OID 28730)
-- Name: credit_profiles fke7hi7slddes7di51xtwpoy058; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.credit_profiles
    ADD CONSTRAINT fke7hi7slddes7di51xtwpoy058 FOREIGN KEY (customer_id) REFERENCES public.customers(id);


--
-- TOC entry 2919 (class 2606 OID 28740)
-- Name: loan_repayments fkmvfjvk48bhsvwbdis0s9uwn1t; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loan_repayments
    ADD CONSTRAINT fkmvfjvk48bhsvwbdis0s9uwn1t FOREIGN KEY (loan_id) REFERENCES public.loans(id);


--
-- TOC entry 2918 (class 2606 OID 28735)
-- Name: loan_applications fks6p85101r2lbn61hmgth4kwg2; Type: FK CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY public.loan_applications
    ADD CONSTRAINT fks6p85101r2lbn61hmgth4kwg2 FOREIGN KEY (customer_id) REFERENCES public.customers(id);


--
-- TOC entry 3052 (class 0 OID 0)
-- Dependencies: 6
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE USAGE ON SCHEMA public FROM PUBLIC;
GRANT ALL ON SCHEMA public TO PUBLIC;


-- Completed on 2025-05-16 09:36:16 EAT

--
-- PostgreSQL database dump complete
--

