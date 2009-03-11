import java.util.TreeSet;
import java.util.Vector;



public class CommandLineParser {
	// from http://journals.ecs.soton.ac.uk/java/tutorial/java/cmdLineArgs/parsing.html
	public boolean m_verbose, m_writeintervals;
	public String m_srcDir, m_wekaInputFile, m_corpusType;
	public Integer m_npregram, m_npostgram;
	public boolean m_nprev, m_prevstlabel, m_prosodic;
	public Vector<String> m_profeatureNames;
	public TreeSet<String> m_profeatures;
	public CommandLineParser() {
		m_profeatureNames = (Vector<String>) new ProsodicFeaturesExtractor().m_featureNames.clone();
		m_profeatures = new TreeSet<String>();
	}
	public void parseArguments(String[] args) {
		int i = 0;
		String arg;
		m_corpusType="all";
		m_verbose = false;
		m_srcDir = "";
		m_wekaInputFile = "";
		m_npregram = m_npostgram = 1;
		m_nprev = false;
		System.out.println("All features: " + m_profeatureNames.toString());
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			// use this type of check for "wordy" arguments
			if (arg.equals("-verbose")) {
				m_verbose = true;
			}
			else if (arg.equals("-nprev")) {
				m_nprev = true;
			}
			else if(m_profeatureNames.contains(arg.substring(1))) {
				System.out.println("\t. Using prosodic feature: "+arg.substring(1));
				m_profeatures.add(arg.substring(1));
			}
			else if (arg.equals("-prevstlabel")) {
				m_prevstlabel = true;
			}
			else if (arg.equals("-prosodic")) {
				m_prosodic = true;
			}
			else if (arg.equals("-writeintervals")) {
				System.out.println("\t. Writing intervals...");
				m_writeintervals = true;
			}
			// use this type of check for arguments that require arguments
			else if (arg.equals("-dir")) {
				if (i < args.length)
					m_srcDir = args[i++];
				else
					System.err.println("-dir requires a dirname");
			}
			else if (arg.equals("-wekafile")) {
				if (i < args.length)
					m_wekaInputFile = args[i++];
				else
					System.err.println("-wekafile requires a file name");
			}
			else if (arg.equals("-npregram")) {
				if (i < args.length)
					m_npregram = Integer.parseInt(args[i++]);		
				else
					System.err.println("-npregram requires a number for npregram");
			}
			else if (arg.equals("-npostgram")) {
				if (i < args.length)
					m_npostgram = Integer.parseInt(args[i++]);		
				else
					System.err.println("-npostgram requires a number for npostgram");
			}
			else if (arg.equals("-corpus")){
				if (i < args.length)
					m_corpusType = args[i++];
				else
					System.err.println("\t. -corpus requires 'fsh' or 'sw' or 'all'");					
			}
		}
		if(m_prosodic) {
			if(m_profeatures.size()==0) {
				System.out.println("\t. Using all prosodic features");
				for(String f:m_profeatureNames) {
					m_profeatures.add(f);
				}
			}
		}
		if(m_verbose) {
			System.out.println("\t. Verbose mode on");
		}
		System.out.println("\t. Using dir  = " + m_srcDir);
		System.out.println("\t. Using corpus: " + m_corpusType);
		System.out.println("\t. Using wekafile output file: " + m_wekaInputFile);
		System.out.println("\t. Using npregram  = " + m_npregram);
		System.out.println("\t. Using npostgram  = " + m_npostgram);
		if(m_nprev) {
			System.out.println("\t. Using number of previous unclassified words as feature");
		}
		if(m_prevstlabel) {
			System.out.println("\t. Using previous statement label as a feature");
		}
		if (i != args.length)
			System.err.println("Usage: Disfluency.java [-verbose] [-npostgram n]"+
					" [-npregram n] [-dir dirname] [-wekafile fname]"+ 
					" [-writeintervals] [-corpus all|fsh|sw] [-nprev] [-prosodic]");
	}
}
