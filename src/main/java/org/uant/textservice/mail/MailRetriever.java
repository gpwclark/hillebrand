package org.uant.textservice.mail;

import org.uant.textservice.mail.IMAPConfig;
import java.util.Properties;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.AuthenticationFailedException;
import com.sun.mail.util.MailConnectException;
import javax.mail.FolderNotFoundException;
import java.lang.IllegalStateException;
import javax.mail.Session;
import javax.mail.Store;

/*
 * The MailRetriever class exists to open the Inbox folder, and get a list
 * of new messages. To do this upon instantiation it connects to the mail
 * store. The mail store is used connect to the inbox in getEmail().
 * This class is used to connect to the mail server and get a list of messages
 * in the inbox.
 */

public class MailRetriever {

  IMAPConfig imapConfig;
  Store store;
  Folder inbox;
  int maxNumAttempts = 5; //TODO hardcoded possible default?

  public MailRetriever(Store store)
                      throws MessagingException,
                             NoSuchProviderException,
                             AuthenticationFailedException {

    this.store = store;
  }

  public void closeMailbox() throws MessagingException {
    try {
       //Folder.close(true) expunges all deleted msgs if arg == true.
      inbox.close(true);
      store.close();
    } catch (MessagingException ex) {

      System.out.println("Messaging Exception, message store is unreachable");
      throw ex;
    }
  }


  public Message[] getNewEmail() throws MessagingException {

    String primaryFolder = "INBOX";
    return getEmail(primaryFolder);
  }

  private Message[] getEmail(String aFolderName) throws MessagingException {

    Message[] messages = null;
    String folderName = aFolderName;

    try {

      inbox = store.getFolder(folderName);
      inbox.open(Folder.READ_WRITE);

      int count = inbox.getMessageCount();
      messages = inbox.getMessages(1, count);
    } catch (IllegalStateException ex) {

      System.out.println("Failed to get or open folder " + folderName);
      ex.printStackTrace();
    } catch (FolderNotFoundException ex) {

      System.out.println("Failed to find folder: " + folderName);
      ex.printStackTrace();
    } catch (IndexOutOfBoundsException ex) {

      System.out.println("Failed to get messages from folder: " + folderName + " specified index was OOB");
      ex.printStackTrace();
    }
    catch (MessagingException ex) {

      System.out.println("Messaging Exception, message store is unreachable");
      ex.printStackTrace();
    }

   return messages;
  }

}
