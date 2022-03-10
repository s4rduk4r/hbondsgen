package hbondsgen.parser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import hbondsgen.container.atom.Atom;
import hbondsgen.container.atom.AtomInvalidParametersProvidedException;
import hbondsgen.container.atom.EDonorAcceptorMarker;
import hbondsgen.container.atom.IAtom;
import hbondsgen.container.bond.Bond;
import hbondsgen.container.bond.BondInvalidParametersProvidedException;
import hbondsgen.container.bond.IBond;
import hbondsgen.container.molecule.IMolecule;
import hbondsgen.container.molecule.Molecule;
import hbondsgen.job.IJobDescriptor;
import hbondsgen.job.JobDescriptor;
import hbondsgen.job.JobDescriptorInvalidParametersException;

public class InputParser implements IInputParser {

	/*
	 * Properties
	 */
	protected final static IInputParser instance = new InputParser();
	
	/*
	 * In-class constants
	 */
	//Error messages
	protected final static String ERR_INVALID_FILE_STRUCTURE = "Invalid input file structure. ";
	protected final static String ERR_TITLE = "Check TITLE section";
	protected final static String ERR_GEOMETRY = "Check GEOMETRY section";
	protected final static String ERR_BOND = "Check BOND section";
	//Regular expressions
	//Special characters
	public final static String DAM_ACCEPTOR = "a";
	public final static String DAM_DONOR = "d";
	public final static String P = "p";
	public final static String BOND_ATOM_SEPARATOR = "\\s+";
	//Regular expressions
	public final static String REGEXP_COMMENT = "#+.*";
	public final static String REGEXP_ATOM = "[a-z]{1,2}\\d+(-\\d+)?";
	public final static String REGEXP_FLOAT = "[+-]?\\d+[.]\\d+";
	public final static String REGEXP_DAM = "[" + DAM_ACCEPTOR + DAM_DONOR +"]";
	public final static String REGEXP_P = "[" + P + "]";
	public final static String REGEXP_ATOMICLINE = REGEXP_ATOM + "\\s+"
													+ REGEXP_FLOAT + "\\s+"
													+ REGEXP_FLOAT + "\\s+"
													+ REGEXP_FLOAT
													+ "(\\s+"	+ REGEXP_DAM + ")?"
													+ "(\\s+" + REGEXP_P + ")?"
													+ "(\\s+)?";
	public final static String REGEXP_BONDLINE = REGEXP_ATOM + BOND_ATOM_SEPARATOR + REGEXP_ATOM 
												 + "(" + BOND_ATOM_SEPARATOR + REGEXP_ATOM + ")*";
	
	
	/*
	 * Interface
	 */
	//Get instance
	public static IInputParser getInstance() {
		return instance;
	}
	
	//IInputParser
	//Parse input file
	@Override
	public IJobDescriptor parse(String filename) throws InputParserFailedToConstructJobException {
		IJobDescriptor result = null;
		String title = null;
		IMolecule[] monomer = null;
		Map<IMolecule, List<IBond>> bondLists = null;
		//Open file
		BufferedReader ofile;
		try {
			ofile = new BufferedReader(new FileReader(filename));
			//Get job title
			title = parse1stSection(ofile);
			//Get monomers geometry
			monomer = parse2ndSection(ofile);
			//Get bond lists
			bondLists = parse3rdSection(monomer[0], monomer[1], ofile);
		} catch (FileNotFoundException errFile) {
			throw new InputParserFailedToConstructJobException(errFile.getMessage());
		} catch (IOException errIO) {
			throw new InputParserFailedToConstructJobException(errIO.getMessage());
		}
		//Apply bond lists
		for(int i = 0; i < 2; ++i)
		{
			monomer[i].setBonds(bondLists.get(monomer[i]));
		}
		//Create job
		try {
			result = new JobDescriptor(title, monomer[0], monomer[1]);
		} catch (JobDescriptorInvalidParametersException errJob) {
			throw new InputParserFailedToConstructJobException(errJob.getMessage());
		}
		//Return ready-to-go job
		return result;
	}
	
	/*
	 * Auxiliary methods
	 */
	//Parse 1st section. Return TITLE
	protected String parse1stSection(BufferedReader ofile) throws InputParserFailedToConstructJobException,
																  IOException 
	{
		String buffer = null;
		String jobName = new String();
		while(null != (buffer = ofile.readLine()))
		{
			//Check if section ended
			if(buffer.isEmpty())
				break;
			//Skip commentary lines
			if(buffer.matches(REGEXP_COMMENT))
				continue;
			//Take job name
			jobName += buffer;
		}
		//Check if section structure is faulty
		if(jobName.isEmpty())
			throw new InputParserFailedToConstructJobException(ERR_INVALID_FILE_STRUCTURE + ERR_TITLE);
		return jobName;
	}
	//Parse 2nd section. Return monomers
	protected IMolecule[] parse2ndSection(BufferedReader ofile) throws InputParserFailedToConstructJobException,
																	   IOException 
	{
		//TODO:
		String buffer = null;
		//1st monomer filler flag. If true, then collected data goes to mol1, otherwise to mol2
		boolean isFirstMolecule = true;
		//Prepare molecule descriptors for filling
		IMolecule[] result = new IMolecule[2];
		//Fill in molecule descriptors
		while(null != (buffer = ofile.readLine()))
		{
			//Check if section ended
			if(buffer.isEmpty())
				break;
			//Skip commentary lines
			if(buffer.matches(REGEXP_COMMENT))
				continue;
			//Check for atom descriptions
			buffer = buffer.toLowerCase();
			if(buffer.matches(REGEXP_ATOMICLINE)) {
				String s[] = buffer.split("\\s+");
				EDonorAcceptorMarker dam = EDonorAcceptorMarker.none;
				//Check if atom has DAM
				if(s.length > 4) {
					if(s[4].equals(DAM_ACCEPTOR)) {
						dam = EDonorAcceptorMarker.acceptor;
					}
					if(s[4].equals(DAM_DONOR)) {
						dam = EDonorAcceptorMarker.donor;
					}
				}
				//Check if atom has P marker
				boolean isPlaneAtom = false;
				if(s.length > 4 || s.length > 5) {
					if(s[s.length -1].equals(P))
						isPlaneAtom = true;
				}
				Atom atom;
				try {
					atom = new Atom(s[0], new Double(s[1]), new Double(s[2]), new Double(s[3]), dam);
				} catch (AtomInvalidParametersProvidedException errAtom) {
					throw new InputParserFailedToConstructJobException(errAtom.getMessage());
				}
				IMolecule mol = isFirstMolecule ? result[0] : result[1];
				mol.addAtom(atom, isPlaneAtom);
			} else {
				//Check for monomer name
				if(null == result[0]) {
					result[0] = new Molecule(buffer);
				}
				else {
					if(null == result[1]) {
						result[1] = new Molecule(buffer);
						isFirstMolecule = false;
					}
				}
			}
		}
		//Check if section structure is faulty
		boolean isFaultySection = (null == result[0]) || (null == result[1]) 
								|| (result[0].getName().isEmpty()) || (result[1].getName().isEmpty())
								|| (0 == result[0].getAtomCount()) || (0 == result[1].getAtomCount());
		if(isFaultySection) {
			result[0] = null;
			result[1] = null;
			throw new InputParserFailedToConstructJobException(ERR_INVALID_FILE_STRUCTURE + ERR_GEOMETRY);
		}
		//Lock molecules for changes
		for(int i = 0; i < 2; ++i) {
			result[i].lastAtomAdded();
		}
		return result;
	}
	//Parse 3rd section. Return bond lists
	protected Map<IMolecule, List<IBond>> parse3rdSection(IMolecule mol1, IMolecule mol2, BufferedReader ofile) 
										  throws InputParserFailedToConstructJobException,
										  		 IOException
	{
		String buffer = null;
		Map<IMolecule, List<IBond>> result = null;
		IMolecule mol = null;
		while(null != (buffer = ofile.readLine()))
		{
			//Check if section ended
			if(buffer.isEmpty())
				break;
			//Skip commentary lines
			if(buffer.matches(REGEXP_COMMENT))
				continue;
			//Get bond
			buffer = buffer.toLowerCase();
			if(buffer.matches(REGEXP_BONDLINE)) {
				String[] bondAtoms = buffer.split(BOND_ATOM_SEPARATOR);
				IAtom imn0 = null;
				List<IAtom> bondedToIMN0 = new LinkedList<IAtom>();
				for(int i = 0; i < bondAtoms.length; ++i)
				{
					if(bondAtoms[i].matches(REGEXP_ATOM)) {
						//Get IMN0 atom
						if(null == imn0) {
							imn0 = mol.getAtom(bondAtoms[i]);
						} else {//Get bonded to IMN0 atoms
							bondedToIMN0.add(mol.getAtom(bondAtoms[i]));
						}
					}
				}
				//Store found bond
				try {
					result.get(mol).add(new Bond(imn0, bondedToIMN0));
				} catch (BondInvalidParametersProvidedException errBond) {
					throw new InputParserFailedToConstructJobException(errBond.getMessage());
				}
				continue;
			}
			//Otherwise it's a molecule name
			if(buffer.equals(mol1.getName())) {
				mol = mol1;
				result = new TreeMap<IMolecule, List<IBond>>();
				result.put(mol1, new LinkedList<IBond>());
			} else {
				if(buffer.equals(mol2.getName())) {
					mol = mol2;
					result.put(mol2, new LinkedList<IBond>());
				} else {
					//If molecule name is incorrect - stop parsing
					throw new InputParserFailedToConstructJobException(ERR_INVALID_FILE_STRUCTURE + ERR_BOND);
				}
			}
		}
		return result;
	}
	
	/*
	 * Singleton constructor
	 */
	private InputParser() {
	}
}
