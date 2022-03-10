package hbondsgen.parser;

import hbondsgen.job.IJobDescriptor;

public interface IInputParser {
	//Parse input file
	public IJobDescriptor parse(String filename) throws InputParserFailedToConstructJobException;
}
