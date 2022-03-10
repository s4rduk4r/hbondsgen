package hbondsgen.generator;

import java.util.List;

import hbondsgen.container.atom.IAtom;

public class ActiveAtoms {
	/*
	 * Properties
	 */
	public IAtom center = null;
	public List<IAtom> activeAtoms = null;
	
	/*
	 * Constructor
	 */
	public ActiveAtoms(IAtom centerAtom) {
		center = centerAtom;
	}
}
