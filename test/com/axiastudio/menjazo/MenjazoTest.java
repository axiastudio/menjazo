package com.axiastudio.menjazo;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

import static org.junit.Assert.*;

public class MenjazoTest {

    @Test
    public void test() throws Exception {

        // push to Alfresco
        String idObject = Menjazo.create()
                .open("http://127.0.0.1:8080/alfresco/service/cmis", "admin", "admin")
                .path("/Test2/test.pdf")
                .mime("application/pdf")
                .fromStream(new FileInputStream(new File("test.pdf")));

        // pull from Alfresco
        InputStream stream = Menjazo.create()
                .open("http://127.0.0.1:8080/alfresco/service/cmis", "admin", "admin")
                .path("/Test2/test.pdf")
                .stream();

        // write to file
        FileOutputStream outputStream = new FileOutputStream(new File("test-copy.pdf"));
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = stream.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }

    }

}