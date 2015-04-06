package net.spider;

import com.gargoylesoftware.htmlunit.html.HtmlPage;
import java.util.List;

public class Spider {

    HtmlBrowser htmlBrowser = new HtmlBrowser();
    String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";
    
    public Spider() {
    }

    /**
     * start crawl from here
     */
    void excuteSpider() {
        //TODO
    }

    /**
     * Get all the links related to phones from the web page source
     * @param htmlPage
     * @return 
     */
    List<String> getAllAnchors(HtmlPage htmlPage) {
        //TODO
        return null;
    }
    
    /**
     * Crawl infomation from web page and store them in object ItemData
     * @param htmlPage
     * @return 
     */
    ItemData getItemDataFromHtmlPage(HtmlPage htmlPage){
        //TODO
        return  null;
    }

}
