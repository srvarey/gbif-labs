package org.gbif.registry2.search.util;

import org.gbif.api.model.registry.temporal.DateRange;
import org.gbif.api.model.registry.temporal.SingleDate;
import org.gbif.api.model.registry.temporal.TemporalCoverage;

import java.util.Calendar;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * A utility to extract string "decades" (eg "1980", "1840") from TemporalCoverages.
 */
public class DecadeExtractor {

  private static Set<Integer> decadesFromInt(int start, int end) {
    Set<Integer> decades = Sets.newHashSet();
    // round to decade
    start = (start / 10) * 10;
    end = (end / 10) * 10;
    for (int year = start; year <= end; year += 10) {
      decades.add(year);
    }

    return decades;
  }

  /**
   * Produces a list of 4 digit decades with no duplicates, following normal ordering.
   * TODO: handle VerbatimTimePeriod?
   * 
   * @param temporalCoverages the various time periods
   * @return a list of 4 digit decades with no duplicates, ordered numerically
   */
  public static List<Integer> extractDecades(List<TemporalCoverage> temporalCoverages) {
    SortedSet<Integer> decades = new TreeSet<Integer>();
    if (temporalCoverages != null && !temporalCoverages.isEmpty()) {
      for (TemporalCoverage tc : temporalCoverages) {
        if (tc instanceof DateRange) {
          DateRange dr = (DateRange) tc;
          Calendar cal = Calendar.getInstance();
          if (dr.getStart() != null && dr.getEnd() != null) {
            cal.setTime(dr.getStart());
            int start = cal.get(Calendar.YEAR);
            cal.setTime(dr.getEnd());
            int end = cal.get(Calendar.YEAR);
            decades.addAll(decadesFromInt(start, end));
          }
        } else if (tc instanceof SingleDate) {
          SingleDate sd = (SingleDate) tc;
          if (sd.getDate() != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(sd.getDate());
            int year = cal.get(Calendar.YEAR);
            decades.addAll(decadesFromInt(year, year));
          }
        }
      }
    }

    List<Integer> returnList = Lists.newArrayList();
    returnList.addAll(decades);

    return returnList;
  }

  /**
   * Convenience method that wraps its argument and passes to the List<> version.
   * 
   * @param temporalCoverage the various time periods
   * @return a list of 4 digit decades with no duplicates, ordered numerically
   */
  public static List<Integer> extractDecades(TemporalCoverage temporalCoverage) {
    List<TemporalCoverage> tcList = Lists.newArrayList();
    tcList.add(temporalCoverage);

    return extractDecades(tcList);
  }
}
