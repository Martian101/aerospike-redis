package com.aerospike.examples;

import com.aerospike.client.AerospikeClient;
import com.aerospike.jedis.RedisClient;

public class AerospikeExample {

	public static void main(String[] args) {
		// Create an Aerospike client
		AerospikeClient asClient = new AerospikeClient("127.0.0.1", 3000);
		
		// Create a Jedis client using the Aerospike client with a namespace and set
		RedisClient jedis = new RedisClient(asClient, "test", "redisSet");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		jedis.rpush("list", "element");
		int length = jedis.llen("list").intValue();
		String element = jedis.lpop("list");

		/*Excerpt From: Tiago Macedo and Fred Oliveira. “Redis Cookbook.” */
	}

}
