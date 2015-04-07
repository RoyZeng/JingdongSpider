package net.spider;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.IOException;
import java.net.MalformedURLException;
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
    void excuteSpider() throws  Exception{
        List <String> AllAnchors=getAllAnchors(entryUrl);
        
       /* for(int i=0;i<AllAnchors.size();i++){
        	
        	HtmlBrowser Browser=new HtmlBrowser();
        	HtmlPage page=Browser.getHtmlPage(AllAnchors.get(i));
        	
        	getItemDataFromHtmlPage(page);

        }*/
    }

    /**
     * Get all the links related to phones from the web page source
     * @param url
     * @return 
     */
    List<String> getAllAnchors(String url) throws  Exception{
        WebClient webClient = new WebClient(BrowserVersion.CHROME);
        
        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);
        
        List<String> linkPage=getOnePageAnchors(url);
        for(int i=0;!getNextPages(url).isEmpty();i++){
        	
        	List<String> nextLinks=getOnePageAnchors(getNextPages(url));
        	linkPage.addAll(nextLinks);        	

        	url=getNextPages(url);
        }
        webClient.closeAllWindows();       
        return linkPage;
        
    }
    
    List<String> getOnePageAnchors(String url) throws  Exception{
        WebClient webClient = new WebClient(BrowserVersion.CHROME);


        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        HtmlPage page_1 = (HtmlPage)webClient.getPage(url);                
        //final List<String> href = (List<String>)page_1.getByXPath("//div[@id='plist']/ul[1]/li/div/div[1]/a[@href]/@href");
       
        List<String> hrefArray =new ArrayList<String>();
        for(int i=1;i<=60;i++){
        	
    	   final String href = page_1.getFirstByXPath("//div[@id='plist']/ul[1]/li["+i+"]/div/div[1]/a[@href]/@href").toString();
    	   
    	   int startIndex_1=href.lastIndexOf("=");
    	   int endIndex_1=href.indexOf("]");
    	   String link_1=href.substring(startIndex_1+1, endIndex_1);
    	   //System.out.println(link_1);
    	   hrefArray.add(link_1);
       }    
        webClient.closeAllWindows();
        System.out.print(hrefArray);
        return hrefArray;
    }
    
    
    String getNextPages(String url) throws  Exception {
        WebClient webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setJavaScriptEnabled(false);
        webClient.getOptions().setCssEnabled(false);

        
        HtmlPage page = (HtmlPage)webClient.getPage(url);    
        final String nextPage =page.getFirstByXPath("//*[@id='J_bottomPage']/span[1]/a[10]/@href").toString();
        
        int startIndex=nextPage.indexOf("/");
        int endIndex=nextPage.indexOf("]");
        String nextPage_f="http://list.jd.com/"+nextPage.substring(startIndex+1, endIndex);
        
        webClient.closeAllWindows();
        return nextPage_f;
    }
    
    /**
     * Crawl infomation from web page and store them in object ItemData
     * @param htmlPage
     * @return 
     */
    ItemData getItemDataFromHtmlPage(HtmlPage htmlPage){
    	
    	ItemData item=new ItemData();
    	String id=htmlPage.getFirstByXPath("//*[@id='parameter2']/li[2]/text()");
    	System.out.print(id);
    	
    	
        return  null;
    }

}
