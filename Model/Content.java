import java.net.*;
import java.io.*;
import java.nio.channels.*;
import java.nio.*;
import java.nio.charset.*;
import java.util.*;
import java.util.concurrent.*;

public interface Content extends Sendable {
    //正文的类型
    String type();
    //返回正文的长度
    //在正文还没有准备之前，即还没有调用prepare（）方法之前，length（）方法返回-1
    long length();
}
