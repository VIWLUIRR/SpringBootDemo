package com.github.viwluirr.file;


import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author: viwluirr
 * Date: 2020-06-15
 * Time: 13:57
 */
public class FileDeal {
    public static void outToPath(byte[] bytes,String path) {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path))){
            bos.write(bytes);
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void appendToPath(byte[] bytes,String path) {
        try(BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(path,true))){
            bos.write(bytes);
            bos.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static byte[] readFromPath(String path) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try(BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path))){
            byte[] read = new byte[4096];
            int readLength = 0;
            while((readLength = bis.read(read)) != -1){
                bytes.write(read,0,readLength);
            }
            bytes.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes.toByteArray();
    }
    @Test
    public void testOutToPath(){
        byte[] bytes = readFromPath("D:/1.jpg");
        byte[] target = Arrays.copyOfRange(bytes,4153731,4153731+97010);
        outToPath(target,"2.7z");
    }
}
