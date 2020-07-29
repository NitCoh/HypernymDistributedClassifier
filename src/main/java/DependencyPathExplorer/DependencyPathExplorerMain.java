package DependencyPathExplorer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class DependencyPathExplorerMain {


    public static void main(String[] args) throws Exception{
        JobConf conf = new JobConf();
        FileInputFormat.setInputPaths(conf,new Path(args[0]));
        FileOutputFormat.setOutputPath(conf,new Path(args[1]));
        Job job = new Job(conf);


        job.getJob().setJobName("DependencyPathExplorer");
        job.getJob().setJarByClass(DependencyPathExplorerMain.class);
        //TODO: explore it.
        job.getJob().setInputFormatClass(TextInputFormat.class);
        job.getJob().setOutputFormatClass(TextOutputFormat.class);
        job.getJob().setMapperClass(DependencyPathExplorer.MapperClass.class);
        job.getJob().setCombinerClass(DependencyPathExplorer.CombinerClass.class);
        job.getJob().setReducerClass(DependencyPathExplorer.ReducerClass.class);
        System.exit(job.getJob().waitForCompletion(true) ? 0 : 1);
    }
}
