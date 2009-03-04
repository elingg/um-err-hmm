import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;

public class Disfluency {
	public static void main(String[] args) {
		// get all xml documents in a directory
		// extract features and write out into file
		CommandLineParser argp = new CommandLineParser();
		argp.parseArguments(args);
		File dir = new File(argp.m_srcDir);
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	        	if(name.startsWith("."))
	        		return false;
	        	if(!name.endsWith(".xml"))
	        		return false;
	        	return true;
	        }
	    };
	    File[] files = dir.listFiles(filter);
	    // Writing training data...
    	ParseDocument pd = new ParseDocument();
    	File outputfile = new File(argp.m_wekaInputFile);
    	WekaInput wi = new WekaInput(outputfile);
    	wi.startHeaderWrite();
    	wi.writeFeatureHeader("word", "string");
    	wi.writeFeatureHeader("pos", "string");
    	for(int i=0; i<argp.m_npregram; i++) {
    		wi.writeFeatureHeader("pre".concat(String.valueOf(i)), "string");
    		wi.writeFeatureHeader("prepos".concat(String.valueOf(i)), "string");
    	}
    	for(int i=0; i<argp.m_npostgram; i++) {
    		wi.writeFeatureHeader("post".concat(String.valueOf(i)), "string");
    		wi.writeFeatureHeader("postpos".concat(String.valueOf(i)), "string");
    	}
    	wi.writeFeatureHeader("label", "string");
    	wi.startDataWrite();
	    for(File file : files) {
			// parse it into SpeakerDocs
	    	SpeakerDoc sd;
	    	sd = pd.process(file);
	    	Vector<AG> sentences = sd.getAG();
	    	for(AG sentence : sentences) {
		    	// for each sentence in this
	    		String sentstr = sentence.getMetadata().getSentence();
	    		System.out.println("Sentence unit:");
	    		System.out.println(sentstr);
	    		Vector<Annotation> anns = sentence.getAnnotations();
	    		// each annotation is either a filler or a word...
	    		// for each word...
	    		String[] subsent = sentstr.split(" "); // will have FP tags as well.
	    		// strip empty words and strip "."
	    		Vector<String> words = new Vector<String>();
	    		for(String sub: subsent) {
	    			if((sub.length()==0) || 
	    					(sub.compareTo(".")==0) ||
	    					(sub.compareTo(",")==0) ||
	    					(sub.compareTo("?")==0) ||
	    					(sub.startsWith("[")&&sub.endsWith("]"))) {
	    				continue;
	    			}
	    			if(sub.startsWith("'")) {
	    				sub = "\\"+sub;
	    			}
	    			words.add(sub);
	    		}
	    		Vector<Annotation> wordanns = new Vector<Annotation>();
	    		for(int i = 0; i<anns.size(); i++) {
	    			Annotation ann = anns.get(i);
	    			if((ann.getType().compareTo("word")!=0) || 
	    					(ann.getTag().compareTo(".")==0) || 
	    					(ann.getTag().compareTo(",")==0)) {
	    				continue;
	    			}
	    			wordanns.add(ann);
	    		}
	    		if(words.size()!=wordanns.size()) {
	    			System.out.printf("ERROR! Number of annotations: %d, Number of words: %d",
	    					wordanns.size(),words.size());
	    			return;
	    		}
	    		for(int i=0; i< wordanns.size(); i++) {
		    		Vector<String> features = new Vector<String>();
	    	    	//   for each word
	    			Annotation ann = wordanns.get(i);
	    			String empty = new String("$");
	    	    	
	    			//    get word
	    			features.add(words.get(i));
	    	    	//    get POS tag for word
	    			features.add(ann.getTag());
	    	    	
	    			//    get n-pregrams
	    			for(int ip=0; ip<argp.m_npregram; ip++) {
	    				int wordindex = i - argp.m_npregram + ip;
	    				if(wordindex<0) {
	    					features.add(empty);
	    	    			features.add(empty.concat("POS"));
	    				} else {
	    					features.add(words.get(wordindex));
	    					features.add(wordanns.get(wordindex).getTag());
	    				}
	    			}
	    	    	//    get n-postgrams
	    			for(int ip=0; ip<argp.m_npostgram; ip++) {
	    				int wordindex = i + 1 + ip;
	    				if(wordindex>= words.size()) {
	    					features.add(empty);
	    	    			features.add(empty.concat("POS"));
	    				} else {
	    					features.add(words.get(wordindex));
	    					features.add(wordanns.get(wordindex).getTag());
	    				}
	    			}
	    	    	//    get times that word spans
	    			Double starttime = sentence.getOffsetTimeForAnchor(ann.getStartAnchor());
	    			Double endtime = sentence.getOffsetTimeForAnchor(ann.getStopAnchor());
	    			//    get prosodic features given times: TODO
	    			String sentenceType = sentence.getMetadata().getSentenceType();
	    			// ... question, backchannel, statement
	    			features.add(sentenceType);
	    			wi.writeData(features);
	    			System.out.println("Features:");
	    			for(String feature: features) {
	    				System.out.print(" ".concat(feature));
	    			}
	    			System.out.println("");
	    		}
	    	} // for each sentence
	    	break; // only first file for now.
	    } // for each file
	}
}
