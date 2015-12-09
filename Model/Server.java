import java.io.*;
import java.nio.*;
import java.nio.charset.*;
import java.net.*;
import java.nio.channels.*;
import java.util.*;
import java.util.concurrent.*;   //有现成的api来实现线程池 比自己写的健壮

public class Server {
    private int port = 8000;
    private ExecutorService executorService;  // api中的线程池
    private final int POOL_SIZE = 4;
    private int portForShutdown = 8001;       //监听关闭服务器命令的端口
    private ServerSocket serverSocketForAdmin; // 用来关闭服务器的一个欢迎字
    private Selector  selector = null;  //选择器
    private ServerSocketChannel serverSocketChannel = null; // 欢迎套接字信道
    private boolean isShutdown = false; //服务器是否已经关闭
    private Thread AdminThread = null; // 给管理员的线程
    private ArrayList<SocketChannel>  workSocket = new ArrayList<SocketChannel>();
    private Object gate1 = new Object();
    private Object gate2 = new Object();



    //初始化线程池，初始化管理员线程，初始化欢迎套接字信道
    public   Server() throws IOException {
        initThreadPool();
        initServerSocketChannel();
        initAdminThread();
        System.out.println("服务启动");
    }

    public  void initThreadPool() {
        //构造线程池可用线程数， 系统的cpu越多，线程数也越多
        executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors()*POOL_SIZE);
    }

    public   void initServerSocketChannel() throws IOException{
        //构造选择器
        selector = Selector.open();
        //构造欢迎套接字信道
        serverSocketChannel  = ServerSocketChannel.open();
        //设置为非阻塞的方法
        serverSocketChannel.configureBlocking(false);
        //设置与这个欢迎套接字信道相关联的 欢迎套接字的地址可重用，防止服务器关闭后，重新启动服务器的绑定端口的问题
        serverSocketChannel.socket().setReuseAddress(true);
        // 开启管理员监听的端口 
        serverSocketForAdmin = new ServerSocket(portForShutdown);
        //监听端口
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
    }

    public  void initAdminThread() {
            //单独一个线程, 用来服务管理员的线程
        AdminThread = new Thread() {
            public void start(){
                this.setDaemon(true);                //设置为守护线程（也称为后台线程)
                super.start();
            }
            public void run(){
                while (!isShutdown){
                    Socket socketForShudown = null;
                    try{
                        socketForShudown = serverSocketForAdmin.accept();
                        //读取客户端发来的消息
                        BufferedReader br = new BufferedReader(new InputStreamReader(socketForShudown.getInputStream()));
                        String command = br.readLine();
                        if (command.equals("shutdown")) {
                            long beginTime = System.currentTimeMillis();
                            socketForShudown.getOutputStream().write("服务器正在关闭\r\n".getBytes());
                            isShutdown = true;
                            //线程池不再接受新的任务，但是会在继续执行完成工作对列中现有的任务
                            executorService.shutdown();
                            //等待关闭线程池，每次等待的超时时间为30秒
                            while(!executorService.isTerminated()) {
                                executorService.awaitTermination(30,TimeUnit.SECONDS); // 这里会阻塞的
                            }
                           // serverSocketChannel.close();
                            long endTime = System.currentTimeMillis();
                            synchronized(gate1) {
                                while (!workSocket.isEmpty()) {
                                    SocketChannel socketChannel = (SocketChannel) workSocket.remove(0);
                                    if (socketChannel != null) {
                                        socketChannel.socket().close();
                                        socketChannel.close();
                                       
                                    }
                                }
                            }
                            synchronized(gate2) {
                                selector.wakeup();
                            }
                            socketForShudown.getOutputStream().write(("服务器已经关闭, " + "关闭服务器用了" + (endTime - beginTime) + "毫秒\r\n").getBytes());
                            socketForShudown.close(); //关闭具体的服务字
                            serverSocketForAdmin.close(); // 关闭服务字
                        } else {
                            socketForShudown.getOutputStream().write("错误的命令\r\n".getBytes());
                            socketForShudown.close();
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }
        };
        AdminThread.start();
    }


    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream sockOut = socket.getOutputStream();
        return new PrintWriter(sockOut, true);
    }

    private BufferedReader getReader(Socket socket)throws IOException {
        InputStream sockIn = socket.getInputStream();
        return new BufferedReader(new InputStreamReader(sockIn));
    }

    public void service() throws IOException{
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT, new AcceptHandler());
        while (!isShutdown) {
            //select这里阻塞的
            synchronized (gate2) {}
            int n = selector.select();
            if (n == 0) continue;
            Set readyKeys = selector.selectedKeys();
            Iterator it = readyKeys.iterator();
            while (it.hasNext()) {
                SelectionKey key = null;
                try {
                    key = (SelectionKey)it.next();
                    it.remove();
                    final Handler handler = (Handler)key.attachment();
                    handler.handle(key);
                    //锁
                    synchronized(gate1) {
                        if (handler.getSocketChannel() != null) workSocket.add(handler.getSocketChannel());
                    }
                } catch (IOException e) {
                    e.printStackTrace(); 
                    try {
                        if (key != null) {
                            key.cancel();
                            key.channel().close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }
    public static void main(String args[]) throws IOException {
         new Server().service();
    }

}
