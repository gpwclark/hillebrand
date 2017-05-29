package org.uant.textservice;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.MockMessageGenerator;
import org.uant.textservice.db.ResourceDriver;
import org.uant.textservice.db.ResourceDb;
import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.db.TestEmailGenerator;
import org.uant.textservice.logic.ProcessResponse;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDBO;

import org.uant.textservice.mockData.getDDL;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.uant.textservice.communication.MessageProcessor;

import java.sql.*;
import javax.sql.DataSource;

import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ProcessResponseTest extends TestCase {
    private ReceivedMessageHandler msgGetter;
    private MessageDriver msgDb;
    private ResourceDriver resourceDb;
    private TestEmailGenerator testEmailGen;
    private Connection conn;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ProcessResponseTest ( String testName ) {
        super( testName );
    }

    @Before
    public void setUp() {
        msgGetter = new MockMessageGenerator();
        testEmailGen = new TestEmailGenerator();
        DataSource ds = DataSourceFactory.getMySQLDataSource();
        try (
                Connection conn = ds.getConnection();
            ){
            String resources = getDDL.get("test_ddls/resources.ddl");
            String messages = getDDL.get("test_ddls/data_in_test.ddl");
            conn.createStatement().executeUpdate(resources);
            conn.createStatement().executeUpdate(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resourceDb = new ResourceDb(ds);
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
    public void testProcessResponse() {
        String validQuery = "aljdslskjdlkjldsjlsaSTATUSlkajaldskj";
        String validQuery1 = "aljdslskjdlkjldsjlsastatusssslkajaldskj";
        String invalidQuery = "lakjdlksjdlksajdlanlkjwna;oiuw;nwnwwlkja;s?><M";
        BlockingQueue<Integer> processMsgPipe = new LinkedBlockingQueue<Integer>();
        BlockingQueue<Integer> sendMsgPipe = new LinkedBlockingQueue<Integer>();

        MessageProcessor mp = new MessageProcessor(processMsgPipe, sendMsgPipe);

        //valid query and resource
        final String sender = testEmailGen.getRandomTestEmail();
        final String body = validQuery;

        ReceivedMessage msg = msgGetter.createMessage(sender, body);
        msgDb.insertMessage(msg);

        //MessageDBO record = pr.processMessageResponse(msg.hash);

        mp.processMessage(msg.hash);
        MessageDBO record = msgDb.getMessage(msg.hash);

        assertEquals("all orders shipped", record.getResponse());
        assertEquals(true, record.getValidResource());
        assertEquals(true, record.getValidRequest());
        assertEquals(true, record.getProcessed());
        assertEquals(false, record.getSent());

        //invalid query and valid resource
        final String sender1 = testEmailGen.getRandomTestEmail();
        final String body1 = invalidQuery;

        ReceivedMessage msg1 = msgGetter.createMessage(sender1, body1);
        msgDb.insertMessage(msg1);

        //MessageDBO record1 = pr.processMessageResponse(msg1.hash);

        mp.processMessage(msg1.hash);
        MessageDBO record1 = msgDb.getMessage(msg1.hash);

        assertEquals("invalid query", record1.getResponse());
        assertEquals(true, record1.getValidResource());
        assertEquals(false, record1.getValidRequest());
        assertEquals(true, record.getProcessed());
        assertEquals(false, record.getSent());

        //valid query and invalid resource
        final String sender2 = "invalid@email.com";
        final String body2 = validQuery1;

        ReceivedMessage msg2 = msgGetter.createMessage(sender2, body2);
        msgDb.insertMessage(msg2);

        mp.processMessage(msg2.hash);
        MessageDBO record2 = msgDb.getMessage(msg2.hash);

        assertEquals("invalid resource", record2.getResponse());
        assertEquals(false, record2.getValidResource());
        assertEquals(true, record2.getValidRequest());
        assertEquals(true, record2.getProcessed());
        assertEquals(false, record2.getSent());
    }
}
