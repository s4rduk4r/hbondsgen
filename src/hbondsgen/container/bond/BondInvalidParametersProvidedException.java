package hbondsgen.container.bond;

public class BondInvalidParametersProvidedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -407437598077853152L;

	public BondInvalidParametersProvidedException(boolean isIMN0, boolean isBondedToIMN0) {
		super("Invalid bond parameters " + (isIMN0 ? "IMN0; " : "") + (isBondedToIMN0 ? "bondedToIMN0" : ""));
	}
}
