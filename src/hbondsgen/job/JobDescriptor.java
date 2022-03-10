package hbondsgen.job;

import hbondsgen.container.molecule.IMolecule;

public class JobDescriptor implements IJobDescriptor {
	/*
	 * Properties
	 */
	//Job title
	protected String title = null;
	protected IMolecule[] monomer = new IMolecule[2];
	
	/*
	 * Interface
	 */
	//Get job title
	@Override
	public String getTitle() {
		return title;
	}
	//Get monomer 1
	@Override
	public IMolecule getMonomer1() {
		return monomer[0];
	}
	//Get monomer 2
	@Override
	public IMolecule getMonomer2() {
		return monomer[1];
	}
	
	/*
	 * Auxiliary methods
	 */
	
	/*
	 * Constructor
	 */
	public JobDescriptor(String jobTitle, IMolecule mol1, IMolecule mol2) throws JobDescriptorInvalidParametersException {
		if(null == jobTitle || null == mol1 || null == mol2)
			throw new JobDescriptorInvalidParametersException(null == jobTitle, null == mol1, null == mol2);
		title = jobTitle;
		monomer[0] = mol1;
		monomer[1] = mol2;
	}
}
