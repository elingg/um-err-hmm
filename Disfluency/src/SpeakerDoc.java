import java.util.Vector;


public class SpeakerDoc {

	Integer m_id;
	String m_speaker; // either a or b really
	Vector<AG> m_AGSet;
	void setID(Integer id) { 
		m_id = id;
	}
	public SpeakerDoc() {}
	public SpeakerDoc(Integer id, String speaker) {
		m_id = id;
		m_speaker = speaker;
	}
	void setId(Integer id) {
		m_id = id;
	}
	Integer getId() {
		return m_id;
	}
	String getSpeakerName() {
		return m_speaker;
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
