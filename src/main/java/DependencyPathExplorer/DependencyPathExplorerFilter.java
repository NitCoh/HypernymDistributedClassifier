package DependencyPathExplorer;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import software.amazon.awssdk.utils.Pair;

import java.io.IOException;
import java.util.*;

public class DependencyPathExplorerFilter {

    public static class MapperClass extends Mapper<LongWritable,Text,Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            Pair<String,String> keyVal = SyntacticNgramsUtils.parseKeyValFromInput(value.toString(),2);
            if(keyVal == null)return;

            context.write(new Text(keyVal.left()),new Text(keyVal.right()));
        }

    }


    public static class ReducerClass extends Reducer<Text,Text,Text,Text> {
        int dpMin;

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {
            dpMin = Integer.parseInt(context.getConfiguration().get("DPMin",null));

        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
            List<Text> stash = new ArrayList<>();
            for(Text nounPair : values){
                if(stash.size() >= dpMin)
                    context.write(new Text(Constants.step2PrefixForNounPairs+Constants.specialDelimiter+key.toString()),nounPair);
                else
                    stash.add(nounPair);
            }

            if(stash.size()>=dpMin){
                //*_path
                for(Text nounPair : stash){
                    //#_path
                    context.write(new Text(Constants.step2PrefixForNounPairs+Constants.specialDelimiter+key.toString()),nounPair);
                }
                context.write(new Text(Constants.step2PrefixForPath+Constants.specialDelimiter+key.toString()),new Text("NULL"));
            }
        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }




}
