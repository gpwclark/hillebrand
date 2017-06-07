package org.uant.textservice.mail;

import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.mail.MailSender;
import org.uant.textservice.mail.MessageWrapper;
import org.uant.textservice.mail.MailRetriever;
import org.uant.textservice.mail.MailConfig;
import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.mail.MailAuthenticator;
import org.uant.textservice.mail.MailReader;
import org.uant.textservice.mail.MailConnector;

import org.uant.textservice.message.ReceivedMessageHandler;
import org.uant.textservice.message.ReceivedMessage;

import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.NoSuchProviderException;
import javax.mail.AuthenticationFailedException;
import com.sun.mail.util.MailConnectException;

import java.util.ArrayList;

public class EmailMessageGetter implements ReceivedMessageHandler {
    /*
    */

    MailAuthenticator mailAuth;
    IMAPConfig imapConfig;
    ArrayList<ReceivedMessage> newMessages;
    ArrayList<MessageWrapper> messages;
    MailConnector mailConn;
    Store store;
    MailRetriever mailbox;
    MailReader reader;

    public EmailMessageGetter(String mailAuthProps, String imapProps) {
        //TODO it looks like there is a specic way to retrieve pwd
        //on commandline with the javax.mail API look into this
        //as a safer alternative!
        this.mailAuth = DataSourceFactory.getMailAuth(mailAuthProps);
        this.imapConfig = DataSourceFactory.getImapConfig(imapProps);
        this.messages = null;
        this.store = null;
        this.mailbox = null;
        this.reader = null;
    }

    public ArrayList<ReceivedMessage> getMessages() {
        //TODO interval at which program sleeps before checking mailbox again.
        //Thread.sleep(10000);
        this.newMessages = new ArrayList<ReceivedMessage>();

        try {
            //TODO check isconnected to avoid init.
            //OR have mailconnector take care of that?
            if (this.store == null || !this.store.isConnected()){
                this.mailConn = MailConnector.getMailConnectorObj();
                this.mailConn.InitMailConnectorStore(this.imapConfig, this.mailAuth);
                this.store = mailConn.getStore();
            }

            /* initialize mailbox */
            this.mailbox = new MailRetriever(this.store);

            /* check mailbox and get messages in list */
            this.reader = new MailReader(this.mailbox.getNewEmail());

            this.messages = this.reader.readMessages();

            /* put emails in compatible textservice format */
            for (MessageWrapper wrappedEmail : this.messages) {
                this.newMessages.add(new ReceivedMessage(wrappedEmail.email , wrappedEmail.emailBody));
            }
            this.reader.markMessagesDeleted();

            /* close mailbox (deletes messages) */
            this.mailbox.closeMailbox();

        } catch(MessagingException ex){
            System.out.println("Failed to get messages");
            ex.printStackTrace();
        }
        return this.newMessages;
    }
}
