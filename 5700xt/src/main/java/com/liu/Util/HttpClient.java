package com.liu.Util;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

@Component
public class HttpClient {
    //声明连接池
    public PoolingHttpClientConnectionManager phc;

    public HttpClient() {
        this.phc =new PoolingHttpClientConnectionManager();
        //配置各种连接参数
        phc.setMaxTotal(100);//最大连接数
        phc.setDefaultMaxPerRoute(10);//设置每台主机最大连接数
    }

    //根据请求地址下载页面数据
    public String doGetHtml(String url){
        //获取httpclient对象
        CloseableHttpClient build = HttpClients.custom().setConnectionManager(this.phc).build();
        //创建httpGet请求对象
        HttpGet httpGet=new HttpGet(url);
        httpGet.setConfig(this.getConfig());
        //使用httpclient发起请求获取响应
        CloseableHttpResponse response=null;
        try {
             response = build.execute(httpGet);
            //解析响应
            if (response.getStatusLine().getStatusCode()==200){
                //判断响应体是否不为空，只有当响应体不为空时才能继续解析
                if (response.getEntity()!=null){
                    return EntityUtils.toString(response.getEntity(), "utf8");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //如果请求头为空，则返回空串
         return "";
    }
    //下载图片，返回图片的名称
    public String doGetImg(String url){
        //获取httpclient对象
        CloseableHttpClient build = HttpClients.custom().setConnectionManager(this.phc).build();
        //创建httpGet请求对象
        HttpGet httpGet=new HttpGet(url);
        httpGet.setConfig(this.getConfig());
        //使用httpclient发起请求获取响应
        CloseableHttpResponse response=null;
        try {
            response = build.execute(httpGet);
            //解析响应
            if (response.getStatusLine().getStatusCode()==200){
                //判断响应体是否不为空，只有当响应体不为空时才能继续解析
                if (response.getEntity()!=null){
                    //获取图片后缀
                    String imgName=url.substring(url.lastIndexOf("."));
                    //创建图片名，重命名图片
                    String picName= UUID.randomUUID().toString()+imgName;
                    //声明OutPutStream
                    OutputStream outputStream=new FileOutputStream(new File("E:\\英雄时刻\\"+picName));
                    //下载图片
                    response.getEntity().writeTo(outputStream);
                    //返回图片名称
                    return picName;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if (response!=null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        //如果下载失败，则返回空字符串
        return "";
    }
    //设置请求信息
    private RequestConfig getConfig() {


        return RequestConfig.custom()
                .setConnectTimeout(5000)//最大创建连接时间
                .setConnectionRequestTimeout(1000)//获取链接最大时间
                .setSocketTimeout(10*10000)//数据传输的最长时间
                .build();
    }
}
