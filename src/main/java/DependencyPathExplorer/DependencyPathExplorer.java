package DependencyPathExplorer;

import javafx.util.Pair;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class DependencyPathExplorer {

    public static class MapperClass extends Mapper<Text,Text,Text, Text> {
        @Override
        public void setup(Context context) throws IOException, InterruptedException {
        }

        @Override
        public void map(Text key, Text value, Context context) throws IOException, InterruptedException{
            String[] rowObjs = value.toString().split("\t");
            List<NgramToken> tokens = SyntacticNgramsUtils.parse(rowObjs);
            if(tokens == null || tokens.size() == 0) return;

            Map<String,Pair<NgramToken,NgramToken>> map = SyntacticNgramsUtils.findPaths(tokens);
            for(Map.Entry<String,Pair<NgramToken,NgramToken>> s : map.entrySet()){
                StringJoiner builder = new StringJoiner(",");
                builder.add(SyntacticNgramsUtils.stem(s.getValue().getKey().getWord()));
                builder.add(SyntacticNgramsUtils.stem(s.getValue().getValue().getWord()));
                context.write(new Text(s.getKey()),new Text(builder.toString()));
            }

        }

        @Override
        public void cleanup(Context context) throws IOException, InterruptedException {
        }
    }

    public static class CombinerClass extends Reducer<Text,Text,Text,Text> {

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }

    public static class ReducerClass extends Reducer<Text,Text,Text,Text> {

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {

        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {

        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }



}
