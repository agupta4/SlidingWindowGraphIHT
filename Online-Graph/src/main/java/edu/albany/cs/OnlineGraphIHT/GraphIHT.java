/**
 * 
 */
package edu.albany.cs.OnlineGraphIHT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.stat.StatUtils;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.ConnectedComponents;
import edu.albany.cs.base.PreRec;
import edu.albany.cs.headApprox.HeadApprox;
import edu.albany.cs.scoreFuncs.FuncType;
import edu.albany.cs.tailApprox.TailApprox;

/**
 * @author abhishekgupta
 * This algorithm works in a window sliding environment where each window corresponds to one time snapshot.
 * This technique updates the x^i vector in each iteration and works with the next window on previously evaluated
 * x^i vector.
 *
 */
public class GraphIHT{
	/** graphSize, number of nodes in the input graph */
	private final int graphSize;
	/** edges in our graph, should notice that the graph should be connected. */
	private final ArrayList<Integer[]> edges;
	/** we use identity edge cost 1.0D in our algorithm. */
	private final ArrayList<Double> edgeCosts;
	
	private final int s;
	
	private final int g;

	private final double B;

	private final boolean singleNodeInitial;
	private final double[] c;
	private final Function function;
	private final double eta = 5.0D;
	private ConnectedComponents cc;

	private final int[] trueSubGraph;
	private int verboseLevel = 0;

	public int[] supportX;

	public ArrayRealVector x;
	public HashSet<Integer> resultNodesTail;
	public double funcValueTail = 0.0D;
	public double runTime = 0.0D;
	public double funcValue = 0D;
	public int timestamp;
	
	
	public GraphIHT(int graphSize, ArrayList<Integer[]> edges, ArrayList<Double> edgeCosts, int s, int g, double b,
			boolean singleNodeInitial, double[] c, Function function, int[] trueSubGraph, int timestamp, ArrayRealVector X) {
		super();
		this.graphSize = graphSize;
		this.edges = edges;
		this.edgeCosts = edgeCosts;
		this.s = s;
		this.g = g;
		this.B = b;
		this.singleNodeInitial = singleNodeInitial; 
		this.c = c;
		this.function = function;
		this.trueSubGraph = trueSubGraph;
		this.timestamp = timestamp;
		this.x = X;
		this.resultNodesTail = new HashSet<Integer>();
		if(checkinput())
			this.x = run();
		else{
			this.x = null;
			System.out.println("Error Creating the input. Input parameters are not valid");
			System.exit(0);
		}
			
	}
	/**
	 * This function maximizes the provided fx.
	 * @return ArrayRealVector xi
	 */
	private ArrayRealVector run() {
		//Generate X randomly for first timestamp
		long start = System.nanoTime();
		ArrayRealVector xi = null;
		if(this.timestamp == 0){
			if(function.getFuncID() == FuncType.EMS || function.getFuncID() == FuncType.LR){
				if(singleNodeInitial){
					xi = this.initializeX_RandomSingleNode();
				}else{
					xi = this.initializeX_MaximumCC();
				}
			}
			else
				xi = new ArrayRealVector(this.initializeRandom());
			
		}else{
			xi = this.x;	//taking t-1 instance of feature vector
		}
		int maxiter = 100;
		int iter = 0;
		while (true) {
			double oldfunc = this.function.getFuncValue(xi.toArray());
			
			double[] gradient = this.function.getGradient(xi.toArray());
			if(function.getFuncID() == FuncType.EMS )
				gradient = normalizegradient(gradient, xi.toArray());
			
			HeadApprox pcsfHead = new HeadApprox(edges, edgeCosts, gradient, s, g, B, trueSubGraph);
			
			ArrayList<Integer> omega = pcsfHead.bestForest.nodesInF;
			ArrayRealVector projection = this.projectionF(gradient, omega);
			ArrayRealVector b = xi.subtract(projection.mapMultiply(eta));

			TailApprox pcsfTail = new TailApprox(edges, edgeCosts, b.toArray(), s, g, B, trueSubGraph);
			ArrayList<Integer> S = pcsfTail.bestForest.nodesInF;
			xi = this.projectionF(b.toArray(), S);
			if(function.getFuncID() == FuncType.EMS )
				xi = this.normalize(xi);
			

			this.resultNodesTail = new HashSet<Integer>(pcsfTail.bestForest.nodesInF);
			
			this.funcValueTail = this.TailFuncValue(this.resultNodesTail);
			double newFunc = this.function.getFuncValue(xi.toArray());
			if (iter >= maxiter || (newFunc - oldfunc) < 1e-12)
				break;
			iter++;
		}
		long end = System.nanoTime();
		this.runTime = (long) ((end - start));
		this.funcValue = this.function.getFuncValue(xi.toArray());
		System.out.println(xi);
		return xi;
	}
	
	private double TailFuncValue(HashSet<Integer> resultNodesTail2) {
		// TODO Auto-generated method stub
		double[] tmp = new double[this.graphSize];
		Arrays.fill(tmp, 0.0D);
		for(int node: resultNodesTail2){
			tmp[node] = 1.0D;
		}
		
		return this.function.getFuncValue(tmp);
	}

	private ArrayRealVector normalize(ArrayRealVector xi) {
		// TODO Auto-generated method stub
		ArrayRealVector sol = new ArrayRealVector(xi);
		
		for(int i = 0; i < sol.getDimension(); i++){
			if(xi.getEntry(i) < 0.0D){
				sol.setEntry(i, 0.0D);
			}else if(xi.getEntry(i) > 1.0D){
				sol.setEntry(i, 1.0D);
			}
		}
		
		return sol;
	}

	private ArrayRealVector projectionF(double[] gradient, ArrayList<Integer> omega) {
		// TODO Auto-generated method stub
		double[] result = null;
		for(double element: gradient){result = ArrayUtils.add(result, element);}
		
		if(omega == null){
			return new ArrayRealVector(gradient);
		}else{
			for(int i = 0; i < result.length; i++){
				if(!omega.contains(i)){
					result[i] = 0.0D;
				}
			}
		}
		return new ArrayRealVector(result);
	}

	private double[] normalizegradient(double[] gradient, double[] array) {
		// TODO Auto-generated method stub
		double[] normalizegrad = new double[this.graphSize];
		
		for(int l = 0; l < normalizegrad.length; l++){
			if((array[l] == 0.0D) && (gradient[l] < 0.0D)){
				normalizegrad[l] = 0.0D;
			}else if((array[l] == 1.0D) && (gradient[l] > 0.0D)){
				normalizegrad[l] = 0.0D;
			}
			else{
				normalizegrad[l] = gradient[l];
			}
		}
		return normalizegrad;
	}

	private boolean checkinput() {
		// TODO Auto-generated method stub
		Set<Integer> Nodes = new HashSet<Integer>();
		for(Integer[] edge: this.edges){
			Nodes.add(edge[0]);
			Nodes.add(edge[1]);
		}
		if(Nodes.size()!=this.graphSize)
			return false;
		ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < graphSize; i++) {
			adj.add(new ArrayList<Integer>());
		}
		for (Integer[] edge : this.edges) {
			adj.get(edge[0]).add(edge[1]);
			adj.get(edge[1]).add(edge[0]);
		}
		cc = new ConnectedComponents(adj);
		boolean t = cc.checkConnectivity();
		return t;
	}
	
	private double[] initializeRandom() {
		double[] x0 = new double[c.length];
		Random rand = new Random();
		for (int i = 0; i < c.length; i++) {
			if (rand.nextDouble() < 0.5D) {
				x0[i] = 1.0D;
			} else {
				x0[i] = 0.0D;
			}
		}
		return x0;
	}
	
	private ArrayRealVector initializeX_MaximumCC() {
		ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < this.graphSize; i++) {
			adj.add(new ArrayList<Integer>());
		}
		for (Integer[] edge : this.edges) {
			adj.get(edge[0]).add(edge[1]);
			adj.get(edge[1]).add(edge[0]);
		}

		ConnectedComponents cc = new ConnectedComponents(adj);
		int[] abnormalNodes = null;
		double mean = StatUtils.mean(c);
		double std = Math.sqrt(StatUtils.variance(c));
		for (int i = 0; i < this.c.length; i++) {
			if (Math.abs(c[i]) >= mean + std) {
				abnormalNodes = ArrayUtils.add(abnormalNodes, i);
			}
		}
		cc.computeCCSubGraph(abnormalNodes);
		int[] largestCC = cc.findLargestConnectedComponet(abnormalNodes);
		double[] x0 = new double[this.c.length];
		for (int i = 0; i < x0.length; i++) {
			x0[i] = 0.0D;
		}
		for (int i : largestCC) {
			x0[i] = 1.0D;
		}
		//return x0;
		return new ArrayRealVector(x0);
	}

	private ArrayRealVector initializeX_RandomSingleNode() {
		int[] abnormalNodes = null;
		double mean = StatUtils.mean(c);
		double std = Math.sqrt(StatUtils.variance(c));
		for (int i = 0; i < graphSize; i++) {
			if (c[i] >= mean + 2.0D * std) {
				abnormalNodes = ArrayUtils.add(abnormalNodes, i);
			}
			//The range should be [(Mu-2std), (Mu+2std)]
		}
		
		int index = new Random().nextInt(abnormalNodes.length);
		double[] x0 = new double[graphSize];
		for (int i = 0; i < x0.length; i++) {
			x0[i] = 0.0D;
		}
		x0[abnormalNodes[index]] = 1.0D;
		//System.out.println(new ArrayRealVector(x0));
		return new ArrayRealVector(x0);
		
	}
	
}
