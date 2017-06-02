package org.uant.textservice.mail;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class MailAuthenticator extends Authenticator {
  String username;
  String password;
  PasswordAuthentication passAuth;

  public MailAuthenticator(String newUsername, String newPassword){
    username = newUsername;
    password = newPassword;
    this.passAuth =  new PasswordAuthentication(username, password);
  }

  /* TODO may want this to be protected later.
   */
  public PasswordAuthentication getPasswordAuthentication(){
        return this.passAuth;
  }
}
