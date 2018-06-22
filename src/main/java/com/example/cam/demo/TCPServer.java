package com.example.cam.demo;

public class TCPServer {
    public static void start(){
                new Thread(new ServerThread()).start();
    }
}
