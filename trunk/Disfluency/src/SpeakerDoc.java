import java.util.Vector;


public class SpeakerDoc {

	Integer m_Id;
	String m_Speaker; // either a or b really
	Vector<AG> m_AGSet;
	void setID(Integer id) { 
		m_Id = id;
	}
	public SpeakerDoc() {}
	public SpeakerDoc(Integer id, String speaker) {
		m_Id = id;
		m_Speaker = speaker;
	}
	void setId(Integer id) {
		m_Id = id;
	}
	Integer getId() {
		return m_Id;
	}
	String getSpeakerName() {
		return m_Speaker;
	}
	void addAG(AG ag) {
		m_AGSet.add(ag);
	}
	Vector<AG> getAG() {
		return m_AGSet;
	}
	public static void main(String[] args) {
		
	}

}
