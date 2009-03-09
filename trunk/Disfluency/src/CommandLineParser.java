

public class CommandLineParser {
	// from http://journals.ecs.soton.ac.uk/java/tutorial/java/cmdLineArgs/parsing.html
	public boolean m_verbose;
	public String m_srcDir, m_wekaInputFile;
	public Integer m_npregram, m_npostgram;
	public void parseArguments(String[] args) {
		int i = 0;
		String arg;
		m_verbose = false;
		m_srcDir = "";
		m_wekaInputFile = "";
		m_npregram = m_npostgram = 1;
		while (i < args.length && args[i].startsWith("-")) {
			arg = args[i++];
			// use this type of check for "wordy" arguments
			if (arg.equals("-verbose")) {
				System.out.println("verbose mode on");
				m_verbose = true;
			}

			// use this type of check for arguments that require arguments
			else if (arg.equals("-dir")) {
				if (i < args.length)
					m_srcDir = args[i++];
				else
					System.err.println("-dir requires a dirname");
				if (m_verbose)
					System.out.println("dir  = " + m_srcDir);
			}
			else if (arg.equals("-wekafile")) {
				if (i < args.length)
					m_wekaInputFile = args[i++];
				else
					System.err.println("-wekafile requires a file name");
				if (m_verbose)
					System.out.println("wekafile  = " + m_wekaInputFile);
			}
			else if (arg.equals("-npregram")) {
				if (i < args.length)
					m_npregram = Integer.parseInt(args[i++]);		
				else
					System.err.println("-npregram requires a number for npregram");
				if (m_verbose)
					System.out.println("npregram  = " + m_npregram);
			}
			else if (arg.equals("-npostgram")) {
				if (i < args.length)
					m_npostgram = Integer.parseInt(args[i++]);		
				else
					System.err.println("-npostgram requires a number for npostgram");
				if (m_verbose)
					System.out.println("npostgram  = " + m_npostgram);
			}
			// use this type of check for a series of flag arguments
//			else {
//				for (j = 1; j < arg.length(); j++) {
//					flag = arg.charAt(j);
//					switch (flag) {
//					case 'x':
//						if (vflag) System.out.println("Option x");
//						break;
//					case 'n':
//						if (vflag) System.out.println("Option n");
//						break;
//					default:
//						System.err.println("ParseCmdLine: illegal option " + flag);
//					break;
//					}
//				}
//			}
		}
		if (i != args.length)
			System.err.println("Usage: Disfluency.java [-verbose] [-npostgram n] [-npregram n] [-dir dirname] [-wekafile fname]");
	}
}
