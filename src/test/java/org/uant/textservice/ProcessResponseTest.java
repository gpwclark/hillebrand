package org.pcweavers.textservice;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.MockMessageGenerator;
import org.uant.textservice.db.ResourceDb;
import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MockInbox;
import org.uant.textservice.db.TestEmailGenerator;
import org.uant.textservice.logic.ProcessResponse;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.db.MessageDBO;

import org.uant.textservice.mockData.getDDL;

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
    private InboxHandler inbox;
    private ResourceDb resourceDb;
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
            String customers = getDDL.get("test_ddls/customer.ddl");
            String messages = getDDL.get("test_ddls/data_in_test.ddl");
            conn.createStatement().executeUpdate(customers);
            conn.createStatement().executeUpdate(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resourceDb = new ResourceDb(ds);
        inbox = new MockInbox(ds);
    }

    @After
    public void tearDown() {}

    @Test
    public void testProcessResponse() {
        String validQuery = "aljdslskjdlkjldsjlsaSTATUSlkajaldskj";
        String validQuery1 = "aljdslskjdlkjldsjlsastatusssslkajaldskj";
        String invalidQuery = "lakjdlksjdlksajdlanlkjwna;oiuw;nwnwwlkja;s?><M";

        ProcessResponse pr = new ProcessResponse(resourceDb, inbox);

        //valid query and customer
        final String sender = testEmailGen.getRandomTestEmail();
        final String body = validQuery;

        ReceivedMessage msg = msgGetter.createMessage(sender, body);

        inbox.insertMessage(msg);
        MessageDBO record = pr.processMessageResponse(msg.hash);

        assertEquals("all orders shipped", record.getResponse());
        assertEquals("true", record.getValidResource());
        assertEquals("true", record.getValidRequest());

        inbox.insertMessage(record);

        record = inbox.getMessage(msg.hash);
        assertEquals("all orders shipped", record.getResponse());
        assertEquals("true", record.getValidResource());
        assertEquals("true", record.getValidRequest());

        //invalid query and valid customer
        final String sender1 = testEmailGen.getRandomTestEmail();
        final String body1 = invalidQuery;

        ReceivedMessage msg1 = msgGetter.createMessage(sender1, body1);

        inbox.insertMessage(msg1);
        MessageDBO record1 = pr.processMessageResponse(msg1.hash);

        assertEquals("invalid query", record1.getResponse());
        assertEquals("true", record1.getValidResource());
        assertEquals("false", record1.getValidRequest());

        inbox.insertMessage(record1);

        record1 = inbox.getMessage(msg1.hash);
        assertEquals("invalid query", record1.getResponse());
        assertEquals("true", record1.getValidResource());
        assertEquals("false", record1.getValidRequest());
        //valid query and invalid customer
        final String sender2 = "invalid@email.com";
        final String body2 = validQuery1;

        ReceivedMessage msg2 = msgGetter.createMessage(sender2, body2);

        inbox.insertMessage(msg2);
        MessageDBO record2 = pr.processMessageResponse(msg2.hash);

        assertEquals("invalid customer", record2.getResponse());

        inbox.insertMessage(record2);

        record2 = inbox.getMessage(msg2.hash);
        assertEquals("invalid customer", record2.getResponse());
    }
}
