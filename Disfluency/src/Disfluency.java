import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.HashSet;
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
	HashSet<String> m_featureActive;
	HashMap<String, HashSet<String>> m_featureDict; // indexed by feature-name
	
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
    	Iterator<Map.Entry<String, String>> xmlwav = xmlwavfiles.entrySet().iterator();
		// extract features from training files...
		System.out.println("Building vocab: ");
		while(xmlwav.hasNext()) {
			  Map.Entry<String,String> entry = xmlwav.next();
			  disfl.extractAndWriteFeatures(entry.getKey(), entry.getValue(), wi, true, argp.m_verbose);
		}
		xmlwav = xmlwavfiles.entrySet().iterator();
		// extract features from training files...
    	// that each feature takes
		disfl.writeFeatureHeaders(wi); // delay writing to here since we may need nominal values
    	wi.startDataWrite();
		System.out.println("Extracting features into: "+wekafname+" file...");
		while(xmlwav.hasNext()) {
			  Map.Entry<String,String> entry = xmlwav.next();
			  if(argp.m_verbose) {
				  System.out.println("Processing: "+entry.getKey()+
					  " and "+entry.getValue()+"...");
			  }
			  disfl.extractAndWriteFeatures(entry.getKey(), entry.getValue(), wi, false, argp.m_verbose);
		}
		System.out.println("Done extracting features into: "+wekafname+" file.");
		// run weka experiment on wekafile
	    // Experiment exp = new Experiment();
	}
	public Disfluency(int npregram, int npostgram) {
		m_featureNames = new Vector<String>();
		m_featureTypes = new Vector<String>();
		m_featureDict = new HashMap<String, HashSet<String>>();
		m_featureActive = new HashSet<String>();
		
		m_npregram = npregram; m_npostgram = npostgram; 
		
		// word...
		m_featureNames.add("word"); 
		m_featureTypes.add("string"); 
		// pos...
		m_featureNames.add("pos"); 
		m_featureTypes.add("nominal"); 
		m_featureDict.put("pos",new HashSet<String>());
		m_featureActive.add("pos");
    	for(int i=0; i<m_npregram; i++) {
    		// pre...
    		String featname = "pre".concat(String.valueOf(i));
    		m_featureNames.add(featname); 
    		m_featureTypes.add("string"); 
    		// prepos...
    		featname = "prepos".concat(String.valueOf(i));
    		m_featureNames.add(featname); 
    		m_featureTypes.add("nominal"); 
    		m_featureDict.put(featname,new HashSet<String>());
    		m_featureActive.add(featname);
    	}
    	for(int i=0; i<m_npostgram; i++) {
    		// post...
    		String featname = "post".concat(String.valueOf(i));
    		m_featureNames.add(featname); 
    		m_featureTypes.add("string"); 
    		// postpos...
    		featname = "postpos".concat(String.valueOf(i));
    		m_featureNames.add(featname); 
    		m_featureTypes.add("nominal"); 
    		m_featureDict.put(featname,new HashSet<String>());
    		m_featureActive.add(featname);
    	}
    	// add prosodic features (as numerics)
		ProsodicFeaturesExtractor prosodic = new ProsodicFeaturesExtractor();
		Vector<String> pfnames = new Vector<String>();
//		prosodic.getFeatureNames();
	    for(String name : pfnames) {
	    	m_featureNames.add(name);
			m_featureTypes.add("numeric");
    		m_featureActive.add(name);
	    }

	    // add the final label to the end...
		m_featureNames.add("label"); 
		m_featureTypes.add("nominal");
		m_featureDict.put("label",new HashSet<String>());
		m_featureActive.add("label");
	}
	
	public void writeFeatureHeaders(WekaInput wi) {
		for(int i=0; i<m_featureNames.size(); ++i) {
			if(!m_featureActive.contains(m_featureNames.get(i))) {
				continue;
			}
			if(m_featureTypes.get(i)=="nominal") {
				wi.writeFeatureHeaderForNominal(m_featureNames.get(i),
						m_featureDict.get(m_featureNames.get(i)));
			} else {
				wi.writeFeatureHeader(m_featureNames.get(i), m_featureTypes.get(i));
			}
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
	    // for now dev1, add others if you want later.
		String xmldir = new String(dataDir);
		xmldir = xmldir.concat("/text/dev1");
	    File[] xmlfiles = getFilesWithExtension(xmldir,"xml");
	    String speechdir = new String(dataDir);
		speechdir = speechdir.concat("/speech/dev1_wav");
		
		// File[] speechfiles = getFilesWithExtension(speechdir,"wav");
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
	public void extractAndWriteFeatures(String xmlfile, String wavfile, WekaInput wi, 
			boolean buildVocabOnly, boolean verbose) {
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
    			if(verbose) {
    				System.out.printf("ERROR! Number of annotations: %d, Number of words: %d\n",
    						wordanns.size(),words.size());
    				System.out.println("Words:");
    				System.out.println(words.toString());
    				System.out.println("Annotations:");
    				System.out.println(wordanns.toString());
    				System.out.println("Comment: "+sentence.getMetadata().getTBComment());
    			}
    			continue; // onto next sentence
    		}
    		for(int i=0; i< wordanns.size(); i++) {
	    		Vector<String> features = new Vector<String>();
    	    	//   for each word
    			Annotation ann = wordanns.get(i);
    			String empty = new String("$");
    			// 	m_featureDict.put("word", empty);
    			//  get word
    			if(m_featureActive.contains("word")) {
    				features.add(words.get(i));
    			} 
    	    	//  get POS tag for word
    			if(m_featureActive.contains("pos")) {
    				features.add(ann.getTag());
    				m_featureDict.get("pos").add(ann.getTag());
    			}    	    	
    			//    get n-pregrams
    			for(int ip=0; ip<m_npregram; ip++) {
    	    		String featname = "pre".concat(String.valueOf(ip));
    	    		String posfeatname = "prepos".concat(String.valueOf(ip));
    				int wordindex = i - m_npregram + ip;
    				if(wordindex<0) {
    					if(m_featureActive.contains(featname)) {
    						features.add(empty);
    					}
    					if(m_featureActive.contains(posfeatname)) {
    						features.add(empty.concat("POS"));
        					m_featureDict.get(posfeatname).add(empty.concat("POS"));
    					}
    				} else {
    					if(m_featureActive.contains(featname)) {
    						features.add(words.get(wordindex));
    					}
    					if(m_featureActive.contains(posfeatname)) {
    						features.add(wordanns.get(wordindex).getTag());    						
    						m_featureDict.get(posfeatname).add(wordanns.get(wordindex).getTag());
    					}
    				}
    			}
    	    	//    get n-postgrams
    			for(int ip=0; ip<m_npostgram; ip++) {
    	    		String featname = "post".concat(String.valueOf(ip));
    	    		String posfeatname = "postpos".concat(String.valueOf(ip));
    				int wordindex = i + 1 + ip;
    				if(wordindex>= words.size()) {
    					if(m_featureActive.contains(featname)) {
    						features.add(empty);
    					}
    					if(m_featureActive.contains(posfeatname)) {
    						features.add(empty.concat("POS"));
        					m_featureDict.get(posfeatname).add(empty.concat("POS"));
    					}
    				} else {
    					if(m_featureActive.contains(featname)) {
    						features.add(words.get(wordindex));
    					}
    					if(m_featureActive.contains(posfeatname)) {
    						features.add(wordanns.get(wordindex).getTag());
        					m_featureDict.get(posfeatname).add(wordanns.get(wordindex).getTag());
    					}
    				}
    			}
    	    	//    get times that word spans
    			Double starttime = sentence.getOffsetTimeForAnchor(ann.getStartAnchor());
    			Double endtime = sentence.getOffsetTimeForAnchor(ann.getStopAnchor());
    			
    			String sentenceType = new String("none");
    			if(i==wordanns.size()-1) { // if last word
    	   			sentenceType = sentence.getMetadata().getSentenceType();
        			// ... question, backchannel, statement
    			}
				if(m_featureActive.contains("label")) {
					features.add(sentenceType);
					m_featureDict.get("label").add(sentenceType);
				}
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
		Vector<Vector<Double>> pfeats = new Vector<Vector<Double>>();
		Vector<String> prosodicfnames = prosodic.getFeatureNames();
//		prosodic.extractFeatures(wavfile, wordStartOffset, wordStopOffset);
    	for(int iword = 0; iword<allwordFeatures.size(); iword++) {
    		Vector<String> features = allwordFeatures.get(iword);
    		if(pfeats.size()>0) {
    			Vector<Double> prosodicfeatures = pfeats.get(iword);
    			for(int ip=0; ip<prosodicfeatures.size(); ip++) {
    				if(m_featureActive.contains(prosodicfnames.get(ip))) {
    					features.add(String.valueOf(prosodicfeatures.get(ip)));
    					m_featureDict.get(features.get(ip)).add(String.valueOf(prosodicfeatures.get(ip)));
    				}
    			}
    		}
        	if(!buildVocabOnly) {
        		wi.writeData(features);
        	}
    	}
	}
}
