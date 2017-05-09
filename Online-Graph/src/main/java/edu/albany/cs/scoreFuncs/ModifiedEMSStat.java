package edu.albany.cs.scoreFuncs;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.stat.StatUtils;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.Utils;

public class ModifiedEMSStat implements Function{
	
	private double[] b = null;
	private int n;
	private double[][] matrix = null;
	private int timeStamp;
	private FuncType funcID;
	
	public ModifiedEMSStat(double[] b, double[][] matrix, int timeStamp){
		this.funcID = FuncType.EMS;
		if(!checkinput(b, matrix)){
			System.out.println("EMS functio error, Input Parameter is not valid");
			System.exit(0);
		}
		this.b = b;
		this.matrix = matrix;
		this.n = b.length;
		this.timeStamp = timeStamp;
	}
	
	private boolean checkinput(double[] b2, double[][] matrix) {
		// TODO Auto-generated method stub
		if(b2 == null || matrix == null){
			return false;
		}else{
			for(double element: b2){
				if(element <= 0.0D)
					return false;
			}
			return true;
		}
		
	}

	public double[] getGradient(double[] x) {
		// TODO Auto-generated method stub
		if(x == null || matrix == null ||matrix[0] != x){
			Utils.error("Error: Invalid parameters", 0);
		}
		double sigmaCX = 0.0D;
		for(int t = 0; t < this.timeStamp; t++)
		{
			double[] c = this.getCounts(t);
			double temp= new ArrayRealVector(c).dotProduct(new ArrayRealVector(x));
			sigmaCX += temp;
		}
		double sigmaX = StatUtils.sum(x);
		//Problem in here
		ArrayRealVector sigmaC = null;
		for(int t = 0; t < this.timeStamp; t++){
			double[] c = this.getCounts(t);
			sigmaC = sigmaC.add(new ArrayRealVector(c));
		}
		double[] gradient = new double[n];
		for(int i = 0; i < this.n; i++){
			gradient[i] = sigmaC.getEntry(i) / sigmaX - sigmaCX / Math.pow(sigmaX, 2);
		}
		
		return gradient;
	}

	public BigDecimal[] getGradientBigDecimal(BigDecimal[] x) {
		// TODO Auto-generated method stub
		double[] x0 = null;
		for(int i = 0; i < x.length; i++){
			x0 = ArrayUtils.add(x0, x[i].doubleValue());
		}
		double[] gradient = this.getGradient(x0);
		BigDecimal[] grad = new BigDecimal[gradient.length];
		for(int i = 0; i < gradient.length; i++){
			grad[i] = new BigDecimal(gradient[i]);
		}
		return grad;
	}
	public double[] getGradient(int[] S) {
		// TODO Auto-generated method stub
		double[] x = new double[this.n];
		Arrays.fill(x, 0.0D);
		for(int i: S){
			x[i] = 1.0D;
		}
		
		return x;
	}

	public double getFuncValue(double[] x) {
		// TODO Auto-generated method stub
		double numerator = 0;
		if (x == null || matrix == null || x.length != matrix[0].length) {
			new IllegalArgumentException("Error : Invalid parameters ...");
			System.exit(0);
		}
		for(int t = 0; t < this.timeStamp; t++)
		{
			double[] c = this.getCounts(t);
			double temp= new ArrayRealVector(c).dotProduct(new ArrayRealVector(x));
			numerator += Math.pow(temp, 2);
		}
		double denominator = StatUtils.sum(x);
		if(denominator <= 0.0D){
			Utils.error("EMS function error", 0);
		}
		double funcValue = numerator/denominator;
		if (!Double.isFinite(funcValue)) {
			Utils.error("EMS func Value is not a real value, f is " + funcValue, 0);
		}
		return funcValue;
	}

	private double[] getCounts(int t){
		double[] c = new double[n];
		
		for(int x = 0; x < n; x++){
			c[x] = this.matrix[x][t];
		}
		
		return c;
	}
	
	public double getFuncValue(int[] S) {
		// TODO Auto-generated method stub
		double[] x = new double[this.n];
		Arrays.fill(x, 0.0D);
		for(int i = 0; i < S.length; i++){
			x[S[i]] = 1.0D;
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
