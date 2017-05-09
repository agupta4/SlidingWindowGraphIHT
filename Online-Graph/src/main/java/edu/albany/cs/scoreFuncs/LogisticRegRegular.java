package edu.albany.cs.scoreFuncs;
/**
 * This function is not convex and won't converge.
 */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.Utils;

public class LogisticRegRegular implements Function{
	private double[] b;
	private double[] w;
	private final int n;
	private int yt = 0;
	private double bt = 0;
	private double lambda = 1.28D;
	private final FuncType funcID;
	
	public LogisticRegRegular(double[] b, double[] w,int yt, double bt2){
		super();
		this.b = b;
		this.w = w;
		this.yt = yt;
		this.bt = bt2;
		if(!checkinput(b,w)){
			Utils.error("Invalid Parameters", 0);
		}
		this.n = b.length;
		this.funcID = FuncType.LR;
	}
	
	
	private boolean checkinput(double[] b2, double[] w2) {
		// TODO Auto-generated method stub
		if(b2 == null || w2 == null){
			return false;
		}
		for(int i = 0; i < b2.length; i++){
			if(b2[i] <= 0.0){
				return false;
			}
		}
		
		return true;
	}
	
	public double sigmoid(double z){
		return (1.0D/(1.0D+Math.exp(-z)));
	}
	//Gradient with regularization term
	public double[] getGradient(double[] x) {
		// TODO Auto-generated method stub
		if(this.w == null || x == null || this.w.length != x.length){
			System.out.println("Input error!!");
			System.exit(0);
		}
		
		double[] gradient = new double[n];
		double lr = new ArrayRealVector(w).dotProduct(new ArrayRealVector(x)) + this.bt;
		//Gradient with Regularization term
		for(int i = 0 ; i < n; i++){
			gradient[i] = -2 * (sigmoid(lr)*(1-sigmoid(lr))*w[i]) * (sigmoid(lr) - yt) + (2 * lambda * x[i]);
		}
		
		return gradient;
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
		for(int i: S){
			x[i] = 1.0D;
		}
		return this.getGradient(x);
	}

	//Function value for LR with regularization term
	public double getFuncValue(double[] x) {
		// TODO Auto-generated method stub
		if(this.w == null || x == null || this.w.length != x.length){
			System.out.println("Input error!!");
			System.exit(0);
		}
		double lr= new ArrayRealVector(this.w).dotProduct(new ArrayRealVector(x)) + bt;
		double norm = new ArrayRealVector(x).getNorm();
		double func = this.sigmoid(lr) - Math.pow(yt,2);
		func = func + lambda * norm;
		return func;
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

	public double[] getArgMaxFx(ArrayList<Integer> S) {
		// TODO Auto-generated method stub
		return null;
	}

	public FuncType getFuncID() {
		// TODO Auto-generated method stub
		return this.funcID;
	}

}
