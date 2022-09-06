package com.gm721.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Session extends Thread {
    private final Socket socket;

    Session(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
        try (
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream())
        ) {
            System.out.println("Session start");
            String comand;
            String fileName;
            int id;
            boolean run = true;
            while (run) {
                comand = inputStream.readUTF();
                System.out.println(comand);
                switch (comand) {
                    case "PUT":
                        fileName = inputStream.readUTF();
                        int length = inputStream.readInt();
                        byte [] fileContent = new byte[length];
                        inputStream.read(fileContent);
                        if(!(new String(fileContent).equals("null"))) {
                            id = ServerFileManager.create(fileName, fileContent);
                            if (id > 0) {
                                outputStream.writeUTF("200");
                                outputStream.flush();
                                System.out.println("200");
                                outputStream.writeInt(id);
                                outputStream.flush();
                            } else {
                                outputStream.writeUTF("403");
                                System.out.println("403");
                            }
                        } else {
                            id = ServerFileManager.create(fileName,inputStream.readUTF().split(" "));
                            if (id > 0) {
                                outputStream.writeUTF("200");
                                outputStream.flush();
                                System.out.println("200");
                                outputStream.writeInt(id);
                                outputStream.flush();
                            } else {
                                outputStream.writeUTF("403");
                            }
                        }
                        break;
                    case "GET":
                        byte[] byteOut;
                        comand = inputStream.readUTF();
                        switch (comand) {
                            case "BY_NAME":
                                fileName = inputStream.readUTF();
                                byteOut = ServerFileManager.getByName(fileName);
                                break;
                            case "BY_ID" :
                                byteOut = ServerFileManager.getById(inputStream.readInt());
                                break;
                            default:
                                byteOut = "404".getBytes();
                        }
                        if (new String(byteOut).equals("404")) {
                            outputStream.writeUTF("404");
                            outputStream.flush();
                        } else {
                            outputStream.writeUTF("200");
                            outputStream.flush();
                            outputStream.writeInt(byteOut.length);
                            outputStream.flush();
                            outputStream.write(byteOut);
                            outputStream.flush();
                        }
                        break;
                    case "DELETE":
                        comand = inputStream.readUTF();
                        switch (comand) {
                            case "BY_NAME":
                                fileName = inputStream.readUTF();
                                if (ServerFileManager.deleteByName(fileName)) {
                                    outputStream.writeUTF("200");
                                } else {
                                    outputStream.writeUTF("404");
                                }
                                break;
                            case "BY_ID" :
                                id = inputStream.readInt();
                                if (ServerFileManager.deleteById(id)) {
                                    outputStream.writeUTF("200");
                                } else {
                                    outputStream.writeUTF("404");
                                }
                                break;
                            default:
                                outputStream.writeUTF("404");
                        }
                        break;
                    case "exit":
                        run = false;
                        break;
                }
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }
}
