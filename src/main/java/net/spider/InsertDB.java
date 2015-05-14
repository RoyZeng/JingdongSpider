package net.spider;
import java.sql.*; 
import java.util.List;

import org.eclipse.jetty.util.ajax.JSON;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.logging.log4j.LogManager;//
import org.apache.logging.log4j.Logger;//

public class InsertDB {
    public static final String DBDRIVER="org.gjt.mm.mysql.Driver";  
    public static final String DBURL="jdbc:mysql://localhost:3306/jdspider";  
    public static final String DBUSER="root";  
    public static final String DBPASSWORD="123456";  
    public static final String ItemsFilePath = "E:/JingdongSpider/resources/AllItems.txt";
    private static final Logger logger = LogManager.getLogger(InsertDB.class);
    
    public static void insert() throws Exception{          
        Connection conn = null ;  
        Class.forName(DBDRIVER);  
        conn = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);  
        Statement stmt = conn.createStatement();  
        ItemData[] items=DataProcessor.readItemsFromFile(ItemsFilePath);
        JSONArray array = JSONArray.fromObject(items);    

        for (int i=0 ; i<items.length ; i++) { 
            JSONObject jsonObject = array.getJSONObject(i);
            //logger.info(jsonObject);          
            try {
            	int ID=jsonObject.getInt("ID");
                logger.info(ID);
                String Name=jsonObject.getString("name");
                double Price=jsonObject.getDouble("price");
                String Brand=jsonObject.getString("brand");
                String Model=jsonObject.getString("model");
                String Date=jsonObject.getString("date");
                int Rate=jsonObject.getInt("rate");
                List<String> ReviewList=jsonObject.getJSONArray("reviewList");
                String CrawlDate=jsonObject.getString("crawlDate");

                StringBuilder Reviews=new StringBuilder();
                for (int j = 0; j < ReviewList.size(); j++) {  
                    Reviews.append(ReviewList.get(j)).append("|");  
                }  
                String Review=Reviews.toString();
                Review=Review.replace("\"", "");
                logger.info(Review);
                try {
                    String sql = "INSERT INTO items(ID,Name,Price,Brand,Model,Date,Rate,ReviewList,CrawlDate) VALUE("+
                            ID+",\""+Name+"\","+Price+",\""+Brand+"\",\""+Model+"\",\""+Date+"\","+Rate+",\""+Review+"\",\""+CrawlDate+"\")";
                    stmt.executeUpdate(sql);
                }catch (Exception e) {
                	continue;
                }
            }
            catch (Exception e) {
            	continue;
            }
            
        }  
        stmt.close();  
        conn.close();  
    }     

}
