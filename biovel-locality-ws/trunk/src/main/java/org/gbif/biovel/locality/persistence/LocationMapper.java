package org.gbif.biovel.locality.persistence;

import org.gbif.biovel.locality.model.Location;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface LocationMapper {

  /**
   * @return A list of locations ordered by locality, that are recorded by the individual provided, or an empty list.
   */
  List<Location> listLocations(@Param("recordedBy") String recordedBy);
}
