package edu.albany.cs.scoreFuncs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;

import edu.albany.cs.Interface.Function;

public class Log_Regression_function2 implements Function {
	
	private final double[] b;
	private final double[][] w;
	private final int n;
	private int[] y;
	private int[] bt;
	private final FuncType funcID;
	
	public Log_Regression_function2(double[] b, double[][] w, int[] yt, int[] bt2){
		this.b = b;
		this.w = w;
		this.y = yt;
		this.bt = bt2;
		if(!checkinput(b,w)){
			System.out.println("=======Invalid parameter========");
			System.exit(0);
		}
		this.n = b.length;
		this.funcID = FuncType.LR;
	}

	private boolean checkinput(double[] b2, double[][] c2) {
		if(b2 == null || c2 == null){
			return false;
		}
		for(int i = 0; i < b2.length; i++){
			if(b2[i] <= 0D){
				return false;
			}
		}
		return true;
	}
	
	public double logistic(double z){
		double dr = 1.0 + Math.exp(-z);
		return 1.0 / (dr);
	}
	
	public double[] getGradient(double[] x) {
		// TODO Auto-generated method stub
		double[] c = this.getWeight(0);
		if(x == null || x.length != c.length){
			System.out.println("Invalid parameters");
			System.exit(0);
		}
		int T = this.w[0].length; 
		double[] grad = new double[n];
		double norm = 0.5;//Math.pow(new ArrayRealVector(x).getNorm(),1);
		Arrays.fill(grad, 0D);
		for(int t = 0;t < T ; t++){
			double[] weight = this.getWeight(t);
			double lr = this.logistic(new ArrayRealVector(weight).dotProduct(new ArrayRealVector(x)) + this.bt[t]);
			double[] temp = (new ArrayRealVector(weight).mapMultiply(lr - y[t])).toArray();
			grad = new ArrayRealVector(grad).add(new ArrayRealVector(temp)).toArray();
		}
		
		return new ArrayRealVector(grad).mapAdd(norm).toArray();
	}

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

	public double[] getGradient(int[] S) {
		// TODO Auto-generated method stub
		double[] x = new double[n];
		Arrays.fill(x, 0.0D);
		for(int i:S){
			x[i] = 1.0D;
		}
		return this.getGradient(x);
	}

	public double getFuncValue(double[] x) {
		// TODO Auto-generated method stub
		double[] c = this.getWeight(0);
		if(x == null || x.length != c.length){
			System.out.println("=======Invalid parameters===========");
			System.exit(0);
		}
		int T = this.w[0].length; //Counting number of snapshots
		double func = 0.0;
		double norm = Math.pow(new ArrayRealVector(x).getNorm(),1);
		for(int i = 0; i < T; i++){
			double[] weight = this.getWeight(i);
			double lr = this.logistic(new ArrayRealVector(weight).dotProduct(new ArrayRealVector(x)) + this.bt[i]);
			
			//System.out.println((1-y[i]) * Math.log(1-lr));
			//Since, remaining term will either be 0 or NaN
			if(lr >= 1.0)
				func = func + ( (this.y[i] * Math.log(lr)));
			else if(lr <= 0.0)
				func = func + ( ((1 - this.y[i]) * Math.log(1-lr)));
			else
				func = func + ((this.y[i] * Math.log(lr) + (1-y[i]) * Math.log(1-lr)));
		}
		return -1.0 * func + 0.5 * norm;
	}

	public double getFuncValue(int[] S) {
		// TODO Auto-generated method stub
		double[] x = new double[n];
		Arrays.fill(x, 0.0D);
		for(int i: S){
			x[i] = 1.0D;
		}
		return this.getFuncValue(x);
	}



	public FuncType getFuncID() {
		// TODO Auto-generated method stub
		return this.funcID;
	}
	
	public double[] getWeight(int t){
		double[] c = new double[n];
		
		for(int x = 0; x < n; x++){
			c[x] = w[x][t];
		}
		
		return c;
	}

	public double[] getArgMaxFx(ArrayList<Integer> S) {
		// TODO Auto-generated method stub
		return null;
	}
	

}
