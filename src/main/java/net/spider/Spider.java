package net.spider;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;


public class Spider {

    HtmlBrowser htmlBrowser = new HtmlBrowser();
    String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";

    public Spider() {
    }

    /**
     * start crawl from here
     */
    void excuteSpider() throws Exception {
        //List<String> AllAnchors = getAllAnchors(entryUrl);
        //record all links in a file
        //DataProcessor.string2File("E:/test.txt", JSONArray.fromObject(AllAnchors).toString());
        List<ItemData> items = items("z:/test.txt");
        String data = DataProcessor.itemData2Json(items);
        DataProcessor.string2File("z:/testItems.txt", data);
    }

    List<ItemData> items(String path) throws Exception {
        String[] linkArray = DataProcessor.readUrlsFromFile(path);
        int i = 0;
        List<ItemData> items = new ArrayList<>();
        htmlBrowser.enbaleJS();
        while (i < linkArray.length) {
            System.out.println(linkArray[i]);
            HtmlPage htmlPage = htmlBrowser.getHtmlPage(linkArray[i]);
            htmlPage = htmlBrowser.getHtmlPageAfterClick(htmlPage);

            FileWriter fileWriter = new FileWriter(new File("z:/testItem" + i + ".txt"));
            fileWriter.write(htmlPage.asXml());
            //items.add(getItemDataFromHtmlPage(htmlPage));    		
            i++;
        }
        return items;
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
            System.out.println(htmlanchor.getNodeValue().toString());
            OnePageLinks.add(htmlanchor.getNodeValue().toString());
        }
        //System.out.println(OnePageLinks);
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
            nextPageString = "http://list.jd.com" + nextPage.getNodeValue().toString();
            url = nextPageString;
            allPages.add(nextPageString);
            System.out.println(nextPageString);
        }
        //System.out.print(allPages);  	
        return allPages;
    }

    /**
     * Crawl infomation from web page and store them in object ItemData
     *
     * @param htmlPage
     * @return
     */
    ItemData getItemDataFromHtmlPage(HtmlPage htmlPage) {
        ItemData item = new ItemData();
        //id
        DomAttr attribute = (DomAttr) htmlPage.getFirstByXPath("//*[@id='parameter2']/li[2]/@title");
        String idString = attribute.getNodeValue().toString();
        int ID = Integer.parseInt(idString);
        System.err.println(ID);
        item.setID(ID);

        //name
        DomAttr nameDom = (DomAttr) htmlPage.getFirstByXPath(" //*[@id='parameter2']/li[1]/@title");
        String Name = nameDom.getNodeValue().toString();
        System.err.println(Name);
        item.setName(Name);

        //price
        String priceString = htmlPage.getFirstByXPath("//*[@id='jd-price']/text()").toString().substring(1);
        double Price = Double.parseDouble(priceString);
        System.err.println(Price);
        item.setPrice(Price);

        //brand
        String Brand = htmlPage.getFirstByXPath("//div[@id='product-detail-2']/table/tbody/tr[3]/td[2]/text()").toString();
        System.err.println(Brand);
        item.setBrand(Brand);

        //date
        String Date = htmlPage.getFirstByXPath("//div[@id='product-detail-2']/table/tbody/tr[6]/td[2]/text()").toString();
        System.err.println(Date);
        item.setBrand(Date);

        //model
        String Model = htmlPage.getFirstByXPath("//div[@id='product-detail-2']/table/tbody/tr[4]/td[2]/text()").toString();
        System.err.println(Model);
        item.setBrand(Model);

        //rate
        String rateString = htmlPage.getFirstByXPath("//div[@id='i-comment']/div[1]/strong/text()").toString();
        int Rate = Integer.parseInt(rateString);
        System.err.println(Rate);
        item.setRate(Rate);

        //reviewList
        final List<String> ReviewList = (List<String>) htmlPage.getByXPath("//*[@id='comment-0']/div[2]/div/table/tbody/tr/td[1]/div[1]/span/text()");
        System.err.println(ReviewList);
        item.setReviewList(ReviewList);

        return item;
    }

}
