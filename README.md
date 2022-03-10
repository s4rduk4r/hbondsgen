# hbondsgen
Hydrogen bonds finder helper tool. Meant to be used before [dimerconstr](https://github.com/s4rduk4r/dimerconstr) to make an input file for the latter

# Usage
```sh
java -jar ./hbondsgen.run inputfile1 [[inputfile2] [inputfile3] ...]
```

## Help
```sh
java -jar ./hbondsgen.run -help
```

# Input file structure

Example input file is here - https://github.com/s4rduk4r/hbondsgen/blob/main/example_input.txt

> Input file consists of commentary lines and 3 sections: `TITLE`, `GEOMETRY`, `BONDS`.<br>
> Each section must be terminated by an empty line.<br>
> Commentary lines start with '#' symbol. Anything after this symbol will be ignored.<br>
> 
> **EXAMPLE:**<br>
> \# This is a commentary line<br>
>
> `TITLE` section contains basic description of job and contains any printable characters.<br>
>
> **EXAMPLE:**<br>
> `TITLE`. Sample input for `hbondsgen` utility. m1Cyt..m1Cyt-imino example. 13-10-2012<br>
>
> `GEOMETRY` section consists of 2 monomer descriptions. <br>
> Monomer description consists of monomer name and any number of atom descriptions.<br>
> Atom descriptions must be represented in a specific way:<br>
>  `IMN`	`X`	`Y`	`Z`	`DAM`	`P`<br>
>  where:	`IMN` - intramolecular name of specific atom. E.g. N1, O2, H3, H4-1, H1-2, etc.<br>
>  	`X`, `Y`, `Z` - cartesian coordinates of specific atom. Must be floating point number<br>
>  	`DAM`	- donor-acceptor marker. Defines if particular atom is donor or acceptor or neither.<br>
>  		Acceptors defined as 'A' or 'a'<br>
>  		Donors defined as 'D' or 'd'<br>
>  		If atom is neither donor or acceptor then it must not have DAM<br>
>  	`P` - plane atom marker. Plane atoms define molecular plane. Only first 3 will be taken<br>
>
> **EXAMPLE:**<br>
> H2O<br>
> O1	0.000000	0.000000	0.117057	A	P<br>
> H1-1	0.000000	0.763564	-0.468227	D	P<br>
> H1-2	0.000000	-0.763564	-0.468227	D	P<br>
> 
> `BONDS` section consists of covalent bond descriptions.<br>
> Each description must begin with monomer name and be followed by covalent bond description lines.<br>
> Each covalent bond description line must comply the format:<br>
>  `IMN0`	`IMN1`	`IMN2`	...	`IMNn`<br>
>  where:	`IMN0` - atom creating covalent bond with following atoms<br>
>  	`IMN1`, `IMN2`, ..., `IMNn` - atoms creating covalent bond with `IMN0`<br>
>
> **EXAMPLE:**<br>
> H2O<br>
> O1	H1-1	H1-2<br>
> H1-1	O1<br>
> H1-2	O1<br>

# Output
Output is an input file for [dimerconstr](https://github.com/s4rduk4r/dimerconstr)
