package hbondsgen.container.bond;

import hbondsgen.container.atom.IAtom;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Bond implements IBond {

	/*
	 * Properties
	 */
	//IMN0 atom
	IAtom imn0 = null;
	//Bonded atoms
	List<IAtom> bondedAtoms = null;
	
	/*
	 * In-class constants
	 */
	//
	
	/*
	 * Interface
	 */
	//Get IMN0
	@Override
	public IAtom getIMN0() {
		return imn0;
	}

	//Get bonded to IMN0 atoms
	@Override
	public Iterator<IAtom> getBondedAtoms() {
		return bondedAtoms.iterator();
	}

	/*
	 * Constructor
	 */
	//Initialization constructor
	public Bond(IAtom atomIMN0, List<IAtom> bondedToIMN0) throws BondInvalidParametersProvidedException {
		//Check for validity IMN0
		boolean isIMN0Invalid = null == atomIMN0;
		//Check for validity bonded atoms
		boolean isBondedToIMN0Invalid = null == bondedToIMN0 || bondedToIMN0.isEmpty();
		if(!isBondedToIMN0Invalid) {
			for(IAtom bondedAtom : bondedToIMN0) {
				if(null == bondedAtom) {
					isBondedToIMN0Invalid = true;
					break;
				}
			}
		}
		//Throw exception if needed
		if(isIMN0Invalid || isBondedToIMN0Invalid)
			throw new BondInvalidParametersProvidedException(isIMN0Invalid, isBondedToIMN0Invalid);
		//Create object normally
		imn0 = atomIMN0;
		bondedAtoms = bondedToIMN0;
	}
	
	//Copy constructor
	public Bond(Bond right) {
		imn0 = right.imn0;
		bondedAtoms = new LinkedList<IAtom>(right.bondedAtoms);
	}
}
