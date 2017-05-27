package org.uant.textservice;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.MockMessageGenerator;

import junit.framework.TestCase;
import junit.framework.TestSuite;
import javax.mail.AuthenticationFailedException;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for MockMessageGenerator
 */
public class MockMessageGeneratorTest extends TestCase {
    private ReceivedMessageHandler msgGetter;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MockMessageGeneratorTest( String testName ) {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     public static Test suite() {
     return new TestSuite( AppTest.class );
     }
     */

    @Before
    public void setUp() {
        msgGetter = new MockMessageGenerator();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testMessageGetter() {

        ReceivedMessage msg = msgGetter.getMessage();
        assertNotNull(msg.sender);
        assertNotNull(msg.body);
        assertNotNull(msg.timestamp);
        System.out.println("msg.sender " + msg.sender );
        System.out.println("msg.body " + msg.body);
        System.out.println("msg.ts " + msg.timestamp);
    }

}
