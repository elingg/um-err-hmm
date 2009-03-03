import java.io.File;
import java.io.FilenameFilter;


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
		// parse it into SpeakerDocs
	    File[] files = dir.listFiles(filter);
	    for(File file : files) {
	    	SpeakerDoc sd;
	    	ParseDocument pd;
	    	sd = pd.process(file);
	    	sd.getAllOffsets();
	    }

	}
}
