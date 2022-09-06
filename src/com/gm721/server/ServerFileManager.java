package com.gm721.server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class ServerFileManager {
    private static final File dir = new File("D:\\My Java Projects\\MyFileServer\\src\\com\\gm721\\server\\data");
    private static final File fileMapPath = new File("D:\\My Java Projects\\MyFileServer\\src\\com\\gm721\\client\\fileMap.txt");
    private static final File lastIdPath =  new File("D:\\My Java Projects\\MyFileServer\\src\\com\\gm721\\client\\lastId.txt");
    private static HashMap <Integer, String> fileMap = new HashMap<>();
    private static Integer lastId = 0;

    static {
        if(fileMapPath.exists()){
            try (ObjectInputStream oisFileMap = new ObjectInputStream(new FileInputStream(fileMapPath));
                 ObjectInputStream oisLastId = new ObjectInputStream(new FileInputStream(lastIdPath));
                 ) {
                fileMap = (HashMap<Integer, String>) oisFileMap.readObject();
                lastId = (int) oisLastId.readObject();
            } catch (IOException e) {
                e.getMessage();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private static synchronized void writeFileMap() throws IOException {
        if(!fileMapPath.exists()) {
            fileMapPath.createNewFile();
            lastIdPath.createNewFile();
        }
        try (ObjectOutputStream oosFileMap = new ObjectOutputStream(new FileOutputStream(fileMapPath));
             ObjectOutputStream oosLastId = new ObjectOutputStream(new FileOutputStream(lastIdPath));
                ) {
            oosFileMap.writeObject(fileMap);
            oosLastId.writeObject(lastId);
        }
    }

    public static synchronized int create(String fileName,byte[] content) {
        File file = new File(dir, fileName);
        try {
            if (file.createNewFile()) {
                write(file, content);
                lastId++;
                fileMap.put(lastId,fileName);
                writeFileMap();
                return lastId;
            } else {
                System.out.println("403");
                return -1;
            }
        } catch (IOException ex) {
            System.out.println("404");
            return -2;
        }
    }

    public static synchronized int create(String fileName,String[] content) {
        File file = new File(dir, fileName);
        try {
            if(file.createNewFile()){
                write(file,content);
                write(file, content);
                lastId++;
                fileMap.put(lastId,fileName);
                writeFileMap();
                return lastId;
            } else {
                return -1;
            }
        } catch (IOException ex) {
            return -2;
        }
    }

    public static synchronized void write(File file, byte[] data) throws  IOException {
        FileOutputStream fos = new FileOutputStream(file, false);
        fos.write(data,0,data.length);
        fos.flush();
        fos.close();
    }

    public static synchronized void write(File file, String[] data) throws  IOException {
        FileWriter fos = new FileWriter(file, false);
        for(String str: data){
            System.out.println(str);
            fos.write(str + " ");
        }
        fos.flush();
        fos.close();
    }

    public static synchronized byte[] getByName(String fileName) {
        File file = new File(dir, fileName);
        if(file.exists()){
            try {
                return Files.readAllBytes(Paths.get(file.getPath()));
            } catch (IOException e) {
                e.getMessage();
                return "404".getBytes();
            }
        } else {
            return "404".getBytes();
        }
    }

    public static synchronized byte[] getById(int id) {
        if(fileMap.containsKey(id)){
            String fileName = fileMap.get(id);
            System.out.println(fileName);
            return getByName(fileName);
        } else {
            return "404".getBytes();
        }
    }

    public static synchronized boolean deleteByName(String fileName) throws IOException {
        File file = new File(dir, fileName);
        for( Map.Entry <Integer,String> i : fileMap.entrySet()){
            if(i.getValue().equals(fileName)){
                fileMap.remove(i.getKey(),fileName);
            }
        }
        writeFileMap();
        return file.delete();
    }

    public static synchronized boolean deleteById(int id) throws IOException {
        if(fileMap.containsKey(id)) {
            String fileName = fileMap.get(id);
            File file = new File(dir, fileName);
            fileMap.remove(id,fileName);
            writeFileMap();
            return file.delete();
        } else {
            return false;
        }
    }

    public static synchronized boolean cleanDir() {
        File[] files = dir.listFiles();
        for(File f : files){
            if(!f.delete()) {
                return false;
            }
        }
        return true;
    }

    private static synchronized boolean checkFileName(String file_name) {
        for(int i = 1; i < 11; i++) {
            if(file_name.equals(String.format("file%d",i))){
                return true;
            }
        }
        return false;
    }
}
