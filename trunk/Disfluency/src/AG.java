import java.util.HashMap;
import java.util.Vector;

public class AG {
	Metadata m_meta;
    HashMap<String, Double> m_anchors;
	Vector<Annotation> m_anns;
	String m_id;
	String m_timeline;
	
	public AG() {}
	public AG(String id, String timeline) { 
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
	
	public static void main(String[] args) {
		
	}

}
