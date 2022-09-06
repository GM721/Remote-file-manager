package com.gm721.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class MainClient {

    private static Scanner in = new Scanner(System.in);
    private static String getMessage;
    private static byte[] getBytes;
    private static String  command;

    private static final int port = 5484;
    private static final String address = "127.0.0.1";

    public static void main(String[] args) {
        boolean run = true;
        try (
                Socket socket = new Socket(InetAddress.getByName(address),port);
                DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                DataInputStream inputStream = new DataInputStream(socket.getInputStream())
        ) {
            while (run) {
                System.out.print("Enter action (1 - get a file, 2 - send a file, 3 - create a file, 4 - delete a file): ");
                command = in.nextLine();
                switch (command) {
                    case "1":
                        getRequest(outputStream, inputStream);
                        break;
                   /* case "2":
                        putRequest(outputStream, inputStream);
                        break; */
                    case "3":
                        putRequest(outputStream, inputStream);
                        break;
                    case "4":
                        deleteRequest(outputStream, inputStream);
                        break;
                    case "exit":
                        outputStream.writeUTF("exit");
                        System.out.println("The request was sent.");
                        ClientFileManager.cleanDir();
                        run = false;
                }
            }
            System.out.println("Received: " + inputStream.readUTF());
        } catch (IOException e) {
            e.getMessage();
        }

    }

    private static void getRequest(DataOutputStream outputStream, DataInputStream inputStream) throws IOException{
        outputStream.writeUTF("GET");
        outputStream.flush();
        System.out.print("Do you want to get the file by name or by id (1 - name, 2 - id): ");
        command = in.nextLine();
        switch (command) {
            case "1":
                outputStream.writeUTF("BY_NAME");
                outputStream.flush();
                System.out.println("Enter name: ");
                outputStream.writeUTF(in.nextLine());
                break;
            case "2":
                outputStream.writeUTF("BY_ID");
                outputStream.flush();
                System.out.println("Enter id: ");
                outputStream.writeInt(in.nextInt());
                in.nextLine();
        }
        System.out.println("The request was sent.");
        if (inputStream.readUTF().equals("200")) {
            System.out.println("The response says that the file was successfully get!");
            int length = inputStream.readInt();
            getBytes = new byte[length];
            inputStream.read(getBytes);
            System.out.println("Input file name: ");
            ClientFileManager.saveFile(in.nextLine(), getBytes);
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }

    private static void putRequest(DataOutputStream outputStream, DataInputStream inputStream) throws IOException {
        outputStream.writeUTF("PUT");
        outputStream.flush();
        System.out.print("Enter filename: ");
        String fileName = in.nextLine();
        outputStream.writeUTF(fileName);
        outputStream.flush();
        byte[] fileContent = ClientFileManager.getFileContent(fileName);
        if(sendByteArray(fileContent,outputStream)) {
            System.out.print("Enter file content: ");
            outputStream.writeUTF(in.nextLine());
            outputStream.flush();
        }
        getMessage = inputStream.readUTF();
        if (getMessage.equals("200")) {
            System.out.println("The response says that the file was created with id " + inputStream.readInt());
        } else {
            System.out.println("The response says that creating the file was forbidden!");
        }
    }

    private static boolean sendByteArray (byte[] fileContent, DataOutputStream outputStream) throws IOException {
        outputStream.writeInt(fileContent.length);
        outputStream.flush();
        outputStream.write(fileContent);
        outputStream.flush();
        return (new String(fileContent).equals("null"));
    }

    private static void deleteRequest(DataOutputStream outputStream, DataInputStream inputStream) throws IOException {
        outputStream.writeUTF("DELETE");
        outputStream.flush();
        System.out.print("Do you want to delete the file by name or by id (1 - name, 2 - id): ");
        command = in.nextLine();
        switch (command) {
            case "1":
                outputStream.writeUTF("BY_NAME");
                outputStream.flush();
                System.out.println("Enter name: ");
                outputStream.writeUTF(in.nextLine());
                break;
            case "2":
                outputStream.writeUTF("BY_ID");
                outputStream.flush();
                System.out.println("Enter id: ");
                outputStream.writeInt(in.nextInt());
        }
        System.out.println("The request was sent.");
        getMessage = inputStream.readUTF();
        if (getMessage.equals("200")) {
            System.out.println("The response says that the file was successfully deleted!");
        } else {
            System.out.println("The response says that the file was not found!");
        }
    }
}