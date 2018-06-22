package com.example.cam.demo;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.Channel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TcpNioServer {
    //通道管理器

    public Selector selector;

    boolean isstart=false;

    private Map<String,SocketAddress> map=new HashMap<>();
    /**
     * 获得一个ServerSocket通道，并对该通道做一些初始化的工作
     * @param port  绑定的端口号
     * @throws IOException
     */
    public void initServer(int port) throws IOException {
        // 获得一个ServerSocket通道
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        // 设置通道为非阻塞
        serverChannel.configureBlocking(false);
        // 将该通道对应的ServerSocket绑定到port端口
        serverChannel.socket().bind(new InetSocketAddress(port));
        // 获得一个通道管理器
        this.selector = Selector.open();
        //将通道管理器和该通道绑定，并为该通道注册SelectionKey.OP_ACCEPT事件,注册该事件后，
        //当该事件到达时，selector.select()会返回，如果该事件没到达selector.select()会一直阻塞。
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    /**
     * 采用轮询的方式监听selector上是否有需要处理的事件，如果有，则进行处理
     * @throws IOException
     */
    public void listen()throws IOException {
        System.out.println("服务端启动成功！");
        while (true) {
            //当注册的事件到达时，方法返回；否则,该方法会一直阻塞
            selector.select();
            // 获得selector中选中的项的迭代器，选中的项为注册的事件
            Iterator<SelectionKey> ite = this.selector.selectedKeys().iterator();
            while (ite.hasNext()) {
                SelectionKey key = (SelectionKey)ite.next();
                // 删除已选的key,以防重复处理
                ite.remove();
                // 客户端请求连接事件
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel)key
                            .channel();
                    // 获得和客户端连接的通道
                    SocketChannel channel =server.accept();
                    // 设置成非阻塞
                    channel.configureBlocking(false);
                    //在这里可以给客户端发送信息哦
//                    channel.write(ByteBuffer.wrap(new String("向客户端发送了一条信息").getBytes()));
                    //在和客户端连接成功之后，为了可以接收到客户端的信息，需要给通道设置读的权限。
                    channel.register(this.selector, SelectionKey.OP_READ);
                    // 获得了可读的事件
                    startThread();
                } else if (key.isReadable()) {
                    read(key);
                }
            }
        }

    }
    /**
     * 处理读取客户端发来的信息 的事件
     * @param key
     * @throws IOException
     */
    public void read(SelectionKey key) throws IOException {
        // 服务器可读取消息:得到事件发生的Socket通道
        SocketChannel channel = (SocketChannel)key.channel();
        // 创建读取的缓冲区
        ByteBuffer buffer = ByteBuffer.allocate(12*10+11);
        if(channel.read(buffer)==-1){
            System.out.println(" 断开连接");
            channel.close();
            key.channel();
            return;
        }
        byte[] data =buffer.array();
        String msg = new String(data).trim();
        System.out.println(new Date().toString() +" 服务端收到信息："+byteArrayToHexStr(data));
        //协议规范 第三位为can信息的条数 接下来是can信息 最后接8为GPS 时钟信息
        if((data[0]!=65)||(data[1]!=84))
            return;
        int size=data[2];
        BlockQueuePool.getInstance().cans.clear();
        if(!(size>10||size<0)){
            int i=0;
            for(;i<size;i++){
                byte[] can=new byte[12];
                System.arraycopy(data,3+i*12,can,0,12);
                BlockQueuePool.getInstance().cans.add(can);
                System.out.println("添加Can信息"+byteArrayToHexStr(can));
            }
            byte[] time=new byte[8];
            System.arraycopy(data,i*12+3,time,0,8);
            BlockQueuePool.getInstance().cans.add(time);
        }
//        ByteBuffer outBuffer = ByteBuffer.wrap(msg.getBytes());
//        channel.write(outBuffer);// 将消息回送给客户端

    }
    /**
     * 启动服务端测试
     * @throws IOException
     */

    public static void start() throws IOException {
        TcpNioServer server = new TcpNioServer();
        server.initServer(8899);
        server.listen();

    }
    public String byteArrayToHexStr(byte[] byteArray) {
        if (byteArray==null){
            return null;
        }
        char[] hexArray = "0123456789ABCDEF".toCharArray();
        char[] hexChars = new char[byteArray.length * 2];
        for (int j = 0; j < byteArray.length; j++) {
            int v = byteArray[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * 广播给所有用户
     *
     * @param selector
     * @param content
     * @throws IOException
     */
    public void broadcastToAllClient(Selector selector,byte[] content)
            throws IOException {

        for (SelectionKey key : selector.keys()) {
            Channel targetchannel = key.channel();
            if (targetchannel instanceof SocketChannel) {
                ((SocketChannel) targetchannel).write(ByteBuffer.wrap(content));
                System.out.println(" send"+byteArrayToHexStr(content));
            }

        }

    }

    public void startThread(){

        if(!isstart) {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            byte[] bytes = BlockQueuePool.getInstance().queue.take().getBytes();
                            try {
                                broadcastToAllClient(selector, bytes);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            thread.start();
            isstart=true;
        }
    }
}
