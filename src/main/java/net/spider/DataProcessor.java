package net.spider;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import net.sf.json.JSONArray;

public class DataProcessor {

    public DataProcessor() {

    }

    /**
     * Static method
     * ItemData array to json string
     * @param dataList
     * @return
     */
    public static String itemData2Json(List<ItemData> dataList) {
        String jsonString = JSONArray.fromObject(dataList).toString();
        return jsonString;
    }

    /**
     * Static method
     * json string to ItemData array
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
     * @param filePath
     * @param data data to be written to file
     * @throws IOException
     */
    public static void string2File(String filePath, String data) throws IOException {
        FileWriter fw = new FileWriter(new File(filePath));
        fw.write(data);
        fw.close();
    }
}
