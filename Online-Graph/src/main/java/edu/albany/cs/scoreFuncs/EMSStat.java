package edu.albany.cs.scoreFuncs;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.ArrayIndexComparator;
import edu.albany.cs.base.Utils;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.stat.StatUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author baojian bzhou6@albany.edu
 *
 */
public class EMSStat implements Function {

	private final double[] b;
	/** attribute 1 : each node i has a feature c_i */
	private final double[] c;
	private final FuncType funcID;
	private final int n;

	public EMSStat(double[] b, double[] c) {
		funcID = FuncType.EMS;
		if (!checkInput(b, c)) {
			Utils.error("EMS func error. Input parameter is invalid.", 0);
		}
		this.b = b;
		this.c = c;
		this.n = b.length;
	}

	private boolean checkInput(double[] b, double c[]) {
		if (b == null || c == null) {
			return false;
		} else {
			for (int i = 0; i < b.length; i++) {
				if (b[i] <= 0.0D) {
					return false;
				}
			}
			return true;
		}
	}

	 
	public double[] getGradient(double[] x) {

		if (x == null || c == null || x.length != c.length) {
			new IllegalArgumentException("Error : Invalid parameters ...");
			System.exit(0);
		}
		double[] gradient = new double[n];
		double sigmaX = StatUtils.sum(x);
		if (sigmaX == 0.0D) {
			Utils.error("EMS func Value error. The denominator should not be zero.", 0);
		}
		double sigmaCX = new ArrayRealVector(x).dotProduct(new ArrayRealVector(c));
		for (int i = 0; i < gradient.length; i++) {
			gradient[i] = sigmaCX*((c[i] / sigmaX) - (sigmaCX / Math.pow(sigmaX, 2)));
		}
		return gradient;
	}

	 
	public double getFuncValue(double[] x) {

		double funcValue = 0.0D;
		if (x == null || c == null || x.length != c.length) {
			new IllegalArgumentException("Error : Invalid parameters ...");
			System.exit(0);
		}
		double sigmaX = StatUtils.sum(x);
		double sigmaCX = new ArrayRealVector(x).dotProduct(new ArrayRealVector(c));
		if (sigmaX <= 0.0D) {
			Utils.error("EMS func Value error ...", 0);
		} else {
			funcValue = Math.pow(sigmaCX, 2) / sigmaX;
		}
		if (!Double.isFinite(funcValue)) {
			Utils.error("EMS func Value is not a real value, f is " + funcValue, 0);
		}
		return funcValue;
	}

	 
	public double[] getArgMaxFx(ArrayList<Integer> S) {
		double[] result = new double[this.b.length];
		Double[] vectorRatioCB = new Double[S.size()];
		for (int i = 0; i < vectorRatioCB.length; i++) {
			vectorRatioCB[i] = c[S.get(i)] / b[S.get(i)];
		}
		ArrayIndexComparator arrayIndexComparator = new ArrayIndexComparator(vectorRatioCB);
		Integer[] indexes = arrayIndexComparator.indexes;
		Arrays.sort(indexes, arrayIndexComparator);
		ArrayList<Integer> sortedS = new ArrayList<Integer>(); // v_1,v_2,...,v_m
		for (int index : indexes) {
			sortedS.add(S.get(index));
		}
		double maxF = -Double.MAX_VALUE;
		double[] argMaxX = null;
		for (int k = 1; k <= sortedS.size(); k++) {
			List<Integer> Rk = sortedS.subList(0, k);
			double[] x = new double[n];
			for (int i = 0; i < n; i++) {
				x[i] = 0.0D;
			}
			for (int index : Rk) {
				x[index] = 1.0D;
			}
			double fk = getFuncValue(x);
			if (fk > maxF) {
				maxF = fk;
				argMaxX = x;
			}
		}
		result = argMaxX;
		return result;
	}

	 
	public FuncType getFuncID() {
		return funcID;
	}

	 
	public double[] getGradient(int[] S) {
		double[] x = new double[n];
		Arrays.fill(x, 0.0D);
		for (int i : S) {
			x[i] = 1.0D;
		}
		return getGradient(x);
	}

	 
	public double getFuncValue(int[] S) {
		double[] x = new double[n];
		Arrays.fill(x, 0.0D);
		for (int i : S) {
			x[i] = 1.0D;
		}
		return getFuncValue(x);
	}

	 
	public BigDecimal[] getGradientBigDecimal(BigDecimal[] x) {
		double[] xD = new double[n];
		for (int i = 0; i < n; i++) {
			xD[i] = x[i].doubleValue();
		}
		double[] gradient = getGradient(xD);
		BigDecimal[] grad = new BigDecimal[n];
		for (int i = 0; i < n; i++) {
			grad[i] = new BigDecimal(gradient[i]);
		}
		return grad;
	}
}
