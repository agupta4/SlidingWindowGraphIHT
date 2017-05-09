package edu.albany.cs.scoreFuncs;

import edu.albany.cs.Interface.Function;

/**
 * score function factory for EBP EMS and Kulldorff statistics
 * 
 * @author baojian
 *
 */
public class ScoreFuncFactory {

	public static Function getFunc(FuncType funcID, double[] b, double[] c) {

		if (funcID == null) {
			return null;
		} else if (funcID.equals(FuncType.Kulldorff)) {
			return new KulldorffStat(b, c);
		} else if (funcID.equals(FuncType.EMS)) {
			return new EMSStat(b, c);
		} else if (funcID.equals(FuncType.EBP)) {
			return new EBPStat(b, c);
		}else if(funcID.equals(FuncType.LR)){
			return new LogisticReg(b, c);
		}
		else {
			System.out.println("Unknown Type ...");
			return null;
		}
	}
	//One for logistic regression function to manage parameter y
	public static Function getFunc(FuncType funcID, double[] b, double[] c, int yt, double bt){
		if(funcID == null){
			return null;
			
		}else if(funcID.equals(FuncType.LR)){
			return new LogisticReg(b, c, yt, bt);
		}
		else{
			System.out.println("Unknown Type...");
			return null;
		}
	}
	public static Function getFunc(FuncType funcID, double[] b, double[][] w, int[] yt, int[] bt){
		
		if(funcID == null)
			return null;
		else if(funcID.equals(FuncType.LRReg2))
			return new Log_Regression_function2(b, w, yt, bt);
		return null;
		
	}
}
