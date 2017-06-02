package org.uant.textservice.mail;

//TODO will need to remove rest of imports
import javax.mail.internet.MimeMessage;
import javax.mail.internet.InternetAddress;
import javax.mail.Message;
import javax.mail.MessagingException;

  //TODO
  // take out messaging stuff in this class
  // it is now bloated for the sake of the test case
  // and needs to be parsed apart into something more
  // sensible. make sure to get rid of bogus, imports
  // instance variables, and methods.

public class SMTPConfig extends MailConfig {

  String userEmail;
  MimeMessage emailMessage;

  public SMTPConfig(String host, String port, String protocol, String userEmail){

    this.host = host;
    this.port  = port;
    this.protocol  = protocol;
    this.userEmail = userEmail;

    this.props.setProperty("mail.smtp.user", userEmail);
    this.props.setProperty("mail.smtp.host", host);
    this.props.setProperty("mail.smtp.port", port);
    this.props.setProperty("mail.smtp.starttls.enable", "true");
    this.props.setProperty("mail.smtp.debug", "true");
    this.props.setProperty("mail.smtp.auth", "true");
    this.props.setProperty("mail.smtp.socketFactory.port", port);
    this.props.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
    this.props.setProperty("mail.smtp.socketFactory.fallback", "false");
  }


  public String getHost(){
    return host;
  }

  public int getPort(){
    return Integer.parseInt(port);
  }

  public String getFromEmail(){
    return userEmail;
  }

  public void setMessage(MimeMessage newEmailMessage, String recipientEmail, String emailText) throws MessagingException{
    emailMessage = newEmailMessage;
    //TODO change message
    emailMessage.setText(emailText);
    //TODO setSubject *IF* it is not going to phone
    //msg.setSubject(m_subject);

    emailMessage.setFrom(new InternetAddress(this.getFromEmail()));
    emailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(recipientEmail));
  }

  public MimeMessage getMessage(){
    return emailMessage;
  }

}
