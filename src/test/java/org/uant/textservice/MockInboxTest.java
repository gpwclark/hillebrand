package org.uant.textservice;

import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MockInbox;
import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.MockMessageGenerator;
import org.uant.textservice.db.TestEmailGenerator;
import org.uant.textservice.db.DataSourceFactory;
import java.util.UUID;

import java.sql.*;
import javax.sql.DataSource;

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
import org.uant.textservice.mockData.getDDL;
import org.uant.textservice.db.MessageDBO;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for MockInbox
 */
public class MockInboxTest extends TestCase {
    private ReceivedMessageHandler msgGetter;
    private InboxHandler inbox;
    private TestEmailGenerator testEmailGen;

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
        msgGetter = new MockMessageGenerator();
        testEmailGen = new TestEmailGenerator();
        DataSource ds = DataSourceFactory.getMySQLDataSource();
        try (
                Connection conn = ds.getConnection();
            ){
            String messages = getDDL.get("test_ddls/data_in_test.ddl");
            conn.createStatement().executeUpdate(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        inbox = new MockInbox(ds);
    }

    @After
    public void tearDown() {
        DataSource ds = DataSourceFactory.getMySQLDataSource();
        try (
                Connection conn = ds.getConnection();
            ){
            conn.createStatement().execute("DROP TABLE IF EXISTS MESSAGES;");
            conn.createStatement().execute("DROP TABLE IF EXISTS RESOURCES;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testInbox() {

        final String sender = testEmailGen.getRandomTestEmail();
        final String body = UUID.randomUUID().toString();

        ReceivedMessage msg = msgGetter.createMessage(sender, body);

        inbox.insertMessage(msg);
        MessageDBO record = inbox.getMessage(msg.hash);

        assertEquals(msg.sender, record.getSender());
        assertEquals(msg.body, record.getBody());
        assertEquals(msg.hash, record.getHash());
        assertEquals(null, record.getResponse());
        assertEquals(false, record.getValidResource());
        assertEquals(false, record.getValidRequest());
        assertEquals(false, record.getSent());
        assertEquals(false, record.getProcessed());
        assertEquals(msg.timestamp, record.getTimestamp());
    }
}
