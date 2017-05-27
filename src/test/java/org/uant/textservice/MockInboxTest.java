package org.uant.textservice;

import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MockInbox;
import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.MockMessageGenerator;

import java.util.Map;

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
 * Unit test for MockInbox
 */
public class MockInboxTest extends TestCase {
    private ReceivedMessageHandler msgGetter;
    private InboxHandler inbox;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MockInboxTest( String testName ) {
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
        inbox = new MockInbox();
        msgGetter = new MockMessageGenerator();
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testInbox() {

        ReceivedMessage msg = msgGetter.getMessage();
        inbox.storeMessage(msg);
        Map record = inbox.getMessage(msg.hash);

        assertEquals(msg.sender, record.get("sender"));
        assertEquals(msg.body, record.get("body"));
        assertEquals("false", record.get("processed"));
        assertEquals(Long.toString(msg.timestamp), record.get("timestamp"));
    }

}
