CREATE TABLE occurrence (
  id mediumint(8) unsigned NOT NULL AUTO_INCREMENT,
  dataset_id tinyint(3) unsigned NOT NULL,
  institution_code varchar(100) NOT NULL,
  collection_code varchar(100) NOT NULL,
  catalogue_number varchar(100) NOT NULL,
  scientific_name varchar(255) NOT NULL,
  locality varchar(1000),
  PRIMARY KEY (id)
) ENGINE=MyISAM AUTO_INCREMENT=1001 DEFAULT CHARSET=utf8;
