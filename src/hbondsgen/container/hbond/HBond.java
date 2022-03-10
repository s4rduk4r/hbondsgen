package hbondsgen.container.hbond;

import hbondsgen.container.atom.IAtom;

public class HBond {
	/*
	 * Properties
	 */
	public final IAtom acceptor;
	public final IAtom donor;
	
	/*
	 * Interface
	 */
	//Equal
	public boolean isEqual(HBond right, boolean isMonomersEqual) {
		boolean isEqual = acceptor.isEqual(right.acceptor, isMonomersEqual) && donor.isEqual(right.donor, isMonomersEqual);
		return isEqual;
	}
	
	/*
	 * Constructor
	 */
	public HBond(IAtom acceptor, IAtom donor) {
		this.acceptor = acceptor;
		this.donor = donor;
	}
}
