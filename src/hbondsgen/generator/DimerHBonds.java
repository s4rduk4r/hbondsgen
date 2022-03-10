package hbondsgen.generator;

import java.util.Vector;

import hbondsgen.container.hbond.HBond;

public class DimerHBonds extends Vector<HBond> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7437727779398708675L;
	
	/*
	 * Interface
	 */
	//Equal
	public boolean isEqual(DimerHBonds right, boolean isMonomersEqual) {
		boolean isEqual = super.get(0).isEqual(right.get(0), isMonomersEqual) && super.get(1).isEqual(right.get(1), isMonomersEqual)
				|| (super.get(0).isEqual(right.get(1), isMonomersEqual) && super.get(1).isEqual(right.get(0), isMonomersEqual));
		return isEqual;
	}

	/*
	 * Constructor
	 */
	public DimerHBonds() {
		super(2);
	}
}
