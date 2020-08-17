package DependencyPathExplorer;

import com.opencsv.CSVWriter;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CSVBuilder {
    public static void main(String[] args){
        //args[0] will get local-path to the file
        //Format:w1_w2\tlabel_X,index_count,index_count.....
        //a_book	label_0,139_1
        //
        String outputFilePath = "./CSVs/output2_dpmin6.csv";
        long featureVectorTotalSize = Long.parseLong(args[1]);
        // first create file object for file placed at location
        // specified by filepath
        File outputFile = new File(outputFilePath);
        try {
            // create FileWriter object with file as parameter
            FileWriter outputFileW = new FileWriter(outputFile);


            // create CSVWriter object filewriter object as parameter
            CSVWriter writer = new CSVWriter(outputFileW);

            // adding header to csv
            List<String> stash = new ArrayList<>();
            stash.add("Word1");
            stash.add("Word2");
            for(long i=0;i<featureVectorTotalSize;i++){
                stash.add("Feature_"+i);
            }
            stash.add("True Label");

            String[] header = new String[stash.size()];
            header = stash.toArray(header);
            writer.writeNext(header);

            File[] files = new File(args[0]).listFiles();

            assert files != null;
            for(File file : files) {

                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        stash.clear();
                        //Format:w1_w2\tlabel_X,index_count,index_count.....
                        String[] lineBreaker = line.split("\t");
                        if (lineBreaker.length >= 2) {
                            String[] words = lineBreaker[0].split("_");

                            stash.addAll(Arrays.asList(words));
                            String[] repres = lineBreaker[1].split(",");
                            if (repres.length > 0) {
                                long lastIndex = 0;
                                for (int i = 1; i < repres.length; i++) {
                                    //build non-sparse representation
                                    String[] pair = repres[i].split("_");
                                    if (pair.length > 1 && StringUtils.isNumeric(pair[0]) && StringUtils.isNumeric(pair[1])) {
                                        long curIndex = Long.parseLong(pair[0]);
                                        for (long j = lastIndex; j < curIndex; j++) {
                                            stash.add("0");
                                        }
                                        stash.add(pair[1]);
                                        lastIndex = curIndex + 1;
                                    } else
                                        System.out.println("Bad input");
                                }
                                //Fill rest with zeros
                                for (long j = lastIndex; j < featureVectorTotalSize; j++) {
                                    stash.add("0");
                                }
                                //last label
                                String[] pair = repres[0].split("_");
                                if (pair.length > 1 && StringUtils.isNumeric(pair[1]))
                                    stash.add(pair[1]);

                                if (stash.size() != featureVectorTotalSize + 3) {
                                    System.out.println("Problem in CSV building, Line: ");
                                    System.out.println(line);
                                    System.out.println(file);
                                    System.exit(1);
                                }
                                String[] liner = new String[stash.size()];
                                liner = stash.toArray(liner);
                                writer.writeNext(liner);
                            }
                        }

                    }
                }
            }
            // closing writer connection
            writer.close();
        }
        catch ( IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
