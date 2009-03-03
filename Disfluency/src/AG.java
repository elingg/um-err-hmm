import java.util.HashMap;
import java.util.Vector;

public class AG {
	Metadata m_meta;
    HashMap<String, Double> m_anchors;
	Vector<Annotation> m_anns;
	
	public AG() {}
	
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
