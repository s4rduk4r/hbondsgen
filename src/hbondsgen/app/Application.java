package hbondsgen.app;

import java.io.IOException;
import hbondsgen.generator.HBondsGenerator;
import hbondsgen.job.IJobDescriptor;
import hbondsgen.parser.IInputParser;
import hbondsgen.parser.InputParser;
import hbondsgen.parser.InputParserFailedToConstructJobException;
import hbondsgen.storage.Storage;

public class Application {
	/*
	 * Properties
	 */
	//Singleton instance
	private static final Application instance = new Application();
	//Parser
	IInputParser parser = InputParser.getInstance();
	//Logger
	//Storage
	
	/*
	 * In-class constants
	 */
	//Help key
	protected static final String KEY_HELP = "-help";
	
	/*
	 * Interface
	 */
	//Get singleton instance
	public static final Application getInstance() {
		return instance;
	}
	//Run
	public void run(String[] args) {
		//USAGE
		if(0 == args.length) {
			System.out.printf(usage());
			return;
		}
		//HELP
		if(args[0].equals(KEY_HELP)) {
			System.out.printf(help());
			return;
		}
		//Launch logger
		//Launch storage
		//Main program cycle
		for(String inputFile : args)
		{
			try {
				//Parse inputfile to create a job
				IJobDescriptor job = parser.parse(inputFile);
				//Work on job
				HBondsGenerator generator = new HBondsGenerator(job);
				System.out.println("Processing `" + job.getTitle() + "`");
				generator.generate();
				System.out.printf("Found potential dimers: %d", generator.getHBonds().hbonds.size());
				//Store results
				String fileName = job.getMonomer1().getName() + "-" + job.getMonomer2().getName() + "-job.txt";
				Storage.getInstance().toFile(generator.getHBonds(), fileName);
			} catch (InputParserFailedToConstructJobException e) {
				System.err.printf("File `" + inputFile + "` error: " + e.getMessage());
			} catch (IOException e) {
				System.err.printf("Unable to store results. Reason: ", e.getMessage());
			}
		}
		//Terminate storage
		//Terminate logger
	}
	
	/*
	 * Auxiliary methods
	 */
	//Usage
	protected String usage() {
		return	"USAGE: hbondsgen inputfile1 [[inputfile2] [inputfile3] ...]\n" +
				"-------------------------------\n" +
				"For info about inputfile structure use " + KEY_HELP + "\n";
	}
	
	//Help
	protected String help() {
		return	"Input file structure description\n" +
				"---------------------------------\n" +
				"Input file consists of commentary lines and 3 sections: TITLE, GEOMETRY, BONDS.\n" +
				"Each section must be terminated by an empty line.\n" +
				"Commentary lines start with '#' symbol. Anything after this symbol will be ignored.\n" +
				"EXAMPLE:\n" +
				"# This is a commentary line\n\n" +
				" TITLE section contains basic description of job and contains any printable characters.\n" +
				"EXAMPLE:\nTITLE. Sample input for `hbondsgen` utility. m1Cyt..m1Cyt-imino example. 13-10-2012\n\n" +
				" GEOMETRY section consists of 2 monomer descriptions. \n" +
				"Monomer description consists of monomer name and any number of atom descriptions.\n" +
				"Atom descriptions must be represented in a specific way:\n" +
				" IMN\tX\tY\tZ\tDAM\tP\n" +
				" where:\tIMN - intramolecular name of specific atom. E.g. N1, O2, H3, H4-1, H1-2, etc.\n" +
				" \tX, Y, Z - cartesian coordinates of specific atom. Must be floating point number\n" +
				" \tDAM	- donor-acceptor marker. Defines if particular atom is donor or acceptor or neither.\n" +
				" \t\tAcceptors defined as 'A' or 'a'\n" +
				" \t\tDonors defined as 'D' or 'd'\n" +
				" \t\tIf atom is neither donor or acceptor then it must not have DAM\n" +
				" \tP - plane atom marker. Plane atoms define molecular plane. Only first 3 will be taken\n" +
				"EXAMPLE:\n" +
				"H2O\n" +
				"O1\t0.000000\t0.000000\t0.117057\tA\tP\n" +
				"H1-1\t0.000000\t0.763564\t-0.468227\tD\tP\n" +
				"H1-2\t0.000000\t-0.763564\t-0.468227\tD\tP\n\n" +
				"BONDS section consists of covalent bond descriptions.\n" +
				"Each description must begin with monomer name and be followed by covalent bond description lines.\n" +
				"Each covalent bond description line must comply the format:\n" +
				" IMN0\tIMN1\tIMN2\t...\tIMNn\n" +
				" where:\tIMN0 - atom creating covalent bond with following atoms\n" +
				" \tIMN1, IMN2, ..., IMNn - atoms creating covalent bond with IMN0\n" +
				"EXAMPLE:\n" +
				"H2O\n" +
				"O1\tH1-1\tH1-2\n" +
				"H1-1\tO1\n" +
				"H1-2\tO1\n\n";
	}
	
	/*
	 * Singleton constructor
	 */
	private Application() {
	}
	
	
	/*
	 * Entry point
	 */
	public static void main(String[] args) {
		Application.getInstance().run(args);
	}
}
