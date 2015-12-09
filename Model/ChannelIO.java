import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

public class ChannelIO {
    protected SocketChannel socketChannel;
    protected ByteBuffer requestBuffer;  //存放请求数据
    private static int requestBufferSize = 4096;

    public ChannelIO(SocketChannel socketChannel, boolean blocking) throws IOException {
        this.socketChannel = socketChannel;
        this.socketChannel.configureBlocking(blocking);  //设置模式
        requestBuffer = ByteBuffer.allocate(requestBufferSize);
    }

    public SocketChannel getSocketChannel() {
        return socketChannel;
    }

    /*
    *如果原缓冲区的剩余容量不够，就创建一个新的缓冲区， 容量为原来的两倍
    *把原来的缓冲区的数据复制到新缓冲区
    */
    protected void resizeRequestBuffer(int remaining) {
        //极限位置到当前位置的大小
        if (requestBuffer.remaining() < remaining) {
            //把容量增大到原来的两倍
            ByteBuffer bb = ByteBuffer.allocate(requestBuffer.capacity() * 2);
            requestBuffer.flip();
            bb.put(requestBuffer);  //把原来的缓冲区的数据复制到新缓冲区
            requestBuffer = bb;
        }
    }

    /*接受数据，把他们存放到requestBuffer中，如果requestBuffer的剩余量不足5% 就通过resizeRequestBuffer()方法扩充容量
    */
    public int read() throws IOException {
        resizeRequestBuffer(requestBufferSize/20);
        return socketChannel.read(requestBuffer);
    }

    /* 返回requestBuffer， 它存放了请求数据 */
    public ByteBuffer getReadBuf(){
        return requestBuffer;
    }

    //发送参数指定的ByteBuffer中的数据
    public int write(ByteBuffer src) throws IOException {
        return socketChannel.write(src);
    }
    
    /*把FileChannel中的数据写到SocketChannel中
    */
    public long transferTo(FileChannel fc, long pos, long len) throws IOException {
        //Transfers bytes from this channel's file to the given writable byte channel.
        return fc.transferTo(pos, len, this.socketChannel);
    }

    //关闭SocketChannel
    public void close() throws IOException {
        socketChannel.close();
    }
    
}
