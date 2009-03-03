import java.util.HashMap;


public class Annotation {
	String m_id;
	String m_type;
	String m_startAnchor;
	String m_stopAnchor;
	HashMap<String, String> m_features;
	public Annotation() {}
	public String getType() {
		return m_type;
	}
	public String getStartAnchor() {
		return m_startAnchor;
	}
	public String getStopAnchor() {
		return m_stopAnchor;
	}
	public String getMdeTokenId() {
		return m_features.get("mdeTokenId");
	}
	public String getTag() {
		return m_features.get("tag");
	}
	public String getIndex() {
		return m_features.get("index");
	}
	public void setID(String id) { 
		m_id = id;
	}
	public void setType(String type) { 
		m_type = type;
	}
	public void setStartAnchor(String anchor) { 
		m_startAnchor = anchor;
	}
	public void setStopAnchor(String anchor) { 
		m_stopAnchor = anchor;
	}
	public void setMdeTokenId(String mde_token) {
		m_features.put("mdeTokenId",mde_token);
	}
	public void setTag(String tag) {
		m_features.put("tag",tag);
	}
	public void setIndex(String index) {
		m_features.put("index",index);
	}
	public static void main(String[] args) {
	
	}

}
