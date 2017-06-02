package org.uant.textservice;

import org.uant.textservice.mail.MailConnector;
import com.sun.mail.util.MailConnectException;
import org.uant.textservice.mail.MailAuthenticator;
import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.mail.SMTPConfig;
import org.uant.textservice.data.GreenMailConfig;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import javax.mail.AuthenticationFailedException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.Transport;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
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
 * Unit test for MailConnectorAuthFailure
 */
public class MailConnectorTest extends TestCase {
    private GreenMail mailServerImaps;
    private GreenMail mailServerSmtps;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public MailConnectorTest( String testName ) {
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
        mailServerImaps = new GreenMail(ServerSetupTest.IMAPS);
        mailServerImaps.start();

        mailServerSmtps = new GreenMail(ServerSetupTest.SMTPS);
        mailServerSmtps.start();
    }

    @After
    public void tearDown() {
        mailServerImaps.stop();
        mailServerSmtps.stop();
    }

    @Test
    public void testStoreAuthenticationFail() throws IOException,
           MessagingException,
           UserException,
           InterruptedException {
               //create auth obj
               GreenMailUser user = mailServerImaps.setUser(GreenMailConfig.EMAIL_USER_ADDRESS, GreenMailConfig.USER_NAME, GreenMailConfig.USER_PASSWORD);

               MailAuthenticator mailAuth = new MailAuthenticator("wrongLogin", user.getPassword());

               String newHost = GreenMailConfig.LOCALHOST;
               String newPort = "3993";
               String newProtocol = "imaps";

               IMAPConfig imapConfig = new IMAPConfig(newHost, newPort, newProtocol);
               Exception ex = null;
               try {
                   MailConnector mailConn = MailConnector.getMailConnectorObj();
                   mailConn.InitMailConnectorStore(imapConfig, mailAuth);
               } catch (Exception e) {
                   ex = e;
                   assertThat(e).isInstanceOf(AuthenticationFailedException.class);
               }
               assertNotNull(ex);

    }

    @Test
    public void testGetTransport() throws IOException,
           MessagingException,
           UserException,
           InterruptedException {

               GreenMailUser user = mailServerSmtps.setUser(GreenMailConfig.EMAIL_USER_ADDRESS, GreenMailConfig.USER_NAME, GreenMailConfig.USER_PASSWORD);

               //create auth obj
               MailAuthenticator mailAuth = new MailAuthenticator(user.getLogin(), user.getPassword());

               String newHost = GreenMailConfig.LOCALHOST;
               String newPort = "3465";
               String newProtocol = "smtps";
               String newEmail =  GreenMailConfig.EMAIL_USER_ADDRESS;

               SMTPConfig smtpConfig = new SMTPConfig(newHost, newPort, newProtocol, newEmail);

               Transport transport = null;

               try {
                   MailConnector mailConn = MailConnector.getMailConnectorObj();
                   mailConn.InitMailConnectorTransport(smtpConfig, mailAuth);
                   transport = mailConn.getTransport();
               } catch (Exception e) {
                   e.printStackTrace();
               }
               assertNotNull(transport);
    }

    @Test
    public void testGetStore() throws IOException,
           MessagingException,
           UserException,
           InterruptedException {

               GreenMailUser user = mailServerImaps.setUser(GreenMailConfig.EMAIL_USER_ADDRESS, GreenMailConfig.USER_NAME, GreenMailConfig.USER_PASSWORD);

               //create auth obj
               MailAuthenticator mailAuth = new MailAuthenticator(user.getLogin(), user.getPassword());

               String newHost = GreenMailConfig.LOCALHOST;
               String newPort = "3993";
               String newProtocol = "imaps";

               IMAPConfig imapConfig = new IMAPConfig(newHost, newPort, newProtocol);
               Store store = null;

               try {
                   MailConnector mailConn = MailConnector.getMailConnectorObj();
                   mailConn.InitMailConnectorStore(imapConfig, mailAuth);
                   store = mailConn.getStore();
               } catch (Exception e) {
                   e.printStackTrace();
               }
               assertNotNull(store);
    }
}
