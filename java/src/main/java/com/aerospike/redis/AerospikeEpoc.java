package com.aerospike.redis;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * DateDiff -- compute the difference between two dates.
 */
public class AerospikeEpoc {
  public static void main(String[] av) {
    /** The date at the end of the last century */
    Date aerospikeEpoc = new GregorianCalendar(2010, 01, 01, 0, 0).getTime();
    Date unixEpoc = new GregorianCalendar(1970, 01, 01, 0, 0).getTime();

    // Get msec from each, and subtract.
    long diff = aerospikeEpoc.getTime() - unixEpoc.getTime();

    System.out.println("The Aerospike Epoc in milliseconds: " + diff);
  }
}