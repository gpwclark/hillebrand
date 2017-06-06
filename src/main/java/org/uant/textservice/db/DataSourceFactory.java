package org.uant.textservice.db;

import java.sql.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;

import org.uant.textservice.mail.MailAuthenticator;
import org.uant.textservice.mail.IMAPConfig;
import org.uant.textservice.mail.SMTPConfig;

//import javax.sql.DataSource;
//import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

public class DataSourceFactory {

    public static DataSource getMySQLDataSource() {
        Properties props = new Properties();
        FileInputStream fis = null;
        JdbcDataSource mysqlDS = null;
        try {
            fis = new FileInputStream("properties/db.properties");
            props.load(fis);
            mysqlDS = new JdbcDataSource();
            mysqlDS.setURL(props.getProperty("MYSQL_DB_URL"));
            mysqlDS.setUser(props.getProperty("MYSQL_DB_USERNAME"));
            mysqlDS.setPassword(props.getProperty("MYSQL_DB_PASSWORD"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mysqlDS;
    }

    //TODO put mail stuff in its own class
    //TODO extract send msg stuff from smtp config
    public static SMTPConfig getSmtpConfig(String propsFile) {
        Properties props = new Properties();
        FileInputStream fis = null;
        SMTPConfig smtpConfig = null;
        try {
            fis = new FileInputStream(propsFile);
            props.load(fis);

            smtpConfig = new SMTPConfig(props.getProperty("HOST"),
                                        props.getProperty("PORT"),
                                        props.getProperty("PROTOCOL"),
                                        props.getProperty("EMAIL"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return smtpConfig;
    }

    public static IMAPConfig getImapConfig(String propsFile) {
        Properties props = new Properties();
        FileInputStream fis = null;
        IMAPConfig imapConfig = null;
        try {
            fis = new FileInputStream(propsFile);
            props.load(fis);
            imapConfig = new IMAPConfig(props.getProperty("HOST"),
                                        props.getProperty("PORT"),
                                        props.getProperty("PROTOCOL"));

        } catch (IOException e) {
            e.printStackTrace();
        }
        return imapConfig;
    }

    public static MailAuthenticator getMailAuth(String propsFile) {
        Properties props = new Properties();
        FileInputStream fis = null;
        MailAuthenticator mailAuth = null;
        try {
            fis = new FileInputStream(propsFile);
            props.load(fis);

            mailAuth = new MailAuthenticator(props.getProperty("USER_LOGIN"),
                                             props.getProperty("USER_PASSWORD"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mailAuth;
    }

}
