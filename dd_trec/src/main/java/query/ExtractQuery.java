package query;


import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.*;
import edu.stanford.nlp.ling.CoreLabel;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by cat on 16/8/21.
 */
public class ExtractQuery {

    private static String serializedClassifier = "/backup/dd_trec/MDP/stanford-ner/classifiers/english.all.3class.distsim.crf.ser.gz";

    public static String extract(String feedbackPath) throws IOException, ClassCastException, ClassNotFoundException {

//        String feedback_path = "/Volumes/HDD/link/adventure/项目/dd-trec/feedback";

        final Map<String, Integer> countMap = new HashMap();

        AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier.getClassifier(serializedClassifier);

        File file = new File(feedbackPath);
        String sentence = FileUtils.readFileToString(file, "utf-8");
        
//        remove all the html tag in sentence
        sentence = Jsoup.clean(sentence, Whitelist.none());
        String sentence_with_label = classifier.classifyWithInlineXML(sentence);

        try {
			FrequencyWords.mostFrequentWords(sentence_with_label, countMap);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			System.out.println(sentence_with_label);
			e.printStackTrace();
		}

        List<Map.Entry<String, Integer>> sorted_list = FrequencyWords.sortByValue(countMap);

        StringBuilder sb = new StringBuilder();
        int i = 0;
        while (i < 2 && i < sorted_list.size()) {
            sb.append(sorted_list.get(i).getKey()+" ");
            i++;
        }

        return sb.toString();
    }
}
