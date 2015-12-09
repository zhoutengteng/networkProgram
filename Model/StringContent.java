import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

public class StringContent implements Content {
    private String myss;

    public StringContent(String str) {
        this.myss = str;
    }

    private String type = null;

    public String type() {
        if (type != null) return type;
        String nm = "ss.txt";
        if (nm.endsWith(".html") || nm.endsWith(".htm")) {
            type = "text/html;charset=iso-8859-1"; //HTML网页
        } else if ((nm.indexOf('.') < 0) || nm.endsWith(".txt")) {
            type = "text/plain;charset=iso-8859-1";    //纯文本文件
        } else {
            type = "application/octet-stream";     //二进制文件
        }
        return type;
    }

    private long position = 0;
    private long length = -1; //字符长度
    
    public long length() {
        return length;
    }

    /*以当前文件 创建FileChannel对象*/
    public void prepare() throws IOException {
        length = myss.length();
    }
    
    /*发送正文， 如果发送完毕，就返回false， 否则返回true*/
    public boolean send(ChannelIO channelIO)throws IOException {
        if (position < 0) {
            throw new IllegalStateException();
        }
        if (position >= length) {
            return false;  //如果发送完毕，就返回false
        }
        //这里可以划分成多次发送？？？？？？？？？？？？？？？？？？？？/
        channelIO.write(Charset.forName("GBK").encode(myss));
        position += length;
        return (position < length);
    }

    public void release() throws IOException {
        /*
        if (fileChannel != null) {
            fileChannel.close();   //关闭 fileChannel
            fileChannel = null;
        }
        */
    }
    



}



