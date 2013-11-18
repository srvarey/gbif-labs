package org.gbif.biovel.locality.ws;

import org.gbif.biovel.locality.model.Location;
import org.gbif.biovel.locality.persistence.LocationMapper;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.common.base.Preconditions;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Path("locality")
@Singleton
public class LocalityResource {

  private final LocationMapper locationMapper;

  @Inject
  public LocalityResource(LocationMapper locationMapper) {
    this.locationMapper = locationMapper;
  }

  /**
   * Returns the list of locations where the recordedBy is present.
   * 
   * @param recordedBy The scopes the list of locations (Mandatory)
   * @return The list ordered by locality, or an empty list
   */
  @GET
  public List<Location> listByRecordedBy(@QueryParam("recordedBy") String recordedBy) {
    Preconditions.checkNotNull(recordedBy, "recordedBy is a mandatory parameter");
    return locationMapper.listLocations(recordedBy);
  }
}
