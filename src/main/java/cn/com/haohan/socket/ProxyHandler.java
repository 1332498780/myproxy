package cn.com.haohan.socket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProxyHandler implements Runnable{

    private InputStream clientInput;
    private OutputStream proxyOutput;

    public ProxyHandler(InputStream inputStream, OutputStream outputStream){
        this.clientInput = inputStream;
        this.proxyOutput = outputStream;
    }

    public void run() {

        try {
            byte[] bytes = new byte[512];
            while(true){
                int count= -1;
                if((count = this.clientInput.read(bytes)) != -1){
                    String readStr = new String(bytes,0,count,"UTF-8");
                    System.out.println("客户端发送内容："+readStr);
                    this.proxyOutput.write(bytes,0,count);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
