/* 
 * Copyright 2012-2015 Aerospike, Inc.
 *
 * Portions may be licensed to Aerospike, Inc. under one or more contributor
 * license agreements.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.aerospike.jedis;

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