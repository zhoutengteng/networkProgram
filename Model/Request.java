import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.regex.*;

/*代表客户的HTTP请求*/

public class Request {
    //枚举类，表示HTTP请求方式
    static class Action {
        private String name;
        private Action(String name){
            this.name = name;
        }
        public String toString() {
            return name;
        }
        static Action GET = new Action("GET");
        static Action PUT = new Action("PUT");
        static Action POST = new Action("POST");
        static Action HEAD = new Action("HEAD");

        public static Action parse(String s) {
            if (s.equals("GET")) return GET;
            if (s.equals("PUT")) return PUT;
            if (s.equals("POST")) return POST;
            if (s.equals("HEAD")) return HEAD;
            throw new IllegalArgumentException(s);
        }
    }

    private Action action;
    private String version;
    private URI uri;

    public Action action() {
        return this.action;
    }
    public String version(){
        return this.version;
    }
    public URI uri() {
        return uri;
    }
    private Request(Action a, String v, URI u) {
        this.action = a;
        this.version = v;
        this.uri = u;
    }
    public String toString() {
        return (action + " " + version + " " + uri);
    }

    private static Charset requestCharset = Charset.forName("GBK");

    /*判断ByteBuffer 是否包含了HTTP请求的所有数据。
    *HTTP请求以"\r\n\r\n"结尾
    */
    public static boolean isComplete(ByteBuffer bb) {
        ByteBuffer temp = bb.asReadOnlyBuffer();
        temp.flip();
        String data = requestCharset.decode(temp).toString();
        if (data.indexOf("\r\n\r\n") != -1) {
            return true;
        }
        return false;
    }

    /*删除请求的正文，不处理HTTP请求中的正文部分, 仅支持GET和HEAD请求方式
    *
    */
    private static ByteBuffer deleteContent(ByteBuffer bb) {
        ByteBuffer temp  =  bb.asReadOnlyBuffer();
        String data = requestCharset.decode(temp).toString();
        if (data.indexOf("\r\n\r\n") != -1) {
            data = data.substring(0,data.indexOf("\r\n\r\n") + 4);
            return  requestCharset.encode(data);
        }
        return bb;
    }   

    /*设定用于解析HTTP请求的字符串匹配模式。
       GET /dir/file HTTP/1.1
       Host: home
       解析为
        group[1] ="GET"
        group[2] = "/dir/file"
        group[3] = "1.1"
        group[4] = "hostname"
     */
    private static Pattern requestPattren = Pattern.compile("\\A([A-Z]+) +([^ ]+) HTTP/([0-9\\.]+)$" + 
                               ".*^Host: ([^ ]+)$.*\r\n\r\n\\z", Pattern.MULTILINE | Pattern.DOTALL);

   //解析HTTP请求，创建相应的Request对象

    public static Request parse(ByteBuffer bb) throws Exception {

        bb = deleteContent(bb); //删除请求正文
        CharBuffer cb = requestCharset.decode(bb); //解码
        System.out.println("request str :\n" + cb.toString());
        Matcher m =  requestPattren.matcher(cb.toString());  //进行字符串匹配
        //如果HTTP请求与指定的字符串模式不匹配， 说明请求数据不正确
        if (!m.matches()) {
            //don't two parse
            System.out.println("parse Exception");
            //throw new Exception();
            return null;
        }
        Action a;
        try {
            a = Action.parse(m.group(1));
        } catch (IllegalArgumentException x) {
            throw new Exception();
        }

        URI u;
        try {
            u = new URI("http://" + m.group(4) + m.group(2));
        } catch (URISyntaxException x) {
            throw new Exception();
        
        }

        System.out.println(m.group(1) +  "  " + m.group(2) +  "  " + m.group(3) + "  " + m.group(4));
        return new Request(a,m.group(3),u);

    }
}



