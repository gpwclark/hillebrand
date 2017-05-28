package org.uant.textservice.db;

import java.util.ArrayList;
import java.util.Random;

import java.io.InputStream;
import java.util.Properties;

import java.io.IOException;

public final class TestEmailGenerator {
    String customer = "customer";
    String emailSuffix = "@localhost.com";
    ArrayList<String> testEmails = new ArrayList<String>();
    int NUM_EMAILS = 40;

    public TestEmailGenerator(){
//        try {
//            Properties props = getProperties();
//            String numTestEmails = props.getProperty("numTestEmails");
//            NUM_EMAILS = Integer.parseInt(numTestEmails);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        for (int i = 0; i < NUM_EMAILS; i++) {
            testEmails.add(i, customer + String.valueOf(i) + emailSuffix);
        }
    }

    public ArrayList<String> getTestEmails() {
        return testEmails;
    }

    public String getRandomTestEmail() {
        Random rand = new Random();
        int value = rand.nextInt(NUM_EMAILS);
        return testEmails.get(value);
    }

    public Properties getProperties() throws IOException {
        Properties props =  new Properties();
        //InputStream in = new FileInputStream();
        //try {
        //    in = getClass().getResourceAsStream("/org/uant/textservice/properties/config.properties");
        //    try {
        //        props.load(in);
        //    }
        //    finally {
        //        in.close();
        //    }
        //}
        //catch (IOException e) {
        //    e.printStackTrace();
        //}
        InputStream in = getClass().getResourceAsStream("/org/uant/textservice/properties/config.properties");
        props.load(in);
        in.close();
        return props;
    }
}
