package net.spider;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class Spider  {

    public static final String ItemUrlFilePath = "E:/Coding/Java/JingdongSpider/resources/PartialItemLinks.txt";
    public static final String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";
    HtmlBrowser htmlBrowser = new HtmlBrowser();
    public int count = 0;
    public static final int THREAD_NUM = 50;
    List<ItemData> itemList = new ArrayList<>();
    List<SpiderThread> spiderThreadList = new ArrayList<>();

    public Spider() {

    }

    /**
     * start crawling the urls and store the page infomation into the file
     */
    public void Crawl() {

        //read url file and split them into pieces to fit the thread number
        //TODO
        //initialize the array of SpiderThread and start the thread
        for (int i = 0; i < THREAD_NUM; i++) {
            SpiderThread st = new SpiderThread(null);
            spiderThreadList.add(st);
            st.start();
        }

        //waiting for the end of threads
        for (SpiderThread st : spiderThreadList) {
            try {
                st.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(Spider.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        //collecting the crawler results
        for (SpiderThread st : spiderThreadList) {
            List<ItemData> threadItemDataList = st.getItemList();
            itemList.addAll(threadItemDataList);
        }
        //store the cralwer results into the file
         //TODO
    }

    /**
     * start crawl from here
     */
    public void run() {
        try {
            items = items(ItemUrlFilePath, count);
        } catch (Exception e) {
        }
        String data = DataProcessor.itemData2Json(items);
        try {
            DataProcessor.string2File("E:/testItems" + count + ".txt", data);
        } catch (IOException e) {
        }
        System.out.println(count);
    }

    List<ItemData> items(String path, int i) throws Exception {
        String[] linkArray = DataProcessor.readUrlsFromFile(path);
        List<ItemData> itemDataList = new ArrayList<>();
        int length = linkArray.length / (threadCount - 1);
        int index = i * length;
        if (i == threadCount - 1) {
            int j = (threadCount - 1) * length;
            while (j < linkArray.length) {
                HtmlPage htmlPage = htmlBrowser.getHtmlPage(linkArray[j]);
                itemDataList.add(getItemDataFromHtmlPage(htmlPage));
                j++;
            }
        } else {
            int j = index;
            int end = index + length;
            while (j < end) {
                HtmlPage htmlPage = htmlBrowser.getHtmlPage(linkArray[j]);
                itemDataList.add(getItemDataFromHtmlPage(htmlPage));
                j++;
            }
        }
        return itemDataList;
    }

    /**
     * Get all the links related to phones from the web page source
     *
     * @param url
     * @return
     */
    //List<String> getAllAnchors(String url, int minium) throws Exception {
    List<String> getAllAnchors(String url) throws Exception {
        List<String> allPages = new ArrayList<>();
        allPages = getAllPages(url);
        List<String> links = new ArrayList<>();
        List<String> allLinks = new ArrayList<>();

        int i = 0;
        int size = allPages.size();
        while (i < size) {
            links = getOnePageAnchors(allPages.get(i));
            allLinks.addAll(links);
            i++;
        }
        return allLinks;
    }

    List<String> getOnePageAnchors(String url) throws Exception {
        HtmlPage page_1 = htmlBrowser.getHtmlPage(url);
        final List<DomAttr> href = (List<DomAttr>) page_1.getByXPath("//div[@id='plist']/ul[1]/li/div/div[1]/a[@href]/@href");
        List<String> OnePageLinks = new ArrayList<>();

        for (DomAttr htmlanchor : href) {
            System.out.println(htmlanchor.getNodeValue());
            OnePageLinks.add(htmlanchor.getNodeValue());
        }
        return OnePageLinks;
    }

    List<String> getAllPages(String url) throws Exception {
        List<String> allPages = new ArrayList<>();
        allPages.add(url);
        String nextPageString = url;

        while (true) {
            HtmlPage page = htmlBrowser.getHtmlPage(url);
            final DomAttr nextPage = (DomAttr) page.getFirstByXPath("//*[@id='J_bottomPage']/span[1]/a[10]/@href");
            if (nextPage == null) {
                break;
            }
            nextPageString = "http://list.jd.com" + nextPage.getNodeValue();
            url = nextPageString;
            allPages.add(nextPageString);
            System.out.println(nextPageString);
        }
        return allPages;
    }

    /**
     * Crawl infomation from web page and store them in object ItemData
     *
     * @param htmlPage
     * @return ItemData
     */
    public ItemData getItemDataFromHtmlPage(HtmlPage htmlPage) {
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
