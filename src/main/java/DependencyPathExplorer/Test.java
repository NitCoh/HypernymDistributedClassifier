package DependencyPathExplorer;



import software.amazon.awssdk.utils.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Test {
    public static void main(String[] args){
//        String value = "aboundance\taboundance/NN/ROOT/0 of/IN/prep/1 mapps/NNS/pobj/2 and/CC/cc/3 pieces/NNS/conj/3\t11\t1827,2\t1879,2\t1906,4\t2008,3\n";
//        String[] rowObjs = value.split("\t");
//        List<NgramToken> tokens = SyntacticNgramsUtils.parse(rowObjs);
//        if(tokens == null || tokens.size() == 0) return;
//
//        Map<String, Pair<NgramToken,NgramToken>> map = SyntacticNgramsUtils.findPaths(tokens);
//        String s = SyntacticNgramsUtils.stem("roys");
//        System.out.println("Happy");

        String val = "hel/net/NN/conj-n/3";
        String keyval = "comoros\tterritory\tTrue";
        System.out.println(Boolean.valueOf("True"));
//        System.out.println(Arrays.toString(SyntacticNgramsUtils.backParse(keyval,'\t',2)));
//        Pair<String,String> ret = SyntacticNgramsUtils.parseKeyValFromInput(keyval,2);
//        System.out.println(ret);
    }
}
