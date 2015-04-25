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

    public static final String ItemUrlFilePath = "E:/JingdongSpider/resources/PartialItemLinks.txt";
    public static final String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";
    HtmlBrowser htmlBrowser = new HtmlBrowser();
    public int count = 0;
    public static final int THREAD_NUM =4;
    List<ItemData> itemList = new ArrayList<>();
    List<SpiderThread> spiderThreadList = new ArrayList<>();

    public Spider() {

    }

    /**
     * start crawling the urls and store the page infomation into the file
     */
    public void Crawl() {
        //read url file and split them into pieces to fit the thread number
    	String[] linkArray = DataProcessor.readUrlsFromFile(ItemUrlFilePath);
    	System.out.println(linkArray.length);
    	List<String> toPassArray=new ArrayList<>();
        List<ItemData> itemDataList = new ArrayList<>();
        int length = linkArray.length / (THREAD_NUM - 1);
        //initialize the array of SpiderThread and start the thread
        for (int i = 0; i < THREAD_NUM; i++) {
        	toPassArray.clear();
        	int index=i*length;
            if(i==THREAD_NUM-1){
            	int start=(THREAD_NUM-1)*length;
            	int end=linkArray.length;
            	while (start<end) {
             		toPassArray.add(linkArray[start]);
                    start++;
                }          
            }                        	           	                             
            else{
            	int end=index+length;
                while (index < end) {
              	    toPassArray.add(linkArray[index]);
                    index++;
                }  
             }
             System.out.println("pass:"+toPassArray);
             SpiderThread st = new SpiderThread(toPassArray);
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
        System.out.println(itemList);
        //store the cralwer results into the file
        String data = DataProcessor.itemData2Json(itemList);
        try {
            DataProcessor.string2File("E:/testItems.txt", data);
        } catch (IOException e) {
        }
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

}
