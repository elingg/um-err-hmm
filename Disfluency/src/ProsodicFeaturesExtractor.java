import java.util.Vector;import java.io.*;// TODO// For now, this class has knowledge of which prosodic features to extract// Caller gives files, and asks to extract prosodic features at certain timespublic class ProsodicFeaturesExtractor {	Vector<String> m_featureNames;		public ProsodicFeaturesExtractor() { 		m_featureNames = new Vector<String>();		m_featureNames.add("pitchdifference"); // etc	}	public Vector<String> getFeatureNames() {		return m_featureNames;	}	// since we don't have Pair in java, just use two vectors of start and corresponding	// stop times...	public Vector<Vector<Double>> 	extractFeatures(String wavfile, Vector<Double> startoffsets, Vector<Double> endoffsets) {		Integer numintervals = startoffsets.size();		assert(numintervals==endoffsets.size());		Vector<Vector<Double>> prosodicFeatures = new Vector<Vector<Double>>();		try 		{				String str = "intervals.txt";			File file= new File(str);			BufferedWriter output = new BufferedWriter(new FileWriter(file));					for(int i=0; i<startoffsets.size(); i++)		{			double start = startoffsets.get(i).doubleValue()/1000;			output.write((new Double(start)).toString());			output.newLine();			double end = endoffsets.get(i).doubleValue()/1000;			output.write((new Double(end)).toString());			output.newLine();			prosodicFeatures.add(new Vector<Double>());				}		    output.close();	    }		catch (IOException e) {			e.printStackTrace();		}				//String[] cmd = {"/Applications/Praat.app/Contents/MacOS/Praat", " pitchscript.praat", wavfile}; 		String cmd1 = "rm -f features.txt";	    String debugCurrentDir = System.getProperty("user.dir");		String cmd = "/Applications/Praat.app/Contents/MacOS/Praat src/pitchscript.praat " + wavfile;		try		{			Runtime.getRuntime().exec(cmd1);			Process p =Runtime.getRuntime().exec(cmd);			try			{				int exitCode = p.waitFor();				}			catch(Exception e){e.printStackTrace();}		}				catch(IOException e)		{			e.printStackTrace();		}		// interact with Praat script and fill in prosodic features. Write and delete intermediate		// files if needed..		// TODO		//		//for(int i=0; i<startoffsets.size(); i++)		{						try{				// Open the file that is the first 				// command line parameter				FileInputStream fstream = new FileInputStream("features.txt");				// Get the object of DataInputStream				DataInputStream in = new DataInputStream(fstream);				BufferedReader br = new BufferedReader(new InputStreamReader(in));				String strLine;				int lineno=0;				//Read File Line By Line				while ((strLine = br.readLine()) != null)   {					if(strLine.equals("--undefined--"))					   {					      strLine="0";					   }					((Vector<Double>)prosodicFeatures.get(lineno)).add(new Double(strLine));					// Print the content on the console					System.out.println (strLine);					lineno++;				}				//Close the input stream				in.close();			}catch (Exception e){//Catch exception if any				System.err.println("Error: " + e.getMessage());			}				}		// sanity checks...		assert(prosodicFeatures.size()==numintervals); // as many prosodic features as intervals.		for(int interval=0; interval<numintervals; interval++) {			assert(prosodicFeatures.get(interval).size()==m_featureNames.size());		}		return prosodicFeatures;	}}