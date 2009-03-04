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
	    for(File file : files) {
			// parse it into SpeakerDocs
	    	SpeakerDoc sd;
	    	sd = pd.process(file);
	    	Vector<AG> sentences = sd.getAG();
	    	for(AG sentence : sentences) {
		    	// for each sentence in this
	    		String sentstr = sentence.getMetadata().getSentence();
	    		Vector<Annotation> anns = sentence.getAnnotations();
	    		// each annotation is either a filler or a word...
	    		// for each word...
	    		String[] rawwords = sentstr.split(" "); // will have FP tags as well.
	    		
	    		if(rawwords.length!=anns.size()) {
	    			System.out.printf("ERROR! Number of annotations: %d, Number of words: %d",anns.size(),rawwords.length);
	    			return;
	    		}
	    		Vector<Annotation> wordanns = new Vector<Annotation>();
	    		Vector<String> words = new Vector<String>();
	    		for(int i = 0; i<anns.size(); i++) {
	    			Annotation ann = anns.get(i);
	    			if(ann.getType().compareToIgnoreCase("filler")==0) {
	    				continue;
	    			}
	    			String word = rawwords[i];
	    			wordanns.add(ann);
	    			words.add(word);
	    		}
	    		for(int i=0; i< wordanns.size(); i++) {
	    	    	//   for each word
	    			Annotation ann = wordanns.get(i);
	    			String empty = new String("$");
	    	    	
	    			//    get word
	    			String word = words.get(i);
	    	    	
	    			//    get n-pregrams
	    			Vector<String> prewords = new Vector<String>(argp.m_npregram);
	    			for(int ip=0; ip<argp.m_npregram; ip++) {
	    				int wordindex = i - argp.m_npregram + ip;
	    				if(wordindex<0) {
	    					prewords.add(ip, empty);
	    				} else {
	    					prewords.add(ip, words.get(i));
	    				}
	    			}
	    	    	//    get n-postgrams
	    			Vector<String> postwords = new Vector<String>(argp.m_npostgram);
	    			for(int ip=0; ip<argp.m_npostgram; ip++) {
	    				int wordindex = i + 1 + ip;
	    				if(wordindex>= words.size()) {
	    					postwords.add(ip, empty);
	    				} else {
	    					postwords.add(ip, words.get(i));
	    				}
	    			}
	    	    	//    get POS tag for word
	    			String pos = ann.getTag();
	    	    	//    get times that word spans
	    			Double starttime = sentence.getOffsetTimeForAnchor(ann.getStartAnchor());
	    			Double endtime = sentence.getOffsetTimeForAnchor(ann.getStopAnchor());
	    			String sentenceType = sentence.getMetadata().getSentenceType();
	    			// ... question, backchannel, statement
	    			
	    			//    get prosodic features given times
	    			// word, prewords, postwords, pos, statement label
	    			// starttime, endtime
	    			System.out.printf("Word: %s, POS: %s, StartTime: %g, EndTime: %g\n", word, pos, starttime, endtime);
	    		}
	    	}
	    }

	}
}
