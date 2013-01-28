-- 
--  node
-- 
CREATE TABLE node
(
  key character(36) NOT NULL PRIMARY KEY,
  title varchar(255) NOT NULL CHECK (char_length(trim(title)) > 2),
  alias varchar(255) CHECK (char_length(trim(alias)) > 2),
  description text NOT NULL CHECK (char_length(trim(description)) > 10),
-- TODO change and update mybatis
  language varchar(100) NOT NULL, 
  email varchar(100) CHECK (char_length(trim(email)) > 5), 
  phone varchar(50) CHECK (char_length(trim(phone)) > 5),
  homepage varchar(100) CHECK (char_length(trim(homepage)) > 10),
  logo_url varchar(100) CHECK (char_length(trim(logo_url)) > 10),
  address varchar(255) CHECK (char_length(trim(address)) > 0),
  city varchar(100) CHECK (char_length(trim(city)) > 0),
  province varchar(100) CHECK (char_length(trim(province)) > 0),
-- TODO change and update mybatis  
  country varchar(100) NOT NULL CHECK (char_length(trim(country)) > 2),
  postal_code varchar(50) CHECK (char_length(trim(postal_code)) > 0),
  latitude double precision,
  longitude double precision,
  created timestamp with time zone NOT NULL,
  modified timestamp with time zone NOT NULL,
  deleted timestamp with time zone
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node OWNER TO postgres;

-- 
--  organization
-- 
CREATE TABLE organization
(
  key character(36) NOT NULL PRIMARY KEY,
  endorsing_node_key character(36) NOT NULL REFERENCES node(key),
  endorsement_approved boolean NOT NULL,
  title varchar(255) NOT NULL CHECK (char_length(trim(title)) > 2),
  alias varchar(255) CHECK (char_length(trim(alias)) > 2),
  description text NOT NULL CHECK (char_length(trim(description)) > 10),
-- TODO change and update mybatis
  language varchar(100) NOT NULL, 
  email varchar(100) CHECK (char_length(trim(email)) > 5), 
  phone varchar(50) CHECK (char_length(trim(phone)) > 5),
  homepage varchar(100) CHECK (char_length(trim(homepage)) > 10),
  logo_url varchar(100) CHECK (char_length(trim(logo_url)) > 10),
  address varchar(255) CHECK (char_length(trim(address)) > 0),
  city varchar(100) CHECK (char_length(trim(city)) > 0),
  province varchar(100) CHECK (char_length(trim(province)) > 0),
-- TODO change and update mybatis  
  country varchar(100) NOT NULL CHECK (char_length(trim(country)) > 2),
  postal_code varchar(50) CHECK (char_length(trim(postal_code)) > 0),
  latitude double precision,
  longitude double precision,
  created timestamp with time zone NOT NULL,
  modified timestamp with time zone NOT NULL,
  deleted timestamp with time zone
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node OWNER TO postgres;

-- 
--  tag
-- 
CREATE TABLE tag
(
  key serial NOT NULL PRIMARY KEY,
  value varchar(255) NOT NULL CHECK (char_length(trim(value)) > 0),
  creator varchar(255) NOT NULL CHECK (char_length(trim(creator)) > 0), 
  created timestamp with time zone NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE tag OWNER TO postgres;

-- 
--  node_tag
-- 
CREATE TABLE node_tag
(
  node_key character(36) NOT NULL REFERENCES node(key),
  tag_key integer NOT NULL UNIQUE REFERENCES tag(key) ON DELETE CASCADE,
  PRIMARY KEY (node_key, tag_key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node_tag OWNER TO postgres;

-- 
--  organization_tag
-- 
CREATE TABLE organization_tag
(
  organization_key character(36) NOT NULL REFERENCES organization(key),
  tag_key integer NOT NULL UNIQUE REFERENCES tag(key) ON DELETE CASCADE,
  PRIMARY KEY (organization_key, tag_key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organization_tag OWNER TO postgres;

-- 
--  contact
-- 
CREATE TABLE contact
(
  key serial NOT NULL PRIMARY KEY,
  name varchar(255) CHECK (char_length(trim(name)) > 2), 
  description text CHECK (char_length(trim(description)) > 10), 
  email varchar(100) NOT NULL CHECK (char_length(trim(email)) > 5), 
  phone varchar(255) CHECK (char_length(trim(phone)) > 5), 
  organization varchar(255) CHECK (char_length(trim(organization)) > 2), 
  address varchar(255) CHECK (char_length(trim(address)) > 0), 
  city varchar(255) CHECK (char_length(trim(city)) > 0), 
  province varchar(255) CHECK (char_length(trim(province)) > 0), 
-- TODO change and update mybatis  
  country varchar(255) CHECK (char_length(trim(country)) > 2), 
  postal_code varchar(255) CHECK (char_length(trim(postal_code)) > 0),
  created timestamp with time zone NOT NULL,
  modified timestamp with time zone NOT NULL
)
WITH (
  OIDS=FALSE
);
ALTER TABLE contact OWNER TO postgres;

-- 
--  node_contact
-- 
CREATE TABLE node_contact
(
  node_key character(36) NOT NULL REFERENCES node(key),
  contact_key integer NOT NULL UNIQUE REFERENCES contact(key) ON DELETE CASCADE,
  PRIMARY KEY (node_key, contact_key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node_contact OWNER TO postgres;

-- 
--  organization_contact
-- 
CREATE TABLE organization_contact
(
  organization_key character(36) NOT NULL REFERENCES organization(key),
  contact_key integer NOT NULL UNIQUE REFERENCES contact(key) ON DELETE CASCADE,
  PRIMARY KEY (organization_key, contact_key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE organization_contact OWNER TO postgres;
