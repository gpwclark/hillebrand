package org.uant.textservice;

import org.uant.textservice.db.MessageDBO;
import org.uant.textservice.mail.EmailMessageSender;
import org.uant.textservice.mail.MailSender;
import org.uant.textservice.mail.MailConnector;
import org.uant.textservice.mail.SMTPConfig;
import org.uant.textservice.mail.MailConfig;
import org.uant.textservice.data.GreenMailConfig;
import junit.framework.TestCase;
import org.uant.textservice.mail.MailAuthenticator;
import junit.framework.TestSuite;
import javax.mail.Transport;
import javax.mail.AuthenticationFailedException;
import javax.mail.Flags;

import java.security.Security;
import java.util.Date;
import java.util.Properties;

import java.io.IOException;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Message;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.icegreen.greenmail.user.UserException;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetupTest;
import com.sun.mail.smtp.SMTPTransport;
import com.icegreen.greenmail.user.GreenMailUser;

import java.util.Arrays;
/**
 * Unit test for EmailMessageSender
 */
public class EmailMessageSenderTest extends TestCase {
    private GreenMail mailServer;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EmailMessageSenderTest( String testName ) {
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
        mailServer = new GreenMail(ServerSetupTest.SMTPS);
        mailServer.start();
    }

    @After
    public void tearDown() {
        mailServer.stop();
    }

    @Test
    public void testSendMail() throws IOException, MessagingException, UserException, InterruptedException {

        // create user on mail server
        GreenMailUser user = mailServer.setUser(GreenMailConfig.EMAIL_USER_ADDRESS, GreenMailConfig.USER_NAME, GreenMailConfig.USER_PASSWORD);

        String mailAuth = "properties/testMailAuth.properties";
        String smtpConfig = "properties/testSmtp.properties";

        EmailMessageSender ems = new EmailMessageSender(mailAuth, smtpConfig);
        MessageDBO message = new  MessageDBO(822, GreenMailConfig.EMAIL_TO, "status", GreenMailConfig.EMAIL_TEXT, true, true, false, true, 87);

        // need to deal with
        MessageDBO sentMessage = null;
        try {
            sentMessage = ems.sendMessage(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // fetch messages from server
        MimeMessage[] messages = mailServer.getReceivedMessages();
        assertNotNull(messages);
        assertEquals(1, messages.length);
        MimeMessage m = messages[0];
        assertTrue(String.valueOf(m.getContent()).contains(GreenMailConfig.EMAIL_TEXT));
        assertEquals(GreenMailConfig.EMAIL_TO, m.getAllRecipients()[0].toString());
        Flags flags = m.getFlags();
        Flags.Flag[] sf = flags.getSystemFlags();
        for (int i = 0; i < sf.length; i++) {
            if (sf[i] == Flags.Flag.SEEN)
                //assertTrue(m.isSet(Flags.Flag.DELETED));
                assertTrue(m.isSet(Flags.Flag.SEEN));
        }
    }
}
