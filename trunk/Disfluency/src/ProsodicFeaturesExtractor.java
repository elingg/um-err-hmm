import java.util.Vector;

// TODO
// For now, this class has knowledge of which prosodic features to extract
// Caller gives files, and asks to extract prosodic features at certain times
public class ProsodicFeaturesExtractor {
	Vector<String> m_featureNames;
	
	public ProsodicFeaturesExtractor() { 
		// m_featureNames.add("F0"); // etc
	}
	public Vector<String> getFeatureNames() {
		return m_featureNames;
	}
	// since we don't have Pair in java, just use two vectors of start and corresponding
	// stop times...
	public Vector<Vector<Double>> 
	extractFeatures(String wavfile, Vector<Double> startoffsets, Vector<Double> endoffsets) {
		Integer numintervals = startoffsets.size();
		assert(numintervals==endoffsets.size());
		Vector<Vector<Double>> prosodicFeatures = new Vector<Vector<Double>>();
		// interact with Praat script and fill in prosodic features. Write and delete intermediate
		// files if needed..
		// TODO
		//
		
		// sanity checks...
		assert(prosodicFeatures.size()==numintervals); // as many prosodic features as intervals.
		for(int interval=0; interval<numintervals; interval++) {
			assert(prosodicFeatures.get(interval).size()==m_featureNames.size());
		}
		return prosodicFeatures;
	}
}
