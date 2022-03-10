package hbondsgen.generator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import hbondsgen.container.atom.EDonorAcceptorMarker;
import hbondsgen.container.atom.IAtom;
import hbondsgen.container.bond.IBond;
import hbondsgen.container.hbond.HBond;
import hbondsgen.container.molecule.IMolecule;
import hbondsgen.job.IJobDescriptor;
import hbondsgen.math.Line2d;
import hbondsgen.math.Point2d;
import hbondsgen.math.Segment2d;
import hbondsgen.math.Vector3d;

public class HBondsGenerator implements IHBondsGenerator {
	/*
	 * Properties
	 */
	//Job title
	protected String jobName = null;
	//Monomers
	protected IMolecule[] monomer = new IMolecule[2];
	//Generated H-bonds
	protected List<DimerHBonds> hbonds = null;
	
	/*
	 * In-class constants
	 */
	//Maximum H-bond length, Angstrom
	protected final static Double Rmax = 3.0d;
	
	/*
	 * Interface
	 */
	//Generate H-bonds
	@Override
	public void generate() {
		//Get active atoms for 1st monomer only
		List<ActiveAtoms> activeAtomsList = createActiveAtoms(monomer[0].getGraph());
		//Construct H-bonds pairs for each pair Center-ActiveAtom
		for(ActiveAtoms aatoms : activeAtomsList)
		{//CENTER_CYCLE_START
			IAtom atomC = monomer[0].getAtom(aatoms.center.getIMN());
			for(IAtom atomA : aatoms.activeAtoms)
			{//ACTIVE_ATOM_CYCLE_START
				atomA = monomer[0].getAtom(atomA.getIMN());
				//Calculate distance bounds
				Double[] Rmin = new Double[2];
				Double dAC = atomC.getPosition().sub(atomA.getPosition()).length();
				Iterator<IAtom> iAtomM = EDonorAcceptorMarker.acceptor == atomC.getDAM() ? 
						monomer[1].getDonors() : 
						monomer[1].getAcceptors();
				while(iAtomM.hasNext())
				{//ATOM_M_CYCLE_START
					Iterator<IAtom> iAtomN = EDonorAcceptorMarker.acceptor == atomA.getDAM() ?
							monomer[1].getDonors() :
							monomer[1].getAcceptors();
					IAtom atomM = iAtomM.next();
					Rmin[0] = atomC.getVanDerWaalsRadius() + atomM.getVanDerWaalsRadius();
					while(iAtomN.hasNext())
					{//ATOM_N_CYCLE_START
						IAtom atomN = iAtomN.next();
						if(atomN == atomM)
							continue;
						Rmin[1] = atomA.getVanDerWaalsRadius() + atomN.getVanDerWaalsRadius();
						Double a = (Rmin[0] - Rmin[1]) * (Rmin[0] - Rmin[1]);
						Double b = Math.sqrt(Rmax * Rmax - Rmin[0] * Rmin[0]);
						Double c = Math.sqrt(Rmax * Rmax - Rmin[1] * Rmin[1]);
						Double dMin = Math.sqrt(a + (dAC - b - c) * (dAC - b - c));
						Double dMax = Math.sqrt(a + (dAC + b + c) * (dAC + b + c));
						//Double delta = Math.sqrt(9.0d - 2.7d * 2.7d);
						//Double dMin = dAC - delta;
						//Double dMax = dAC + delta;
						Double dMN = atomM.getPosition().sub(atomN.getPosition()).length();
						if(dMN >= dMin && dMN <= dMax) {
							addHBondPairs(atomC, atomA, atomM, atomN);
						}
					}//ATOM_N_CYCLE_END
				}//ATOM_M_CYCLE_END
			}//ACTIVE_ATOM_CYCLE_END
		}//CENTER_CYCLE_END
	}

	//Get generated H-bonds
	@Override
	public HBonds getHBonds() {
		//If no H-bonds generated - return null
		if(null == hbonds || hbonds.isEmpty())
			return null;
		return new HBonds(jobName, monomer[0], monomer[1], hbonds);
	}
	
	/*
	 * Auxiliary methods
	 */
	//Create Active Atoms for each Center
	protected List<ActiveAtoms> createActiveAtoms(IMolecule graph) {
		List<ActiveAtoms> result = null;
		//Get all possible centers
		List<IAtom> centerAtoms = new LinkedList<>();
		Iterator<IAtom> iAtom = graph.getAcceptors();
		while(iAtom.hasNext())
			centerAtoms.add(iAtom.next());
		iAtom = graph.getDonors();
		while(iAtom.hasNext())
			centerAtoms.add(iAtom.next());
		//Get all potential active atoms
		List<IAtom> potentialActiveAtoms = new LinkedList<>(centerAtoms);
		//Find all active atoms for each center
		for(IAtom center : centerAtoms)
		{//CENTER_CYCLE_START
			ActiveAtoms activeAtoms = new ActiveAtoms(center);
			Vector3d posCenter = center.getPosition();
			for(IAtom candidate : potentialActiveAtoms)
			{//ACTIVE_ATOM_CANDIDATE_CYCLE_START
				//Skip if candidate is current center
				if(candidate == center)
					continue;
				Vector3d posCandidate = candidate.getPosition();
				Line2d lineCenterCandidate = new Line2d(posCenter.x, posCenter.y, posCandidate.x, posCandidate.y);
				boolean isValidCandidate = true;
				for(IBond bond : graph.getBonds())
				{//BOND_INTERSECTION_CHECK_CYCLE_START
					//If candidate is invalid - take next one
					if(!isValidCandidate)
						break;
					IAtom imn0 = bond.getIMN0();
					Vector3d posIMN0 = imn0.getPosition();
					Iterator<IAtom> iBondedAtoms = bond.getBondedAtoms();
					while(iBondedAtoms.hasNext())
					{
						IAtom bondedAtom = iBondedAtoms.next();
						//Take Candidate forming bond with Center as valid ActiveAtom 
						if(imn0 == center && bondedAtom == candidate) {
							if(null == activeAtoms.activeAtoms) {
								activeAtoms.activeAtoms = new LinkedList<>();
							}
							activeAtoms.activeAtoms.add(candidate);
							isValidCandidate = false;
							break;
						}
						Vector3d posBondedAtom = bondedAtom.getPosition();
						Line2d lineBond = new Line2d(posIMN0.x, posIMN0.y, posBondedAtom.x, posBondedAtom.y);
						//Get intersection point
						Point2d p = lineCenterCandidate.intersection(lineBond);
						//Check if bond is really intersected
						Segment2d segCenterCandidate = new Segment2d(posCenter.x, posCenter.y, posCandidate.x, posCandidate.y);
						Segment2d segBond = new Segment2d(posIMN0.x, posIMN0.y, posBondedAtom.x, posBondedAtom.y);
						//If bond has been crossed - then this candidate can't be active atom
						if(segCenterCandidate.isBelongExclusive(p) && segBond.isBelongExclusive(p)) {
							isValidCandidate = false;
							break;
						}
					}
				}//BOND_INTERSECTION_CHECK_CYCLE_END
				//Add candidate if it's valid
				if(isValidCandidate) {
					if(null == activeAtoms.activeAtoms) {
						activeAtoms.activeAtoms = new LinkedList<>();
					}
					activeAtoms.activeAtoms.add(candidate);
				}
			}//ACTIVE_ATOM_CANDIDATE_CYCLE_END
			//Save results
			if(!activeAtoms.activeAtoms.isEmpty()) {
				if(null == result)
					result = new LinkedList<>();
				result.add(activeAtoms);
			}
		}//CENTER_CYCLE_END
		return result;
	}
	
	//Add H-bond pairs
	protected void addHBondPairs(IAtom c, IAtom a, IAtom m, IAtom n) {
		DimerHBonds hbondsPair = new DimerHBonds();
		HBond hbond = null;
		//Store C..M bond
		if(EDonorAcceptorMarker.acceptor == c.getDAM()) {
			hbond = new HBond(c, m);
		} else {
			hbond = new HBond(m, c);
		}
		hbondsPair.add(hbond);
		//Store A..N bond
		if(EDonorAcceptorMarker.acceptor == a.getDAM()) {
			hbond = new HBond(a, n);
		} else {
			hbond = new HBond(n, a);
		}
		hbondsPair.add(hbond);
		//If such complex already known - do nothing
		if(checkIfHasDuplicate(hbondsPair))
			return;
		if(null == hbonds) {
			hbonds = new LinkedList<>();
		}
		hbonds.add(hbondsPair);
	}
	
	//Filter found H-bonds to unique ones
	protected boolean checkIfHasDuplicate(DimerHBonds hbondsPair) {
		//If no H-bonds stored - do nothing
		if(null == hbonds || hbonds.isEmpty())
			return false;
		//Otherwise search for duplicates and eradicate one of them until only uniques left
		/*
		 * If Monomer1 and Monomer2 aren't equal, then follow condition is true:
		 * 	C..M A..N == A..N C..M
		 *  If Monomer1 and Monomer2 are equal, then follow condition is true:
		 *  C..M A..N == A..N C..M == M..C N..A == N..A M..C
		 */
		/*
		 * Let A1, A2 be H-bonds acceptors and D1, D2 are H-bonds donors.
		 * Let HB1 = A1..D1, HB2 = A2..D2. Then dimer DIMER is HB1 HB2. 
		 * HB1 HB2 == HB2 HB1
		 * If Monomer1 and Monomer2 aren't equal, then follow condition is true:
		 * 	A1..D1 A2..D2 == A2..D2 A1..D1
		 *  If Monomer1 and Monomer2 are equal, then follow condition is true:
		 *  A1..D1 A2..D2 == A2..D2 A1..D1 == D1..A1 D2..A2 == D2..A2 D1..A1
		 *  
		 */
		boolean isMonomersEqual = monomer[0].getName().equals(monomer[1].getName());
		for(DimerHBonds dhb : hbonds)
		{
			if(hbondsPair.isEqual(dhb, isMonomersEqual))
				return true;
		}
		return false;
	}
	
	/*
	 * Constructor
	 */
	public HBondsGenerator(IJobDescriptor job) {
		jobName = job.getTitle();
		monomer[0] = job.getMonomer1();
		monomer[1] = job.getMonomer2();
	}
}
