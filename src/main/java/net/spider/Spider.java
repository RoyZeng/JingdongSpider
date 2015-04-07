package net.spider;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONArray;

public class Spider {

    HtmlBrowser htmlBrowser = new HtmlBrowser();
    String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";

    public Spider() {
    }

    /**
     * start crawl from here
     */
    void excuteSpider() throws Exception {
        List<String> AllAnchors = getAllAnchors(entryUrl, 200);
        //record all links in a file
        DataProcessor.string2File("z:/test.txt", JSONArray.fromObject(AllAnchors).toString());

    }

    /**
     * Get all the links related to phones from the web page source
     *
     * @param url
     * @return
     */
    List<String> getAllAnchors(String url, int maxmium) throws Exception {
        List<String> linkPage = new ArrayList<>();
        String pageUrl = url;
        do {
            List<String> nextLinks = getOnePageAnchors(pageUrl);
            linkPage.addAll(nextLinks);
            pageUrl = getNextPages(pageUrl);
            if (linkPage.size() > maxmium) {
                return linkPage;
            }
        } while (!pageUrl.isEmpty());
        return linkPage;
    }

    List<String> getOnePageAnchors(String url) throws Exception {
        HtmlPage page_1 = htmlBrowser.getHtmlPage(url);
        //final List<String> href = (List<String>)page_1.getByXPath("//div[@id='plist']/ul[1]/li/div/div[1]/a[@href]/@href");
        List<String> hrefArray = new ArrayList<>();
        for (int i = 1; i <= 60; i++) {
            final String href = page_1.getFirstByXPath("//div[@id='plist']/ul[1]/li[" + i + "]/div/div[1]/a[@href]/@href").toString();
            int startIndex_1 = href.lastIndexOf("=");
            int endIndex_1 = href.indexOf("]");
            String link_1 = href.substring(startIndex_1 + 1, endIndex_1);
            hrefArray.add(link_1);
        }
        System.out.print(hrefArray);
        return hrefArray;
    }

    String getNextPages(String url) throws Exception {
        HtmlPage page = htmlBrowser.getHtmlPage(url);
        final String nextPage = page.getFirstByXPath("//*[@id='J_bottomPage']/span[1]/a[10]/@href").toString();

        int startIndex = nextPage.indexOf("/");
        int endIndex = nextPage.indexOf("]");
        String nextPage_f = "http://list.jd.com/" + nextPage.substring(startIndex + 1, endIndex);
        return nextPage_f;
    }

    /**
     * Crawl infomation from web page and store them in object ItemData
     *
     * @param htmlPage
     * @return
     */
    ItemData getItemDataFromHtmlPage(HtmlPage htmlPage) {

        ItemData item = new ItemData();
        String id = htmlPage.getFirstByXPath("//*[@id='parameter2']/li[2]/text()");
        System.out.print(id);

        return null;
    }

}
