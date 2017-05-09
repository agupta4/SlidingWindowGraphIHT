package edu.albany.cs.scoreFuncs;
/**
 * This function is not convex and didn't converge.
 * @author abhishekgupta
 * @version 1.0
 */

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.Utils;
public class LogisticReg implements Function {
	private final double[] b;
	private final double[] c;
	private final int n;
	private int y = 0;
	private double bt = 0;
	private final FuncType funcID;
	

	public LogisticReg(double[] b, double[] c) {
		super();
		this.b = b;
		this.c = c;
		if(!checkinput(b,c)){
			Utils.error("Invalid Parameters", 0);
		}
		this.n = b.length;
		this.funcID = FuncType.LR;
	}
	public LogisticReg(double[] b, double[] c, int y, double bt){
		super();
		this.b = b;
		this.c = c;
		this.y = y;
		this.bt = bt;
		if(!checkinput(b,c)){
			System.out.println("Invalid Parameters");
			System.exit(0);
		}
		this.n = this.b.length;
		this.funcID = FuncType.LR;
	}
	/**
	 * Evaluates logistic value for value z
	 * @param z
	 * @return double logistic value
	 */
	private double logistic(double z){
		return (1.0/(1.0+Math.exp(-1.0 * z)));
	}
	private boolean checkinput(double[] b2, double[] c2) {
		// TODO Auto-generated method stub
		if(b2 == null || c2 == null){
			return false;
		}
		for(int i = 0; i < b2.length; i++){
			if(b2[i] <= 0.0D){
				return false;
			}
		}
		return true;
	}
	/**
	 * Calculates gradient for Logistic Regression cost function.
	 * @param x as feature vector
	 * @return double[] gradient for the cost function 
	 */
	public double[] getGradient(double[] x) {
		// TODO Auto-generated method stub
		//double bt = 1.0;
		if(x == null || c == null || x.length != c.length){
			Utils.error("Error! Invalid parameters", 0);
		}
		double lr = this.logistic(new ArrayRealVector(this.c).dotProduct(new ArrayRealVector(x))+this.bt);
		//System.out.println("logistic value:"+lr);
		double[]  gradient = null;
		for(double ci: this.c){
			double val =lr*(1-lr) * ci * (lr - this.y);
			gradient = ArrayUtils.add(gradient, val);	
		}
		return gradient;
	}
	/**
	 * Calculates the gradient in bigdecimal format
	 * @param vector x in bigdecimal format
	 * @return gradient vector in bigdecimal format
	 */
	public BigDecimal[] getGradientBigDecimal(BigDecimal[] x) {
		// TODO Auto-generated method stub
		double[] x0 = null;
		for(BigDecimal val: x){
			x0 = ArrayUtils.add(x0, val.doubleValue());
		}
		double[] grad = this.getGradient(x0);
		BigDecimal[] gradient = new BigDecimal[grad.length];
		for(int i = 0; i < grad.length; i++){
			gradient[i] = new BigDecimal(grad[i]);
		}
		return gradient;
	}
	/**
	 * Evaluate feature vector x according to nodes in forest S and calculate gradient.
	 * @param forest S consisting of nodes being evaluated from algo or intermediate steps in algo 
	 * @return double gradient vector 
	 */
	public double[] getGradient(int[] S) {
		// TODO Auto-generated method stub
		double[] x = new double[n];
		Arrays.fill(x, 0.0D);
		for(int i: S){
			x[i] = 1.0D;
		}
		return this.getGradient(x);
	}
	/**
	 * Calculates function value at vector x. Function is (g(wt^Tx + bt) - yt)^2
	 * @param feature vector x
	 * @return double function value
	 */
	public double getFuncValue(double[] x) {
		// TODO Auto-generated method stub
		double func = 0.0;
		if(x == null || c == null || x.length != c.length){
			Utils.error("Error! Invalid parameters", 0);
		}
		double lr = this.logistic(new ArrayRealVector(this.c).dotProduct(new ArrayRealVector(x)) + this.bt);
		
		func = Math.pow(lr - this.y, 2);
		
		return func;
	}
	/**
	 * Evaluate feature vector x according to nodes in forest S and calculates function value at vector x
	 * @param forest S consisting of nodes being evaluated from algo or intermediate steps in algo
	 * @return double function value
	 */
	public double getFuncValue(int[] S) {
		// TODO Auto-generated method stub
		double[] x = new double[n];
		Arrays.fill(x, 0.0D);
		for(int i: S){
			x[i] = 1.0D;
		}
		return this.getFuncValue(x);
	}

	public double[] getArgMaxFx(ArrayList<Integer> S) {
		// TODO Auto-generated method stub
		
		
		return null;
	}

	public FuncType getFuncID() {
		// TODO Auto-generated method stub
		return this.funcID;
	}

}
