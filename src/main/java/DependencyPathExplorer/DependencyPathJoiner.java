package DependencyPathExplorer;

import org.apache.commons.lang3.StringUtils;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import software.amazon.awssdk.utils.Pair;

import java.io.IOException;

public class DependencyPathJoiner {

    public static class MapperClass extends Mapper<LongWritable, Text,Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{
            Pair<String,String> keyVal = SyntacticNgramsUtils.parseKeyValFromInput(value.toString(),2);
            if(keyVal == null)return;
            String[] parsedKey = keyVal.left().split(Constants.specialDelimiter);
            if(parsedKey.length < 2) return;
            switch (parsedKey[0]){
                case Constants.step3PrefixForPath:
                    context.write(new Text(parsedKey[1]+Constants.specialDelimiter+"A"),new Text(keyVal.right()));
                    break;
                case Constants.step2PrefixForNounPairs:
                    context.write(new Text(parsedKey[1]+Constants.specialDelimiter+"B"),new Text(keyVal.right()));
                    break;
                default:
                    break;
            }
        }
    }
    public static class PartitionerClass extends Partitioner<Text,Text>{
        @Override
        public int getPartition(Text key, Text value, int numPartitions) {

            String [] spliitedVals = key.toString().split(Constants.specialDelimiter);
            if(spliitedVals.length >=1) {
                return Math.floorMod(spliitedVals[0].hashCode(),numPartitions);
            }else
                return 0;
        }
    }

    public static class ReducerClass extends Reducer<Text,Text,Text,Text> {

        private static Long index = null;
        private Long N = null;

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {
        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
            String[] parsedKey = key.toString().split(Constants.specialDelimiter);
            if(parsedKey.length < 2) return;
            String depPath = parsedKey[0];
            if(parsedKey[1].equals("A")){
                Text val = values.iterator().next();
                if(val == null) return;
                String[] parsedVal = val.toString().split("\t");
                if(parsedVal.length < 2 || !StringUtils.isNumeric(parsedVal[0]) || !StringUtils.isNumeric(parsedVal[1]))return;
                index = Long.parseLong(parsedVal[0]);
                N = Long.parseLong(parsedVal[1]);
            }else if(parsedKey[1].equals("B") && index != null && N != null){
                for(Text nounPair : values){
                    //output: key: nounPair, value: path_index_N
                    context.write(nounPair,new Text(depPath+Constants.specialDelimiter+index+Constants.specialDelimiter+N));
                }
                N = null;
                index = null;
            }
        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }
}
