package com.github.viwluirr.postman;

import com.github.viwluirr.dto.Descript;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

public class PostmanThread extends Thread {
    private String url;
    private String encode;
    private byte[] fileBytes;
    private Descript descript;

    public PostmanThread(String url, String encode, byte[] fileBytes, Descript descript) {
        this.url = url;
        this.encode = encode;
        this.fileBytes = fileBytes;
        this.descript = descript;
    }

    public void run() {
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
    }
}
