package org.uant.textservice;

import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.ReceivedMessage;

import org.uant.textservice.mail.EmailMessageGetter;
import org.uant.textservice.mail.MailRetriever;
import org.uant.textservice.mail.MailAuthenticator;
import org.uant.textservice.mail.MailConnector;
import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.data.GreenMailConfig;
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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.Security;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icegreen.greenmail.user.GreenMailUser;
import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.icegreen.greenmail.util.DummySSLSocketFactory;

/**
 * Unit test for EmailMessageGetter
 */
public class EmailMessageGetterTest extends TestCase {
    private GreenMail mailServer;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EmailMessageGetterTest( String testName ) {
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
        Security.setProperty("ssl.SocketFactory.provider",
                DummySSLSocketFactory.class.getName());
        mailServer = new GreenMail(ServerSetupTest.IMAPS);
        mailServer.start();
    }

    @After
    public void tearDown() {
        mailServer.stop();
    }

    @Test
    public void testGetMail() throws IOException, MessagingException, UserException, InterruptedException {
        // create user on mail server
        GreenMailUser user = mailServer.setUser(GreenMailConfig.EMAIL_USER_ADDRESS, GreenMailConfig.USER_NAME, GreenMailConfig.USER_PASSWORD);

        // create an e-mail message using javax.mail ..
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(GreenMailConfig.EMAIL_TO));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress( GreenMailConfig.EMAIL_USER_ADDRESS));
        message.setSubject(GreenMailConfig.EMAIL_SUBJECT);
        message.setText(GreenMailConfig.EMAIL_TEXT);

        // use greenmail to store the message
        user.deliver(message);

        String mailAuth = "properties/testMailAuth.properties";
        String imapConfig = "properties/testImap.properties";
        EmailMessageGetter emg = new EmailMessageGetter(mailAuth, imapConfig);

        ArrayList<ReceivedMessage> newMessages = null;
        try {
            newMessages = emg.getMessages();
        } catch (Exception e) {
            e.printStackTrace();
        }

        assertNotNull(newMessages);
        assertThat(1, equalTo(newMessages.size()));
        assertTrue(String.valueOf(newMessages.get(0).body).contains(GreenMailConfig.EMAIL_TEXT));
        assertEquals(GreenMailConfig.EMAIL_TO, newMessages.get(0).sender);
    }

}
