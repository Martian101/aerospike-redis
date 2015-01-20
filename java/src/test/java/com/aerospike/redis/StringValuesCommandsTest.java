package com.aerospike.redis;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.aerospike.client.AerospikeClient;
import com.aerospike.client.AerospikeException;
import com.aerospike.jedis.RedisClient;

public class StringValuesCommandsTest  {

	AerospikeClient asClient = new AerospikeClient("127.0.0.1", 3000);
	RedisClient jedis = new RedisClient(asClient, "test", "redisSet");


	@Test
	public void setAndGet() {
		String status = jedis.set("foo", "bar");
		assertEquals("OK", status);

		String value = jedis.get("foo");
		assertEquals("bar", value);

		assertEquals(null, jedis.get("bar"));
	}

	@Test
	public void getSet() {
		Object value = jedis.getSet("foo", "bar");
		assertEquals(null, value);
		value = jedis.get("foo");
		assertEquals("bar", value);
	}

	@Test
	public void mget() {
		List<String> values = jedis.mget("foo", "bar");
		List<String> expected = new ArrayList<String>();
		expected.add(null);
		expected.add(null);

		assertEquals(expected, values);

		jedis.set("foo", "bar");

		expected = new ArrayList<String>();
		expected.add("bar");
		expected.add(null);
		values = jedis.mget("foo", "bar");

		assertEquals(expected, values);

		jedis.set("bar", "foo");

		expected = new ArrayList<String>();
		expected.add("bar");
		expected.add("foo");
		values = jedis.mget("foo", "bar");

		assertEquals(expected, values);
	}

	@Test
	public void setnx() {
		long status = jedis.setnx("foo", "bar");
		assertEquals(1, status);
		assertEquals("bar", jedis.get("foo"));

		status = jedis.setnx("foo", "bar2");
		assertEquals(0, status);
		assertEquals("bar", jedis.get("foo"));
	}

	@Test
	public void setex() {
		String status = jedis.setex("foo", 20, "bar");
		assertEquals("OK", status);
		long ttl = jedis.ttl("foo");
		assertTrue(ttl > 0 && ttl <= 20);
	}

	@Test
	public void mset() {
		String status = jedis.mset("foo", "bar", "bar", "foo");
		assertEquals("OK", status);
		assertEquals("bar", jedis.get("foo"));
		assertEquals("foo", jedis.get("bar"));
	}

	@Test
	public void msetnx() {
		long status = jedis.msetnx("foo", "bar", "bar", "foo");
		assertEquals(1, status);
		assertEquals("bar", jedis.get("foo"));
		assertEquals("foo", jedis.get("bar"));

		status = jedis.msetnx("foo", "bar1", "bar2", "foo2");
		assertEquals(0, status);
		assertEquals("bar", jedis.get("foo"));
		assertEquals("foo", jedis.get("bar"));
	}

	@Test(expected = AerospikeException.class)
	public void incrWrongValue() {
		jedis.set("foo", "bar");
		jedis.incr("foo");
	}

	@Test
	public void incr() {
		jedis.set("fooI", 0);
		long value = jedis.incr("fooI");
		assertEquals(1, value);
		value = jedis.incr("fooI");
		assertEquals(2, value);
	}

	@Test(expected = AerospikeException.class)
	public void incrByWrongValue() {
		jedis.set("foo", "bar");
		jedis.incrBy("foo", 2);
	}

	@Test
	public void incrBy() {
		jedis.set("fooBy", 0);
		long value = jedis.incrBy("fooBy", 2);
		assertEquals(2, value);
		value = jedis.incrBy("fooBy", 2);
		assertEquals(4, value);
	}

	@Test(expected = AerospikeException.class)
	public void incrByFloatWrongValue() {
		jedis.set("foo", "bar");
		jedis.incrByFloat("foo", 2d);
	}

	@Test(expected = AerospikeException.class)
	public void decrWrongValue() {
		jedis.set("foo", "bar");
		jedis.decr("foo");
	}

	@Test
	public void decr() {
		jedis.set("foo", 0);
		long value = jedis.decr("foo");
		assertEquals(-1, value);
		value = jedis.decr("foo");
		assertEquals(-2, value);
	}

	@Test(expected = AerospikeException.class)
	public void decrByWrongValue() {
		jedis.set("foo", "bar");
		jedis.decrBy("foo", 2);
	}

	@Test
	public void decrBy() {
		jedis.set("foo", 0);
		long value = jedis.decrBy("foo", 2);
		assertEquals(-2, value);
		value = jedis.decrBy("foo", 2);
		assertEquals(-4, value);
	}

	@Test
	public void append() {
		long value = jedis.append("foo", "bar");
		assertEquals(3, value);
		assertEquals("bar", jedis.get("foo"));
		value = jedis.append("foo", "bar");
		assertEquals(6, value);
		assertEquals("barbar", jedis.get("foo"));
	}

	@Test
	public void substr() {
		jedis.set("s", "This is a string");
		assertEquals("This", jedis.substr("s", 0, 3));
		assertEquals("ing", jedis.substr("s", -3, -1));
		assertEquals("This is a string", jedis.substr("s", 0, -1));
		assertEquals(" string", jedis.substr("s", 9, 100000));
	}

	@Test
	public void strlen() {
		jedis.set("s", "This is a string");
		assertEquals("This is a string".length(), jedis.strlen("s").intValue());
	}

	@Test
	public void incrLargeNumbers() {
		jedis.set("foo", 0);
		long value = jedis.incr("foo");
		assertEquals(1, value);
		assertEquals(1L + Integer.MAX_VALUE,
				(long) jedis.incrBy("foo", Integer.MAX_VALUE));
	}

	@Test(expected = AerospikeException.class)
	public void incrReallyLargeNumbers() {
		jedis.set("foo", Long.toString(Long.MAX_VALUE));
		long value = jedis.incr("foo");
		assertEquals(Long.MIN_VALUE, value);
	}

	@Test
	public void incrByFloat() {
		jedis.set("foo", 0.0);
		double value = jedis.incrByFloat("foo", 10.5);
		assertEquals(10.5, value, 0.0);
		value = jedis.incrByFloat("foo", 0.1);
		assertEquals(10.6, value, 0.0);
	}

	@Test
	public void psetex() {
		String status = jedis.psetex("foo", 20000, "bar");
		assertEquals("OK", status);
		long ttl = jedis.ttl("foo");
		assertTrue(ttl > 0 && ttl <= 20000);
	}
	@Before
	public void reset(){
		jedis.del("foo");
		jedis.del("bar");
		jedis.del("s");
	}
}