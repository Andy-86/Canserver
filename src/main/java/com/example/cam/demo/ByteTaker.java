package com.example.cam.demo;

public class ByteTaker {
    private int id;
    private byte[] bytes;
    public ByteTaker(byte[] can){
        this.bytes=can;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
