# Redis implemented with Aerospike
In migrating any application from one database to another there are a set of risks and rewards. Migrating from Redis to Aerospike the rewards are based around scalability, reliability and ease of administration (your DevOps team will not abandon ship). The risk is that you do not have the semantic equivalent operations available to your developers.
 
This project is an example of how to obtain the semantic equivalent of Redis operations, on simple types, from Aerospike.
 
## Redis
Redis is a very fast, in memory database, it is dead easy to use and is very popular with developers. It is a essentially a great big hashtable, map or dictionary, where a Value is stored and referenced by a Key. The value can be something as simple as a String, a number or Byte array, or can be more complex like a Hashtable/Map/Dictionary, List, Queue or Set (Sets can be quite exotic).
 
In order to scale Redis across multiple computers in a cluster, the key space needs to be Sharded, essentially chopped up into partitions and distributed across the computers in the cluster. This process involves cunning algorithms and sleepless nights, it is fraught with danger and the risk of going bald or being divorced is high. Is not for an undertaking for the novice.
 
Another side effect of Sharding and clustering Redis is the cost. Redis is in-memory, read this as in RAM. So if you have a 5TB database and you want redundancy x2 you need 10TB of RAM in your cluster - this is expensive.
 
## Aerospike
Aerospike takes all of the pain away from scaling to multiple computers in a cluster, in fact it scales almost linearly as you add new nodes to the cluster. The cluster automatically rebalances by migrating data from one node to another, and this all happens without any loss of production. A Smart Client takes care of all of the Sharding and data partitioning and is a first class observer of the cluster. So if the cluster changes the Smart Client ensures that requests are directed to the correct node and there is no loss of service.
 
Aerospike also uses Flash storage in a unique way to produce RAM like speed with the enormous cost saving of Flash.
 
So if you have an application that uses Redis key-value operations, and you want to scale it (and keep your hair and/or marriage), this paper will show you how to put a very simple Redis veneer on to Aerospike.
 
## Redis veneer – Why?
There are several ways to refactor an application from one database technology to another. The most expensive way is to recode each database call in the new technology API. Arguably you will get a seamless result and possible a modicum of better performance, but it will cost you time and money (wait... time is money). There is a bigger “wound” in the application code and it takes time and effort for that wound to heal.
 
Using a veneer requires a small wound in the code base. Essentially a change to the includes/imports and a change in a type declaration. The rest of the code remains the same.

Building a Redis client with Aerospike as the implementation is one of the easiest ways to migrate from Redis to Aerospike. The client should:

1. Implement an existing interface, such as Jedis (Java), Predis (PHP), node_redis (node.js), Radix (Go), etc.
2. Use an active Aerospike client or optionally create one using
Host address(es) and Port - One or more nodes in the Aerospike cluster
[Namespace, Set and Bin](https://www.aerospike.com/docs/architecture/data-model.html) to contain the Redis value
3. Methods/Functions should have identical signatures as Redis libraries
4. The implemented Aerospike client should pass the same unit tests of the original Redis client.

Lets look at each one of these points:

### 1. Implement and existing interface
Whenever changes are made to an application, a "wound" is made in the code. The wound is "healed" by standard software engineering techniques. This always costs time and money.

The smallest wound in the application code is achieved by implementing an existing interface allows the Aerospike implementation to be "dropped" into an existing application.

Changes to the existing application should be minimal and not much more than simple changes to import/include/require statements, and the creation of the new client instance.

### 2. Use Aerospike Client
Aerospike client instances are thread safe and handle all of the complexity of server connections, worker thread pooling and result buffering. Essentially, all of the hard work is done for you and, to an application program, the Aerospike client simply appears as an API library. All of the complexity of clustering and monitoring is elegantly manager and hidden from the application developer.

In the majority of cases, only 1 Aerospike client instance is needed per process. While your application may have many facets and use cases, it can share an Aerospike client instance for all these.

Redis is a key-value store that is like a large Map or Dictionary in that is stores a single value for a key. The value can be something simple like a String or Integer, or something more complex like a List or Map, but in the end for each Key there is a single value. Keys in Redis are stored and can be queried using pattern matching.

Aerospike is a key-value store where a [Record](https://www.aerospike.com/docs/architecture/data-model.html) is stored as the value associated with the key. A Record consists of one, or more, Bins. A [Bin](https://www.aerospike.com/docs/architecture/data-model.html) is like a field or column and has a name and a type.

Each Key, in Aerospike, also includes a [Namespace](https://www.aerospike.com/docs/architecture/data-model.html) which is a storage and policy definition, a [Set](https://www.aerospike.com/docs/architecture/data-model.html) which is like a Table.

In the reference implementation you can see that an Aerospike client instance is embedded in the Redis client, along with the Namespace and Set to be used as the Redis replacement.

### 3. Identical Signatures
Using identical signatures in the Aerospike implementation of a Redis client will ensure that application will only require the smallest number of code changes.

The side advantage of this is that the Developers of the application will also have an easy migration from Redis to Aerospike because the API is familiar.

### 4. Pass existing unit tests
Aerospike and Redis are both rich in capabilities. The Aerospike implementation of Redis client should be the semantic equivalent of the existing client. That is, it should give the same result.

One way to ensure this it to run the unit tests for the Redis client against the Aerospike implementation. For example, the Jedis unit tests against an Aerospike implementation of Jedis.

## Implementing Jedis with Aerospike 
We have chosen to use Java and Jedis as our example. Why Jedis? Jedis is widely used, stable and fully featured Redis Client. We will only cover Key-value, List and Hash commands.  We will not include Set, SortedSet, Queues or PubSub.

To implement a drop in replacement for Jedis we will create a AerospikeRedis class that has the same method signatures as Jedis and we will test it with the Jedis unit tests to ensure it is the semantic equivalent. 

The example code is available on GitHub at https://github.com/helipilot50/aerospike-redis.git

# How to build

Maven is used to build this example. Change directory to the `java` subdirectory then Use the following command:
```bash
mvn clean package
```
A JAR will be produced in the `target` subdirectory `aerospike-jedis-1.0.0-full.jar`

Example code is found in the `examples` subdirectory

## Simple Example
This is a small example of a Java application that uses the Jedis client
```java
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
          
                  /* Excerpt From: Tiago Macedo and Fred Oliveira. "Redis Cookbook." */
                  }
          
          }
```
Here is the same application that uses an Aerospike implementation of the Jedis client.
```java
package com.aerospike.examples;
          
          import com.aerospike.client.AerospikeClient;
          import com.aerospike.jedis.RedisClient;
          
          public class AerospikeExample {
          
              public static void main(String[] args) {
                  // Create an Aerospike client
                  AerospikeClient asClient = new AerospikeClient("localhost", 3000);
          
                  // Create a Jedis client using the Aerospike client with a namespace and set
                  RedisClient jedis = new RedisClient(asClient, "test", "redisSet");
                  jedis.set("foo", "bar");
                  String value = jedis.get("foo");
                  jedis.rpush("list", "element");
                  int length = jedis.llen("list").intValue();
                  String element = jedis.lpop("list");
          
                  /*Excerpt From: Tiago Macedo and Fred Oliveira. "Redis Cookbook." */
              }
          
          }
```
Note that in the Aerospike example, a standard Aerospike client is created and passed into the Constructor of the `RedisClient` along with the Namespace `test` and the Set `redisSet`. All the other code remains the same.

## Operations
The example implementation code is available on GitHub at https://github.com/helipilot50/aerospike-redis.git. This is example code only, and has not been subject to production strength testing, so no whining.

### Key-value operations

Most of the Key-value operations are implemented directly by corresponding Aerospike operations and WritePolicy settings. By default Aerospike with create or update a value during a write operation. A WritePolicy modifies the default behaviour of a write operation.

EX postfix on Redis  commands specify that the command will also set an expiration on the value. To implement this in Aerospike we set the expiry value in the WritePolicy to be the time to live in seconds .

NX postfix on Redis commands indicate that the write will only be successful if the value does not exist. To implement this in Aerospike we set the recordExistsAction value in the WritePolicy to be RcordExistsAction.CREATE_ONLY. The write will fail if the value alread exists.

The results of each Aerospike API call are processed to match the expected return types of Jedis.

Here are examples of the implementation code for SET, SETEX and SETNX

SET 
```java
public String set(Object key, Object value){
    return set(null, key, value);
}
          
public String set(WritePolicy wp, Object key, Object value){
    Key asKey = new Key(this.namespace, this.redisSet, Value.get(key));
    Bin keyBin = new Bin(this.keyBin , key);
    Bin valueBin = new Bin(this.redisBin, Value.get(value));
    this.asClient.put((wp == null) ? this.writePolicy : wp, asKey, keyBin, valueBin);
    return "OK";    
}
```
The set() method calls the Aerospike put() method with a null, or default, WritePolicy. The method is overloaded to ensure Jecis compatibility and to provide code reuse.

SETEX
```java
public String setex(String key, int expiration, String value) {
    WritePolicy wp = new WritePolicy();
    wp.expiration = expiration;
    set(wp, key, Value.get(value));
    return "OK";
}
```
The setex() method creates a WritePolicy with an expiration value, and then calls the set() method, passing in the WritePolicy.

SETNX
```java
public long setnx(Object key, Object value) {
    try {
        WritePolicy wp = new WritePolicy();
        wp.recordExistsAction = RecordExistsAction.CREATE_ONLY;
        set(wp, key, value);
        return 1;
    } catch (AerospikeException e){
        if (e.getResultCode() == ResultCode.KEY_EXISTS_ERROR)
            return 0;
        else
            throw e;
        }
}
```
The setnx() method creates a WritePolicy with a RecordExistsAction of CREATE_ONLY. This specifies that the write will only be successful if the records does not exist. The set() method is called with the WritePolicy and the output is processed to to ensure the same semantics as Jedis. 

Another set of interesting Redis command are INCR, INCRBY, DECR and DECRBY. These increment and decrement counters atomically.

In Aerospike they are all implemented using the Operate operation. Operate allows multiple operations to be done on a single record atomically. The Write operations are performed first and the Read operations follow the write operations.

Here is how the INCRBY command is implemented:
```java
public long incrBy(Object key, long increment) {
    Key asKey = new Key(this.namespace, this.redisSet, Value.get(key));
    Bin keyBin = new Bin(this.keyBin , key);
    Bin addBin = new Bin(this.redisBin, Value.get(increment));
    Record record = this.asClient.operate(this.writePolicy, asKey, 
        Operation.put(keyBin), 
        Operation.add(addBin), 
        Operation.get(this.redisBin));
    return record.getInt(this.redisBin);
}
```
Note the Add operation increments the Bin by a value passed in (decrements use a negative number), this is followed by a Get operation to read the value and return it.

#### Implemented Commands
This is a list of Key-value commands in the example implementation code available on GitHub at https://github.com/helipilot50/aerospike-redis.git.

Operation | Description
----------|------------
[SET](jedis/set.html) *key value* | Sets the *key* to hold the given *value*. Existing data is overwritten
[SETEX](jedis/setex.html) *key expiry value* | Sets the *key* to hold the given *value*. Existing data is overwritten, plus the value has an expiry time.
[SETNX](jedis/setnx.html) *key value* | Sets the *key* to hold the given *value*. It will only set the key if it does not already exist.
[MSET](jedis/mset.html) *key value, [...key value]* | Sets multiple *keys* to hold the given *values*. Existing data is overwritten
[GET](jedis/get.html) *key* | Returns the *value* referenced by the *key*.
[MGET](jedis/mget.html) *...keys* | Returns the values referenced by the *keys*.
[EXISTS](jedis/exists.html) *key* | Returns a boolean indicating the *key* exists.
[DEL](jedis/delete.html) *key* | Deletes the value referenced by the *key*.
[RENAME](jedis/rename.html) *oldKey newKey*| Rename a key
[PERSIST](jedis/persist.html) *key* | Remove the existing timeout on key
[INCR](jedis/incr.html) *key* | Increments an integer, referenced by the *key*, by 1. 
[INCRBY](jedis/incrby.html) *key value* | Increments an integer, referenced by the *key*, by *value*.
[DECR](jedis/decr.html) *key* | Decrements an integer, referenced by the *key*, by 1. 
[DECRBY](jedis/decrby.html) *key value* | Decrements an integer, referenced by the *key*, by *value*.
[EXPIRE](jedis/expire.html) *key expiration* | Sets an *expiration* time to live on a *key*.
[EXPIREAT](jedis/expireat.html) *key timestamp* | Performs the same operation as EXPIRE, except you specify a UNIX timestamp
[PEXPIRE](jedis/pexpire.html) *key expiration* | Sets an *expiration* time to live on a *key*.
[PEXPIREAT](jedis/pexpire.html) *key expiration* | Sets an *expiration* time to live on a *key*.
[TTL](jedis/ttl.html) *key* | Returns the remaining time to live of a *key*.
[PTTL](jedis/pttl.html) *key* | Returns the amount of remaining time in milliseconds.
[GETSET](jedis/getset.html) *key value* | Sets the string *value* of a *key* and return its old value.
[APPEND](jedis/append.html) *key value* | this command appends the *value* at the end of the string referenced by *key*.
[GETRANGE](jedis/getrange.html) *key start end* | Returns the substring of the string value stored at key, determined by the offsets *start* and *end*.
[STRLEN](jedis/strlen.html) *key start end* | Returns the length of the string value stored at key. An error is returned when key holds a non-string value.

### Hash operations
Aerospike supports the Bin (data) type of Map which is the equivalent of the Redis Hash type.

Hash operations are implemented in a User Defined Function module that is registered with the Aerospike cluster. The UDFs are written in Lua. UDFs are executed on the server where the record is located.

Each Redis command is implemented by a Java method invoking the UDF. Here is an example of HSET:
```Java
public long hset(String key, String field, String value) {
    Key asKey = new Key(this.namespace, this.redisSet, key);
    return (Long) this.asClient.execute(batchPolicy, asKey, 
        "redis", "HSET", 
        Value.get(this.redisBin), 
    Value.get(field), Value.get(value));
}
```
The hset() method calls the Aerospike execute() method to invoke the UDF on the server where the record is located.

This is the Lua code that implements the UDF
```Lua
function HSET(rec, bin, field, value)
   local created = 0
   if (EXISTS(rec, bin)) then
       created = 0
   else    
       created = 1
   end
   local m = rec[bin]
   if (m == nil) then
       m = map()
   end
   if (m[field] == nil) then
       created = 1
   end
    m[field] = value
    rec[bin] = m
    UPDATE(rec)
    return created
 end
```
All the hard work is tone in the UDF. The Map (hash) and value are created if the don’t exist and the record is updated.
Implemented Commands
This is a list of Hash commands in the example implementation code available on GitHub at https://github.com/helipilot50/aerospike-redis.git.

Operation   | Description
------------|------------
[HSET](jedis/hset.html) *hash-name key value* | Sets a *value* on a *hash* with the given *key*. 
[HSETNX](jedis/hsetnx.html) *hash-name key value* | Sets a *value* on a *hash* with the given *key*. 
[HMSET](jedis/hmset.html) *hash-name key1 value1 [key2 value2 ...]* | Allows you to set several values in a hash with a single command
[HGET](jedis/hget.html) *hash-name key* | Returns the *value* at *key* in the given *hash-name*.
[HMGET](jedis/hmget.html) *hash-name key* | Returns the *value* at *key* in the given *hash-name*.
[HINCRBY](jedis/hincrby.html) *hash-name key* | Increments the *value* at *key* in the given *hash-name*, and returns the new *value*.
[HEXISTS](jedis/hexists.html) *hash-name key* | Checks the existance the *value* at *key* in the given *hash-name*.
[HDEL](jedis/hdel.html) *hash-name key* | Deletes a key/value pair, referenced by *key* in the given *hash-name*.”
[HLEN](jedis/hlen.html) *hash-name* | Get the number of fields in a hash.
[HKEYS](jedis/hkeys.html) *hash-name* | Returns all keys in the given *hash_name*.
[HVALS](jedis/hvals.html) *hash-name* | Returns all the values in the given *hash_name*.
[HGETALL](jedis/hgetall.html) *hash-name* | Returns all the key/value pairs in the given *hash_name*.

### List operations
Aerospike supports the Bin (data) type of List which is the equivalent of the Redis list type.

Like Hash operations, List operations are implemented in a User Defined Function module  written in Lua. 

As with the Hash commands, List command is implemented by a Java method invoking the UDF. Here is an example of LSET:

Java
```java
public String lset(String key, int index, String value) {
    Key asKey = new Key(this.namespace, this.redisSet, key);
    return (String) this.asClient.execute(this.writePolicy, asKey, 
    "redis", "LSET", 
    Value.get(this.redisBin), Value.get(index), Value.get(value));
    }
}
```
The lset() method calls the Aerospike execute() method to invoke the UDF on the server where the record is located.

Lua
```lua
function LSET (rec, bin, index, value)
   if (EXISTS(rec, bin)) then
      local l = rec[bin]
      l[index] = value
      rec[bin] = l
      UPDATE(rec)
      return "OK"
   end
end
```
The UDF function LSET locates the correct bin and sets the value in the list.
#### Implemented Commands
This is a list of List commands in the example implementation code available on GitHub at https://github.com/helipilot50/aerospike-redis.git.

Operation   | Description
------------|------------
[RPUSH](jedis/rpush.html) *key value*| Append one or multiple *value* to a list, referenced by *key*.
[RPUSHX](jedis/rpushx.html) *key value* | Append a value to a list, referenced by *key*, only if the list exists.
[LPUSH](jedis/lpush.html) *key value* | Prepend one value to a list, referenced by *key*.
[LPUSHX](jedis/lpushx.html) *key value* | Prepend a value to a list, only if the list exists.
[LLEN](jedis/llen.html) *key* | Get the length of a list, referenced by *key*.
[LRANGE](jedis/lrange.html) *key start stop* | Get a range of elements from a list, referenced by *key*.
[LTRIM](jedis/ltrim.html) *key start stop* | Trim a list to the specified range, referenced by *key*.
[LSET](jedis/lset.html) *key index value* | Set the value of an element in a list by its index, referenced by *key*.
[LINDEX](jedis/lindex.html) *key index* | Get an element from a list by its index, referenced by *key*.
[LREM](jedis/lrem.html) *key count value* | Remove elements from a list, referenced by *key*.
[LPOP](jedis/lpop.html) *key* | Remove and get the first element in a list, referenced by *key*.
[RPOP](jedis/rpop.html) *key* | Remove and get the last element in a list, referenced by *key*.
[RPOPLPUSH](jedis/rpoplpush.html) *source destination* | Remove the last element in a list, append it to another list and return it, referenced by *key*.
[LINSERT](jedis/linsert.html) *key BEFORE or AFTER pivot value* | Insert an element before or after another element in a list, referenced by *key*.

### DB operations
These are a few Redis utility commands that we implemented to enable the unit tests to pass with the correct semantics.

Lets look at DBSIZE as an example
```java
public long dbSize() {
    Pattern pattern = Pattern.compile("ns_name=" + this.namespace + ":set_name=" 
        + this.redisSet + ":n_objects=(\\d+)");
    String[] infoStrings = infoAll(this.infoPolicy, this.asClient, "sets");
    long size = 0;
    for (String info : infoStrings){
        Matcher matcher = pattern.matcher(info);
        while (matcher.find()){
        size += Long.parseLong(matcher.group(1));
        }
    }
    return size;
}
```
The code to implement the DBSIZE operation makes an Info call to each node in the cluster to get the number of objects (records) in the redisSet. The n_objects from each node is summed to produce the size of the "Redis" database.

#### Implemented Commands
This is a list of database commands in the example implementation code available on GitHub at https://github.com/helipilot50/aerospike-redis.git.
 
Operation   | Description
------------|------------
[DBSIZE](jedis/dbsize.html) | Return the number of keys in the selected database (cluster).
[PING](jedis/ping.html) | Pings the server (cluster).
[ECHO](jedis/echo.html) *message* | Echoeßs the *message* to the client console.

 
##Conclusion
Its is fairly easy to implement Redis functionality in Aerospike. What you get is a redis solution that will scale and reliable without any heavy lifting on your part, all the hard work is done by Aerospike.

Follow the principles of:

1. Implement an existing interface
2. Use an active Aerospike client and supply a Namespace, Set and Bin to contain the Redis value
3. Methods/Functions should have identical signatures as Redis libraries
4. The implemented Aerospike client should pass the same unit tests of the original Redis client.




