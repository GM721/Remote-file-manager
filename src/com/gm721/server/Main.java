package com.gm721.server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        String address = "127.0.0.1";
        final int port = 5484;
        boolean run = true;
        System.out.println("Server started!");
        while(run){
            try (
                    ServerSocket server = new ServerSocket(port)
            ) {
                Session session = new Session(server.accept());
                session.run();
            } catch (UnknownHostException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
