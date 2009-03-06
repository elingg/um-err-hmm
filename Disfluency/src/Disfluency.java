import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
//import weka.classifiers.functions.SMO;
//import weka.classifiers.Classifier;
//import weka.experiment.Experiment;

// main driver class...
public class Disfluency {
	int m_npregram, m_npostgram;
	Vector<String> m_featureNames;
	Vector<String> m_featureTypes;
		
	public static void main(String[] args) {
		CommandLineParser argp = new CommandLineParser();
		argp.parseArguments(args);

		Disfluency disfl = new Disfluency(argp.m_npregram,argp.m_npostgram);
		
		// get training datafiles (get xml and wav files)
		HashMap<String, String> xmlwavfiles = disfl.getTrainingFiles(argp.m_srcDir);
		
		System.out.println("Using "+argp.m_npregram+" pregrams and "+argp.m_npostgram+" postgrams as features");
		System.out.println("Detected "+xmlwavfiles.size()+" wav-xml pairs under "+argp.m_srcDir);
		// create arff file
		String wekafname = argp.m_wekaInputFile; 
		if(wekafname.length()==0) {
			wekafname = "tmp.arff";
		}
		File wekafile = new File(wekafname);
    	WekaInput wi = new WekaInput(wekafile);
    	
    	wi.startHeaderWrite();
    	disfl.writeFeatureHeaders(wi);
    	
    	wi.startDataWrite();
    	Iterator<Map.Entry<String, String>> xmlwav = xmlwavfiles.entrySet().iterator();
		// extract features from training files...
		System.out.println("Extracting features into: "+wekafname+" file...");
		while(xmlwav.hasNext()) {
			  Map.Entry<String,String> entry = xmlwav.next();
			  System.out.println("Processing: "+entry.getKey()+
					  " and "+entry.getValue()+"...");
			  disfl.extractAndWriteFeatures(entry.getKey(), entry.getValue(), wi);
		}
		System.out.println("Done extracting features into: "+wekafname+" file.");
		// run weka experiment on wekafile
	    // Experiment exp = new Experiment();
	}
	public Disfluency(int npregram, int npostgram) {
		m_featureNames = new Vector<String>();
		m_featureTypes = new Vector<String>();
		m_npregram = npregram; m_npostgram = npostgram; 
		m_featureNames.add("word"); m_featureTypes.add("string");
		m_featureNames.add("pos"); m_featureTypes.add("string");
    	for(int i=0; i<m_npregram; i++) {
    		m_featureNames.add("pre".concat(String.valueOf(i))); m_featureTypes.add("string");
    		m_featureNames.add("prepos".concat(String.valueOf(i))); m_featureTypes.add("string");
    	}
    	for(int i=0; i<m_npostgram; i++) {
    		m_featureNames.add("post".concat(String.valueOf(i))); m_featureTypes.add("string");
    		m_featureNames.add("postpos".concat(String.valueOf(i))); m_featureTypes.add("string");
    	}
    	// TODO: add prosodic features (as numerics/strings)
		m_featureNames.add("label"); m_featureTypes.add("string");
		ProsodicFeaturesExtractor prosodic = new ProsodicFeaturesExtractor();
		Vector<String> pfnames = prosodic.getFeatureNames();
	    for(String name : pfnames) {
	    	m_featureNames.add(name);
		m_featureTypes.add("string");
	    }
	}
	public void writeFeatureHeaders(WekaInput wi) {
		for(int i=0; i<m_featureNames.size(); ++i) {
			wi.writeFeatureHeader(m_featureNames.get(i), m_featureTypes.get(i));
		}
	}

	public class MyFilter implements FilenameFilter {
		String m_ext;
		public MyFilter(String ext) { m_ext = ext; }
        public boolean accept(File dir, String name) {
        	if(name.startsWith("."))
        		return false;
        	if(!name.endsWith(".".concat(m_ext)))
        		return false;
        	return true;
        }
    };
	public File[] getFilesWithExtension(String dir, String ext) {
		File txtdir = new File(dir);
		MyFilter filter = this.new MyFilter(ext);
	    File[] files = txtdir.listFiles(filter);		
	    return files;
	}
	public HashMap<String, String> getTrainingFiles(String dataDir) {
		// data/speech/dev1_wav
		// data/speech/dev2_wav
		// data/speech/eval_wav
		// data/text/dev1
		// data/text/dev2
		// data/text/eval
		String xmldir = new String(dataDir);
		xmldir = xmldir.concat("/text/dev1");
	    File[] xmlfiles = getFilesWithExtension(xmldir,"xml");
	    String speechdir = new String(dataDir);
		speechdir = speechdir.concat("/speech/dev1_wav");
		
		// File[] speechfiles = getFilesWithExtension(speechdir,"wav");
	    // add others if you want later.
	    // TODO: associate them and return
		HashMap<String, String> xmlwavfiles = new HashMap<String, String>();
		for(File xmlfile:xmlfiles) {
			String[] fields = xmlfile.getName().split("\\.");
			String wavname = fields[0];
			wavname = wavname.concat(".wav");
			wavname = speechdir+"/"+wavname;
			File speechfile = new File(wavname);
			if(!speechfile.isFile())
				continue;
			xmlwavfiles.put(xmlfile.getAbsolutePath(), speechfile.getAbsolutePath());
//			System.out.println(xmlfile.getAbsolutePath());
//			System.out.println(speechfile.getAbsolutePath());
		}	    
	    
		return xmlwavfiles;
	}
	static String quoteWord(String word) {
		String quoted = new String();
		quoted = "\""+word+"\"";
		return quoted;
	}
	public void extractAndWriteFeatures(String xmlfile, String wavfile, WekaInput wi) {
    	SpeakerDoc sd;
    	ParseDocument pd = new ParseDocument();
    	sd = pd.process(new File(xmlfile));
    	Vector<AG> sentences = sd.getAG();
    	Vector<Vector<String>> allwordFeatures = new Vector<Vector<String>>();
    	Vector<Double> wordStartOffset = new Vector<Double>();
    	Vector<Double> wordStopOffset = new Vector<Double>();
    	for(AG sentence : sentences) {
	    	// for each sentence in this
    		String sentstr = sentence.getMetadata().getSentence();
//    		System.out.println("Sentence unit:");
//    		System.out.println(sentstr);
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
    			System.out.printf("ERROR! Number of annotations: %d, Number of words: %d\n",
    					wordanns.size(),words.size());
    			System.out.println("Words:");
    			System.out.println(words.toString());
    			System.out.println("Annotations:");
    			System.out.println(wordanns.toString());
    			System.out.println("Comment: "+sentence.getMetadata().getTBComment());
    			continue; // onto next sentence
    		}
    		for(int i=0; i< wordanns.size(); i++) {
	    		Vector<String> features = new Vector<String>();
    	    	//   for each word
    			Annotation ann = wordanns.get(i);
    			String empty = new String("$");
    	    	
    			//    get word
    			features.add(quoteWord(words.get(i)));
    	    	//    get POS tag for word
    			features.add(ann.getTag());
    	    	
    			//    get n-pregrams
    			for(int ip=0; ip<m_npregram; ip++) {
    				int wordindex = i - m_npregram + ip;
    				if(wordindex<0) {
    					features.add(empty);
    	    			features.add(empty.concat("POS"));
    				} else {
    					features.add(quoteWord(words.get(wordindex)));
    					features.add(wordanns.get(wordindex).getTag());
    				}
    			}
    	    	//    get n-postgrams
    			for(int ip=0; ip<m_npostgram; ip++) {
    				int wordindex = i + 1 + ip;
    				if(wordindex>= words.size()) {
    					features.add(empty);
    	    			features.add(empty.concat("POS"));
    				} else {
    					features.add(quoteWord(words.get(wordindex)));
    					features.add(wordanns.get(wordindex).getTag());
    				}
    			}
    	    	//    get times that word spans
    			Double starttime = sentence.getOffsetTimeForAnchor(ann.getStartAnchor());
    			Double endtime = sentence.getOffsetTimeForAnchor(ann.getStopAnchor());
    			
    			String sentenceType = sentence.getMetadata().getSentenceType();
    			// ... question, backchannel, statement
    			features.add(sentenceType);
    			allwordFeatures.add(features);
    			wordStartOffset.add(starttime);
    			wordStopOffset.add(endtime);
//    			System.out.println("Features:");
//    			for(String feature: features) {
//    				System.out.print(" ".concat(feature));
//    			}
//    			System.out.println("");
    		}
    	} // for each sentence
    	// add prosodic features...
    	ProsodicFeaturesExtractor prosodic = new ProsodicFeaturesExtractor();
		Vector<Vector<Double>> pfeats = prosodic.extractFeatures(wavfile, wordStartOffset, wordStopOffset);
    	for(int iword = 0; iword<allwordFeatures.size(); iword++) {
    		Vector<String> features = allwordFeatures.get(iword);
    		if(pfeats.size()>0) {
    			Vector<Double> prosodicfeatures = pfeats.get(iword);
    			for(int ip=0; ip<prosodicfeatures.size(); ip++) {
    				features.add(String.valueOf(prosodicfeatures.get(ip)));
    			}
    		}
			wi.writeData(features);
    	}
	}
}
