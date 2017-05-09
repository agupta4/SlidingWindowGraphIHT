package edu.albany.cs.TestGHTP;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.base.APDMInputFormat;
import edu.albany.cs.base.Constants;
import edu.albany.cs.scoreFuncs.FuncType;
import edu.albany.cs.scoreFuncs.ScoreFuncFactory;
import edu.cs.albany.OnlineGraphGHTP.GraphGHTP;

public class TestGHTP {
	private int numofthreads;
	private final String resultFileName = Constants.BWSNOutputFolder + "graph_GHTP_BWSN_Result.txt";
	private int verboseLevel = 0;
	public TestGHTP(int numofthreads) {
		super();
		this.numofthreads = numofthreads;
		this.run();
	}
	
	private void run() {
		// TODO Auto-generated method stub
		ExecutorService es = Executors.newFixedThreadPool(this.numofthreads);
		//Iterating through each file in data folder
		for(final File file: new File(Constants.BWSNDataFolder).listFiles()){
			final APDMInputFormat apdm = new APDMInputFormat(file);
			
			final ArrayList<Integer[]> edges= apdm.data.intEdges;
			final ArrayList<Double> edgecosts = apdm.data.identityEdgeCosts;
			final int graphsize = apdm.data.numNodes;
			final double[][] matrix = apdm.data.matrix;
			final int T = Integer.parseInt((file.getName()).split("_")[1].split(".txt")[0]);
			es.execute(new Thread(){
				public void run(){
					//Initializing Sparsity
					ArrayList<ArrayRealVector> X = new ArrayList<ArrayRealVector>();
					ArrayList<Double> bestFuncValue = new ArrayList<Double>();
					ArrayList<GraphGHTP> bestGraph = new ArrayList<GraphGHTP>();
					for(int t = 0; t < T; t++){
						
						double[] c = this.getCounts(matrix, t, graphsize);
						double[] b = new double[apdm.data.numNodes];
						Arrays.fill(b, 1.0D); 	//The default value for b would be 1.0D
						Function func = ScoreFuncFactory.getFunc(FuncType.EMS, b, c);
						
						if(verboseLevel == 0){
							System.out.println("---------");
							System.out.println("processing file: " + file.getName() + "at timestamp " + t +" Func: " + FuncType.EMS);
						}
						int[] S = null;
						for(int i = 10; i <= 100; i++){
							S = ArrayUtils.add(S, graphsize*(i/100));
						}
						double bestFuncV = -Double.MAX_VALUE;
						GraphGHTP bestghtp = null;
						ArrayRealVector Xi = null;
						if(t == 0){
							double[]  arr = new double[graphsize];
							Arrays.fill(arr, 0.0D);
							Xi = new ArrayRealVector(arr);
						}else{
							Xi = X.get(t-1);
						}
						for(int s: S){
							int g = 1;
							double B = s - 1 + 0.0D;
							GraphGHTP ghtp= new GraphGHTP(graphsize, edges, edgecosts, c, s, g, B, true, apdm.data.trueSubGraphNodes, func, true, t, Xi);
							if(bestFuncV < ghtp.funcValueTail){
								bestFuncV = ghtp.funcValueTail;
								bestghtp = ghtp;
								Xi = ghtp.x;
							}
							
						}
						X.add(Xi);	//Best X corresponding to each interval
						bestFuncValue.add(bestFuncV);	//Best Function value corresponding to best each interval
						bestGraph.add(bestghtp);	//Best Graph corresponding to each interval
					}
					
				}

				private double[] getCounts(double[][] matrix, int t, int graphsize) {
					// TODO Auto-generated method stub
					double[] c = new double[graphsize];
					
					for(int x = 0; x < graphsize; x++){
						c[x] = matrix[x][t];
					}
					
					return c;
				}
			});
		}
	}
	/**
	 * Will Evaluate x*
	 */
	
	private void EvaluateOptimalX() {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args){
		TestGHTP ghtp = new TestGHTP(1);
		
		ghtp.EvaluateOptimalX();
		
	
	}

	
}
