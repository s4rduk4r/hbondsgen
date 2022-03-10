package hbondsgen.generator;

import java.util.LinkedList;
import java.util.List;

import hbondsgen.container.molecule.IMolecule;

public class HBonds {
	/*
	 * Properties
	 */
	//Title
	public String title = null;
	//Monomers
	public IMolecule[] monomer = new IMolecule[2];
	//H-bonds
	public List<DimerHBonds> hbonds = null;
	
	/*
	 * Constructor
	 */
	public HBonds(String name, IMolecule mol1, IMolecule mol2, List<DimerHBonds> hbondList) {
		title = name;
		monomer[0] = mol1;
		monomer[1] = mol2;
		hbonds = new LinkedList<>(hbondList);
	}
}
