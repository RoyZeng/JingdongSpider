package net.spider;

import com.gargoylesoftware.htmlunit.html.DomAttr;
import com.gargoylesoftware.htmlunit.html.DomText;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;//


public class Spider extends Thread{

    HtmlBrowser htmlBrowser = new HtmlBrowser();
    String entryUrl = "http://list.jd.com/list.html?cat=9987,653,655";
    public int count=0;
    public int threadCount = 50;
    List<ItemData> items=new ArrayList<>();
    public Spider(int count1) {
    	count=count1;
    }

    /**
     * start crawl from here
     */ 
    public void run() {
        //List<String> AllAnchors = getAllAnchors(entryUrl);
        //record all links in a file
        //DataProcessor.string2File("E:/test.txt", JSONArray.fromObject(AllAnchors).toString());      
		try {
			//List<ItemData> item=new ArrayList<>();
			items = items("E:/test/test.txt",count);
			//items.addAll(items);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}    	
        String data = DataProcessor.itemData2Json(items);
        try {
			DataProcessor.string2File("E:/testItems"+count+".txt", data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        System.out.println(count);
    }

   List<ItemData> items(String path,int i) throws Exception {
        String[] linkArray = DataProcessor.readUrlsFromFile(path);
        List<String> linkArrayList=Arrays.asList(linkArray);
        List<String> notCrawlurl = new ArrayList(linkArrayList);
        List<ItemData> items = new ArrayList<>();      
        int length=linkArray.length/(threadCount-1);
        int index=i*length;
        if(i==threadCount-1){
        	int j=(threadCount-1)*length;
        	 while (j < linkArray.length) {        
             	HtmlPage htmlPage = htmlBrowser.getHtmlPage(linkArray[j]);
                 items.add(getItemDataFromHtmlPage(htmlPage));
                 j++;
             }
        }
        else{
        	int j= index;
            int end=index+length;
//            System.out.println(length);
            while (j < end) {        
            	HtmlPage htmlPage = htmlBrowser.getHtmlPage(linkArray[j]);
                items.add(getItemDataFromHtmlPage(htmlPage));
                j++;
            }
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
     * @throws Exception 
     * @throws Exception 
     */
    public ItemData getItemDataFromHtmlPage(HtmlPage htmlPage){
        ItemData item = new ItemData();
        //id
        DomAttr attribute = (DomAttr) htmlPage.getFirstByXPath("//*[@id='parameter2']/li[2]/@title");
        String idString = attribute.getNodeValue().toString();
        Integer ID = Integer.parseInt(idString);
        System.err.println(ID);
        item.setID(ID);

        //name
        DomAttr nameDom = (DomAttr) htmlPage.getFirstByXPath(" //*[@id='parameter2']/li[1]/@title");
        String Name = nameDom.getNodeValue().toString();
        System.err.println(Name);
        item.setName(Name);

        //price
    	String line;
    	StringBuffer pageBuffer = new StringBuffer();
    	URL pageUrl = null;
		try {
			pageUrl = new URL("http://p.3.cn/prices/mgets?skuIds=J_"+idString+"&type=1");
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    	try {
    		BufferedReader br = new BufferedReader(new InputStreamReader(pageUrl.openStream()));
    		while ((line = br.readLine()) != null) {
    			pageBuffer = pageBuffer.append(line);
    		}
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	String priceString1=pageBuffer.toString();
    	
    	JSONArray array=JSONArray.fromObject(priceString1);
    	JSONObject jsonObject=array.getJSONObject(0);    	  
    	String priceString = jsonObject.getString("p");   
    	System.err.println(priceString);
    	double Price=Double.parseDouble(priceString);
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
		HtmlPage page=htmlBrowser.getHtmlPage("http://club.jd.com/review/"+idString+"-1-1.html");
		DomText rateString=page.getFirstByXPath("//*[@id='i-comment']/div[1]/strong/text()");
		Integer Rate=0;
		if(rateString!=null){
			Rate = Integer.parseInt(rateString.toString());
		    System.err.println(rateString);
		}
	    item.setRate(Rate);
	    
        //reviewList
	    List<String> ReviewList=new ArrayList<>();
	    String judge="心 得：";  
        int i=0;
	    while(true){
	    	try{	  
	    		String Review=null;
	    		String judge1=page.getFirstByXPath("//*[@id='comment-"+i+"']/div/div[2]/div[2]/dl[1]/dt/text()").toString();	    		
	    		if(judge1.equals(judge)) 
	    			 Review=page.getFirstByXPath("//*[@id='comment-"+i+"']/div/div[2]/div[2]/dl[1]/dd/text()").toString();
	    		else Review=page.getFirstByXPath("//*[@id='comment-"+i+"']/div/div[2]/div[2]/dl[2]/dd/text()").toString();
		    		ReviewList.add(Review.toString());   		
		        	i++;	
	    	}catch (Exception e){
	        	break;
	    	}
        }
        System.err.println(ReviewList);
        item.setReviewList(ReviewList);
        
        return item;
    }

}