package DependencyPathExplorer;

import javafx.util.Pair;
import org.apache.commons.collections.map.HashedMap;

import java.util.*;

public class SyntacticNgramsUtils {

    public static List<NgramToken> parse(String [] rowObj){
        if(rowObj == null || rowObj.length != 4)
            return null;

        String syntacticNgrams = rowObj[1];
        String[] grams = syntacticNgrams.split(" ");

        if(grams.length != 4)
            return null;

        List<NgramToken> tokens = new ArrayList<>();
        for(int i=0;i<grams.length;i++){
            tokens.add(new NgramToken(i+1,grams[i].split("/")));
        }
        return tokens;
    }

    public static String buildPath(NgramToken src, NgramToken target,List<NgramToken> tokens){
        return null;
    }


    public static Map<String,Pair<NgramToken,NgramToken>> findPaths(List<NgramToken> tokens){
        String[] nounsPOS = {"NN"};
        Map<String,Pair<NgramToken,NgramToken>> map = new HashMap<>();
        for(int i=0;i<tokens.size()-1;i++){
            NgramToken current = tokens.get(i);
            if(!Arrays.asList(nounsPOS).contains(current.getPos()))
                continue;
            List<NgramToken> rest = tokens.subList(i,tokens.size());
            rest.forEach((NgramToken tok) -> {
                if(Arrays.asList(nounsPOS).contains(tok.getPos())){
                    String path = SyntacticNgramsUtils.buildPath(current,tok,tokens);
                    if(path != null){
                        map.put(path, new Pair(current, tok));
                    }
                }
            });
        }
        return map;
    }

    public static String stem(String s){
        return null;
    }
}
