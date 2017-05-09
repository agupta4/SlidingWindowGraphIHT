package edu.albany.cs.TestIHT;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.linear.ArrayRealVector;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.OnlineGraphIHT.GraphIHT;
import edu.albany.cs.base.APDMInputFormat;
import edu.albany.cs.base.Constants;
import edu.albany.cs.base.PreRec;
import edu.albany.cs.scoreFuncs.FuncType;
import edu.albany.cs.scoreFuncs.ScoreFuncFactory;

public class TestIHT {
	
	private int numofThreads;
	private final String resultFile = Constants.BWSNOutputFolder + "graph_IHT_BWSN_preRec.txt";
	private int verboseLevel = 0;
	public TestIHT(int numofThreads){
		this.numofThreads = numofThreads;
		
		this.run(); 	//This method will run for T instances on each file
	}
	
	private void run() {
		// TODO Auto-generated method stub
		ExecutorService es = Executors.newFixedThreadPool(this.numofThreads);
		
		
		for(final File file: new File(Constants.BWSNDataFolder).listFiles()){
			final APDMInputFormat apdm = new APDMInputFormat(file);
			final int T = Integer.parseInt(file.getName().split("_")[1].split(".txt")[0]);
			
			
			final ArrayList<Double> edgeCosts = apdm.data.identityEdgeCosts;
			final ArrayList<Integer[]> edges = apdm.data.intEdges;
			final int graphsize = apdm.data.numNodes;
			final double[][] matrix = apdm.data.matrix;
			es.execute(new Thread(){
				public void run(){
					ArrayList<ArrayRealVector> X = new ArrayList<ArrayRealVector>();
					ArrayList<Double> funcValue = new ArrayList<Double>();
					ArrayList<GraphIHT> bestGraphs = new ArrayList<GraphIHT>();
					
					for(int t = 0; t < T; t++){
						long start = System.currentTimeMillis();
						double[] c = this.getCounts(matrix, t, graphsize);
						double[] b = new double[apdm.data.numNodes];
						int[] truesubgraphs = apdm.data.trueSubGraphNodes;
						//Initialize b for EmsStat
						Arrays.fill(b, 1.0D);
						Function func = ScoreFuncFactory.getFunc(FuncType.EMS, b, c);
						
						if(verboseLevel == 0){
							System.out.println("---------");
							System.out.println("processing file: " + file.getName() + "at timestamp " + t +" Func: " + FuncType.EMS);
						}
						
						int[] S = null;	//Will contain 10%, 20% and so on for k
						for(int k = 20; k <= 50; k+=5){
							double n = graphsize * (k/100.0);
							
							S = ArrayUtils.add(S, (int)n);
						}
						
						System.out.println("Iteration No.: " +t);
						double bestFuncValue = -Double.MAX_VALUE;
						//double[] bestFuncs = null;
						GraphIHT bestGraphIHT = null;
						ArrayRealVector Xi = null;
						if(t==0){
							double[] arr = new double[graphsize];
							Arrays.fill(arr, 0.0D);
							 Xi = new ArrayRealVector(arr);
						}else{
							Xi = X.get(t-1);
						}
						ArrayList<ArrayRealVector> bestXi = new ArrayList<ArrayRealVector>();
						for(int s: S){
							int g = 1;
							double B = s - 1 + 0.0D;
							GraphIHT IHT = new GraphIHT(graphsize, edges, edgeCosts, s, g, B, true, c, func, truesubgraphs, t, Xi);
							Xi = IHT.x;
							if(bestFuncValue < IHT.funcValueTail){
								bestFuncValue = IHT.funcValueTail;
								bestGraphIHT = IHT;
								bestXi.add(Xi);
								if(verboseLevel == 0){
									PreRec PR = new PreRec(IHT.resultNodesTail, truesubgraphs);
									System.out.println(PR.toString());
									System.out.println("Function value: "+bestFuncValue);
								}
							}else{
								if(verboseLevel == 0){
									PreRec PR = new PreRec(IHT.resultNodesTail, truesubgraphs);
									System.out.println(PR.toString());
									System.out.println("Function value: "+IHT.funcValueTail);
								}
							}
							
						}
						X.add(bestXi.get(bestXi.size()-1));	//Best X value should be the last one updated
						funcValue.add(bestFuncValue);	//Best function value should be the last updated one
						bestGraphs.add(bestGraphIHT);	//Best IHT graphs in each time stamp
						
						long end = System.currentTimeMillis();
						/*try{
							FileWriter fw = new FileWriter(new File(Constants.BWSNOutputFolder+"Runningtime.txt"), true);
							fw.write(String.valueOf((end - start)/1e3));
							fw.write("\n");
							fw.flush();
							fw.close();
						}catch(IOException e){
							e.printStackTrace();
						}*/
					}
					System.out.println(X);
					System.out.println(funcValue);
					//Printing out the results
					try {
						FileWriter FW = new FileWriter(new File(resultFile));
						for(int i = 0; i < funcValue.size(); i++){
							PreRec PR = new PreRec(bestGraphs.get(i).resultNodesTail, apdm.data.trueSubGraphNodes);
							FW.write(PR.pre + " " + PR.rec+ " " + PR.fmeasure + " " +funcValue.get(i));
							FW.write("\n");
							
						}
						FW.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
							
					
					System.out.println("------------------------------------------------");
				}

				public double[] getCounts(double[][] matrix, int t, int graphsize) {
					// TODO Auto-generated method stub
					double[] c = new double[graphsize];
					
					for(int x = 0; x < graphsize; x++){
						c[x] = matrix[x][t];
					}
					
					return c;
				}
				
			});
			
		}
		
		es.shutdown();
	}

	public static void main(String[] args){
		TestIHT IHT = new TestIHT(1);
	}

}
