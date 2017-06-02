package org.uant.textservice.mail;

import javax.mail.Message;

public class MessageWrapper{
  public final String email;
  public final String emailBody;
  public final Message msg;

  public MessageWrapper(String email, String emailBody, Message msg){
    this.email = email;
    this.emailBody = emailBody;
    this.msg = msg;
  }
}

