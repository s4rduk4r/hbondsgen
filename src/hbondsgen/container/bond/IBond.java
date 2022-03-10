package hbondsgen.container.bond;

import java.util.Iterator;

import hbondsgen.container.atom.IAtom;

public interface IBond {
	//Get IMN0
	public IAtom getIMN0();
	//Get bonded to IMN0 atoms
	public Iterator<IAtom> getBondedAtoms();
}
