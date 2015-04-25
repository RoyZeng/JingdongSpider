package net.spider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import org.apache.logging.log4j.LogManager;

public class SpiderThread extends Thread {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Spider.class);
    HtmlBrowser htmlBrowser = new HtmlBrowser();
    List<String> urlList;
    List<ItemData> itemDataList = new ArrayList<>();

    public SpiderThread(List<String> urlList) {
        this.urlList = urlList;
    }

    @Override
    public void run() {
        int i = 0;
        System.out.println("run:" + urlList);
        while (i < urlList.size()) {
            HtmlPage htmlPage = htmlBrowser.getHtmlPage(urlList.get(i));
            itemDataList.add(getItemDataFromHtmlPage(htmlPage));
            i++;
        }
    }

    List<ItemData> getItemList() {
        return itemDataList;
    }

    /**
     * Crawl infomation from web page and store them in object ItemData
     *
     * @param htmlPage
     * @return ItemData
     */
    ItemData getItemDataFromHtmlPage(HtmlPage htmlPage) {
        ItemData item = new ItemData();
        //id
        DomAttr attribute = (DomAttr) htmlPage.getFirstByXPath("//*[@id='parameter2']/li[2]/@title");
        String idString = attribute.getNodeValue();
        Integer ID = Integer.parseInt(idString);
        System.err.println(ID);
        item.setID(ID);

        //name
        DomAttr nameDom = (DomAttr) htmlPage.getFirstByXPath(" //*[@id='parameter2']/li[1]/@title");
        String Name = nameDom.getNodeValue();
        System.err.println(Name);
        item.setName(Name);

        //price
        String line;
        StringBuffer pageBuffer = new StringBuffer();
        URL pageUrl = null;
        try {
            pageUrl = new URL("http://p.3.cn/prices/mgets?skuIds=J_" + idString + "&type=1");
        } catch (MalformedURLException e1) {
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
            while ((line = br.readLine()) != null) {
                pageBuffer = pageBuffer.append(line);
            }
        } catch (IOException e) {
        }
        String priceString1 = pageBuffer.toString();

        JSONArray array = JSONArray.fromObject(priceString1);
        JSONObject jsonObject = array.getJSONObject(0);
        String priceString = jsonObject.getString("p");
        System.err.println(priceString);
        double Price = Double.parseDouble(priceString);
        item.setPrice(Price);

        //brand
        String Brand = htmlPage.getFirstByXPath("//div[@id='product-detail-2']/table/tbody/tr[3]/td[2]/text()").toString();
        System.err.println(Brand);
        item.setBrand(Brand);

        //date
        String Date = htmlPage.getFirstByXPath("//div[@id='product-detail-2']/table/tbody/tr[6]/td[2]/text()").toString();
        System.err.println(Date);
        item.setDate(Date);

        //model
        String Model = htmlPage.getFirstByXPath("//div[@id='product-detail-2']/table/tbody/tr[4]/td[2]/text()").toString();
        System.err.println(Model);
        item.setModel(Model);

        //rate   
        HtmlPage page = htmlBrowser.getHtmlPage("http://club.jd.com/review/" + idString + "-1-1.html");
        DomText rateString = page.getFirstByXPath("//*[@id='i-comment']/div[1]/strong/text()");
        Integer Rate = 0;
        if (rateString != null) {
            Rate = Integer.parseInt(rateString.toString());
            System.err.println(rateString);
        }
        item.setRate(Rate);

        //reviewList
        List<String> ReviewList = new ArrayList<>();
        String judge = "心 得：";
        int i = 0;
        while (true) {
            try {
                String Review = null;
                String judge1 = page.getFirstByXPath("//*[@id='comment-" + i + "']/div/div[2]/div[2]/dl[1]/dt/text()").toString();
                if (judge1.equals(judge)) {
                    Review = page.getFirstByXPath("//*[@id='comment-" + i + "']/div/div[2]/div[2]/dl[1]/dd/text()").toString();
                } else {
                    Review = page.getFirstByXPath("//*[@id='comment-" + i + "']/div/div[2]/div[2]/dl[2]/dd/text()").toString();
                }
                ReviewList.add(Review);
                i++;
            } catch (Exception e) {
                break;
            }
        }
        System.err.println(ReviewList);
        item.setReviewList(ReviewList);

        return item;
    }

}
