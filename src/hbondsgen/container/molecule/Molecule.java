package hbondsgen.container.molecule;

import hbondsgen.container.atom.Atom;
import hbondsgen.container.atom.EDonorAcceptorMarker;
import hbondsgen.container.atom.IAtom;
import hbondsgen.container.bond.Bond;
import hbondsgen.container.bond.BondInvalidParametersProvidedException;
import hbondsgen.container.bond.IBond;
import hbondsgen.math.Matrix3d;
import hbondsgen.math.Vector3d;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class Molecule implements IMolecule, Comparable<Molecule> {
	/*
	 * Properties
	 */
	//Molecule name
	protected String name = null;
	//Canonical form (XY plane oriented)
	protected Map<String, IAtom> geometry = new TreeMap<String, IAtom>();
	protected Vector<IAtom> plane = new Vector<IAtom>(3);
	//Covalent bonds
	protected List<IBond> bonds = new LinkedList<IBond>();
	//Last atom flag
	protected boolean isLastAtomAdded = false;
	
	/*
	 * In-class constants
	 */
	
	/*
	 * Interface
	 */
	//Comparable<Molecule>
	@Override
	public int compareTo(Molecule right) {
		int result = name.compareTo(right.name);
		if(0 == result) {
			if(isLastAtomAdded != right.isLastAtomAdded) {
				result = geometry.size() - right.geometry.size();
				result = result > 0 ? 1 : (result < 0 ? -1 : 0);
			}
		}
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		Molecule right = (Molecule) obj;
		return name.equals(right.name) && 0 == compareTo(right);
	}
	
	//IMolecule
	//Get molecule name
	@Override
	public String getName() {
		return name;
	}

	//Get atom names
	@Override
	public Set<String> getAtomNames() {
		return geometry.keySet();
	}

	//Get atoms count
	@Override
	public Integer getAtomCount() {
		return geometry.size();
	}
	
	//Get atom by name
	@Override
	public IAtom getAtom(String atomicName) {
		return geometry.get(atomicName);
	}

	//Add atom
	@Override
	public void addAtom(IAtom atom, boolean isPlaneAtom) {
		//If last atom already added - do nothing
		if(isLastAtomAdded)
			return;
		//Otherwise add atom to holder
		atom.setMolecule(this);
		geometry.put(atom.getIMN(), atom);
		//Add plane atom
		if(isPlaneAtom && plane.size() < 3) {
			plane.add(atom);
		}
	}

	//Lock atom geometry
	@Override
	public void lastAtomAdded() {
		isLastAtomAdded = true;
		setMoleculeOnXYplane();
	}

	//Get atoms
	@Override
	public Iterator<IAtom> getAtoms() {
		return geometry.values().iterator();
	}
	
	//Get plane atoms
	@Override
	public Vector<IAtom> getPlaneAtoms() {
		return new Vector<>(plane);
	}

	//Get acceptors
	@Override
	public Iterator<IAtom> getAcceptors() {
		List<IAtom> acceptors = new LinkedList<IAtom>();
		for(IAtom atom : geometry.values()) {
			if(EDonorAcceptorMarker.acceptor == atom.getDAM()) {
				acceptors.add(atom);
			}
		}
		return acceptors.iterator();
	}

	//Get donors
	@Override
	public Iterator<IAtom> getDonors() {
		List<IAtom> donors = new LinkedList<IAtom>();
		for(IAtom atom : geometry.values()) {
			if(EDonorAcceptorMarker.donor == atom.getDAM()) {
				donors.add(atom);
			}
		}
		return donors.iterator();
	}

	//Get graph
	@Override
	public IMolecule getGraph() {
		IMolecule graph = new Molecule(name);
		for(IAtom atom : geometry.values()) {
			IAtom copy = new Atom((Atom) atom);
			Vector3d pos = new Vector3d(atom.getPosition());
			pos.z = 0.0d;
			copy.setPosition(pos);
			copy.setMolecule(graph);
			graph.addAtom(copy, plane.contains(atom));
		}
		List<IBond> copyBonds = new LinkedList<>();
		for(IBond bond : bonds)
		{
			IAtom copyIMN0 = graph.getAtom(bond.getIMN0().getIMN());
			List<IAtom> copyBonded = new LinkedList<>();
			Iterator<IAtom> iBonded = bond.getBondedAtoms();
			while(iBonded.hasNext())
			{
				copyBonded.add(graph.getAtom(iBonded.next().getIMN()));
			}
			try {
				copyBonds.add(new Bond(copyIMN0, copyBonded));
			} catch (BondInvalidParametersProvidedException e) {
				e.printStackTrace();
			}
		}
		graph.setBonds(copyBonds);
		return graph;
	}

	//Get covalent bonds
	@Override
	public List<IBond> getBonds() {
		return bonds;
	}

	//Set covalent bonds
	@Override
	public void setBonds(List<IBond> molBonds) {
		this.bonds = molBonds;
	}

	/*
	 * Auxiliary methods
	 */
	//Set molecule on XY plane
	protected void setMoleculeOnXYplane() {
		//If not all 3 plane atoms are in place - do nothing
		if(plane.size() < 3)
			return;
		//Take plane atoms
		Iterator<IAtom> atoms = plane.iterator();
		//Set 1st plane atom into (0, 0, 0)
		Vector3d r = new Vector3d(atoms.next().getPosition());
		atoms = geometry.values().iterator();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			atom.setPosition(atom.getPosition().sub(r));
		}
		//Take vectors U, V
		atoms = plane.iterator();
		atoms.next();
		Vector3d u = atoms.next().getPosition();
		IAtom p2 = atoms.next();
		//Calculate first 2 rotation angles
		Double angZ = Math.acos(u.x / Math.sqrt(u.x * u.x + u.y * u.y));
		if(u.y >= 0.0d) 
			angZ = -angZ;
		Double angY = Math.acos(u.z / u.length()) - Math.PI/2.0d;
		//Rotate along Z axis
		Matrix3d rot = new Matrix3d();
		rot.rotZrad(angZ);
		//Rotate along Y axis
		Matrix3d rotY = new Matrix3d();
		rotY.rotYrad(angY);
		rot = rotY.mul(rot);
		//Apply rotation
		atoms = geometry.values().iterator();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			r = atom.getPosition();
			atom.setPosition(rot.mul(r));
		}
		//Calculate last rotation angle
		Vector3d v = p2.getPosition();
		Double angX = Math.acos(v.y / Math.sqrt(v.y * v.y + v.z * v.z));
		if(v.z > 0.0d) 
			angX = -angX;
		//Rotate along X axis
		rot.rotXrad(angX);
		//Mirror flag
		boolean mirrorY = false;
		if(rot.mul(v).y < 0.0d)
			mirrorY = true;
		atoms = geometry.values().iterator();
		while(atoms.hasNext())
		{
			IAtom atom = atoms.next();
			r = atom.getPosition();
			r = rot.mul(r);
			if(mirrorY)
				r.y = -r.y;
			atom.setPosition(r);
		}
	}
	
	/*
	 * Constructors
	 */
	//Initialization constructor
	public Molecule(String molName) {
		name = molName;
	}
	
	//Copy constructor
	public Molecule(Molecule right) {
		name = right.name;
		bonds = new LinkedList<IBond>(right.bonds);
		for(IAtom atom : right.geometry.values()) {
			IAtom copy = new Atom((Atom) atom);
			geometry.put(copy.getIMN(), copy);
			if(right.plane.contains(atom)) {
				plane.add(copy);
			}
		}
		isLastAtomAdded = right.isLastAtomAdded;
	}
}
