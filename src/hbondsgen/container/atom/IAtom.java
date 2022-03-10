package hbondsgen.container.atom;

import hbondsgen.container.molecule.IMolecule;
import hbondsgen.math.Vector3d;

public interface IAtom {
	//Get intramolecular atom name IMN
	public String getIMN();
	//Get parent molecule
	public IMolecule getMolecule();
	//Set parent molecule
	public void setMolecule(IMolecule mol);
	//Get atom coordinates
	public Vector3d getPosition();
	//Set atom coordinates
	public void setPosition(Vector3d r);
	//Get DAM
	public EDonorAcceptorMarker getDAM();
	//Get van der Waals radius
	public Double getVanDerWaalsRadius();
	//Equal
	public boolean isEqual(IAtom right, boolean isMonomersEqual);
}
