package org.uant.textservice.mail;

import java.util.Properties;

public abstract class MailConfig {

  Properties props = new Properties();
  String host;
  String port;
  String protocol;

  public Properties getPropObj(){
    return props;
  }

  public String getProtocol(){
    return protocol;
  }

  public String getHost(){
    return host;
  }

  public int getPort(){
    return Integer.parseInt(port);
  }
}
