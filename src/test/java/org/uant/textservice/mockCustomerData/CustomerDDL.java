package org.uant.textservice.mockCustomerData;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import java.nio.file.Paths;
import java.nio.file.Files;

public class CustomerDDL {
    public static String getDDL(String fileName) {
        String file = "";
        try {
            file =  new String(readAllBytes(get(fileName)));
            //new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return file;
    }
}
