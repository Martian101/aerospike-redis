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

import com.aerospike.client.AerospikeClient;

public class SimpleExample {

	public static void main(String[] args) {
		// Create an Aerospike client
		AerospikeClient asClient = new AerospikeClient("127.0.0.1", 3000);
		
		// Create a Jedis client using the Aerospike client with a namespace and set
		RedisClient jedis = new RedisClient(asClient, "test", "redisSet");
		
		// KV write operations
		jedis.set("cat", "black cat");
		jedis.set("dog", "brown dog");
		jedis.set("bird", "white bird");
		
		// Database size
		long size = jedis.dbSize();
		System.out.println(String.format("Database size: %d", size));
		
		// KV read operation
		String value = jedis.get("cat");
		System.out.println("Value: " + value);
		
	}

}
