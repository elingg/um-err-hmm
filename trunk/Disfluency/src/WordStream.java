
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

// main driver class...
public class WordStream {
	int m_npregram, m_npostgram;
	Vector<String> m_featureNames;
	Vector<String> m_featureTypes;
	HashSet<String> m_featureActive;
	HashSet<String> m_POSDict; // indexed by feature-name
//	HashSet<String> m_wordDict;
	HashSet<String> m_labelDict;
	Word m_empty;
	class SpeakerPair{
		public String a;
		public String b;
	}
	public static void main(String[] args) {
		System.out.println(TimeUtils.now());
		
		CommandLineParser argp = new CommandLineParser();
		argp.parseArguments(args);

		WordStream disfl = new WordStream(argp.m_npregram,argp.m_npostgram,
				argp.m_nprev,argp.m_prevstlabel);
		
		// get training datafiles (get xml and wav files)
		HashMap<String, SpeakerPair> xmlwavfiles = disfl.getTrainingFiles(argp.m_srcDir, argp.m_corpusType);	
		
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
    	
//		System.out.println("Building vocab: ");
//		while(xmlwav.hasNext()) {
//			Map.Entry<String,SpeakerPair> entry = xmlwav.next();
//			TreeSet<Word> wordset = new TreeSet<Word>(TimeOffset);
//			disfl.extractWords(entry.getValue().a, argp.m_verbose,wordset);
//			disfl.extractWords(entry.getValue().b, argp.m_verbose,wordset);
//		}
		System.out.println("Writing out features to "+argp.m_wekaInputFile+" ...");
		// This pass, dictionary is built, so write it out...
		disfl.writeFeatureHeaders(wi); // delay writing to here since we may need nominal values
    	wi.startDataWrite();
		xmlwav = xmlwavfiles.entrySet().iterator();
		while(xmlwav.hasNext()) {
			Map.Entry<String,SpeakerPair> entry = xmlwav.next();
			if(argp.m_verbose) {
				System.out.println("Processing wav: "+entry.getKey());
			}
			TreeSet<Word> wordset = new TreeSet<Word>(TimeOffset);
			disfl.extractWords(entry.getValue().a, argp.m_verbose, wordset);
			disfl.extractWords(entry.getValue().b, argp.m_verbose, wordset);
			if(argp.m_writeintervals) {
				File srcfile = new File(entry.getValue().a);
				String[] fields = srcfile.getName().split("\\.");
				String intervaldir = srcfile.getParent();
				intervaldir = intervaldir.concat("_int");
				new File(intervaldir).mkdir();
				String intervalfname = intervaldir+"/"+fields[0]+".interval.txt";
				File intervalfile = new File(intervalfname);
				BufferedWriter br=null;
				try {
					br= new BufferedWriter(new FileWriter(intervalfile));
					for(Word word : wordset){
						if(argp.m_verbose) {
							System.out.println(word.m_word+"\t"+word.m_startOffsetTime+"\t"+word.m_endOffsetTime+"\t"+word.m_speaker);
						}
						br.write(word.m_startOffsetTime+"\n"+word.m_endOffsetTime+"\n"+word.m_speaker+"\n");
					}
					System.out.println("Processed "+srcfile.getPath()+" into interval file "+intervalfile.getPath());
					br.flush();
					br.close();
				} catch(Exception e) {}
			}
			Object[] wordlist = wordset.toArray();
			String prevlabel = "none";
			for(int iw=0;iw<wordlist.length;iw++) {
				Word currword = (Word)wordlist[iw];
    			Vector<String> features = new Vector<String>();
    			if(disfl.isWordPosFeatureActive()) {
//    				System.out.printf("[%s",currword.m_word);
//    				System.out.printf(" %s]",currword.m_POS);
    				features.add(currword.m_POS);
    			}
//    			System.out.printf("[");
    			for(int ip=0; ip<argp.m_npregram; ip++) {
    				int preiw = iw - 1 - ip;
    				Word preword = null;
    				if(preiw >= wordlist.length || preiw <0) {
    					preword = disfl.m_empty;
    				} else {
        				preword = (Word)wordlist[preiw];
    				}
    				if(disfl.isPrePosFeatureActive(ip)) {
//    					System.out.printf(" %s", preword.m_word);
//    					System.out.printf(" %s", preword.m_POS);
    					features.add(preword.m_POS);
    				}
    			}
//    			System.out.printf("] [");
    			for(int ip=0; ip<argp.m_npostgram; ip++) {
    				int postiw = iw + 1 + ip;
    				Word postword = null;
    				if(postiw>= wordlist.length || postiw < 0) {
    					postword = disfl.m_empty;
    				} else {
        				postword = (Word)wordlist[postiw];
    				}
    				if(disfl.isPostPosFeatureActive(ip)) {
//    					System.out.printf(" %s",postword.m_word);
//    					System.out.printf(" %s",postword.m_POS);
    					features.add(postword.m_POS);
    				}
    			}
    			if(disfl.isnPrevUnclassifiedActive()) {
    				features.add(Integer.valueOf(currword.m_wordsBeforeUnclassified).toString());
    			}
    			if(disfl.isPrevStatementUnitLabelActive()) {
        			if(currword.m_endOfSentence) {
        				features.add(prevlabel);
        			} else {
        				features.add("none");
        			}
    			}
//    			System.out.printf("] ");
    			String senttype = "none";
    			if(currword.m_endOfSentence) {
    				senttype = currword.m_sentenceType;
        			prevlabel = senttype;
    			}
    			features.add(senttype);
//    			System.out.println(senttype);
    			wi.writeData(features);
			}
		}
		wi.closeFile();
		System.out.println("Done extracting features into: "+wekafname+" file.");
	}
	static String getPreWordString(int index) {
		return new String("word_pre").concat(String.valueOf(index));
	}
	static String getPostWordString(int index) {
		return new String("word_post").concat(String.valueOf(index));
	}
	static String getPrePosString(int index) {
		return new String("pos_pre").concat(String.valueOf(index));
	}
	static String getPostPosString(int index) {
		return new String("pos_post").concat(String.valueOf(index));
	}
	public WordStream(int npregram, int npostgram, boolean nprev, boolean prevstlabel) {
		m_featureNames = new Vector<String>();
		m_featureTypes = new Vector<String>();
		m_POSDict = new HashSet<String>();
//		m_wordDict = new HashSet<String>();
		
		m_POSDict.add("RB"); m_POSDict.add("UH"); m_POSDict.add("WP$"); m_POSDict.add("CD");
		m_POSDict.add("FW"); m_POSDict.add("JJ"); m_POSDict.add("VBG"); m_POSDict.add("MD"); 
		m_POSDict.add("NN"); m_POSDict.add("VBP"); m_POSDict.add("HVS"); m_POSDict.add("WRB");
		m_POSDict.add("RP"); m_POSDict.add("IN"); m_POSDict.add("VBD"); m_POSDict.add("SYM");
		m_POSDict.add("TO"); m_POSDict.add(":"); m_POSDict.add("VBN"); m_POSDict.add("NNPS"); 
		m_POSDict.add("RBR"); m_POSDict.add("DT"); m_POSDict.add("VB"); m_POSDict.add("VBZ"); 
		m_POSDict.add("NNS"); m_POSDict.add("XX"); m_POSDict.add("JJS"); m_POSDict.add("RBS"); 
		m_POSDict.add("NNP"); m_POSDict.add("POS"); m_POSDict.add("CC"); m_POSDict.add("PRP$"); 
		m_POSDict.add("JJR"); m_POSDict.add("BES"); m_POSDict.add("PDT"); m_POSDict.add("$POS"); 
		m_POSDict.add("WP"); m_POSDict.add("WDT"); m_POSDict.add("EX"); m_POSDict.add("PRP"); 

		m_featureActive = new HashSet<String>();

		m_labelDict = new HashSet<String>();
		m_labelDict.add("statement"); m_labelDict.add("none"); m_labelDict.add("backchannel"); 
		m_labelDict.add("incomplete"); m_labelDict.add("question");

		m_empty = new Word();
		m_empty.m_POS = "$POS";
		m_empty.m_word = "$";
		
		m_POSDict.add(m_empty.m_POS);
//		m_wordDict.add(m_empty.m_word);
		
		m_npregram = npregram; m_npostgram = npostgram; 
		
		// word...
		m_featureNames.add("word"); 
		m_featureTypes.add("string"); 
		// pos...
		m_featureNames.add("pos"); 
		m_featureTypes.add("nominal"); 
		m_featureActive.add("pos");
    	for(int i=0; i<m_npregram; i++) {
    		// pre...
    		String featname = getPreWordString(i);
    		m_featureNames.add(featname); 
    		m_featureTypes.add("string"); 
    		// prepos...
    		featname = getPrePosString(i);
    		m_featureNames.add(featname); 
    		m_featureTypes.add("nominal"); 
    		m_featureActive.add(featname);
    	}
    	for(int i=0; i<m_npostgram; i++) {
    		// post...
    		String featname = getPostWordString(i);
    		m_featureNames.add(featname); 
    		m_featureTypes.add("string"); 
    		// postpos...
    		featname = getPostPosString(i);
    		m_featureNames.add(featname); 
    		m_featureTypes.add("nominal"); 
    		m_featureActive.add(featname);
    	}
    	if(nprev) {
    		m_featureNames.add("nprev_unclassified"); 
    		m_featureTypes.add("numeric"); 
    		m_featureActive.add("nprev_unclassified");
    	}
    	if(prevstlabel) {
    		m_featureNames.add("prevstlabel"); 
    		m_featureTypes.add("nominal"); 
    		m_featureActive.add("prevstlabel");
    	}
    	// add prosodic features (as numerics)
//		ProsodicFeaturesExtractor prosodic = new ProsodicFeaturesExtractor();
//		Vector<String> pfnames = prosodic.getFeatureNames();
//	    for(String name : pfnames) {
//	    	m_featureNames.add(name);
//			m_featureTypes.add("numeric");
//    		m_featureActive.add(name);
//	    }

	    // add the final label to the end...
		m_featureNames.add("label"); 
		m_featureTypes.add("nominal");
		m_featureActive.add("label");
	}
	boolean isWordPosFeatureActive() {
		if(m_featureActive.contains("pos")) return true;
		return false;
	}
	boolean isPrePosFeatureActive(int index) {
		if(m_featureActive.contains(getPrePosString(index))) return true;
		return false;
	}
	boolean isPostPosFeatureActive(int index) {
	  if(m_featureActive.contains(getPostPosString(index))) return true;
	  return false;
	}
	boolean isnPrevUnclassifiedActive() {
		if(m_featureActive.contains("nprev_unclassified")) return true;
		return false;
	}
	boolean isPrevStatementUnitLabelActive() {
		if(m_featureActive.contains("prevstlabel")) return true;
		return false;
	}
	
	public void writeFeatureHeaders(WekaInput wi) {
		for(int i=0; i<m_featureNames.size(); ++i) {
			if(!m_featureActive.contains(m_featureNames.get(i))) {
				continue;
			}
			if(m_featureTypes.get(i)=="nominal") {
				if(m_featureNames.get(i).startsWith("pos")) {
					wi.writeFeatureHeaderForNominal(m_featureNames.get(i),m_POSDict);
				} else if(m_featureNames.get(i).endsWith("label")) {
					wi.writeFeatureHeaderForNominal(m_featureNames.get(i),m_labelDict);
				}
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
	public HashMap<String, SpeakerPair> getTrainingFiles(String dataDir, String m_corpusType) {
		// data/speech/dev1_wav
		// data/speech/dev2_wav
		// data/speech/eval_wav
		// data/text/dev1
		// data/text/dev2
		// data/text/eval
	    // for now dev1, add others if you want later.
		TreeMap<String, String> dirs = new TreeMap<String, String>();
		dirs.put(dataDir+"/text/dev1", dataDir+"/speech/dev1_wav");
		dirs.put(dataDir+"/text/dev2", dataDir+"/speech/dev2_wav");
//		dirs.put(dataDir+"/text/eval", dataDir+"/speech/eval_wav");
		HashMap<String, SpeakerPair> xmlwavfiles = new HashMap<String, SpeakerPair>();
		for(Map.Entry<String, String> entry: dirs.entrySet()) {
			String xmldir = entry.getKey();
			String speechdir = entry.getValue();
			File[] speechfiles = getFilesWithExtension(speechdir,"wav");
			
			for(File speechfile:speechfiles) {
				if(m_corpusType.equalsIgnoreCase(speechfile.getName().split("\\.")[0].split("_")[0])){
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
				else if(m_corpusType.equalsIgnoreCase("all"))
				{
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
			}	    
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
			if( e2.m_startOffsetTime > (e1.m_startOffsetTime))
				return -1;
			else if( e2.m_startOffsetTime < (e1.m_startOffsetTime))
				return 1;
			return 0;
		}
	};	
	
	public void extractWords(String xmlfile,
			boolean verbose, TreeSet<Word> wordset) {
    	SpeakerDoc sd;
  	
    	ParseDocument pd = new ParseDocument();
    	sd = pd.process(new File(xmlfile));
    	Vector<AG> sentences = sd.getAG();
    	
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
    					(sub.compareTo("+")==0) ||
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
    					(ann.getTag().compareTo("DISFL-IP")==0) ||
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
    			word.m_speaker = sd.getSpeakerName();
    			
    	    	//   for each word
    			Annotation ann = wordanns.get(i);
    			//  get word
    			word.m_word = words.get(i);
//    			m_wordDict.add(words.get(i));
    	    	//  get POS tag for word
    			word.m_POS = ann.getTag();
    			assert(m_POSDict.contains(ann.getTag()));

    			//    get times that word spans
    			Double starttime = sentence.getOffsetTimeForAnchor(ann.getStartAnchor());
    			Double endtime = sentence.getOffsetTimeForAnchor(ann.getStopAnchor());
    			
    			String sentenceType = new String("none");
	   			word.m_endOfSentence = false;
    			if(i==wordanns.size()-1) { // if last word
    				sentenceType = sentence.getMetadata().getSentenceType();
    				// ... question, backchannel, statement
    				word.m_endOfSentence = true;
    			}
    			word.m_sentenceType = sentence.getMetadata().getSentenceType();
    			assert(m_labelDict.contains(sentenceType));

    			word.m_startOffsetTime = starttime;
    			word.m_endOffsetTime = endtime;
    			word.m_wordsBeforeUnclassified = i;
    			wordset.add(word);
    		}
    	} // for each sentence
	}
}
