package cl.buildersoft.framework.exception;

public abstract class BSException extends RuntimeException {
	private static final long serialVersionUID = 8480447889221050761L;
	private String code = "";

	public BSException(String message) {
		super(message);
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return this.code;
	}

	public BSException(Exception e) {
		throw new RuntimeException(e.getMessage());
	}

}
