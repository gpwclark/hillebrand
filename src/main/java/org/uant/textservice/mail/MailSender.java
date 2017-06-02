package org.uant.textservice.mail;

import org.uant.textservice.mail.SMTPConfig;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;
import javax.mail.AuthenticationFailedException;
import com.sun.mail.util.MailConnectException;
import javax.mail.Flags;

public class MailSender {
    /*
     * Issues:
     *  *IF* it is a message to a cellphone number, we don't really want it to
     *  have a subject, otherwise, we do.
     *  *IF* the message is longer than 160 characters, we are going to need
     *  to do something about it. Could the message object take care of it?
     */

    SMTPConfig smtpConfig;
    Session session;
    Transport transport;

    public MailSender(Transport transport, SMTPConfig smtpConfig)
        throws MessagingException,
                          AuthenticationFailedException,
                          MailConnectException {

                   this.transport = transport;
                   this.smtpConfig = smtpConfig;
    }

    //TODO need to deal with null transport
    public void sendMessage(String recipientEmail, String emailText){

        MimeMessage msg = new MimeMessage(session);

        try {

            smtpConfig.setMessage(msg, recipientEmail, emailText);
            msg = smtpConfig.getMessage();
            transport.sendMessage(msg, msg.getAllRecipients());
            //msg.setFlag(Flags.Flag.DELETED, true);
            //TODO this isn't really setting the proper email to seen
            //and with the new impl should really be done when the message
            //is retrieved (should also be deleted there...
            msg.setFlag(Flags.Flag.SEEN, true);
        } catch (MessagingException ex){

            System.out.println("Messaging Exception, message store is unreachable");
            ex.printStackTrace();
        }
    }

    public void closeService(){
        try {
            transport.close();
        } catch (MessagingException ex){

            System.out.println("Messaging Exception, message store is unreachable");
            ex.printStackTrace();
        }
    }
}
