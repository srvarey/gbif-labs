package org.gbif.cube.gmap.density.backfill;

import org.gbif.cube.gmap.GoogleTileUtil;
import org.gbif.cube.gmap.density.io.OccurrenceWritable;
import org.gbif.cube.gmap.io.LatLngWritable;
import org.gbif.cube.gmap.io.TileContentType;
import org.gbif.cube.gmap.io.TileKeyWritable;

import java.io.IOException;
import java.util.Set;

import com.google.common.collect.Sets;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Reads the HBase table, collecting the points by tile
 */
public class TileCollectorMapper extends Mapper<OccurrenceWritable, IntWritable, TileKeyWritable, LatLngWritable> {

  private int numberZooms;


  @Override
  protected void map(OccurrenceWritable o, IntWritable count, Context context) throws IOException, InterruptedException {
    context.setStatus("Latitude[" + o.getLatitude() + "], Longitude[" + o.getLongitude() + "], issues[" + o.getIssues() + "] has count["
      + o.getCount() + "]");

    // Google only goes +/- 85 degrees and we only want maps with no known issues
    if (GoogleTileUtil.isPlottable(o.getLatitude(), o.getLongitude()) && new Integer(0).equals(o.getIssues())) {
      Set<Integer> taxa =
        Sets.newHashSet(o.getKingdomID(), o.getPhylumID(), o.getClassID(), o.getOrderID(), o.getFamilyID(), o.getGenusID(), o.getSpeciesID(),
          o.getTaxonID());

      LatLngWritable location = new LatLngWritable(o.getLatitude(), o.getLongitude(), count.get());
      for (int z = 0; z < numberZooms; z++) {


        context.setStatus("Lat[" + o.getLatitude() + "] lng[" + o.getLongitude() + "] zoom[" + z + " of " + numberZooms + "]");
        // locate the tile
        int tileX = GoogleTileUtil.toTileX(o.getLongitude(), z);
        int tileY = GoogleTileUtil.toTileY(o.getLatitude(), z);

        for (Integer id : taxa) {
          if (id != null) {
            context.write(new TileKeyWritable(TileContentType.TAXON, String.valueOf(id), tileX, tileY, z), location);
          }
        }
        if (o.getPublishingOrganisationKey() != null) {
          context.write(new TileKeyWritable(TileContentType.PUBLISHER, o.getPublishingOrganisationKey(), tileX, tileY, z), location);
        }
        if (o.getDatasetKey() != null) {
          context.write(new TileKeyWritable(TileContentType.DATASET, o.getDatasetKey(), tileX, tileY, z), location);
        }
        if (o.getCountryIsoCode() != null && o.getCountryIsoCode().length() == 2) {
          context.write(new TileKeyWritable(TileContentType.COUNTRY, o.getCountryIsoCode(), tileX, tileY, z), location);
        }
      }
    }
  }

  @Override
  protected void setup(Context context) throws IOException, InterruptedException {
    numberZooms = context.getConfiguration().getInt(Backfill.KEY_NUM_ZOOMS, Backfill.DEFAULT_NUM_ZOOMS);
  }
}
