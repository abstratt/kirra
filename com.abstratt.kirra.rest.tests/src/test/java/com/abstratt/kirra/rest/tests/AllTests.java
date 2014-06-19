package com.abstratt.kirra.rest.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
    public static Test suite() {
        TestSuite suite = new TestSuite(AllTests.class.getName());
        suite.addTest(new TestSuite(SchemaTests.class));
        suite.addTest(new TestSuite(InstanceTests.class));
        return suite;
    }

}
