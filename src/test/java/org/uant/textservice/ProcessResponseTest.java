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

import org.uant.textservice.mockCustomerData.CustomerDDL;

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
        inbox = new MockInbox();
        msgGetter = new MockMessageGenerator();
        testEmailGen = new TestEmailGenerator();
        //DataSource ds = JdbcConnectionPool.create("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "password");
        //Connection conn = ds.getConnection();
        //conn.createStatement().executeUpdate();
        //conn = null;
        //try {
        //    Class.forName("org.h2.Driver");
        //    conn = DriverManager.getConnection("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1", "user", "password");
        //    resourceDb = new ResourceDb(conn);
        //} catch (Exception ex) {
        //    ex.printStackTrace();
        //}
        DataSource ds = DataSourceFactory.getMySQLDataSource();
        try (
                Connection conn = ds.getConnection();
            ){
            String customers = CustomerDDL.getDDL("customer.ddl");
            conn.createStatement().executeUpdate(customers);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        resourceDb = new ResourceDb(ds);
    }

    @After
    public void tearDown() {}

    @Test
    public void testProcessResponse() {
        //TODO need some assertions for method getCustomerMessage();
        //    this will require getting the data.TestEmails class.
        String validQuery = "aljdslskjdlkjldsjlsaSTATUSlkajaldskj";
        String validQuery1 = "aljdslskjdlkjldsjlsastatusssslkajaldskj";
        String invalidQuery = "lakjdlksjdlksajdlanlkjwna;oiuw;nwnwwlkja;s?><M";

        ProcessResponse pr = new ProcessResponse(resourceDb, inbox);

        //valid query and customer
        final String sender = testEmailGen.getRandomTestEmail();
        final String body = validQuery;

        ReceivedMessage msg = msgGetter.createMessage(sender, body);

        inbox.storeMessage(msg);
        Map<String, String> record = pr.processMessageResponse(msg.hash);

        assertEquals("all orders shipped", record.get("response"));
        assertEquals("true", record.get("validResource"));
        assertEquals("true", record.get("validRequest"));

        inbox.storeMessage(msg.hash, record);

        record = inbox.getMessage(msg.hash);
        assertEquals("all orders shipped", record.get("response"));
        assertEquals("true", record.get("validResource"));
        assertEquals("true", record.get("validRequest"));

        //invalid query and valid customer
        final String sender1 = testEmailGen.getRandomTestEmail();
        final String body1 = invalidQuery;

        ReceivedMessage msg1 = msgGetter.createMessage(sender1, body1);

        inbox.storeMessage(msg1);
        Map<String, String> record1 = pr.processMessageResponse(msg1.hash);

        assertEquals("invalid query", record1.get("response"));
        assertEquals("true", record1.get("validResource"));
        assertEquals("false", record1.get("validRequest"));

        inbox.storeMessage(msg1.hash, record1);

        record1 = inbox.getMessage(msg1.hash);
        assertEquals("invalid query", record1.get("response"));
        assertEquals("true", record1.get("validResource"));
        assertEquals("false", record1.get("validRequest"));
        //valid query and invalid customer
        final String sender2 = "invalid@email.com";
        final String body2 = validQuery1;

        ReceivedMessage msg2 = msgGetter.createMessage(sender2, body2);

        inbox.storeMessage(msg2);
        Map<String, String> record2 = pr.processMessageResponse(msg2.hash);

        assertEquals("invalid customer", record2.get("response"));

        inbox.storeMessage(msg2.hash, record2);

        record2 = inbox.getMessage(msg2.hash);
        assertEquals("invalid customer", record2.get("response"));
    }
}
