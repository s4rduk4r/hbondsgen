package hbondsgen.container.molecule;

import hbondsgen.container.atom.IAtom;
import hbondsgen.container.bond.IBond;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public interface IMolecule {
	//Get molecule name
	public String getName();
	//Get atom names
	public Set<String> getAtomNames();
	//Get atoms count
	public Integer getAtomCount();
	//Get atom by name
	public IAtom getAtom(String atomicName);
	//Add atom
	public void addAtom(IAtom atom, boolean isPlaneAtom);
	//Lock atom geometry
	public void lastAtomAdded();
	//Get atoms
	public Iterator<IAtom> getAtoms();
	//Get plane atoms
	public Vector<IAtom> getPlaneAtoms();
	//Get acceptors
	public Iterator<IAtom> getAcceptors();
	//Get donors
	public Iterator<IAtom> getDonors();
	//Get graph
	public IMolecule getGraph();
	//Get covalent bonds
	public List<IBond> getBonds();
	//Set covalent bonds
	public void setBonds(List<IBond> molBonds);
}
