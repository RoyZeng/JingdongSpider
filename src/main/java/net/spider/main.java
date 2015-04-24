package net.spider;

public class main {
	
    //program entry
    public static void main(String[] args) throws Exception  {
    	for(int i=0;i<50;i++){
        	Spider crawler= new Spider(i);
            crawler.start();
    	}
     
    }

}
