package cn.com.haohan.socket;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;


public class LineBuffer {

    private final int DEFAULT_SIZE = 512;
    private byte[] buffers;
    private int index = 0;
    private int CURRENT_SIZE;
    private Charset charset = Charset.forName("UTF-8");

    private InputStream inputStream;

    public LineBuffer(InputStream inputStream){
        this.inputStream = inputStream;
        buffers = new byte[DEFAULT_SIZE];
    }

    /***
     *
     * @param inputStream
     * @param bufferSize
     */
    public LineBuffer(InputStream inputStream,int bufferSize){
        this.inputStream = inputStream;
        if(bufferSize > 0) {
            buffers = new byte[bufferSize];
            CURRENT_SIZE = bufferSize;
        }else {
            buffers = new byte[DEFAULT_SIZE];
            CURRENT_SIZE = DEFAULT_SIZE;
        }
    }


    public String read(){
        int aByte = -1;
        int flag = 0;
        try {
            while((aByte = inputStream.read()) != -1) {
                if(aByte == '\r'){
                    flag = 1;
                }else if(aByte == '\n'){
                    if(flag == 1){
                        index--;
                        break;
                    }
                }else{
                    flag = 0;
                }
                buffers[index++] = (byte)aByte;
                if(index == CURRENT_SIZE)
                    expansion();
            }

            if(index <= 0){
                return null;
            }
            return new String(buffers,0,index,charset);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            reset();
        }
        return null;
    }

    public void close(){
        if(this.inputStream!=null){
            try {
                this.inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void expansion(){
        System.out.println(String.format("扩容前：%d",CURRENT_SIZE));
        CURRENT_SIZE += DEFAULT_SIZE;
        byte[] newBytes = new byte[CURRENT_SIZE+DEFAULT_SIZE];
        System.arraycopy(buffers,0,newBytes,0,index);
        this.buffers = null;
        this.buffers = newBytes;
        System.out.println(String.format("扩容后：%d",CURRENT_SIZE));
    }

    private void reset(){
        index = 0;
    }
}
