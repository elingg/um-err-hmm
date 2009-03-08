
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

//import weka.classifiers.functions.SMO;
//import weka.classifiers.Classifier;
//import weka.experiment.Experiment;


// main driver class...
public class WordStream {
	int m_npregram, m_npostgram;
	Vector<String> m_featureNames;
	Vector<String> m_featureTypes;
	HashSet<String> m_featureActive;
	HashMap<String, HashSet<String>> m_featureDict; // indexed by feature-name
	List<Word> wordStreams = new ArrayList<Word>();
	
	class SpeakerPair{
		public String a;
		public String b;
	}
	
	public static void main(String[] args) {
		CommandLineParser argp = new CommandLineParser();
		argp.parseArguments(args);

		WordStream disfl = new WordStream(argp.m_npregram,argp.m_npostgram);
		
		// get training datafiles (get xml and wav files)
		HashMap<String, SpeakerPair> xmlwavfiles = disfl.getTrainingFiles(argp.m_srcDir);	
		 
		
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
    	Iterator<Map.Entry<String, SpeakerPair>> xmlwav = xmlwavfiles.entrySet().iterator();
		// extract features from training files...
    	
		System.out.println("Building vocab: ");
		while(xmlwav.hasNext()) {
			  Map.Entry<String,SpeakerPair> entry = xmlwav.next();
			  System.out.println(entry.getValue().a);
			  disfl.extractAndWriteFeatures(entry.getValue().a, entry.getKey(), wi, true, argp.m_verbose);
			  disfl.extractAndWriteFeatures(entry.getValue().b, entry.getKey(), wi, true, argp.m_verbose);
			  break;// for now, TODO: REMOVE 
		}
		xmlwav = xmlwavfiles.entrySet().iterator();
		// extract features from training files...
    	// that each feature takes
		disfl.writeFeatureHeaders(wi); // delay writing to here since we may need nominal values
    	wi.startDataWrite();
		System.out.println("Extracting features into: "+wekafname+" file...");
		while(xmlwav.hasNext()) {
			  Map.Entry<String,SpeakerPair> entry = xmlwav.next();
			  if(argp.m_verbose) {
				  System.out.println("Processing: "+entry.getKey()+
					  " and "+entry.getValue()+"...");
			  }
//			  disfl.extractAndWriteFeatures( entry.getValue().a, entry.getKey(), wi, false, argp.m_verbose);
//			  disfl.extractAndWriteFeatures( entry.getValue().b, entry.getKey(), wi, false, argp.m_verbose);
			  break;// for now, TODO: REMOVE 
		}
		Collections.sort(disfl.wordStreams, TimeOffset);
		ArrayList<Word> reverse= new ArrayList<Word>();
//		for(int k=disfl.wordStreams.size()-1; k>=0; k--){
//			reverse.add(k, disfl.wordStreams.get(disfl.wordStreams.size()-k-1));
//			
//		}		
		for(Word word : disfl.wordStreams){
			System.out.println(word.word+"\t"+word.speaker+"\t"+word.StartOffset);
		}
		
		
		wi.closeFile();
		System.out.println("Done extracting features into: "+wekafname+" file.");
		// run weka experiment on wekafile
	    // Experiment exp = new Experiment();
	}
	public WordStream(int npregram, int npostgram) {
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
		Vector<String> pfnames = prosodic.getFeatureNames();
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
	public HashMap<String, SpeakerPair> getTrainingFiles(String dataDir) {
		// data/speech/dev1_wav
		// data/speech/dev2_wav
		// data/speech/eval_wav
		// data/text/dev1
		// data/text/dev2
		// data/text/eval
	    // for now dev1, add others if you want later.
		String xmldir = new String(dataDir);
		xmldir = xmldir.concat("/text/dev1");
	    String speechdir = new String(dataDir);
		speechdir = speechdir.concat("/speech/dev1_wav");
	    File[] speechfiles = getFilesWithExtension(speechdir,"wav");

		
		// File[] speechfiles = getFilesWithExtension(speechdir,"wav");
		HashMap<String, SpeakerPair> xmlwavfiles = new HashMap<String, SpeakerPair>();
		for(File speechfile:speechfiles) {
			String[] fields = speechfile.getName().split("\\.");
			String xmlname = fields[0];
			String xmlnamea="";
			String xmlnameb="";
			xmlnamea = xmlname.concat(".a.parse.ftags.ag.xml");
			xmlnamea = xmldir+"/"+xmlnamea;
			xmlnameb = xmlname.concat(".b.parse.ftags.ag.xml");
			xmlnameb = xmldir+"/"+xmlnameb;			

			File xmlfilea = new File(xmlnamea);
			File xmlfileb = new File(xmlnameb);
			if(!xmlfilea.isFile() && !xmlfileb.isFile())
				continue;
			SpeakerPair sp= new SpeakerPair();
			sp.a= xmlfilea.getAbsolutePath();
			sp.b= xmlfileb.getAbsolutePath();
			xmlwavfiles.put(speechfile.getAbsolutePath(), sp);
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
	
	static final Comparator<Word> TimeOffset = new Comparator<Word>() {
		public int compare(Word e1, Word e2) {
			if( e2.StartOffset > (e1.StartOffset))
				return -1;
			else if( e2.StartOffset < (e1.StartOffset))
				return 1;
			return 0;
		}
	};	
	
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
    			
    			Word word= new Word();
    			word.speaker = sd.getSpeakerName();
    			
	    		Vector<String> features = new Vector<String>();
    	    	//   for each word
    			Annotation ann = wordanns.get(i);
    			String empty = new String("$");
    			// 	m_featureDict.put("word", empty);
    			//  get word
    			word.word=words.get(i);
    			if(m_featureActive.contains("word")) {
    				features.add(words.get(i));
    				word.word = words.get(i);
    			} 
    	    	//  get POS tag for word
    			if(m_featureActive.contains("pos")) {
    				word.POS = ann.getTag();
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
					word.sentenceType = sentence.getMetadata().getSentenceType();
					features.add(sentenceType);
					m_featureDict.get("label").add(sentenceType);
				}
    			allwordFeatures.add(features);
    			wordStartOffset.add(starttime);
    			word.StartOffset = starttime;
    			wordStopOffset.add(endtime);
    			wordStreams.add(word);
    		}
    	} // for each sentence
    	// add prosodic features...
    	if(!buildVocabOnly) {
    		ProsodicFeaturesExtractor prosodic = new ProsodicFeaturesExtractor();
    		Vector<String> prosodicfnames = prosodic.getFeatureNames();
    		Vector<Vector<Double>> pfeats =	prosodic.extractFeatures(wavfile, wordStartOffset, wordStopOffset);
    		for(int iword = 0; iword<allwordFeatures.size(); iword++) {
    			Vector<String> features = allwordFeatures.get(iword);
    			if(pfeats.size()>0) {
    				Vector<Double> prosodicfeatures = pfeats.get(iword);
    				for(int ip=0; ip<prosodicfeatures.size(); ip++) {
    					if(m_featureActive.contains(prosodicfnames.get(ip))) {
    						features.insertElementAt(String.valueOf(prosodicfeatures.get(ip)),features.size()-1); // insert before label
    					}
    				}
    			}
    			wi.writeData(features);
    		}
    	}
	}
}
