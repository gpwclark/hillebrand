package org.uant.textservice;

import org.uant.textservice.db.MessageDriver;
import org.uant.textservice.db.MessageDb;
import org.uant.textservice.db.DataSourceFactory;

import java.util.UUID;

import java.sql.*;
import javax.sql.DataSource;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import javax.mail.AuthenticationFailedException;

import java.util.ArrayList;

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
 * Unit test for MessageDb deletion
 */
public class DeleteOldMessagesTest extends TestCase {
    private MessageDriver msgDb;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public DeleteOldMessagesTest( String testName ) {
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
    public void testDeleteOldMessages() {
        long testTime = 1496013221193L;
        msgDb.deleteMessagesOlderThan(testTime);

        ArrayList<MessageDBO> messages = msgDb.getTable();

        assertEquals(2, messages.size());
        MessageDBO message = messages.get(0);
        MessageDBO message1 = messages.get(1);
        assertEquals("resource5@localhost.com", message.getSender());
        assertEquals("resource139@localhost.com", message1.getSender());
    }
}
