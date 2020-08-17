package DependencyPathExplorer;
import com.amazonaws.auth.*;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduce;
import com.amazonaws.services.elasticmapreduce.AmazonElasticMapReduceClientBuilder;
import com.amazonaws.services.elasticmapreduce.model.*;
import com.amazonaws.services.elasticmapreduce.util.StepFactory;
import software.amazon.awssdk.services.ec2.model.InstanceType;

import java.util.HashMap;
import java.util.Map;

public class SendJob {

    public static HadoopJarStepConfig createStep(String jar,String main, String... args){
        return new HadoopJarStepConfig()
                .withJar(jar)
//                .withMainClass(main)
                .withArgs(args);
    }

    public static StepConfig createConfig(String name,HadoopJarStepConfig hadoopJarStep){
        return new StepConfig()
                .withName(name)
                .withHadoopJarStep(hadoopJarStep)
                .withActionOnFailure("TERMINATE_JOB_FLOW");
    }


    public static void main(String[] args){


        AmazonElasticMapReduce mapRed = AmazonElasticMapReduceClientBuilder.standard()
                .withCredentials(new AWSSessionCredentialsProvider() {
                    @Override
                    public AWSSessionCredentials getCredentials() {
                        return new AWSSessionCredentials() {
                            @Override
                            public String getSessionToken() {
                                return Constants.aws_session_token;
                            }

                            @Override
                            public String getAWSAccessKeyId() {
                                return Constants.aws_access_key_id;
                            }

                            @Override
                            public String getAWSSecretKey() {
                                return Constants.aws_secret_access_key;
                            }
                        };
                    }

                    @Override
                    public void refresh() {

                    }
                })
                .withRegion(Regions.US_EAST_1).build();


//        //Full-english-steps
        HadoopJarStepConfig jarStep1 = createStep(Constants.step1Jar,"",Constants.step1Input,Constants.step1Output);
        HadoopJarStepConfig jarStep2 = createStep(Constants.step2Jar,"",Constants.DPMin,Constants.step1Output,Constants.step2Output);
        HadoopJarStepConfig jarStep3 = createStep(Constants.step3Jar,"",Constants.step2Output,Constants.step3Output);
        HadoopJarStepConfig jarStep4 = createStep(Constants.step4Jar,"",Constants.step2Output,Constants.step3Output,Constants.step4Output);
        HadoopJarStepConfig jarStep5 = createStep(Constants.step5Jar,"",Constants.step4Output,Constants.hypernyms,Constants.step5Output);




        StepFactory stepFactory = new StepFactory();
        StepConfig cfg1 = createConfig("Step1",jarStep1);
        StepConfig cfg2 = createConfig("Step2",jarStep2);
        StepConfig cfg3 = createConfig("Step3",jarStep3);
        StepConfig cfg4 = createConfig("Step4",jarStep4);
        StepConfig cfg5 = createConfig("Step5",jarStep5);
        StepConfig debugCfg = createConfig("Enable debugging",stepFactory.newEnableDebuggingStep());



        Map<String,String> hadoopProps = new HashMap<String,String>();
//        hadoopProps.put("mapred.reduce.slowstart.completed.maps","1");
//        hadoopProps.put("mapred.task.timeout","2000000");

        Configuration configuration = new Configuration()
                .withClassification("mapred-site")
                .withProperties(hadoopProps);

        ///m5.xlarge
        //Configuration Option	Default Value
        //mapreduce.map.java.opts	-Xmx2458m
        //mapreduce.reduce.java.opts	-Xmx4916m
        //mapreduce.map.memory.mb	3072
        //mapreduce.reduce.memory.mb	6144
        //yarn.app.mapreduce.am.resource.mb	6144
        //yarn.scheduler.minimum-allocation-mb	32
        //yarn.scheduler.maximum-allocation-mb	12288
        //yarn.nodemanager.resource.memory-mb	12288

        JobFlowInstancesConfig instances = new JobFlowInstancesConfig()
                .withInstanceCount(5)
                .withMasterInstanceType(InstanceType.M4_XLARGE.toString())
                .withSlaveInstanceType(InstanceType.M4_XLARGE.toString())
                .withHadoopVersion("3.2.1")
                .withKeepJobFlowAliveWhenNoSteps(false)
                .withPlacement(new PlacementType("us-east-1a"));

        RunJobFlowRequest runFlowRequest = new RunJobFlowRequest()
                .withName("Hypernym Classifier")
                .withReleaseLabel("emr-6.0.0")
                .withServiceRole("Ass2EMR")
                .withJobFlowRole("Ass2EMREC2")
                .withInstances(instances)
                .withSteps(debugCfg,cfg1,cfg2,cfg3,cfg4,cfg5)
                .withConfigurations(configuration)
                .withLogUri(Constants.logsPath);

        RunJobFlowResult runJobFlowResult = mapRed.runJobFlow(runFlowRequest);
        String jobFlowId = runJobFlowResult.getJobFlowId();
        System.out.println("Ran job flow with id: " + jobFlowId);
    }

}
