package DependencyPathExplorer;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class Constants {
    public static final String aws_access_key_id = "ASIA5JA4V7A2SOQI7FI5";
    public static final String aws_secret_access_key = "4cjNRxUzIr6SVlOUPEW7puFGb9rCtTLoR79Pnfrv";
    public static final String aws_session_token =
            "FwoGZXIvYXdzEJ3//////////wEaDNTUqYpo4JY35TNj9SLEAbtIo7rzRPGHSsaWMjeZDlwgUCnohyfZGs/I6IpvvpRUN5AGm3Bfpyfk6lIkae0Q/3g6l2TeaCOPU0Olyy38Ivxyib0hRk9fd5Zdfz6xhEuMxAoJO0YHFyrv0++fmTVph6E3AKdV9XMEeLVHsE3zsioeBKvoBOw1u9bb60oBkY2xTiNB/sRj4ar4QNRt5jbf6Y8EGS37YVITHSgui9n61XcmJV5GAiHIHcohkF7cZ8kYBM/Yr7JXVAXcDcZM0rW1aURaLhcosN3L+QUyLeeOGTArWbGPyj55gtSfPfzelZ/H0EsJU92v5otgZfu6L66JCWEgHIszRPh00Q==";
    public static final String step2PrefixForNounPairs = "#";
    public static final String step2PrefixForPath = "*";
    public static final String step3PrefixForPath = "$";
    public static final String specialDelimiter = "_";

    public static final String step4PrefixForLabel = "$";
    public static final String step4PrefixForFVInfo = "#";

    public static final String DPMin = "6";

    public static final String step1Input = "s3n://dspass3ngrams/ngrams/";
    public static final String step1Output = "s3n://thirdassoutput/step1output-"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +"/";
    public static final String step2Output = "s3n://thirdassoutput/step2output-"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +"/";
    public static final String step3Output = "s3n://thirdassoutput/step3output-"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +"/";
    public static final String step4Output = "s3n://thirdassoutput/step4output-"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +"/";
    public static final String step5Output = "s3n://thirdassoutput/step5output-"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +"/";

    public static final String hypernyms = "s3n://dspass3ngrams/hypernyms/";

    public static final String logsPath = "s3n://thirdassoutput/logs0-"+ new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) +"/";

    public static final String step1Jar = "s3://thirdassjars/Step1-Merge.jar";
    public static final String step2Jar = "s3://thirdassjars/Step2-Filter.jar";
    public static final String step3Jar = "s3://thirdassjars/Step3-Index.jar";
    public static final String step4Jar = "s3://thirdassjars/Step4-Join.jar";
    public static final String step5Jar = "s3://thirdassjars/Step5-Builder.jar";

}
