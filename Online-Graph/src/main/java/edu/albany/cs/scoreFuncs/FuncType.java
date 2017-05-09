package edu.albany.cs.scoreFuncs;

/**
 * Score functions that we have now.
 *
 * @author baojian bzhou6@albany.edu
 */
public enum FuncType {

	/** Kulldorff Scan Statistic */
	Kulldorff,
	/** Expectation Based Poisson */
	EBP,
	/** Elevated mean statistic */
	EMS,
	/**Modified Elevated mean Stat*/
	ModEMS,
	/**	Logistic Regression Function */
	LR,
	/**	LR with regularization term */
	LRReg,
	/** Lagrangian Function */
	LRReg2,
	LagrangianFunc,
	/** Unknown Type of Function */
	Unknown;

	public static FuncType defaultFuncType() {
		return FuncType.Unknown;
	}

}
