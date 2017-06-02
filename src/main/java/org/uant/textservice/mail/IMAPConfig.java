package org.uant.textservice.mail;

public class IMAPConfig extends MailConfig {

  public IMAPConfig(String host, String port, String protocol){
    this.host = host;
    this.port = port;
    this.protocol = protocol;

    this.props.put(String.format("mail.%s.host", protocol), host);
    this.props.put(String.format("mail.%s.port", protocol), port);
    this.props.setProperty(
                  String.format("mail.%s.socketFactory.class", protocol),
                  "javax.net.ssl.SSLSocketFactory");
    this.props.setProperty(
                  String.format("mail.%s.socketFactory.fallback", protocol),
                  "false");
    this.props.setProperty(
                  String.format("mail.%s.socketFactory.port", protocol),
                  String.valueOf(port));
    }
}
