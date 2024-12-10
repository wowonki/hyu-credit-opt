package myutil;
import java.util.*;
import java.io.*;

public class CsvLoader {
	// Test for loadCSV() method.
	public static void main(String[] args) {
		List<String[]> datalist = CsvLoader.loadCsv("result_status.csv");
		for (String[] dataArray:datalist) {
			for (String data: dataArray) {
				System.out.print(data + " ");
			}
			
			System.out.println();
			
		}
	}
	
    public static List<String[]> loadCsv(String filePath) {
        List<String[]> dataList = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(","); // Split by "," because we use CSV file.
                dataList.add(values);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dataList;
    }
}
