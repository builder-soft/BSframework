package cl.buildersoft.framework.exception;

/**
 * @author Claudio
 */
public class BSConfigurationException extends BSException {

	private static final long serialVersionUID = -6054613850007173438L;
	public BSConfigurationException(Exception e) {
		super(e);
	}

	public BSConfigurationException(String code) {
		super(code);
	}

	public BSConfigurationException(String code, String message) {
		super(code, message);
	}
}
