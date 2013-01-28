/**

CREATE TABLE products (
    product_no integer,
    name text,
    price numeric,
    CHECK (price > 0),
    discounted_price numeric,
    CHECK (discounted_price > 0),
    CONSTRAINT valid_discount CHECK (price > discounted_price)
);
CREATE TABLE products (
    product_no integer NOT NULL,
    name text NOT NULL,
    price numeric NOT NULL CHECK (price > 0),
    product_no integer UNIQUE,
);

    UNIQUE (a, c)
    
    product_no integer REFERENCES products (product_no),  Foreign key
    FOREIGN KEY (b, c) REFERENCES other_table (c1, c2)
    
CREATE TABLE order_items (
    product_no integer REFERENCES products,
    order_id integer REFERENCES orders,
    quantity integer,
    PRIMARY KEY (product_no, order_id)
);

order_id integer REFERENCES orders ON DELETE CASCADE (deletes the other one)
product_no integer REFERENCES products ON DELETE RESTRICT ()
 

 */

/**
 * Node
 */
CREATE TABLE node
(
  key character(36) NOT NULL PRIMARY KEY,
  title varchar(255) NOT NULL CHECK (char_length(trim(title)) > 2),
  alias varchar(255) CHECK (char_length(trim(alias)) > 2),
  description text NOT NULL CHECK (char_length(trim(description)) > 10),
  language varchar(100) NOT NULL, // TODO, change to char(2) and update mybatis
  email varchar(100) CHECK (char_length(trim(email)) > 5), 
  phone varchar(50) CHECK (char_length(trim(phone)) > 5),
  homepage varchar(100) CHECK (char_length(trim(homepage)) > 10),
  logo_url varchar(100) CHECK (char_length(trim(logo_url)) > 10),
  address varchar(255) CHECK (char_length(trim(address)) > 0),
  city varchar(100) CHECK (char_length(trim(city)) > 0),
  province varchar(100) CHECK (char_length(trim(province)) > 0),
  country varchar(100) NOT NULL CHECK (char_length(trim(country)) > 2),
  postalCode varchar(50) CHECK (char_length(trim(country)) > 0),
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
