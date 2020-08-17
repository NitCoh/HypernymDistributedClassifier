package DependencyPathExplorer;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import software.amazon.awssdk.utils.Pair;

import java.io.IOException;
import java.util.*;

public class FeatureVectorBuilder {
    public static class MapperClass extends Mapper<LongWritable, Text,Text, Text> {

        @Override
        public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException{

            //Hypernym dataset input: w1\tw2\tlabel
            //last step output: key: nounPair, value: path_index_N
            //nounPair\tpath_index_N
            String[] keyVal = SyntacticNgramsUtils.backParse(value.toString(),'\t',2);
            if(keyVal.length < 2)return;

            if(keyVal.length == 2){ //case last step
                //join on w1_w2
                context.write(new Text(keyVal[0]),
                        new Text(Constants.step4PrefixForFVInfo+Constants.specialDelimiter+keyVal[1]));
            }else if(keyVal.length == 3){// case hypernym
                context.write(new Text(SyntacticNgramsUtils.stem(keyVal[0])+Constants.specialDelimiter+SyntacticNgramsUtils.stem(keyVal[1])),
                        new Text(Constants.step4PrefixForLabel+Constants.specialDelimiter+keyVal[2]));
            }
        }
    }

    public static class ReducerClass extends Reducer<Text,Text,Text,Text> {

        @Override
        public void setup(Context context)  throws IOException, InterruptedException {

        }

        @Override
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException,  InterruptedException {
            Integer label = null;
            Map<String,Integer> counter = new HashMap<>();
            for(Text val : values){
                String[] parsedVals = val.toString().split(Constants.specialDelimiter);
                if(parsedVals.length ==0)return;
                //value: $_label or #_path_index_N
                switch (parsedVals[0]){
                    case Constants.step4PrefixForLabel:
                        if(parsedVals.length >= 2) {
                            if (parsedVals[1].equals("True"))
                                label = 1;
                            else if (parsedVals[1].equals("False")) {
                                label = 0;
                            }
                        }
                        break;
                    case Constants.step4PrefixForFVInfo:
                        if(parsedVals.length >= 4){
                            counter.merge(parsedVals[2],1,Integer::sum);
                        }
                        break;
                    default:
                        break;
                }
            }
            Map<String,Integer> sortedMap = new TreeMap(new Comparator() {
                @Override
                public int compare(Object o, Object t1) {
                    if(o instanceof String && t1 instanceof String){
                        long i1 = Long.parseLong((String)o);
                        long i2 = Long.parseLong((String)t1);
                        return Long.compare(i1,i2);
                    }
                    return 0;
                }
            });
            sortedMap.putAll(counter);
            StringJoiner builder = new StringJoiner(",");
            builder.add("label"+Constants.specialDelimiter+label);
            for(Map.Entry<String,Integer> entry: sortedMap.entrySet()){
                builder.add(entry.getKey()+Constants.specialDelimiter+entry.getValue());
            }
            //Output: w1_w2\tindex_count,index_count... (sorted by index)
            if(label !=null && !sortedMap.isEmpty())
                context.write(key, new Text(builder.toString()));
        }

        @Override
        public void cleanup(Context context)  throws IOException, InterruptedException {
        }
    }
}
