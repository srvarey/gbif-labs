package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.GoogleTileUtil;
import org.gbif.cube.gmap.io.LatLngWritable;
import org.gbif.cube.gmap.io.TileContentType;
import org.gbif.cube.gmap.io.TileKeyWritable;
import org.gbif.occurrencestore.api.model.constants.FieldName;
import org.gbif.occurrencestore.persistence.OccurrenceResultReader;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableMapper;

/**
 * Reads the HBase table, collecting the points by tile
 */
public class HBaseTileCollectorMapper extends TableMapper<TileKeyWritable, LatLngWritable> {

  private int numberZooms;

  /**
   * Reads the table, emits the latitude and longitude for each of the generated tile groups at each zoom.
   */
  @Override
  protected void map(ImmutableBytesWritable key, Result row, Context context) throws IOException, InterruptedException {

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
    // TODO: can be removed shortly - see TODO below
    Integer datasetID = OccurrenceResultReader.getInteger(row, FieldName.DATA_RESOURCE_ID);
    String countryIsoCode = OccurrenceResultReader.getString(row, FieldName.I_ISO_COUNTRY_CODE);
    // TODO: the host is the country hosting the data
    // Integer hostCountryIsoCode = OccurrenceResultReader.getInteger(row, FieldName.???);
    Double latitude = OccurrenceResultReader.getDouble(row, FieldName.I_LATITUDE);
    Double longitude = OccurrenceResultReader.getDouble(row, FieldName.I_LONGITUDE);
    Integer issues = OccurrenceResultReader.getInteger(row, FieldName.I_GEOSPATIAL_ISSUE);

    // TODO: eBird has known issues currently, BUT THIS HACK SHOULD BE REMOVED WHEN FIXED
    if (datasetID != null && 43 == datasetID && latitude != null && longitude != null) {
      Double copy = latitude;
      latitude = longitude;
      longitude = copy;
    }

    Set<Integer> taxa = Sets.newHashSet(kingdomID, phylumID, classID, orderID, familyID, genusID, speciesID, taxonID);

    // Google only goes +/- 85 degrees and we only want maps with no known issues
    if (GoogleTileUtil.isPlottable(latitude, longitude) && new Integer(0).equals(issues)) {
      LatLngWritable location = new LatLngWritable(latitude, longitude, 1);
      for (int z = 0; z < numberZooms; z++) {
        context.setStatus("Lat[" + latitude + "] lng[" + longitude + "] zoom[" + z + " of " + numberZooms + "]");
        // locate the tile
        int tileX = GoogleTileUtil.toTileX(longitude, z);
        int tileY = GoogleTileUtil.toTileY(latitude, z);

        for (Integer id : taxa) {
          if (id != null) {
            context.write(new TileKeyWritable(TileContentType.TAXON, String.valueOf(id), tileX, tileY, z), location);
          }
        }
        if (publishingOrganisationKey != null) {
          context.write(new TileKeyWritable(TileContentType.PUBLISHER, publishingOrganisationKey, tileX, tileY, z), location);
        }
        if (datasetKey != null) {
          context.write(new TileKeyWritable(TileContentType.DATASET, datasetKey, tileX, tileY, z), location);
        }
        if (countryIsoCode != null && countryIsoCode.length() == 2) {
          context.write(new TileKeyWritable(TileContentType.COUNTRY, countryIsoCode, tileX, tileY, z), location);
        }
      }
    }
  }

  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    super.setup(context);
    numberZooms = context.getConfiguration().getInt(Backfill.KEY_NUM_ZOOMS, Backfill.DEFAULT_NUM_ZOOMS);
  }
}
