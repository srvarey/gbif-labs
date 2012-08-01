package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.GoogleTileUtil;
import org.gbif.cube.gmap.density.io.OccurrenceWritable;
import org.gbif.occurrencestore.api.model.constants.FieldName;
import org.gbif.occurrencestore.persistence.OccurrenceResultReader;

import java.io.IOException;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;
import org.apache.hadoop.io.IntWritable;

/**
 * Reads the HBase table, collecting the terms we are interested in as we know there are duplicates
 * at the same location. This is only to reduce the amount of grouping needed in later MR jobs.
 */
public class LocationCollectorMapper extends TableMapper<OccurrenceWritable, IntWritable> {

  private final static IntWritable ONE = new IntWritable(1);

  /**
   * Reads the table, emits the Result keyed on the Lat Lng if it is a plottable record
   */
  @Override
  protected void map(ImmutableBytesWritable key, Result row, Context context) throws IOException, InterruptedException {

    Double latitude = OccurrenceResultReader.getDouble(row, FieldName.I_LATITUDE);
    Double longitude = OccurrenceResultReader.getDouble(row, FieldName.I_LONGITUDE);
    Integer issues = OccurrenceResultReader.getInteger(row, FieldName.I_GEOSPATIAL_ISSUE);

    // Google only goes +/- 85 degrees and we only want maps with no known issues
    if (GoogleTileUtil.isPlottable(latitude, longitude) && new Integer(0).equals(issues)) {

      // Note: Make sure everything read here is in the getScanner() in BackFillCallback!
      Integer kingdomID = OccurrenceResultReader.getInteger(row, FieldName.I_KINGDOM_ID);
      Integer phylumID = OccurrenceResultReader.getInteger(row, FieldName.I_PHYLUM_ID);
      Integer classID = OccurrenceResultReader.getInteger(row, FieldName.I_CLASS_ID);
      Integer orderID = OccurrenceResultReader.getInteger(row, FieldName.I_ORDER_ID);
      Integer familyID = OccurrenceResultReader.getInteger(row, FieldName.I_FAMILY_ID);
      Integer genusID = OccurrenceResultReader.getInteger(row, FieldName.I_GENUS_ID);
      Integer speciesID = OccurrenceResultReader.getInteger(row, FieldName.I_SPECIES_ID);
      // taxon != species (it may be a higher taxon, or might be a suspecies)
      Integer taxonID = OccurrenceResultReader.getInteger(row, FieldName.I_NUB_ID);
      String publishingOrganisationKey = OccurrenceResultReader.getString(row, FieldName.I_OWNING_ORG_KEY);
      String datasetKey = OccurrenceResultReader.getString(row, FieldName.I_DATASET_KEY);
      // old portal ID to identify eBIRD
      // TODO: can be removed shortly - see TODO in the TileCollectMapper
      Integer datasetID = OccurrenceResultReader.getInteger(row, FieldName.DATA_RESOURCE_ID);
      String countryIsoCode = OccurrenceResultReader.getString(row, FieldName.I_ISO_COUNTRY_CODE);
      // TODO: the host is the country hosting the data
      // Integer hostCountryIsoCode = OccurrenceResultReader.getInteger(row, FieldName.???);

      context.write(new OccurrenceWritable(kingdomID, phylumID, classID, orderID, familyID, genusID, speciesID, taxonID, issues, datasetID,
        publishingOrganisationKey, datasetKey, countryIsoCode, latitude, longitude, 1), ONE);
    }
  }
}
