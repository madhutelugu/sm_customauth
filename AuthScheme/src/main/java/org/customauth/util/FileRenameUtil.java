package org.customauth.util;

import java.io.File;

public class FileRenameUtil {

    public static void main(String[] args) {
        File f = new File("C:\\siteminder\\Access gateway\\secure-proxy\\proxy-engine\\examples\\siteminderagent\\forms_en-US\\TDIS_ChoiceCollector_files");

       File[] files =  f.listFiles();

       for(File file: files){

           String newFile = file.getAbsolutePath().replaceAll(".download","");

           System.out.println("New File: " + newFile);

           File tempFile = new File(newFile);

           file.renameTo(tempFile);


       }

    }
}
