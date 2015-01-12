package com.aerospike.examples;

import redis.clients.jedis.Jedis;

public class JedisExample {

	public static void main(String[] args) {
		Jedis jedis = new Jedis("localhost");
		jedis.set("foo", "bar");
		String value = jedis.get("foo");
		jedis.rpush("list", "element");
		int length = jedis.llen("list").intValue();
		String element = jedis.lpop("list");

		/*Excerpt From: Tiago Macedo and Fred Oliveira. “Redis Cookbook.” */
		}

}
