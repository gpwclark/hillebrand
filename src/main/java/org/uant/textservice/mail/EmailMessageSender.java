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
    MailConnector mailConn;
    Transport transport;
    MailSender service;

    public EmailMessageSender(String mailAuthProps, String smtpProps) {
        //TODO it looks like there is a specic way to retrieve pwd
        //on commandline with the javax.mail API look into this
        //as a safer alternative!
        this.mailAuth = DataSourceFactory.getMailAuth(mailAuthProps);
        this.smtpConfig = DataSourceFactory.getSmtpConfig(smtpProps);
        this.transport = null;
        this.service = null;

    }

    public MessageDBO sendMessage(MessageDBO message) {
        //TODO interval at which program sleeps before checking mailbox again.

        try {
            //TODO check isConnected() to avoid init
            //OR have mailconnector take care of that?
            if (this.transport != null && this.transport.isConnected()){
                sendMessage(this.transport, this.smtpConfig, message.getSender(), message.getResponse());
            } else {
                this.mailConn = MailConnector.getMailConnectorObj();
                this.mailConn.InitMailConnectorTransport(this.smtpConfig, this.mailAuth);
                this.transport = this.mailConn.getTransport();

                sendMessage(this.transport, this.smtpConfig, message.getSender(), message.getResponse());
            }

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


        try {
            this.service = new MailSender(transport, this.smtpConfig);
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
        this.service.sendMessage(email_to, email_body);
        this.service.closeService();
    }

}
