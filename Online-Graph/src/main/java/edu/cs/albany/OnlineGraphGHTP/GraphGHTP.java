package edu.cs.albany.OnlineGraphGHTP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.stat.StatUtils;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.ConnectedComponents;
import edu.albany.cs.base.PreRec;
import edu.albany.cs.headApprox.HeadApprox;
import edu.albany.cs.tailApprox.TailApprox;

public class GraphGHTP {
	/** graphSize, number of nodes in the input graph */
	private final int graphSize;
	/** edges in our graph, should notice that the graph should be connected. */
	private final ArrayList<Integer[]> edges;
	/** we use identity edge cost 1.0D in our algorithm. */
	private final ArrayList<Double> edgeCosts;
	/** the total sparsity s */
	private final int s;
	/** the maximum number of connected components formed by the forest F. */
	private final int g;
	/** budget B. */
	private final double B;
	/** we randomly select a single node to initialize x0. */
	private final boolean singleNodeInitial;
	/** counts info */
	private final double[] c;
	/** function that will be used in our algorithm. */
	private final Function function;
	/** parameter eta in our algorithm. */
	private double eta = 1.0D;
	/** only use for testing algorithm */
	private int verboseLevel = 0;
	/** connected components of current graph */
	private ConnectedComponents cc;
	/** final vector x that we get in our algorithm. */
	public ArrayRealVector x;
	private final int[] trueSubGraph;
	private final boolean isNonTransportation;
	private final double epsilon = 1e-6;

	/** save function values in each iteration. */
	public ArrayList<Double> fValues;
	public int[] supportX;
	public HashSet<Integer> resultNodesTail = null;
	public double funcValueTail = 0.0D;
	public double runTime = 0.0D;
	public int timestamp;
	
	/**
	 * GraphModelIHTP algorithm
	 *
	 * @param edges
	 *            the edges of graph.
	 * @param edgeCosts
	 *            the cost of corresponding edges.
	 * @param c
	 *            costs or counts in data.
	 * @param s
	 *            sparsity
	 * @param g
	 *            number of cc
	 * @param B
	 *            the cost budget
	 * @param t
	 *            number of iterations
	 * @param singleNodeInitial
	 *            true : initialize x0 with single node.
	 * @param trueSubGraph
	 *            the true subgraph, in some data, there does not exist true
	 *            subgraph.
	 * @param func
	 *            used func.
	 * @param resultFileName
	 *            resultFileName in order to save file.
	 * @param fileName
	 *            fileName
	 */
	public GraphGHTP(int graphSize, ArrayList<Integer[]> edges, ArrayList<Double> edgeCosts, double[] c, int s, int g,
			double B, boolean singleNodeInitial, int[] trueSubGraph, Function func, boolean isNonTransPortation, int timestamp, ArrayRealVector X) {
		this.edges = edges;
		this.graphSize = graphSize;
		this.edgeCosts = edgeCosts;
		this.c = c;
		this.s = s;
		this.g = g;
		this.B = B;
		this.singleNodeInitial = singleNodeInitial;
		this.trueSubGraph = trueSubGraph;
		this.function = func;
		this.isNonTransportation = isNonTransPortation;
		this.timestamp = timestamp;
		this.x = X;
		if (checkInput()) {
			this.x = run(); // run the algorithm
		} else {
			this.x = null;
			System.out.println("input parameter is invalid.");
			System.exit(0);
		}
	}

	private ArrayRealVector run() {
		// TODO Auto-generated method stub
		long start = System.nanoTime();
		ArrayRealVector xi = null;
		if(this.timestamp == 0){
			if(singleNodeInitial){
				xi = this.initializeX_RandomSingleNode();
			}else{
				xi = this.initializeXiMaximumCC(this.isNonTransportation);
			}
		}else{
			xi = this.x;
		}
		
		double[] gradient = this.function.getGradient(xi.toArray());
		gradient = this.normalizegradient(gradient, xi.toArray());
		HeadApprox pcfshead = new HeadApprox(edges, edgeCosts, gradient, s, g, B, trueSubGraph);
		ArrayList<Integer> omega = pcfshead.bestForest.nodesInF;
		ArrayRealVector proj = this.projection(gradient, omega);
		ArrayRealVector v = xi.subtract(proj.mapMultiply(eta));
		ArrayList<Integer> S = this.supp(v);
		double[] b = function.getArgMaxFx(S);
		TailApprox pcfstail = new TailApprox(edges, edgeCosts, b, s, g, B, trueSubGraph);
		xi = this.projection(b, pcfstail.bestForest.nodesInF);
		xi = this.normalize(xi);
		resultNodesTail = new HashSet<Integer>(pcfstail.bestForest.nodesInF);
		this.funcValueTail = getFunctionTailVal(resultNodesTail);
		
		long end = System.nanoTime();
		this.runTime = (end - start)/1e9;
		return xi;
	}

	private ArrayRealVector normalize(ArrayRealVector xi) {
		// TODO Auto-generated method stub
		double[] x0 = null;
		for(int i = 0; i < xi.getDimension(); i++){
			if(xi.getEntry(i) < 0.0D){
				x0 = ArrayUtils.add(x0, 0.0D);
			}else if(xi.getEntry(i) > 0.0D){
				x0 = ArrayUtils.add(x0, 1.0D);
			}else{
				x0 = ArrayUtils.add(x0, xi.getEntry(i));
			}
		}
		
		return new ArrayRealVector(x0);
	}

	private double getFunctionTailVal(HashSet<Integer> nodes) {
		// TODO Auto-generated method stub
		double[] tmp = new double[this.graphSize];
		Arrays.fill(tmp, 0.0D);
		for(Integer node: nodes){
			tmp[node] = 1.0D;
		}
		
		return this.function.getFuncValue(tmp);
	}

	private ArrayList<Integer> supp(ArrayRealVector v) {
		// TODO Auto-generated method stub
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for(int i = 0; i < v.getDimension(); i++){
			if(v.getEntry(i) > 0.0D)
				nodes.add(i);	//Add the node corresponds to which v > 0.0
		}
		
		return nodes;
	}

	private ArrayRealVector projection(double[] gradient, ArrayList<Integer> omega) {
		// TODO Auto-generated method stub
		
		double[] result = Arrays.copyOf(gradient, gradient.length);
		if(omega == null){return new ArrayRealVector(gradient);}
		
		for(int i = 0; i < gradient.length; i++){
			if(!omega.contains(i)){
				result[i] = 0.0D;
			}
		}
		return new ArrayRealVector(result);
	}

	private double[] normalizegradient(double[] gradient, double[] xi) {
		// TODO Auto-generated method stub
		double[] grad = null;
		for(int k = 0; k < gradient.length; k++){
			if(gradient[k] < 0.0D && xi[k] == 0.0D)
				grad = ArrayUtils.add(grad, 0.0D);
			else if(gradient[k] > 1.0D && xi[k] == 1.0D)
				grad = ArrayUtils.add(grad, 0.0D);
			else
				grad = ArrayUtils.add(grad, gradient[k]);
		}
		return grad;
	}

	private boolean checkInput() {
		// TODO Auto-generated method stub
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		for(Integer[] e: this.edges){
			nodes.add(e[0]);
			nodes.add(e[1]);
		}
		if(nodes.size()!=this.graphSize){
			return false;
		}
		ArrayList<ArrayList<Integer>> adj = new ArrayList<ArrayList<Integer>>();
		int i = 0;
		while(i < this.graphSize){
			adj.add(new ArrayList<Integer>());
			i++;
		}
		for (Integer[] edge : this.edges) {
			adj.get(edge[0]).add(edge[1]);
			adj.get(edge[1]).add(edge[0]);
		}
		cc = new ConnectedComponents(adj);
		boolean b = cc.checkConnectivity();
		return b;
	}
	
	private ArrayRealVector initializeX_RandomSingleNode() {
		int[] abnormalNodes = null;
		double mean = StatUtils.mean(c);
		double std = Math.sqrt(StatUtils.variance(c));
		for (int i = 0; i < graphSize; i++) {
			if (c[i] >= mean + 2.0D * std) {
				abnormalNodes = ArrayUtils.add(abnormalNodes, i);
			}
		}
		int index = new Random().nextInt(abnormalNodes.length);
		double[] x0 = new double[graphSize];
		Arrays.fill(x0, 0.0D);
		x0[abnormalNodes[index]] = 1.0D;
		return new ArrayRealVector(x0);
	}
	
	private ArrayRealVector initializeXiMaximumCC(boolean isNonTransPortation) {
		int[] abnormalNodes = null;
		double mean = StatUtils.mean(c);
		double std = Math.sqrt(StatUtils.variance(c));
		if (isNonTransPortation) {
			for (int i = 0; i < graphSize; i++) {
				/** TODO to make sure mean + 2* std is for whole vector */
				if (Math.abs(c[i]) >= mean + 2.0D * std) {
					abnormalNodes = ArrayUtils.add(abnormalNodes, i);
				}
			}
		} else {
			for (int i = 0; i < graphSize; i++) {
				/** TODO to make sure mean + 2* std is for whole vector */
				if (Math.abs(c[i]) <= mean - 2.0D * std) {
					abnormalNodes = ArrayUtils.add(abnormalNodes, i);
				}
			}
		}
		if (abnormalNodes == null) {
			System.out.println("warning: the initial abnormal nodes is null ...");
			int maxIndex = 0;
			double maximalVal = -Double.MAX_VALUE;
			for (int i = 0; i < c.length; i++) {
				if (c[i] > maximalVal) {
					maximalVal = c[i];
					maxIndex = i;
				}
			}
			abnormalNodes = new int[] { maxIndex };
		}
		cc.computeCCSubGraph(abnormalNodes);
		int[] largestCC = cc.findLargestConnectedComponet(abnormalNodes);
		double[] x0 = new double[this.c.length];
		Arrays.fill(x0, 0.0D);
		for (int i = 0; i < largestCC.length; i++) {
			x0[largestCC[i]] = 1.0D;
		}
		if (verboseLevel > 0) {
			System.out.println("size of largestCC: " + largestCC.length);
			PreRec preRec = new PreRec(largestCC, trueSubGraph);
			System.out.println(preRec.toString());
		}
		return new ArrayRealVector(x0);
	}
}
