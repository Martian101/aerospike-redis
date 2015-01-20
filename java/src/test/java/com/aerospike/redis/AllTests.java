package com.aerospike.redis;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ StringValuesCommandsTest.class, HashesCommandsTest.class,
		ListCommandsTest.class  })
public class AllTests {


}
