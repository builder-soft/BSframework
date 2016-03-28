package cl.buildersoft.framework.beans;

import java.io.Serializable;

public class LogInfoBean implements Serializable {
	private static final long serialVersionUID = 4694245068573386927L;
	private String action = null;
	private Long userId = null;
	private String eventKey = null;
	private String message = null;

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String toString() {
		return "LogInfoBean [action=" + action + ", userId=" + userId + ", eventKey=" + eventKey + ", message=" + message + "]";
	}

}
