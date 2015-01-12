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
