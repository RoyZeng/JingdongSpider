package net.spider;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.Layer;
import org.jfree.ui.LengthAdjustmentType;
import org.jfree.ui.TextAnchor;

public class Charts {
	private static final int ID=562379;
    public static final String DBDRIVER="org.gjt.mm.mysql.Driver";  
    public static final String DBURL="jdbc:mysql://localhost:3306/jdspider";  
    public static final String DBUSER="root";  
    public static final String DBPASSWORD="123456";  
    private static final Logger logger = LogManager.getLogger(Charts.class);
    
	public Charts() {
        try {
			this.createChart();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public DefaultCategoryDataset createDataset() throws Exception{
		Connection conn = null ;  
	    Class.forName(DBDRIVER);  
	    conn = DriverManager.getConnection(DBURL, DBUSER, DBPASSWORD);  
	    Statement stmt = conn.createStatement();  
        DefaultCategoryDataset linedataset = new DefaultCategoryDataset();
        
        String series = "价格"; 
        List<String> CrawlDate = new ArrayList<String>();
        List<Double> Price = new ArrayList<Double>();
        
    	String sql="select CrawlDate from items where ID = "+ID;
		Statement stat = conn.createStatement();	
		ResultSet rs=stat.executeQuery(sql);
		while(rs.next()){
			CrawlDate.add(rs.getString(1));	
			logger.info(rs.getString(1));
		}
		logger.info(CrawlDate);
		int i=0;
		while(i<CrawlDate.size()) {
			String sqlPrice="select Price from items where ID = "+ID+" and CrawlDate = \""+CrawlDate.get(i)+"\"";
			Statement statPrice = conn.createStatement();	
			ResultSet rsPrice=statPrice.executeQuery(sqlPrice);
			while(rsPrice.next()) {
				Price.add(rsPrice.getDouble("Price"));
				logger.debug(rsPrice.getDouble("Price"));
				linedataset.addValue(Price.get(i), series, CrawlDate.get(i));			
			}
			i++;
		}
		stmt.close();  
	    conn.close();  
        return linedataset;
	}


	public void createChart() throws Exception{

        try {
               JFreeChart chart = ChartFactory.createLineChart("价格变动","时间","价格", this.createDataset(),PlotOrientation.VERTICAL,true,false,false);
               CategoryPlot plot = chart.getCategoryPlot();
               // 图像属性部分
               plot.setBackgroundPaint(Color.white);
               plot.setNoDataMessage("没有数据");
               // 数据轴属性部分
               NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
               rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
               rangeAxis.setAutoRangeIncludesZero(true); //自动生成
               rangeAxis.setUpperMargin(0.20);
               rangeAxis.setAutoRange(false);
               // 数据渲染部分 主要是对折线做操作
               LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();
               renderer.setBaseItemLabelsVisible(true);
               renderer.setSeriesPaint(0, Color.black); 
               renderer.setBaseShapesFilled(true);
               renderer.setBaseItemLabelsVisible(true);     
               renderer.setBasePositiveItemLabelPosition(new ItemLabelPosition(ItemLabelAnchor.OUTSIDE12, TextAnchor.BASELINE_LEFT));
               renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());  


               renderer.setBaseItemLabelFont(new Font("Dialog", 1, 14));  //设置提示折点数据形状
               plot.setRenderer(renderer);
 
               // 创建文件输出流
               File fos_jpg = new File("E://PriceChart.jpg ");
               // 输出到哪个输出流
               ChartUtilities.saveChartAsJPEG(fos_jpg, chart, 700, 500); 
        } catch (IOException e) {
               e.printStackTrace();
        }
	}

	public static void main(String[] args) {
        Charts my = new Charts();
	} 
}
