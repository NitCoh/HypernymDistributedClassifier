package DependencyPathExplorer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import software.amazon.awssdk.utils.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

public class DependencyPathExplorerMerge {

    public static class MapperClass extends Mapper<LongWritable,Text,Text, Text> {

        @Override
        public void setup(Context context) throws IOException, InterruptedException {
        }

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            try {
                String[] rowObjs = value.toString().split("\t");
                List<NgramToken> tokens = SyntacticNgramsUtils.parse(rowObjs);
                if (tokens == null || tokens.size() == 0) return;

                Map<String, Pair<NgramToken, NgramToken>> map = SyntacticNgramsUtils.findPaths(tokens);
                for (Map.Entry<String, Pair<NgramToken, NgramToken>> s : map.entrySet()) {
                    StringJoiner builder = new StringJoiner("\t");
                    builder.add(s.getKey());
                    builder.add(SyntacticNgramsUtils.stem(s.getValue().left().getWord()));
                    builder.add(SyntacticNgramsUtils.stem(s.getValue().right().getWord()));
                    //key: path\tw1\tw2
                    context.write(new Text(builder.toString()), new Text("1"));
                }
            }catch(Exception e){
                System.out.println("$$$UNKNOWN BUG$$$");
                throw e;
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

        public void reduce(Text key, Iterable<Text> values,Context context) throws IOException, InterruptedException{
            if(values.iterator().hasNext())
                context.write(key,new Text("1"));
        }
    }


    public static class ReducerClass extends Reducer<Text,Text,Text,Text> {

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
            String[] args = key.toString().split("\t");
            if(args.length >=3 && values.iterator().hasNext()) { //path\tw1_w2
                context.write(new Text(args[0]), new Text(args[1] + Constants.specialDelimiter + args[2]));
            }
        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }



}
