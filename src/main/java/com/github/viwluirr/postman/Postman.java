package com.github.viwluirr.postman;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import com.github.viwluirr.crypt.Sm4Example;
import com.github.viwluirr.dto.Descript;
import com.github.viwluirr.file.FileDeal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author: viwluirr
 * Date: 2020-08-27
 * Time: 17:13
 */
public class Postman {
    public static String get(String url, String encode) {
        HttpGet get = new HttpGet(url);
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        String result = "";
        try{
            HttpClientContext ctx = getHttpContext();
            response = client.execute(get,ctx);
            setHttpClientContext(ctx);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }
    public static String post(String url,String encode) {
        String uri = "";
        String parameter = "";
        String result = "";
        if(uri.contains("?")) {
            uri = url.substring(0,url.indexOf("?"));
            parameter = url.substring(url.indexOf("?")+1);
        }
        else{
            uri = url;
        }
        HttpPost post = new HttpPost(uri);
        post.setHeader("Content-Type","application/x-www-form-urlencoded;charset="+encode);
        CloseableHttpClient client = HttpClients.createDefault();
        if(!"".equals(parameter)) {
            post.setEntity(new StringEntity(parameter,encode));
        }
        try {
            HttpClientContext ctx = getHttpContext();
            CloseableHttpResponse response = client.execute(post,ctx);
            setHttpClientContext(ctx);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    public static String postFile(String url, String encode, byte[] fileBytes, Descript descript) {
        String result = "";
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost post = new HttpPost(url);
        // 创建文件上传的请求实体
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.addBinaryBody("file", fileBytes, ContentType.APPLICATION_OCTET_STREAM, descript.getFilename());
        builder.addTextBody("length",descript.getLength());
        builder.addTextBody("filename",descript.getFilename());
        builder.addTextBody("start",descript.getStart());
        builder.addTextBody("end",descript.getEnd());
        builder.addTextBody("block",descript.getBlock());

//        post.setHeader("Content-Type","application/x-www-form-urlencoded;charset="+encode);
//        CloseableHttpClient client = HttpClients.createDefault();
//        if(!"".equals(parameter)) {
//            post.setEntity(new StringEntity(parameter,encode));
//        }
        try {
            HttpEntity entity = builder.build();
            post.setEntity(entity);
            CloseableHttpResponse response = httpClient.execute(post);
            HttpEntity responseEntity = response.getEntity();
            result = EntityUtils.toString(responseEntity);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
    private static HttpClientContext httpContext;
    private static HttpClientContext getHttpContext() {
        HttpClientContext ctx = getHttpClientContext();
        if(ctx == null) {
            httpContext = HttpClientContext.create();
        }
        else{
            httpContext = ctx;
        }
        return httpContext;
    }
    private static HttpClientContext getHttpClientContext(){
        String classpath = "cookie.dat";
        File file = new File(classpath);
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            CookieStore cookie = (CookieStore)ois.readObject();
            HttpClientContext ctx = new HttpClientContext();
            ctx.setCookieStore(cookie);
            return ctx;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
    private static void setHttpClientContext(HttpClientContext httpContext){
        String classpath = "cookie.dat";
        File file = new File(classpath);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
            CookieStore cookie = httpContext.getCookieStore();
            oos.writeObject(cookie);
            oos.flush();
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Test
    public void testPost(){
        String result = Postman.get("https://www.baidu.com","utf-8");
        System.out.println(result);
    }

    public static void main(String[] args) {
        System.out.println("start:"+System.currentTimeMillis());
        String url = args[0];
        String filepath = args[1];
        String filename = args[2];
        String key = args[3];
        byte[] fileBytes = FileDeal.readFromPath(filepath);
        int fileBlock = 4 * 1024 * 1024; //4M
        Descript descript = new Descript();
        descript.setLength(String.valueOf(fileBytes.length));
        descript.setFilename(filename);
        int i = 0;
        ExecutorService executor = Executors.newFixedThreadPool(15);
        while(true){
            int thisBlock = i+fileBlock > fileBytes.length?fileBytes.length-i:fileBlock;
            Descript descriptThread = descript.clone();
            descriptThread.setStart(String.valueOf(i));
            descriptThread.setBlock(String.valueOf(thisBlock));
            descriptThread.setEnd(String.valueOf(i+thisBlock));
            try {
                byte[] encryptData = Sm4Example.encrypt(Arrays.copyOfRange(fileBytes,i,i+thisBlock),Base64.getDecoder().decode(key));
                PostmanThread postmanThread = new PostmanThread(url,"utf-8",encryptData,descriptThread);
                executor.submit(postmanThread);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            i+=thisBlock;
            if(i >= fileBytes.length){
                break;
            }
        }
        executor.shutdown();
        System.out.println("end:"+System.currentTimeMillis());
    }
}
