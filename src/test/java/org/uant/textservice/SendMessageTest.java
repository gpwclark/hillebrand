package org.pcweavers.textservice;

import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.MockMessageGenerator;
import org.uant.textservice.db.InboxHandler;
import org.uant.textservice.db.MockInbox;
import org.uant.textservice.db.TestEmailGenerator;
import org.uant.textservice.message.SendMessageHandler;
import org.uant.textservice.message.MockMessageSender;
import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.mockData.getDDL;
import org.uant.textservice.db.MessageDBO;

import javax.sql.DataSource;
import java.sql.*;

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

public class SendMessageTest extends TestCase {
    private SendMessageHandler msgSender;
    private InboxHandler inbox;
    private ReceivedMessageHandler msgGetter;
    private TestEmailGenerator testEmailGen;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SendMessageTest ( String testName ) {
        super( testName );
    }

    @Before
    public void setUp() {
        testEmailGen = new TestEmailGenerator();
        msgGetter = new MockMessageGenerator();
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
        msgSender = new MockMessageSender(inbox);
    }

    @After
    public void tearDown() {
        DataSource ds = DataSourceFactory.getMySQLDataSource();
        try (
                Connection conn = ds.getConnection();
            ){
            conn.createStatement().execute("DROP TABLE IF EXISTS RESOURCES;");
            conn.createStatement().execute("DROP TABLE IF EXISTS MESSAGES;");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testSendMessage() {
        String validQuery = "aljdslskjdlkjldsjlsaSTATUSlkajaldskj";

        //valid query and resource
            final String sender = testEmailGen.getRandomTestEmail();
            final String body = validQuery;

            ReceivedMessage msg = msgGetter.createMessage(sender, body);

            inbox.insertMessage(msg);
            MessageDBO record = inbox.getMessage(msg.hash);

            record = msgSender.sendMessage(msg.hash);


            inbox.updateMessage(record);
            record = inbox.getMessage(msg.hash);

            assertEquals(true, record.getSent());
    }
}
