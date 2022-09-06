package com.gm721.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ClientFileManager {
    private static File dir = new File("D:\\My Java Projects\\MyFileServer\\src\\com\\gm721\\client\\data");

    public static void saveFile (String fileName, byte[] fileContent) throws IOException {
        FileOutputStream fos = new FileOutputStream(new File(dir, fileName), false);
        fos.write(fileContent,0,fileContent.length);
        fos.flush();
        fos.close();
    }

    public static void write(File file, String[] data) throws  IOException {
        FileWriter fos = new FileWriter(file, false);
        for(String str: data){
            System.out.println(str);
            fos.write(str + " ");
        }
        fos.flush();
        fos.close();
    }

    public static String get(String fileName) {
        File file = new File(dir, fileName);
        if(file.exists()){
            try {
                return new String(Files.readAllBytes(Paths.get(file.getPath())));
            } catch (IOException e) {
                e.getMessage();
                return "error 404";
            }
        } else {
            return "not exist";
        }
    }

    public static byte[] getFileContent (String fileName) throws IOException {
        File file = new File(dir, fileName);
        if(file.exists()){
            return Files.readAllBytes(Paths.get(file.getPath()));
        } else {
            return "null".getBytes();
        }
    }

    public static boolean delete(String file_name) {
        File file = new File(dir, file_name);
        return file.delete();
    }

    public static boolean cleanDir() {
        File[] files = dir.listFiles();
        for(File f : files){
            if(!f.delete()) {
                return false;
            }
        }
        return true;
    }

}
