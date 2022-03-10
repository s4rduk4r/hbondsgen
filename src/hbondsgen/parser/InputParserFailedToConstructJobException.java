package hbondsgen.parser;

public class InputParserFailedToConstructJobException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5339441251305902641L;

	public InputParserFailedToConstructJobException(String failReason) {
		super("InputParser failed to parse file. Reason:\n" + failReason);
	}
}
