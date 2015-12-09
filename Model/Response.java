import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

//接口为自定义
public class Response implements Sendable {
    static class Code {
        private int number;              //枚举类，表示状态代码
        private String reason;
        private Code(int i, String r) {
            number = i;
            reason = r;
        }

        public String toString() {
            return number + " " + reason;
        }
        static Code OK =new Code(200, "OK");
        static Code BAD_REQUEST = new Code(400, "Bad Request");
        static Code NOT_FOUND = new Code(404, "Not Foune");
        static Code METHOD_NOT_ALLOWED = new Code(405, "Method Not Allowed");
    }

    private Code code; //状态码
    private Content content;  //响应正文
    private boolean headersOnly; // 表示HTTP响应中是否仅包含响应头
    private ByteBuffer headerBuffer = null;  //响应头

    public Response(Code rc, Content c) {
        this(rc, c, null);
    }

    public Response(Code rc, Content c, Request.Action head) {
        this.code = rc;
        //这里content必定被子类所强转
        this.content = c;
        headersOnly = (head == Request.Action.HEAD);
    }
    private static String CRLF = "\r\n";
    private static Charset responseCharset = Charset.forName("GBK");
    
    /*创建响应头的内容，把它存放到一个ByteBuffer中*/
    private ByteBuffer headers() {
        CharBuffer cb = CharBuffer.allocate(1024);
        for (;;) {
            try {
                cb.put("HTTP/1.1").put(code.toString()).put(CRLF);
                cb.put("Server:nio/1.1").put(CRLF);
                cb.put("Content-type:").put(content.type()).put(CRLF);
                cb.put("Content-length:").put(Long.toString(content.length())).put(CRLF);
                cb.put(CRLF);
                break;
            } catch (BufferOverflowException x) {
                assert(cb.capacity() < (1 << 16));
                cb = CharBuffer.allocate(cb.capacity()*2);
                continue;
            }
        }
        cb.flip();
        return responseCharset.encode(cb); // 编码
    }

    //准备HTTP响应中的正文， 以及响应头的内容
    public void  prepare() throws IOException {
        content.prepare();
        headerBuffer = headers();
    }

    //发送HTTP响应，如果全部发送完毕，返回false，否则返回true
    public boolean send(ChannelIO cio) throws IOException {
        if (headerBuffer == null) {
            throw new IllegalStateException();
        }
        //发送响应头  
        if (headerBuffer.hasRemaining()) {
            //这里感觉有点问题？？？？？？？？？？？？？？？？？？？？？？？？？？
            //应该是考虑发送失败返回负数的意思
            if (cio.write(headerBuffer) <= 0) return true;
        }
        //发送响应正文
        if (!headersOnly){
            if (content.send(cio)) return true;
        }
        return false;
    }

    /*释放响应正文占用的资源
    */
    public void release() throws IOException {
        content.release();
    }

}






