package com.example.cam.demo;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockQueuePool {
    private BlockQueuePool(){}
    public synchronized static  BlockQueuePool getInstance() {
            return Instance.blockQueuePool;
    }
    public static BlockingQueue<ByteTaker> queue=new ArrayBlockingQueue<ByteTaker>(10);
    public static ArrayList<byte[]> cans=new ArrayList<>();

    private static class Instance{
        public static BlockQueuePool blockQueuePool=new BlockQueuePool();
    }
}
