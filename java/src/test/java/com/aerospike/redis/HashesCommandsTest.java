package com.aerospike.redis;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.ws.Response;

import org.junit.Before;
import org.junit.Test;

import com.aerospike.client.AerospikeClient;
import com.aerospike.jedis.RedisClient;

public class HashesCommandsTest  {
	final byte[] bfoo = { 0x01, 0x02, 0x03, 0x04 };
	final byte[] bbar = { 0x05, 0x06, 0x07, 0x08 };
	final byte[] bcar = { 0x09, 0x0A, 0x0B, 0x0C };

	final byte[] bbar1 = { 0x05, 0x06, 0x07, 0x08, 0x0A };
	final byte[] bbar2 = { 0x05, 0x06, 0x07, 0x08, 0x0B };
	final byte[] bbar3 = { 0x05, 0x06, 0x07, 0x08, 0x0C };
	final byte[] bbarstar = { 0x05, 0x06, 0x07, 0x08, '*' };

	AerospikeClient asClient = new AerospikeClient("127.0.0.1", 3000);
	RedisClient jedis = new RedisClient(asClient, "test", "redisSet");

	@Test
	public void hset() {
		long status = jedis.hset("foo", "bar", "car");
		assertEquals(1, status);
		status = jedis.hset("foo", "bar", "foo");
		assertEquals(0, status);


	}

	@Test
	public void hget() {
		jedis.hset("foo", "bar", "car");
		assertEquals(null, jedis.hget("bar", "foo"));
		assertEquals(null, jedis.hget("foo", "car"));
		assertEquals("car", jedis.hget("foo", "bar"));
	}

	@Test
	public void hsetnx() {
		long status = jedis.hsetnx("foo", "bar", "car");
		assertEquals(1, status);
		assertEquals("car", jedis.hget("foo", "bar"));

		status = jedis.hsetnx("foo", "bar", "foo");
		assertEquals(0, status);
		assertEquals("car", jedis.hget("foo", "bar"));

		status = jedis.hsetnx("foo", "car", "bar");
		assertEquals(1, status);
		assertEquals("bar", jedis.hget("foo", "car"));


	}

	@Test
	public void hmset() {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		String status = jedis.hmset("foo", hash);
		assertEquals("OK", status);
		assertEquals("car", jedis.hget("foo", "bar"));
		assertEquals("bar", jedis.hget("foo", "car"));


	}

	@Test
	public void hmget() {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		jedis.hmset("foo", hash);

		List<String> values = jedis.hmget("foo", "bar", "car", "foo");
		List<String> expected = new ArrayList<String>();
		expected.add("car");
		expected.add("bar");
		expected.add(null);

		assertEquals(expected, values);

	}

	@Test
	public void hincrBy() {
		long value = jedis.hincrBy("foo", "bar", 1);
		assertEquals(1, value);
		value = jedis.hincrBy("foo", "bar", -1);
		assertEquals(0, value);
		value = jedis.hincrBy("foo", "bar", -10);
		assertEquals(-10, value);

	}

	@Test
	public void hincrByFloat() {
		Double value = jedis.hincrByFloat("foo", "bar", 1.5d);
		assertEquals((Double) 1.5d, value);
		value = jedis.hincrByFloat("foo", "bar", -1.5d);
		assertEquals((Double) 0d, value);
		value = jedis.hincrByFloat("foo", "bar", -10.7d);
		assertEquals(Double.compare(-10.7d, value), 0);


	}

	@Test
	public void hexists() {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		jedis.hmset("foo", hash);

		assertFalse(jedis.hexists("bar", "foo"));
		assertFalse(jedis.hexists("foo", "foo"));
		assertTrue(jedis.hexists("foo", "bar"));


	}

	@Test
	public void hdel() {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		jedis.hmset("foo", hash);

		assertEquals(0, jedis.hdel("bar", "foo").intValue());
		assertEquals(0, jedis.hdel("foo", "foo").intValue());
		assertEquals(1, jedis.hdel("foo", "bar").intValue());
		assertEquals(null, jedis.hget("foo", "bar"));


	}

	@Test
	public void hlen() {
		Map<String, String> hash = new HashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		jedis.hmset("foo", hash);

		assertEquals(0, jedis.hlen("bar").intValue());
		assertEquals(2, jedis.hlen("foo").intValue());


	}

	@Test
	public void hkeys() {
		Map<String, String> hash = new LinkedHashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		jedis.hmset("foo", hash);

		Set<String> keys = jedis.hkeys("foo");
		Set<String> expected = new LinkedHashSet<String>();
		expected.add("bar");
		expected.add("car");
		assertEquals(expected, keys);
	}

	@Test
	public void hvals() {
		Map<String, String> hash = new LinkedHashMap<String, String>();
		hash.put("bar", "car");
		hash.put("car", "bar");
		jedis.hmset("foo", hash);

		List<String> vals = jedis.hvals("foo");
		assertEquals(2, vals.size());
		assertTrue(vals.contains("bar"));
		assertTrue(vals.contains("car"));

	}

	@Test
	public void hgetAll() {
		Map<String, String> h = new HashMap<String, String>();
		h.put("bar", "car");
		h.put("car", "bar");
		jedis.hmset("foo", h);

		Map<String, String> hash = jedis.hgetAll("foo");
		assertEquals(2, hash.size());
		assertEquals("car", hash.get("bar"));
		assertEquals("bar", hash.get("car"));

	}

	//    @Test
	//    public void hgetAllPipeline() {
	//	Map<byte[], byte[]> bh = new HashMap<byte[], byte[]>();
	//	bh.put(bbar, bcar);
	//	bh.put(bcar, bbar);
	//	jedis.hmset(bfoo, bh);
	//	Pipeline pipeline = jedis.pipelined();
	//	Response<Map<byte[], byte[]>> bhashResponse = pipeline.hgetAll(bfoo);
	//	pipeline.sync();
	//	Map<byte[], byte[]> bhash = bhashResponse.get();
	//
	//	assertEquals(2, bhash.size());
	//	assertArrayEquals(bcar, bhash.get(bbar));
	//	assertArrayEquals(bbar, bhash.get(bcar));
	//    }
	//
	//    @Test
	//    public void hscan() {
	//	jedis.hset("foo", "b", "b");
	//	jedis.hset("foo", "a", "a");
	//
	//	ScanResult<Map.Entry<String, String>> result = jedis.hscan("foo",
	//		SCAN_POINTER_START);
	//
	//	assertEquals(SCAN_POINTER_START, result.getCursor());
	//	assertFalse(result.getResult().isEmpty());
	//
	//	// binary
	//	jedis.hset(bfoo, bbar, bcar);
	//
	//	ScanResult<Map.Entry<byte[], byte[]>> bResult = jedis.hscan(bfoo,
	//		SCAN_POINTER_START_BINARY);
	//
	//	assertArrayEquals(SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
	//	assertFalse(bResult.getResult().isEmpty());
	//    }

	//    @Test
	//    public void hscanMatch() {
	//	ScanParams params = new ScanParams();
	//	params.match("a*");
	//
	//	jedis.hset("foo", "b", "b");
	//	jedis.hset("foo", "a", "a");
	//	jedis.hset("foo", "aa", "aa");
	//	ScanResult<Map.Entry<String, String>> result = jedis.hscan("foo",
	//		SCAN_POINTER_START, params);
	//
	//	assertEquals(SCAN_POINTER_START, result.getCursor());
	//	assertFalse(result.getResult().isEmpty());
	//
	//	// binary
	//	params = new ScanParams();
	//	params.match(bbarstar);
	//
	//	jedis.hset(bfoo, bbar, bcar);
	//	jedis.hset(bfoo, bbar1, bcar);
	//	jedis.hset(bfoo, bbar2, bcar);
	//	jedis.hset(bfoo, bbar3, bcar);
	//
	//	ScanResult<Map.Entry<byte[], byte[]>> bResult = jedis.hscan(bfoo,
	//		SCAN_POINTER_START_BINARY, params);
	//
	//	assertArrayEquals(SCAN_POINTER_START_BINARY, bResult.getCursorAsBytes());
	//	assertFalse(bResult.getResult().isEmpty());
	//    }

	//    @Test
	//    public void hscanCount() {
	//	ScanParams params = new ScanParams();
	//	params.count(2);
	//
	//	for (int i = 0; i < 10; i++) {
	//	    jedis.hset("foo", "a" + i, "a" + i);
	//	}
	//
	//	ScanResult<Map.Entry<String, String>> result = jedis.hscan("foo",
	//		SCAN_POINTER_START, params);
	//
	//	assertFalse(result.getResult().isEmpty());
	//
	//	// binary
	//	params = new ScanParams();
	//	params.count(2);
	//
	//	jedis.hset(bfoo, bbar, bcar);
	//	jedis.hset(bfoo, bbar1, bcar);
	//	jedis.hset(bfoo, bbar2, bcar);
	//	jedis.hset(bfoo, bbar3, bcar);
	//
	//	ScanResult<Map.Entry<byte[], byte[]>> bResult = jedis.hscan(bfoo,
	//		SCAN_POINTER_START_BINARY, params);
	//
	//	assertFalse(bResult.getResult().isEmpty());
	//    }
	@Before
	public void reset(){
		jedis.del("foo");
		jedis.del("bar");
		jedis.del("s");
	}

}
