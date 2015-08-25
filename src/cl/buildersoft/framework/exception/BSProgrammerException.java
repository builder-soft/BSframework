package cl.buildersoft.framework.exception;

/**
 * @author Claudio
 */
public class BSProgrammerException extends BSException {

	private static final long serialVersionUID = -8449071547035557870L;

	public BSProgrammerException(Exception e) {
		super(e);
	}

	public BSProgrammerException(String message) {
		super(message);
	}

}
