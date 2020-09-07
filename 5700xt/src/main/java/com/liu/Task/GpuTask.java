package com.liu.Task;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liu.Service.UserService;
import com.liu.Util.HttpClient;
import com.liu.pojo.Gpu;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class GpuTask {
    @Autowired
    private UserService userService;
    //注入连接池
    @Autowired
    private HttpClient httpClient;
    //创建一个用于解析json的工具类
    private static final ObjectMapper mapper=new ObjectMapper();

    //每次下载任务完成之后执行下一次下载任务的间隔时间
    @Scheduled(fixedDelay = 10 * 1000)
    public void downLoad() throws Exception {
        //声明初始访问地址
        String url = "https://search.jd.com/Search?keyword=5700%E6%98%BE%E5%8D%A1&suggest=2.def.0.base&wq=5700%E6%98%BE%E5%8D%A1&s=201&click=0&page=";
        for (int i = 0; i < 100; i=i+2) {
            String html = httpClient.doGetHtml(url + i);
            //定义方法解析获取到的html
            this.parse(html);
        }
        System.out.println("5700xt显卡数据抓取完成");
    }

    //创建方法解析获取到的html
    public void parse(String html) throws Exception {
        //获取document
        Document doc = Jsoup.parse(html);
        //获取spu信息
        Elements elements = doc.select("div#J_goodsList > ul > li");
        for (Element element : elements
        ) {
            //获取spu
            long spu;
            if (!"".equals(element.attr("data-spu"))){
                spu = Long.parseLong(element.attr("data-spu"));
            }else {
                spu=0;
            }
            //获取sku信息
            Elements elements1 = elements.select("li.gl-item");
            for (Element element1 : elements1) {
                //获取sku
                long sku = Long.parseLong(element1.select("[data-sku]").attr("data-sku"));
                //根据sku查询商品信息，如果改商品以保存过，则不再进行保存
                Gpu gpu = new Gpu();
                gpu.setSku(sku);
                List<Gpu> gpus = this.userService.findGpu(gpu);
                if (gpus.size() > 0) {
                    //如果该商品存在，则进行下一次循环，商品不保存，因为已存在
                    continue;
                }
                //设置商品spu
                gpu.setSpu(spu);
                //获取商品详细url地址
                String gpuUrl = "https://item.jd.com/" + sku + ".html";
                gpu.setUrl(gpuUrl);
                //获取商品图片
                String imgSrc = "https:"+element1.select("img[data-img]").first().attr("src");
                imgSrc=imgSrc.replace("n7","n1");
                String imgName=this.httpClient.doGetImg(imgSrc);
                System.out.println(imgName);
                gpu.setPic(imgSrc);
                //获取商品价格
                String priceJson = this.httpClient.doGetHtml("https://p.3.cn/prices/mgets?&skuId=" + sku);
                double price = mapper.readTree(priceJson).get(0).get("p").asDouble();
                gpu.setPrice(price);
                //获取商品标题
                String oldTitle = doc.getElementsByAttributeValue("href","//item.jd.com/" + sku + ".html").attr("title");
                String newTitle = oldTitle.replaceAll("[^\\u4e00-\\u9fa5]", "");
                //下面反爬虫，进去就是登陆页面
                //String urlInfo = this.httpClient.doGetHtml(gpuUrl);
                //String title = Jsoup.parse(urlInfo).select("div.sku-name").text();
                gpu.setTitle(newTitle);
                //获取创建以及更新时间
                gpu.setCreated(new Date());
                gpu.setUpdated(gpu.getCreated());
                //保存数据到数据库
                this.userService.save(gpu);
            }
        }
    }
}