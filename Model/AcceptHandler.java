import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;


public class AcceptHandler implements Handler {
    SocketChannel socketChannel = null;
    public void handle(SelectionKey key) throws IOException {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel)key.channel();
        //在非阻塞模式下，serverSocketChannel.accept()有可能返回null
        socketChannel = serverSocketChannel.accept();
        if (socketChannel == null) return;
        System.out.println("接收到客户端连接,来自:" +  socketChannel.socket().getInetAddress() +":" + socketChannel.socket().getPort());
        ChannelIO cio = new ChannelIO(socketChannel, false/*非阻塞模式*/);
        RequestHandler rh = new RequestHandler(cio);
        socketChannel.register(key.selector(), SelectionKey.OP_READ, rh);
    }

    public SocketChannel  getSocketChannel() {
        return this.socketChannel;    
    }


}
