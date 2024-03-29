import java.util.HashMap;
import java.util.Vector;

public class AG { // or Sentence
	Metadata m_meta;
    HashMap<String, Double> m_anchors;
	Vector<Annotation> m_anns;
	String m_id;
	String m_timeline;
	
	public AG() { 
		m_anchors = new HashMap<String, Double>(); 
		m_anns = new Vector<Annotation>();
	}
	public AG(String id, String timeline) { 
		m_anchors = new HashMap<String, Double>();
		m_anns = new Vector<Annotation>();
		m_id = id; 
		m_timeline = timeline;
	}
	
	public void setTimeline(String timeline) {
		m_timeline = timeline;
	}
	public String getTimeline() {
		return m_timeline;
	}
	public void setId(String id) {
		m_id = id;
	}
	public String getId() {
		return m_id;
	}
	public Metadata getMetadata() {
		return m_meta;
	}
	public Double getAnchorOffset(String anchorname) {
		return m_anchors.get(anchorname);
	}
	
	public Vector<Annotation> getAnnotations() {
		return m_anns;
	}

	public void addAnnotation(Annotation ann) {
		m_anns.add(ann);
	}
	
	public void addAnchor(String name, Double offset) {
		m_anchors.put(name, offset);
	}
	
	public void setMetadata(Metadata meta) {
		m_meta = meta;
	}
	
	public Double getOffsetTimeForAnchor(String anchor) {
		Double anchorOffset = this.getAnchorOffset(anchor);
		
		Double startOffset = m_meta.getStartOffset().doubleValue();
		Double endOffset = m_meta.getEndOffset().doubleValue();
		Double startOffsetTime = m_meta.getStartOffsetTime();
		Double endOffsetTime = m_meta.getStopOffsetTime();
		
		Double anchorOffsetTime = startOffsetTime +
		(endOffsetTime-startOffsetTime)*(anchorOffset-startOffset)/(endOffset-startOffset);

		return anchorOffsetTime;
	}
	
	public static void main(String[] args) {
		
	}

}
