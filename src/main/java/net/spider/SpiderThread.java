package net.spider;

import java.util.ArrayList;
import java.util.List;

public class SpiderThread extends Thread{

    HtmlBrowser htmlBrowser = new HtmlBrowser();
    List<String> urlList;
    List<ItemData> itemDataList = new ArrayList<>();
    
    public SpiderThread(List<String> urlList) {
        this.urlList = urlList;
    }

    @Override
    public void run() {
        //TODO
    }
    List<ItemData> getItemList(){
        return  itemDataList;
    }
}
