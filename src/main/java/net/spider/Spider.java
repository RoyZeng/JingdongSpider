package net.spider;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Spider {

    private static final Logger logger = LogManager.getLogger(Spider.class);
    public static final String ItemUrlFilePath = "E:/JingdongSpider/resources/PartialItemLinks.txt";
    public static final String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";
    HtmlBrowser htmlBrowser = new HtmlBrowser();
    public int count = 0;
    public static final int THREAD_NUM = 15;
    List<ItemData> itemList = new ArrayList<>();
    List<SpiderThread> spiderThreadList = new ArrayList<>();

    public Spider() {

    }

    /**
     * start crawling the urls and store the page infomation into the file
     */
    public void Crawl() {
        List<ItemData> itemDataList = new ArrayList<>();

        //read url file and split them into pieces to fit the thread number
        String[] ulrArray = DataProcessor.readUrlsFromFile(ItemUrlFilePath);
        logger.debug(ulrArray.length);
        List<String> urlList = new ArrayList<>(Arrays.asList(ulrArray));

        //initialize the array of SpiderThread and start the thread
        int divisor = ulrArray.length / THREAD_NUM;
        int index = 0;
        for (int i = 0; i < THREAD_NUM; i++) {
            List<String> threadUrlList;
            if (index + divisor < ulrArray.length) {
                threadUrlList = urlList.subList(index, index + divisor);
                index += divisor;
            } else {
                threadUrlList = urlList.subList(index, ulrArray.length - 1);
            }

            //create new thread and pass the url list to the thread
            SpiderThread st = new SpiderThread(threadUrlList);
            spiderThreadList.add(st);
            st.start();
        }

        //waiting for the end of threads
        for (SpiderThread st : spiderThreadList) {
            try {
                st.join();
            } catch (InterruptedException ex) {
                logger.error(ex);
            }
        }

        //collecting the crawler results
        for (SpiderThread st : spiderThreadList) {
            List<ItemData> threadItemDataList = st.getItemList();
            itemList.addAll(threadItemDataList);
        }
        logger.info(itemList);

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
        List<String> allPages, links;
        allPages = getAllPages(url);

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
        String nextPageString;

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
