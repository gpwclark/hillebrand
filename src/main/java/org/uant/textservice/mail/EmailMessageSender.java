package org.uant.textservice.mail;

import org.uant.textservice.db.DataSourceFactory;
import org.uant.textservice.mail.MailSender;
import org.uant.textservice.mail.MessageWrapper;
import org.uant.textservice.mail.MailRetriever;
import org.uant.textservice.mail.MailConfig;
import org.uant.textservice.mail.SMTPConfig;
import org.uant.textservice.mail.MailAuthenticator;
import org.uant.textservice.mail.MailReader;
import org.uant.textservice.mail.MailConnector;

import org.uant.textservice.db.MessageDBO;
import org.uant.textservice.message.ReceivedMessage;
import org.uant.textservice.message.SendMessageHandler;

import javax.mail.Store;
import javax.mail.Transport;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.NoSuchProviderException;
import javax.mail.AuthenticationFailedException;
import com.sun.mail.util.MailConnectException;

import java.util.ArrayList;

public class EmailMessageSender implements SendMessageHandler {
    /*
    */

    MailAuthenticator mailAuth;
    SMTPConfig smtpConfig;

    public EmailMessageSender(String mailAuthProps, String smtpProps) {
        //TODO it looks like there is a specic way to retrieve pwd
        //on commandline with the javax.mail API look into this
        //as a safer alternative!
        this.mailAuth = DataSourceFactory.getMailAuth(mailAuthProps);
        this.smtpConfig = DataSourceFactory.getSmtpConfig(smtpProps);

    }

    public MessageDBO sendMessage(MessageDBO message) {
        //TODO interval at which program sleeps before checking mailbox again.
        Transport transport = null;
        ArrayList<ReceivedMessage> newMessages = new ArrayList<ReceivedMessage>();

        try {
            MailConnector mailConn = MailConnector.getMailConnectorObj();
            mailConn.InitMailConnectorTransport(this.smtpConfig, this.mailAuth);
            transport = mailConn.getTransport();

            sendMessage(transport, this.smtpConfig, message.getSender(), message.getResponse());

        } catch(MessagingException ex){
            System.out.println("Failed to connect in main loop");
            ex.printStackTrace();

        }
        return message;
    }

    private void sendMessage(Transport transport,
            SMTPConfig smtpConfig,
            String email_to,
            String email_body) throws MessagingException {

        MailSender service = null;

        try {
            service = new MailSender(transport, this.smtpConfig);
        } catch (AuthenticationFailedException ex) {
            System.out.println("Fatal error, restart application with correct cmd line arguments");
            ex.printStackTrace();
        } catch (MailConnectException ex) {
            System.out.println("Fatal error, restart application with correct cmd line arguments");
            ex.printStackTrace();
        } catch (MessagingException ex) {
            throw ex;
        }
        //TODO movie in try catch?
        service.sendMessage(email_to, email_body);
        service.closeService();
    }

}
