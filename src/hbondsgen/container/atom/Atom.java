package hbondsgen.container.atom;

import hbondsgen.container.molecule.IMolecule;
import hbondsgen.math.Vector3d;
import hbondsgen.parser.InputParser;
import hbondsgen.periodicTableOfElements.IUPACPeriodicTableOfElements;

public class Atom implements IAtom, Comparable<Atom> {
	/*
	 * Properties
	 */
	//Intramolecular name IMN
	protected String imn = null;
	//Radius-vector to atomic center
	protected Vector3d pos = null;
	//Atomic DAM
	protected EDonorAcceptorMarker dam = null;
	//van der Waals radius
	protected Double radius = null;
	//Parent molecule
	protected IMolecule molecule = null;
	
	/*
	 * Interface
	 */
	//Comparable
	@Override
	public int compareTo(Atom right) {
		int result = imn.compareTo(right.imn);
		if(0 == result) {
			result = (int) (pos.length() - right.pos.length());
			result = result > 0 ? 1 : (result < 0) ? -1 : 0;
		}
		if(0 == result) {
			result = dam == right.dam ? 0: (dam == EDonorAcceptorMarker.acceptor ? 1: -1);
		}
		return result;
	}
	
	//IAtom
	//Get intramolecular atom name IMN
	@Override
	public String getIMN() {
		return imn;
	}
	//Get parent molecule
	@Override
	public IMolecule getMolecule() {
		return molecule;
	}
	//Set parent molecule
	public void setMolecule(IMolecule mol) {
		molecule = mol;
	}
	//Get atom coordinates
	@Override
	public Vector3d getPosition() {
		return pos;
	}
	//Set atom coordinates
	public void setPosition(Vector3d r) {
		pos = r;
	}
	//Get DAM
	@Override
	public EDonorAcceptorMarker getDAM() {
		return dam;
	}
	//Get van der Waals radius
	@Override
	public Double getVanDerWaalsRadius() {
		return radius;
	}
	//Equal
	@Override
	public boolean isEqual(IAtom right, boolean isMonomersEqual) {
		boolean isEqual = (isMonomersEqual ? true : molecule == right.getMolecule()) && imn.equals(right.getIMN());
		return isEqual;
	}

	/*
	 * Auxiliary methods
	 */
	//Check atomic name for validity
	protected void fillInVanDerWaalsRadius(String imn) throws AtomInvalidParametersProvidedException {
		if(!imn.matches(InputParser.REGEXP_ATOM))
			throw new AtomInvalidParametersProvidedException(true, false, false);
		String symbol = imn.split("\\d+(-\\d+)?")[0].toUpperCase();
		if(symbol.length() > 1) {
			symbol = symbol.substring(0, 1) + symbol.substring(1).toLowerCase();
		}
		radius = IUPACPeriodicTableOfElements.getInstance().getAtom(symbol).radius;
	}
	
	/*
	 * Constructor
	 */
	//Initialization constructor - name, {x,y,z}
	public Atom(String name, Vector3d r) throws AtomInvalidParametersProvidedException {
		if(null == name || null == r)
			throw new AtomInvalidParametersProvidedException(null == name,
															 null == r,
															 false);
		fillInVanDerWaalsRadius(name);
		imn = name;
		pos = r;
		dam = EDonorAcceptorMarker.none;
	}
	
	public Atom(String name, Double x, Double y, Double z) throws AtomInvalidParametersProvidedException {
		if(null == name || null == x || null == y || null == z)
			throw new AtomInvalidParametersProvidedException(null == name, 
															 null == x || null == y || null == z, 
															 false);
		fillInVanDerWaalsRadius(name);
		imn = name;
		pos = new Vector3d(x, y, z);
		dam = EDonorAcceptorMarker.none;
	}
	
	//Initialization constructor - name, {x, y, z}, DAM
	public Atom(String name, Vector3d r, EDonorAcceptorMarker damMarker) throws AtomInvalidParametersProvidedException {
		if(null == name || null == r || null == damMarker)
			throw new AtomInvalidParametersProvidedException(null == name, 
															 null == r,
															 null == damMarker);
		fillInVanDerWaalsRadius(name);
		imn = name;
		pos = r;
		dam = damMarker;
	}
	
	public Atom(String name, Double x, Double y, Double z, EDonorAcceptorMarker damMarker) throws AtomInvalidParametersProvidedException {
		if(null == name || null == x || null == y || null == z || null == damMarker)
			throw new AtomInvalidParametersProvidedException(null == name,
															 null == x || null == y || null == z,
															 null == damMarker);
		fillInVanDerWaalsRadius(name);
		imn = name;
		pos = new Vector3d(x, y, z);
		dam = damMarker;
	}
	
	//Copy constructor
	public Atom(Atom right) {
		imn = right.imn;
		pos = new Vector3d(right.pos);
		dam = right.dam;
		radius = right.radius;
	}
}
