package com.aerospike.redis;

import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.aerospike.client.AerospikeClient;

public class RedisClientTest {

	AerospikeClient client;
	RedisClient subject;

	@Before
	public void setUp() throws Exception {
		client = new AerospikeClient("localhost", 3000);
		subject = new RedisClient(client, "test");
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}

	@Test
	public void testRedisClient() throws Exception {
		// TODO
	}

}
