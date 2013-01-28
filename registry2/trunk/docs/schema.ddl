CREATE TABLE node
(
  key character(36) NOT NULL,
  title varchar(255) NOT NULL,
  alias varchar(255),
  description text NOT NULL,
  language varchar(100) NOT NULL,
  email varchar(100),
  phone varchar(50),
  homepage varchar(100),
  logoUrl varchar(100),
  address varchar(255),
  city varchar(100),
  province varchar(100),
  country varchar(100) NOT NULL,
  postalCode varchar(50),
  latitude double precision,
  longitude double precision,
  created timestamp with time zone NOT NULL,
  modified timestamp with time zone NOT NULL,
  deleted timestamp with time zone,
  CONSTRAINT pk PRIMARY KEY (key)
)
WITH (
  OIDS=FALSE
);
ALTER TABLE node
  OWNER TO postgres;
