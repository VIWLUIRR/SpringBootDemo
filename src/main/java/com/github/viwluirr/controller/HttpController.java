package com.github.viwluirr.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.github.viwluirr.config.AppConfig;
import com.github.viwluirr.crypt.Sm4Example;
import com.github.viwluirr.dto.Descript;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;

@RestController
@RequestMapping("/http")
public class HttpController {
    @Autowired
    private AppConfig appConfig;
    @RequestMapping("/transfer")
    public String transfer(Descript descript, @RequestParam("file") MultipartFile file){
        String filename = descript.getFilename().substring(descript.getFilename().lastIndexOf("..")+1);
        filename = filename.substring(filename.lastIndexOf("/")+1);
        filename = filename.substring(filename.lastIndexOf("\\")+1);
        try (RandomAccessFile raFile = new RandomAccessFile(new File("D:/temp/"+filename),"rw");
             FileChannel fcin = raFile.getChannel()){
            byte[] bytes = file.getBytes();
            bytes = Sm4Example.decrypt(bytes,appConfig.getKey());
            while(true){
                try{
                    FileLock flin = fcin.tryLock();
                    break;
                }catch (Exception e){
                    System.out.println("sleep 20ms for file lock");
                    Thread.sleep(20);
                }
            }
            raFile.seek(Long.parseLong(descript.getStart()));
            raFile.write(bytes,0,Integer.parseInt(descript.getBlock()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "hello";
    }
}
