Instructions for processing the GBIF occurrence records using Hadoop and Hive for the Mysql schema in use by EOL.
The output is a density map 

build the occurrence-spatial 
mvn assembly:assembly

Hive:
set mapred.child.java.opts=-Xmx768m;
set mapred.reduce.parallel.copies=50;
set mapred.compress.map.output=true;
set mapred.map.output.compression.codec=com.hadoop.compression.lzo.LzoCodec;
set io.sort.factor=100;
set io.sort.mb=200;
set mapred.job.reuse.jvm.num.tasks=1;

ADD JAR /Users/timrobertson/dev/workspace/occurrence-spatial/target/occurrence-spatial-1.0-SNAPSHOT-jar-with-dependencies.jar;
CREATE TEMPORARY FUNCTION taxonDensityGoogleTileUDTF AS 'org.gbif.occurrence.spatial.udf.TaxonDensityGoogleTileUDTF'; 
CREATE TEMPORARY FUNCTION taxonDensityNormaliseTaxaUDTF AS 'org.gbif.occurrence.spatial.udf.TaxonDensityNormaliseTaxaUDTF';

drop table taxon_density_1;
drop table taxon_density_2;
drop table taxon_density_3;
drop table taxon_density;

create table taxon_density_1
as select kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, latitude, longitude, count(1) as count
from occurrence_record	
where geospatial_issue=0 and latitude is not null and longitude is not null and nub_concept_id is not null
group by kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, latitude, longitude;

create table taxon_density_2
as select taxonDensityGoogleTileUDTF(kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, latitude, longitude, count, 23) 
as (kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id,genus_concept_id, species_concept_id,nub_concept_id,tileX,tileY,zoom,clusterX,clusterY,count)
from taxon_density_1;

create table taxon_density_3
as select kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id,tileX,tileY,zoom,clusterX,clusterY,sum(count) as count
from taxon_density_2
group by kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, tileX,tileY,zoom,clusterX,clusterY;

create table taxon_density(
  taxonId INT,
  tileX INT,
  tileY INT,
  zoom INT,
  clusterX INT,
  clusterY INT,
  count INT)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';
from taxon_density_3
insert overwrite table taxon_density
select taxonDensityNormaliseTaxaUDTF(kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, tileX,tileY,zoom,clusterX,clusterY,count)
as (taxonId,tileX,tileY,zoom,clusterX,clusterY,count);

drop table taxon_density_1;
drop table taxon_density_2;
drop table taxon_density_3;

create table temp_taxon_resource
as select kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, data_provider_id, data_resource_id
from occurrence_record	
where geospatial_issue=0 and latitude is not null and longitude is not null and nub_concept_id is not null
group by kingdom_concept_id, phylum_concept_id, class_concept_id, order_concept_id,family_concept_id, 
genus_concept_id, species_concept_id,nub_concept_id, data_provider_id, data_resource_id;

create table temp_taxon_resource_2 
as select * from (
	select kingdom_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by kingdom_concept_id, data_provider_id,data_resource_id
	union all 
	select phylum_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by phylum_concept_id, data_provider_id,data_resource_id
	union all 
	select class_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by class_concept_id, data_provider_id,data_resource_id
	union all 
	select order_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by order_concept_id, data_provider_id,data_resource_id
	union all 
	select family_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by family_concept_id, data_provider_id,data_resource_id
	union all 
	select genus_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by genus_concept_id, data_provider_id,data_resource_id
	union all 
	select species_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by species_concept_id, data_provider_id,data_resource_id
	union all 
	select nub_concept_id as taxon_id, data_provider_id,data_resource_id from temp_taxon_resource group by nub_concept_id, data_provider_id,data_resource_id
) res group by taxon_id, data_provider_id,data_resource_id;

CREATE TABLE taxon_resource (
  taxon_id INT,
  data_provider_id INT,
  data_resource_id INT,
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY '\t';
temp_taxon_resource_2
insert overwrite table taxon_resource
select taxon_id,data_provider_id,data_resource_id

drop table temp_taxon_resource;
drop table temp_taxon_resource_2;
