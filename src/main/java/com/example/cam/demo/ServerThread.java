package com.example.cam.demo;

public class ServerThread implements Runnable {
    public ServerThread(){
    }

    @Override
    public void run() {
        try{
            TcpNioServer.start();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
