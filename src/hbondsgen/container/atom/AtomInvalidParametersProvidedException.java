package hbondsgen.container.atom;

public class AtomInvalidParametersProvidedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1009320513050443551L;

	/*
	 * Constructor
	 */
	public AtomInvalidParametersProvidedException(boolean imn, boolean pos, boolean dam) {
		super("Invalid atom parameters provided " + 
			  (imn ? "IMN; " : "") + 
			  (pos ? "X Y Z; " : "") + 
			  (dam ? "DAM; " : ""));
	}
}
