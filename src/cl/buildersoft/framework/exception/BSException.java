package cl.buildersoft.framework.exception;

/**
 * @author Claudio
 */
public abstract class BSException extends RuntimeException {
	private static final long serialVersionUID = 8480447889221050761L;
	private String code = "";

	public BSException(String code, String message) {
		super(message);
		this.code = code;

	}

	public BSException(String code) {
		this(code, "Código de error: " + code);
	}

	public String getCode() {
		return this.code;
	}

	public BSException(Exception e) {
		throw new RuntimeException(e.getMessage());
	}

}
