package org.uant.textservice;

import org.uant.textservice.mail.MailRetriever;
import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.mail.MailConfig;
import org.uant.textservice.mail.MailReader;
import org.uant.textservice.mail.MessageWrapper;
import org.uant.textservice.mail.MailConnector;
import org.uant.textservice.data.GreenMailConfig;
import java.util.ArrayList;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;
import org.uant.textservice.mail.MailAuthenticator;
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
 * Unit test for MailRetriever
 */
public class MailReaderTest extends TestCase {
  private GreenMail mailServer;
  private static final String[] EMAILS_TO = {"some1@localhost.com",
                                             "some2@localhost.com",
                                             "some3@localhost.com",
                                             "some4@localhost.com"};

 /**
   * Create the test case
   *
   * @param testName name of the test case
   */
  public MailReaderTest( String testName ) {
    super( testName );
  }

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
  public void testGetMail() throws IOException,
                                   MessagingException,
                                   UserException,
                                   InterruptedException {
    // create user on mail server
    GreenMailUser user = mailServer.setUser(GreenMailConfig.EMAIL_USER_ADDRESS,
                                            GreenMailConfig.USER_NAME,
                                            GreenMailConfig.USER_PASSWORD);

    // create an e-mail message using javax.mail ..
    for (int i = 0; i < EMAILS_TO.length; ++i){
      MimeMessage message = new MimeMessage((Session) null);
      message.setFrom(new InternetAddress(EMAILS_TO[i]));
      message.addRecipient(Message.RecipientType.TO,
                          new InternetAddress(GreenMailConfig.EMAIL_USER_ADDRESS));
      message.setSubject(GreenMailConfig.EMAIL_SUBJECT);
      message.setText(GreenMailConfig.EMAIL_TEXT);
      // use greenmail to store the message
      user.deliver(message);
    }

    //
    String newHost = GreenMailConfig.LOCALHOST;
    String newPort = "3993";
    String newProtocol = "imaps";

    IMAPConfig imapConfig = new IMAPConfig(newHost, newPort, newProtocol);
    MailAuthenticator mailAuth = new MailAuthenticator(user.getLogin(), user.getPassword());

   Store store = null;

   try {
       MailConnector mailConn = MailConnector.getMailConnectorObj();
       mailConn.InitMailConnectorStore(imapConfig, mailAuth);
       store = mailConn.getStore();
   } catch (Exception e) {
       e.printStackTrace();
   }

    MailRetriever mailbox = new MailRetriever(store);
    Message[] emailMessages = mailbox.getNewEmail();
    MailReader reader = new MailReader(emailMessages);

    // Verify inbox has no read messages
    assertEquals(reader.getNumFlags(Flags.Flag.SEEN), 0);

    //Verify we get messages list with all send emails
    ArrayList<MessageWrapper> messages = reader.readMessages();
    assertThat(EMAILS_TO.length, equalTo(messages.size()));

    // Verify emails have correct content
    for (int i =0; i < messages.size(); ++i){
      assertNotNull(messages.get(i));
      assertTrue(String.valueOf(messages.get(i).emailBody).contains(GreenMailConfig.EMAIL_TEXT));
      assertEquals(EMAILS_TO[i], messages.get(i).email);
      assertTrue(messages.get(i).msg.getFlags().contains(Flags.Flag.SEEN));
    }

    // Verify emails have all been read
    assertEquals(reader.getNumFlags(Flags.Flag.SEEN), EMAILS_TO.length);

    reader.markMessagesDeleted();

    assertEquals(reader.getNumFlags(Flags.Flag.DELETED), EMAILS_TO.length);

    mailbox.closeMailbox();
  }
}
