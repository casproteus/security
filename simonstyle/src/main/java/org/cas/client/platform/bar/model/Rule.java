package org.cas.client.platform.bar.model;

public class Rule {

	private int id;
	private int dspIdx;
	private String ruleName;
	private String content;
	private int action;
	private int status;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getDspIdx() {
		return dspIdx;
	}
	public void setDspIdx(int dspIdx) {
		this.dspIdx = dspIdx;
	}
	public String getRuleName() {
		return ruleName;
	}
	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getAction() {
		return action;
	}
	public void setAction(int action) {
		this.action = action;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
    

}
