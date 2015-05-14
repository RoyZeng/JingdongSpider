package net.spider;

public class Main {

    //program entry
    public static void main(String[] args) {
        Spider spider = new Spider();
        spider.Crawl();
/*
        InsertDB insertdb=new InsertDB();
        try {
			insertdb.insert();
		} catch (Exception e) {
			e.printStackTrace();
		}
*/
    }
}
