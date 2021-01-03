package cn.com.haohan.socket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class MyServerSocket implements Runnable{

    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(8000);
            Socket socket = serverSocket.accept();
            InputStream inputStream = socket.getInputStream();
            byte[] bytes = new byte[512];
            int count = -1;
            while((count = inputStream.read(bytes))!=-1){
                String readStr = new String(bytes,0,count,"UTF-8");
                System.out.println("client send: "+readStr);
                socket.getOutputStream().write(responseContent().getBytes());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String responseContent(){
        return "HTTP/1.1 200 OK\r\nContent-Length: 5\r\n\r\nhello";
//        server: Apache-Coyote/1.1
//        Content-Type: text/html;charset=utf-8
    }

    public static void main(String[] args){
        MyServerSocket serverSocket = new MyServerSocket();
        new Thread(serverSocket).start();
    }
}
