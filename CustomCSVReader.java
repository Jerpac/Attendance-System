package customcsvreader;

import java.sql.*;
import java.io.FileReader;
import java.io.File;
import java.io.IOException;

import com.opencsv.CSVReader;

public class CustomCSVReader {
    public static void main(String[] args) {
        String filepath = "src/main/test.csv";
        File file = new File(".");
        for(String fileNames : file.list()) System.out.println(fileNames);

        try {
            // make sure test.csv is in the same directory as this file
            if (new File(filepath).exists()) {
                System.out.println("test.csv exists");
            } else {
                System.out.println("test.csv does not exist");
            }
            // Create an object of file reader
            FileReader filereader = new FileReader(new File(filepath));

            // Create an object of CSVReader
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            // test if csvReader is null
            if (csvReader == null) {
                System.out.println("csvReader is null");
            } else {
                System.out.println("csvReader is not null");
            }

            // // we are going to read data line by line
             while ((nextRecord = csvReader.readNext()) != null) {
                 for (String cell : nextRecord) {
                     System.out.print(cell + "\t");
                 }
                 System.out.println();
             }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}