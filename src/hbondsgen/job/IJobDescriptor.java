package hbondsgen.job;

import hbondsgen.container.molecule.IMolecule;

public interface IJobDescriptor {
	//Get job title
	public String getTitle();
	//Get monomer 1
	public IMolecule getMonomer1();
	//Get monomer 2
	public IMolecule getMonomer2();
}
