package DependencyPathExplorer;



import software.amazon.awssdk.utils.Pair;

import java.util.*;

public class SyntacticNgramsUtils {

    public static List<NgramToken> parse(String[] rowObj) {
        if (rowObj == null)
            return null;

        String syntacticNgrams = rowObj[1];
        String[] grams = syntacticNgrams.split(" ");

        List<NgramToken> tokens = new ArrayList<>();
        for (int i = 0; i < grams.length; i++) {
            try {
                tokens.add(new NgramToken(i + 1, backParse(grams[i],'/',3)));
            }catch(Exception e){
//                System.out.println("---------------Exception: Got line: ");
//                System.out.println(String.join("\t",rowObj));
                return null;
            }
        }
        return tokens;
    }

    public static Pair<String,String> parseKeyValFromInput(String value, int atLeast){
        String[] objs = value.split("\t");
        if(objs.length < atLeast)return null; // expected input
        String[] rest = Arrays.copyOfRange(objs,1,objs.length);
        StringJoiner builder = new StringJoiner("\t");
        for(String str : rest){
            builder.add(str);
        }
        return Pair.of(objs[0],builder.toString());
    }

    public static String[] backParse(String target, char delimiter,int expectedDelimiters){
        List<String> ret = new ArrayList<>();
        int lastIndex = target.length();
        for(int i=target.length()-1;i>=0;i--){
            if(target.charAt(i) == delimiter && expectedDelimiters > 0){
                ret.add(0,target.substring(i+1,lastIndex));
                lastIndex = i;
                expectedDelimiters--;
            }else if(expectedDelimiters == 0 || i==0){
                ret.add(0,target.substring(0,lastIndex));
                break;
            }
        }
        String[] retVal = new String[ret.size()];
        retVal = ret.toArray(retVal);
        return retVal;
    }

    /**
     * Finding the shortest path between the src and the target if exists,
     * by doing backward walk on the tree from the src to the target.
     * @param src
     * @param target
     * @param tokens
     * @return
     */
    public static String findPath(NgramToken src, NgramToken target, List<NgramToken> tokens) {
        List<String> ret = new ArrayList<>();
        NgramToken curToken = src;
        NgramToken parent = tokens.get(Math.max(0,src.getHeadIndex()-1)); // Head-Index is starting from 1

        while (parent.getHeadIndex() != 0 || parent == target) {
            // “authors as Herrick” ---> N:mod:Prep,as,as,Prep:pcomp-n:N
            String curWord = curToken == src ? "" : "," + stem(curToken.getWord());
            String parentWord = parent == target ? "" : stem(parent.getWord()) + ",";
            String depTuple =  parentWord + parent.getPos() + ":" + curToken.getDepLabel() + ":" + curToken.getPos() + curWord;
            ret.add(0,depTuple);
            if (parent == target) {
                return String.join(",",ret);
            }
            curToken = parent;
            parent = tokens.get(Math.max(0,parent.getHeadIndex()-1));
        }
        return null;
    }

    /**
     * Find the shortest paths between t1 to t2 in the tree.
     * @param t1
     * @param t2
     * @param tokens
     * @return
     */
    public static String buildPath(NgramToken t1, NgramToken t2, List<NgramToken> tokens) {
        String t1t2 = SyntacticNgramsUtils.findPath(t1, t2, tokens);
        if (t1t2 != null) {
//            System.out.println("Found path between: " + t1.getWord() +" to: "+ t2.getWord());
            return t1t2;
        }

        String t2t1 = SyntacticNgramsUtils.findPath(t2, t1, tokens);
        if (t2t1 != null) {
//            System.out.println("Found path between: " + t2.getWord() +" to: "+ t1.getWord());
            return t2t1;
        }

//        System.out.println("No path between: " + t1.getWord() +" to: "+ t2.getWord() + " at any direction");
        return null;
    }


    /**
     * Find all shortest paths within the sequence of given tokens.
     *
     * @param tokens - list of tokens from the corresponding line in the n-gram google data-set.
     * @return map between path to noun-pairs it appears in it that found inside the line.
     */
    public static Map<String, Pair<NgramToken, NgramToken>> findPaths(List<NgramToken> tokens) {
        String[] nounsPOS = {"NN","NNS","NNP","NNPS"};
        Map<String, Pair<NgramToken, NgramToken>> map = new HashMap<>();
        for (int i = 0; i < tokens.size() - 1; i++) {
            NgramToken current = tokens.get(i);
            if (!Arrays.asList(nounsPOS).contains(current.getPos()))
                continue;
            List<NgramToken> rest = tokens.subList(i+1, tokens.size());
            rest.forEach((NgramToken tok) -> {
                if (Arrays.asList(nounsPOS).contains(tok.getPos())) {
                    String path = SyntacticNgramsUtils.buildPath(current, tok, tokens);
                    if (path != null) {
                        map.put(path, Pair.of(current, tok));
                    }
                }
            });
        }
        return map;
    }

    public static String stem(String s) {
        Stemmer stemmer = new Stemmer();
        stemmer.add(s.toCharArray(),s.length());
        stemmer.stem();
        return stemmer.toString();
    }
}
