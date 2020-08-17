package DependencyPathExplorer;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.FileInputFormat;
import org.apache.hadoop.mapred.FileOutputFormat;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.jobcontrol.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;

public class DependencyPathExplorerFilterMain {

    public static void main(String[] args) throws Exception{
        for(String arg: args){
            System.out.println("DependencyPathExplorerFilterMain: arg - "+arg);
        }
//        org.apache.log4j.BasicConfigurator.configure();
        JobConf conf = new JobConf();
        conf.set("DPMin",args[0]);
//        conf.set("log4j.logger.org.apache.hadoop","DEBUG,console");
//        conf.set("log4j.logger.org.apache.hadoop.mapred.Job", "INFO");
//        conf.set("log4j.logger.org.apache.hadoop.mapred.TaskTracker", "DEBUG");
        FileInputFormat.setInputPaths(conf,new Path(args[1]));
        FileOutputFormat.setOutputPath(conf,new Path(args[2]));
        Job job = new Job(conf);


        job.getJob().setJobName("DependencyPathExplorerFilter");
        job.getJob().setJarByClass(DependencyPathExplorerFilterMain.class);
        job.getJob().setInputFormatClass(TextInputFormat.class);
        job.getJob().setOutputFormatClass(TextOutputFormat.class);
        job.getJob().setMapperClass(DependencyPathExplorerFilter.MapperClass.class);
        job.getJob().setReducerClass(DependencyPathExplorerFilter.ReducerClass.class);
        job.getJob().setMapOutputKeyClass(Text.class);
        job.getJob().setMapOutputValueClass(Text.class);
        System.out.println("Step 2 begins!");
        if(job.getJob().waitForCompletion(true)){
            System.exit(0);
        }else{
            System.out.println("Didn't finish calmly!");
            System.exit(1);
        }
    }
}
