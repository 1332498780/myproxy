package cn.com.haohan.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class SocketHandle implements Runnable{

    private Socket socket;

    public SocketHandle(Socket socket){
        this.socket = socket;
    }

    public void run() {
        InputStream clientInput = null;
        OutputStream clientOutput = null;

        InputStream proxyInput = null;
        OutputStream proxyOutput = null;

        try {
            clientInput = socket.getInputStream();
            clientOutput = socket.getOutputStream();

            LineBuffer lineBuffer = new LineBuffer(clientInput);
            String line = null;
            String host = null;
            StringBuilder sb = new StringBuilder();
            while((line = lineBuffer.read())!=null){
                if(line.length() == 0){
                    break;
                }
                sb.append(line).append("\r\n");
                String[] segment = line.split(" ");
                if(segment[0].contains("Host")){
                   host = segment[1];
                }
            }
            String header = sb.toString();
            String method = header.substring(0,header.indexOf(" "));
            int port = 80;
            String[] hosts = host.split(":");
            if(hosts.length == 2){
                host = hosts[0];
                port = Integer.valueOf(hosts[1]);
            }
            Socket proxySocket = new Socket(host,port);
            proxyOutput = proxySocket.getOutputStream();
            proxyInput = proxySocket.getInputStream();
            if("CONNECT".equals(method)){
                //https
                proxyOutput.write("HTTP/1.1 200 Connection Established\\r\\n\\r\\n".getBytes());
            }else{
                proxyOutput.write(header.getBytes());
            }
            new Thread(new ProxyHandler(clientInput,proxyOutput)).start();
            byte[] bytes = new byte[512];
            while(true){
                int count= -1;
                if((count = proxyInput.read(bytes)) != -1){
                    String readStr = new String(bytes,0,count,"UTF-8");
                    System.out.println("服务端返回内容："+readStr);
                    clientOutput.write(bytes,0,count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
                try {
                    if(proxyInput != null) {
                        proxyInput.close();
                    }
                    if(proxyOutput!=null){
                        proxyOutput.close();
                    }
                    if(clientInput!=null){
                        clientInput.close();
                    }
                    if(clientOutput!=null){
                        clientOutput.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    public static void main(String[] args){
        try {
            ServerSocket serverSocket = new ServerSocket(9000);
            for(;;){
                new Thread(new SocketHandle(serverSocket.accept())).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
