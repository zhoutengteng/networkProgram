import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

public class FileContent implements Content {
    //假定文件的根目录为"root", 
    private static File ROOT = new File("");
    private File file;

    public FileContent(URI uri) {
                System.out.println("ddd  "  +   uri);
        //file = new File(ROOT, uri.getPath().replace('/', File.separatorChar));
        file = new File(uri.toString().split("/")[3]);
    }

    private String type = null;

    /*确定文件类型*/
    public String type() {
        if (type != null) return type;
        String nm = file.getName();
        if (nm.endsWith(".html") || nm.endsWith(".htm")) {
            type = "text/html;charset=iso-8859-1"; //HTML网页
        } else if ((nm.indexOf('.') < 0) || nm.endsWith(".txt")) {
            type = "text/plain;charset=iso-8859-1";    //纯文本文件
        } else {
            type = "application/octet-stream";     //二进制文件
        }
        return type;
    }

    private FileChannel fileChannel = null;
    private long length = -1; //文件长度
    private long position = -1;   //文件的当前位置
    
    public long length() {
        return length;
    }

    /*以当前文件 创建FileChannel对象*/
    public void prepare() throws IOException {
        if (fileChannel == null) {
            fileChannel = new RandomAccessFile(file, "r").getChannel();
        }
        length = fileChannel.size();
        position = 0;
    }
    
    /*发送正文， 如果发送完毕，就返回false， 否则返回true*/
    public boolean send(ChannelIO channelIO)throws IOException {
        if (fileChannel == null) {
            throw new IllegalStateException();
        }
        if (position < 0) {
            throw new IllegalStateException();
        }
        if (position >= length) {
            return false;  //如果发送完毕，就返回false
        }
        position += channelIO.transferTo(fileChannel, position, length - position);
        return (position < length);
    }

    public void release() throws IOException {
        if (fileChannel != null) {
            fileChannel.close();   //关闭 fileChannel
            fileChannel = null;
        }
    }
    



}



