package org.uant.textservice;

import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.communication.MessageReceiver;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.mockObjects.MockMessageGenerator;
import org.uant.textservice.data.TestEmailGenerator;
import java.util.UUID;

import java.sql.*;
import javax.sql.DataSource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for MessageDb
 */
public class MessageDbTest extends TestCase {
    private ReceivedMessageHandler msgGetter;
    private MessageDriver msgDb;
    private TestEmailGenerator testEmailGen;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MessageDbTest( String testName ) {
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
        msgDb = new MessageDb(ds);
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
    public void testmsgDb() {

        BlockingQueue<Integer> processMsgPipe = new LinkedBlockingQueue<Integer>();
        MessageReceiver mr = new MessageReceiver(msgGetter, processMsgPipe);

        ArrayList<Integer> msgHashes = mr.receiveMessages();

        //msgDb.insertMessage(msg);
        MessageDBO record = msgDb.getMessage(msgHashes.get(0));

        //assertEquals(msg.sender, record.getSender());
        //assertEquals(msg.body, record.getBody());
        //assertEquals(msg.hash, record.getHash());
        assertEquals(null, record.getResponse());
        assertEquals(false, record.getValidResource());
        assertEquals(false, record.getValidRequest());
        assertEquals(false, record.getSent());
        assertEquals(false, record.getProcessed());
        //assertEquals(msg.timestamp, record.getTimestamp());
    }
}
