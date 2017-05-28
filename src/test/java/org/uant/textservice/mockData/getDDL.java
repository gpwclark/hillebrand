package org.uant.textservice.mockData;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;
import java.nio.file.Paths;
import java.nio.file.Files;

public class getDDL {
    public static String get(String fileName) {
        String file = "";
        try {
            file = new String(Files.readAllBytes(Paths.get(fileName)));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return file;
    }
}
