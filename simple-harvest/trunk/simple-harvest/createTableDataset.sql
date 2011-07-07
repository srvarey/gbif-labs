CREATE TABLE IF NOT EXISTS `dataset` (
  `dataset_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL,
  `url` varchar(1000) NOT NULL,
  `type` varchar(50) NOT NULL,
  PRIMARY KEY (`dataset_id`)
) ENGINE=MyISAM  DEFAULT CHARSET=latin1 AUTO_INCREMENT=13 ;

--
-- Table content of `dataset`
--

INSERT INTO `dataset` (`name`, `url`, `type`) VALUES
('crbip', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=crbip', 'biocase'),
('cal', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cal', 'biocase'),
('mola', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=mola', 'biocase'),
('wal_fut', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=wal_fut', 'biocase'),
('mzs_ave', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=mzs_ave', 'biocase'),
('palbot', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=palbot', 'biocase'),
('guy', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=guy', 'biocase'),
('cirm', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cirm', 'biocase'),
('cbnfc', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cbnfc', 'biocase'),
('floraine', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=floraine', 'biocase'),
('cfbp', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=cfbp', 'biocase'),
('mzs_po', 'http://www.gbif.fr/biocase/pywrapper.cgi?dsa=crbip', 'biocase'),
('cnidariamzs', 'http://www.gbif.fr:8080/ipt/resource.do?r=cnidariamzs', 'ipt');