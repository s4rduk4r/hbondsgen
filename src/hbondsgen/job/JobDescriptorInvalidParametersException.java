package hbondsgen.job;

public class JobDescriptorInvalidParametersException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1813298483392430617L;

	/*
	 * Constructor
	 */
	public JobDescriptorInvalidParametersException(boolean isTitle, boolean isMol1, boolean isMol2) {
		super("Invalid JobDescriptor parameters " 
			  + (isTitle ? "title; " : "") 
			  + (isMol1 ? "mol1; " : "") 
			  + (isMol2 ? "mol2; " : ""));
	}
}
