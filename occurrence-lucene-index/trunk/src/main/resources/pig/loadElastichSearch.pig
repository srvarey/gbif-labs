register '/opt/elasticsearch-0.16.3/lib/wonderdog/wonderdog-1.0.jar';
register '/opt/elasticsearch-0.16.3/lib/elasticsearch-0.16.3.jar';
register '/opt/elasticsearch-0.16.3/lib/jline-0.9.94.jar';
register '/opt/elasticsearch-0.16.3/lib/jna-3.2.7.jar';
register '/opt/elasticsearch-0.16.3/lib/log4j-1.2.15.jar';
register '/opt/elasticsearch-0.16.3/lib/lucene-analyzers-3.1.0.jar';
register '/opt/elasticsearch-0.16.3/lib/lucene-core-3.1.0.jar';
register '/opt/elasticsearch-0.16.3/lib/lucene-highlighter-3.1.0.jar';
register '/opt/elasticsearch-0.16.3/lib/lucene-memory-3.1.0.jar';
register '/opt/elasticsearch-0.16.3/lib/lucene-queries-3.1.0.jar';
occurrences = LOAD '/user/hive/warehouse/fede_occindex_occurrence_record_1m' USING PigStorage('\u0001') AS 
(id:int, data_provider_id:int, data_resource_id:int, institution_code_id:int, collection_code_id:int, catalogue_number_id:int, taxon_concept_id:int, taxon_name_id:int, kingdom_concept_id:int, phylum_concept_id:int, class_concept_id:int, order_concept_id:int, family_concept_id:int, genus_concept_id:int, species_concept_id:int, nub_concept_id:int, iso_country_code:chararray, latitude:double, longitude:double, cell_id:int, centi_cell_id:int, mod360_cell_id:int, year:int, month:int, occurrence_date:chararray, basis_of_record:int, taxonomic_issue:int, geospatial_issue:int, other_issue:int, deleted:chararray, altitude_metres:int, depth_centimetres:int, modified:chararray);

%default INDEX 'occurrences'
%default OBJ   'occurrence'

STORE occurrences INTO 'es://$INDEX/$OBJ?json=false&size=1000' USING com.infochimps.elasticsearch.pig.ElasticSearchStorage();