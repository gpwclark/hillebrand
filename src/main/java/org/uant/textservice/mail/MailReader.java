package org.uant.textservice.mail;

import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.mail.MessageWrapper;
import javax.mail.Flags;
import javax.mail.Flags.Flag;
import java.util.ArrayList;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Part;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.AuthenticationFailedException;
import javax.mail.FolderNotFoundException;
import java.io.IOException;
import java.lang.IllegalStateException;
import com.sun.mail.util.MailConnectException;
import javax.mail.Session;
import javax.mail.Store;

public class MailReader {

    Message[] messages;
    private boolean textIsHtml = false;

    public MailReader(Message[] newMessages){
        messages = newMessages;
    }

    public ArrayList<MessageWrapper> readMessages() throws MessagingException{
        /*
         * TODO * need to decide on proper behavior if either of
         * these exceptions occur.
         */
        ArrayList<MessageWrapper> messages = new ArrayList<MessageWrapper>();
        for (Message message : this.messages) {
            try {
                if ( !message.getFlags().contains(Flags.Flag.SEEN) ) {
                    Address[] fromAddresses = message.getFrom();
                    try {
                        String content = getText(message);
                        if (content == null)
                            content = "blank";

                        //System.out.println("num " + message.getMessageNumber());
                        messages.add(new MessageWrapper(fromAddresses[0].toString(),
                                                   content,
                                                   message));

                    } catch (Exception ex) {
                        System.out.println("Error reading content!");
                        ex.printStackTrace();
                    }
                }
            } catch (MessagingException ex) {

                System.out.println("Failed to iterate through unread messages in inbox");
                throw ex;
            }
        }

        return messages;
    }

    /**
     * Return the primary text content of the message.
     */
    private String getText(Part p) throws MessagingException, IOException {
            if (p.isMimeType("text/*")) {
                String s = (String)p.getContent();
                textIsHtml = p.isMimeType("text/html");
                return s;
            }

            if (p.isMimeType("multipart/alternative")) {
                // prefer html text over plain text
                Multipart mp = (Multipart)p.getContent();
                String text = null;
                for (int i = 0; i < mp.getCount(); i++) {
                    Part bp = mp.getBodyPart(i);
                    if (bp.isMimeType("text/plain")) {
                        if (text == null)
                            text = getText(bp);
                        continue;
                    } else if (bp.isMimeType("text/html")) {
                        String s = getText(bp);
                        if (s != null)
                            return s;
                    } else {
                        return getText(bp);
                    }
                }
                return text;
            } else if (p.isMimeType("multipart/*")) {
                Multipart mp = (Multipart)p.getContent();
                for (int i = 0; i < mp.getCount(); i++) {
                    String s = getText(mp.getBodyPart(i));
                    if (s != null)
                        return s;
                }
            }

            return null;
        }

    public int getNumFlags(Flag requestedFlag){
        int retVal = 0;
        for (Message message : this.messages) {
            try {
                if ( message.getFlags().contains(requestedFlag)){
                    retVal++;
                }
            } catch (MessagingException ex) {

                System.out.println("Failed to iterate through unread messages in inbox");
                ex.printStackTrace();
            }
        }
        return retVal;
    }

}
