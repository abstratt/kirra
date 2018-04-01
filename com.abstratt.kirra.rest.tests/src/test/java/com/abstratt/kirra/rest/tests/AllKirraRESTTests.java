package com.abstratt.kirra.rest.tests;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllKirraRESTTests {
    public static Test suite() {
        TestSuite suite = new TestSuite(AllKirraRESTTests.class.getName());
        suite.addTest(new TestSuite(SchemaTests.class));
        suite.addTest(new TestSuite(InstanceTests.class));
        suite.addTest(new TestSuite(AccessTests.class));
        return suite;
    }

}
