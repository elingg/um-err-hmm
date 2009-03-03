
public class Metadata {

	Integer m_endOffset;
	Integer m_startOffset;
	Integer m_paragraph;
	String  m_sentence;
	String  m_id;
	String  m_treeBanking;
	String  m_tbComment;
	
	Double m_startOffsetTime;
	Double m_endOffsetTime;
	String m_sentenceType;
	
	public Metadata() {
	}
	
	public Integer getEndOffset() {
		return m_endOffset;
	}
	public Integer getStartOffset() {
		return m_startOffset;
	}
	public Integer getParagraph() {
		return m_paragraph;
	}
	public String getSentence() {
		return m_sentence;
	}
	public String getId() {
		return m_id;
	}
	public String getTreeBanking() {
		return m_treeBanking;
	}
	public String getTBComment() {
		return m_tbComment;
	}
	public void setEndOffset(Integer offset) {
		m_endOffset = offset;
	}
	public void setStartOffset(Integer offset) {
		m_startOffset = offset;
	}
	public void setParagraph(Integer paragraph) {
		m_paragraph = paragraph;
	}
	public void setSentence(String sentence) {
		m_sentence = sentence;
	}
	public void setId(String idstring) {
		m_id = idstring;
		String[] subs = m_id.split("_");
		assert(subs.length == 6);
		m_startOffsetTime = Double.valueOf(subs[3]);
		m_endOffsetTime = Double.valueOf(subs[4]);
		m_sentenceType = subs[5]; // question, statement, backchannel
	}
	public void setTreeBanking(String tbanking) {
		m_treeBanking = tbanking;
	}
	public void setTBComment(String comment) {
		m_tbComment = comment;
	}
	
	public Double getStartOffsetTime() {
		return m_startOffsetTime;
	}
	
	public Double getStopOffsetTime() {
		return m_endOffsetTime;
	}
	public String getSentenceType() {
		return m_sentenceType;
	}
	public static void main(String[] args) {

	}

}
