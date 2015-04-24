package net.spider;

import org.apache.logging.log4j.LogManager;

public class Main {

    private static org.apache.logging.log4j.Logger logger = LogManager.getLogger(Main.class);

    //program entry
    public static void main(String[] args) {
        Spider spider = new Spider();
        spider.Crawl();
    }

}
