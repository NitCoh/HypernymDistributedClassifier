package DependencyPathExplorer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class DependencyPathIndexerMain {
    public static void main(String[] args) throws Exception{
        for(String arg: args){
            System.out.println("DependencyPathIndexerMain: arg - "+arg);
        }
//        org.apache.log4j.BasicConfigurator.configure();
        JobConf conf = new JobConf();
//        conf.set("log4j.logger.org.apache.hadoop","DEBUG,console");
//        conf.set("log4j.logger.org.apache.hadoop.mapred.Job", "INFO");
//        conf.set("log4j.logger.org.apache.hadoop.mapred.TaskTracker", "DEBUG");
        FileInputFormat.setInputPaths(conf,new Path(args[0]));
        FileOutputFormat.setOutputPath(conf,new Path(args[1]));
        Job job = new Job(conf);


        job.getJob().setJobName("DependencyPathIndexer");
        job.getJob().setJarByClass(DependencyPathIndexerMain.class);
        job.getJob().setInputFormatClass(TextInputFormat.class);
        job.getJob().setOutputFormatClass(TextOutputFormat.class);
        job.getJob().setMapperClass(DependencyPathIndexer.MapperClass.class);
        job.getJob().setReducerClass(DependencyPathIndexer.ReducerClass.class);
        job.getJob().setMapOutputKeyClass(Text.class);
        job.getJob().setMapOutputValueClass(Text.class);
        System.out.println("Step 3 begins!");
        if(job.getJob().waitForCompletion(true)){
            System.exit(0);
        }else{
            System.out.println("Didn't finish calmly!");
            System.exit(1);
        }
    }
}
