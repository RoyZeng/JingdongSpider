package net.spider;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.json.JSONArray;

public class DataProcessor {

    public DataProcessor() {

    }

    /**
     * Static method ItemData array to json string
     *
     * @param dataList
     * @return
     */
    public static String itemData2Json(List<ItemData> dataList) {
        String jsonString = JSONArray.fromObject(dataList).toString();
        return jsonString;
    }

    /**
     * Static method json string to ItemData array
     *
     * @param jsonData
     * @return
     */
    public static List<ItemData> json2ItemData(String jsonData) {
        JSONArray jarray = JSONArray.fromObject(jsonData);
        ItemData[] ids = (ItemData[]) JSONArray.toArray(jarray, ItemData.class);
        return Arrays.asList(ids);
    }

    /**
     ** Static method
     *
     * @param filePath
     * @param data data to be written to file
     * @throws IOException
     */
    public static void string2File(String filePath, String data) throws IOException {
        FileWriter fw = new FileWriter(new File(filePath));
        fw.write(data);
        fw.close();
    }

    public static String[] readUrlsFromFile(String path) {
        BufferedReader br = null;
        String fileContent = null;
        try {
            br = new BufferedReader(new FileReader(new File(path)));
            fileContent = br.readLine();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DataProcessor.class.getName()).log(Level.SEVERE, null, ex);
        }
        JSONArray jarray = JSONArray.fromObject(fileContent);
        String[] urls = (String[]) JSONArray.toArray(jarray, String.class);
        return urls;
    }
}
