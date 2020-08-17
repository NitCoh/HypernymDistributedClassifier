package DependencyPathExplorer;

import java.util.Arrays;

public class NgramToken {
    private String word;
    private String pos;
    private String depLabel;
    private Integer headIndex;
    private Integer indexInSentence;

    public NgramToken(String word, String pos, String depLabel, String headIndex, int indexInSentence) {
        this.word = word;
        this.pos = pos;
        this.depLabel = depLabel;
        this.headIndex = Integer.parseInt(headIndex);
        this.indexInSentence = indexInSentence;
    }

    public NgramToken(int indexInSentence,String ... args) {
        try {
            this.word = args[0];
            this.pos = args[1];
            this.depLabel = args[2];
            this.headIndex = Integer.parseInt(args[3]);
            this.indexInSentence = indexInSentence;
        }catch(NumberFormatException e){
//            System.out.println("NumberFormatException: Printing args: "+ Arrays.toString(args));
            throw e;
        }
    }

    public String getWord() {
        return word;
    }
    public String getPos() {
        return pos;
    }

    public String getDepLabel() {
        return depLabel;
    }

    public Integer getHeadIndex() {
        return headIndex;
    }

    public Integer getIndexInSentence() {
        return indexInSentence;
    }
}
