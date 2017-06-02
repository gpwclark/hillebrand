package org.uant.textservice;

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
 * Unit test for MailSender
 */
public class MailSenderTest extends TestCase {
    private GreenMail mailServer;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MailSenderTest( String testName ) {
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

        MailAuthenticator mailAuth = new MailAuthenticator(user.getLogin(), user.getPassword());

        String newHost = GreenMailConfig.LOCALHOST;
        String newPort = "3465";
        String newProtocol = "smtps";
        String newEmail =  GreenMailConfig.EMAIL_USER_ADDRESS;

        SMTPConfig smtpConfig = new SMTPConfig(newHost, newPort, newProtocol, newEmail);
        //SMTPConfig smtpConfig = new SMTPConfig(GreenMailConfig.LOCALHOST, "3465", "smtps", GreenMailConfig.EMAIL_USER_ADDRESS);

        // need to deal with
        Transport transport = null;
        try {
            MailConnector mailConn = MailConnector.getMailConnectorObj();
            mailConn.InitMailConnectorTransport(smtpConfig, mailAuth);
            transport = mailConn.getTransport();

            MailSender service = new MailSender(transport, smtpConfig);
            service.sendMessage(GreenMailConfig.EMAIL_TO, GreenMailConfig.EMAIL_TEXT);
            service.closeService();

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
