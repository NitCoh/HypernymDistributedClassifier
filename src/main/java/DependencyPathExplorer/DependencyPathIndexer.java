package DependencyPathExplorer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.Task;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import software.amazon.awssdk.utils.Pair;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

public class DependencyPathIndexer {

    public static enum Path{
        NUM
    };

    public static class MapperClass extends Mapper<LongWritable,Text,Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            Pair<String,String> keyVal = SyntacticNgramsUtils.parseKeyValFromInput(value.toString(),2);
            if(keyVal == null)return;
            String[] parsedKey = keyVal.left().split(Constants.specialDelimiter);
            //ignoring all unwanted data to not pass redundant keys
            if(parsedKey.length < 2 || !parsedKey[0].equals(Constants.step2PrefixForPath)) return;
            //key: * , val: dep-path
            context.getCounter(Path.NUM).increment(1);
            context.write(new Text("*"),new Text(parsedKey[1]));
        }

    }


    public static class ReducerClass extends Reducer<Text,Text,Text,Text> {

        private long index;

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {
            //Possibly (2^63)-1 features, can be extended with additional counter to be 2^126 and so on.
            index = 0;
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
            if(key.toString().equals("*")) {
                for (Text depPath : values) {
                    //output: key:$_depPath, val:index\tFeatureVectorSize
                    StringJoiner builder = new StringJoiner("\t");
                    builder.add(String.valueOf(index++)).add(String.valueOf(context.getCounter(Task.Counter.MAP_OUTPUT_RECORDS).getValue()));
                    context.write(new Text(Constants.step3PrefixForPath+Constants.specialDelimiter+depPath.toString())
                            ,new Text(builder.toString()));
                }
            }
        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }
}
