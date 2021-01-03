package cn.com.haohan.test;

import cn.com.haohan.socket.LineBuffer;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class LineBufferTest {


    @Test
    public void readALine(){
        LineBuffer lineBuffer = null;
        try {
            FileInputStream fileInputStream = new FileInputStream("e:\\input\\abc.txt");
            lineBuffer = new LineBuffer(fileInputStream,1);
            String line = null;
            while((line = lineBuffer.read()) != null){
                System.out.println(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }finally {
            lineBuffer.close();
        }
    }
}
