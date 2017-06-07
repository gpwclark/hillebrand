package org.uant.textservice.mail;

import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.mail.MailAuthenticator;
import org.uant.textservice.mail.SMTPConfig;
import org.uant.textservice.mail.MailConfig;
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
import javax.mail.Service;
import javax.mail.Store;
import javax.mail.Transport;

/*
 * the thing about MailConnector is twofold.
 * 1. I want authentication objects to be handled in one place. That i can
 * control and make sure are "secure", w/e that means.
 * 2. I want to call one method to either get or retrieve mail, and I don't
 * want to worry about what how I'm getting it. I'll tolerate things like
 * knowing the folder name if I'm trying to get a specific list of emails,
 * but the that should be an overloaded method that by default returns the
 * inbox if no folder object is provided.
 *
 */
public class MailConnector {

    private static MailConnector mailConnObj;
    private MailAuthenticator mailAuth;
    private SMTPConfig smtpConfig;
    private IMAPConfig imapConfig;
    private MailConfig mailConfig;
    private Session session;
    private Store store = null;
    private Transport transport = null;
    private int maxNumAttempts = 5; //TODO hardcoded possible default?

    private MailConnector () throws MessagingException,
            NoSuchProviderException,
            AuthenticationFailedException {}

    public static MailConnector getMailConnectorObj() throws MessagingException{
        if (null == mailConnObj) {
            mailConnObj= new MailConnector();
        }
        return mailConnObj;
    }

    public void InitMailConnectorStore(IMAPConfig newImap, MailAuthenticator newMailAuth) throws MessagingException {
        this.mailAuth = newMailAuth;
        this.imapConfig = newImap;
        InitMailStore();
    }

    public void InitMailConnectorTransport(SMTPConfig newSmtp, MailAuthenticator newMailAuth) throws MessagingException {
        this.mailAuth = newMailAuth;
        this.smtpConfig = newSmtp;
        InitMailTransport();
    }

    /* TODO this is where i need some sweet logic to make sure that I do not
     * have any issues in case I connect/reconnect. perhaps a null check followed
     * by a re-do of the initmailconnector*() function.
     */
    public Transport getTransport(){
        return this.transport;
    }

    public Store getStore(){
        return this.store;
    }

    public void InitMailStore() throws MessagingException {
        boolean connectingToServer = true;
        int connectionAttempts = 0;
        this.store = null;
        Properties properties = this.imapConfig.getPropObj();
        Session session = null;

        while (connectingToServer && (connectionAttempts < maxNumAttempts)){
            try {

                //this.session = Session.getInstance(properties, this.mailAuth);
                //this.store = this.session.getStore(this.imapConfig.getProtocol());
                session = Session.getInstance(properties, this.mailAuth);
                this.store = session.getStore(this.imapConfig.getProtocol());
                this.store.connect(this.mailAuth.getPasswordAuthentication().getUserName(),
                                   this.mailAuth.getPasswordAuthentication().getPassword());
                connectingToServer = false;
            } catch (MailConnectException ex) {

                System.out.println("Failed to connect to store with host or port setting");
                ex.printStackTrace();
            } catch (NoSuchProviderException ex) {

                System.out.println("Failed to connect with protocol");
                ex.printStackTrace();
            } catch (AuthenticationFailedException ex) {

                System.out.println("Failed to authenticate");
                ex.printStackTrace();
            } catch (MessagingException ex) {

                System.out.println("Messaging Exception, message store is unreachable");
                ex.printStackTrace();
            }
            connectionAttempts += 1;
        }

        if(connectingToServer && (connectionAttempts >= maxNumAttempts)){
            System.out.println("Failed to connect to server " + maxNumAttempts + " times.");
            try {
                this.store.connect();
            } catch (MessagingException ex) {
                throw ex;
            }
        }
    }

    public void InitMailTransport() throws MessagingException {
        boolean connectingToServer = true;
        int connectionAttempts = 0;
        this.transport = null;
        Properties properties = this.smtpConfig.getPropObj();
        Session session = null;

        while (connectingToServer && (connectionAttempts < maxNumAttempts)){
            try {

                session = Session.getInstance(properties, this.mailAuth);
                session.setDebug(false);

                this.transport = session.getTransport(this.smtpConfig.getProtocol());

                this.transport.connect(this.smtpConfig.getHost(), this.smtpConfig.getPort(), this.mailAuth.getPasswordAuthentication().getUserName(), this.mailAuth.getPasswordAuthentication().getPassword());
                connectingToServer = false;
            } catch (MailConnectException ex) {

                System.out.println("Failed to connect to transport with host or port setting");
                ex.printStackTrace();
            } catch (NoSuchProviderException ex) {

                System.out.println("Wrong provider.");
                ex.printStackTrace();
            } catch (AuthenticationFailedException ex) {

                System.out.println("Failed to authenticate");
                ex.printStackTrace();
            } catch (MessagingException ex) {

                System.out.println("Messaging Exception, message store is unreachable");
                ex.printStackTrace();
            }
            connectionAttempts += 1;
        }

        if(connectingToServer && (connectionAttempts >= maxNumAttempts)){
            System.out.println("Failed to connect to server " + maxNumAttempts + " times.");
            try {
                this.transport.connect();
            } catch (MessagingException ex) {
                throw ex;
            }
        }
    }
    /* TODO nice to have but doesn't work :/
       public void InitMailSession(Service newService, MailConfig mailConfig) throws MessagingException {
        //session = Session.getStore(imapConfig.getPropObj(), mailAuth);
        boolean connectingToServer = true;
        int connectionAttempts = 0;
        Service service = newService;
        Properties properties = mailConfig.getPropObj();
        Session session = null;

        //System.out.println("\n\n\n\n\n\n"+ mailConfig.getProtocol() + "\n\n\n\n\n\n");
        while (connectingToServer && (connectionAttempts < maxNumAttempts)){
        try {

        session = Session.getInstance(properties, this.mailAuth);
        if (newService instanceof Transport){
        service = session.getTransport(mailConfig.getProtocol());
        service.connect(mailConfig.getHost(), mailConfig.getPort(),
        this.mailAuth.getPasswordAuthentication().getUserName(),
        this.mailAuth.getPasswordAuthentication().getPassword());
        //newTransport.connect(smtpConfig.getHost(), smtpConfig.getPort(), username, password);
        } else {
        service = session.getStore(mailConfig.getProtocol());
        service.connect(this.mailAuth.getPasswordAuthentication().getUserName(),
        this.mailAuth.getPasswordAuthentication().getPassword());
        }

        //newService = getStore(imapConfig.getProtocol());
        //this.store.connect(imapConfig.getProtocol());
        // set to false to break out of loop and return store
        connectingToServer = false;
        } catch (MailConnectException ex) {

        System.out.println("Failed to connect to store with host or port setting");
        ex.printStackTrace();
        } catch (NoSuchProviderException ex) {

        ex.printStackTrace();
        } catch (AuthenticationFailedException ex) {

        System.out.println("Failed to authenticate");
        ex.printStackTrace();
        } catch (MessagingException ex) {

        System.out.println("Messaging Exception, message store is unreachable");
        ex.printStackTrace();
        }
        connectionAttempts += 1;
    }

    if(connectingToServer && (connectionAttempts >= maxNumAttempts)){
    System.out.println("Failed to connect to server " + maxNumAttempts + " times.");
    // throw the messaging exception to caller, MailRetriever has timed out
// trying to connect to the server.
try {
    //newService.connect(username, password);
    this.store.connect();
    } catch (MessagingException ex) {
    throw ex;
    }
}
//return this.store;
       }
       */

}
