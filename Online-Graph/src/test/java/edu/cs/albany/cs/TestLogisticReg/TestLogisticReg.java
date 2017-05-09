package edu.cs.albany.cs.TestLogisticReg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.math3.linear.ArrayRealVector;

import edu.albany.cs.Charts.Chart;
import edu.albany.cs.Interface.Function;
import edu.albany.cs.OnlineGraphIHT.GraphIHT;
import edu.albany.cs.base.APDMInputFormat;
import edu.albany.cs.base.Constants;
import edu.albany.cs.base.PreRec;
import edu.albany.cs.scoreFuncs.FuncType;
import edu.albany.cs.scoreFuncs.ScoreFuncFactory;

public class TestLogisticReg {
	private int numofThreads;
	private final String resultFile = Constants.BWSNOutputFolder + "graph_IHT_BWSN_Result_LR-false.txt";
	private int verboseLevel = 0;
	public TestLogisticReg(int numofThreads) {
		// TODO Auto-generated constructor stub
		System.out.println("Number of files in folder:" +new File(Constants.BWSNLRDataFolder).listFiles().length);
		this.numofThreads = numofThreads;
		this.run();
	}

	private void run() {
		// TODO Auto-generated method stub
		ExecutorService es = Executors.newFixedThreadPool(this.numofThreads);
		//final int count = 0;
		for(final File file: new File(Constants.BWSNLRDataFolder).listFiles()){
			final APDMInputFormat apdm = new APDMInputFormat(file);
			final ArrayList<Double> edgeCost = apdm.data.identityEdgeCosts;
			final ArrayList<Integer[]> edges = apdm.data.intEdges;
			final int graphsize = apdm.data.numNodes;
			final int[] truesubgraph = apdm.data.trueSubGraphNodes;
			final int[] Yt = apdm.data.yt;
			final int[] Bt = apdm.data.bt;
			final int T = Integer.parseInt(file.getName().split("_")[1].split(".txt")[0]);
			//for(int nodes: truesubgraph){System.out.print(nodes+ "\t");}
			es.execute(new Thread(){
				
				@SuppressWarnings("resource")
				public void run(){
					/** ResultSet */
					ArrayList<ArrayRealVector> X = new ArrayList<ArrayRealVector>();
					ArrayList<Double> funcValue = new ArrayList<Double>();
					ArrayList<GraphIHT> bestGraphs = new ArrayList<GraphIHT>();
					double[][] matrix = apdm.data.matrix;
					double[] b = new double[graphsize];
					ArrayList<PreRec> pr = new ArrayList<PreRec>();
					Arrays.fill(b, 1.0D);
					int count = 0;
					//for(int t m = 0; m < 500 ;m++){
					for(int t = 0; t < T; t++){
						//bt and yt will also get generated from apdm file where yt is  
						//label signifying anomalous subgraph presence and bt will be a 
						//constant decided on the basis of anomalous graph presence
						
						double[] c = getCounts(matrix, t, graphsize);
						int yt= Yt[t]; double bt = Bt[t];	//This needs to be handled inside simulation data
						Function func = ScoreFuncFactory.getFunc(FuncType.LR, b, c, yt, bt);
						
						int[] S = new int[]{(int)(0.45*graphsize)};
						
						ArrayRealVector Xi = null;
						if(count++ == 0){
							double[] arr = new double[graphsize];
							Arrays.fill(arr, 0.0D);
							Xi = new ArrayRealVector(arr);
						}else{
							Xi = X.get(X.size()-1);
						}
						double bestfuncValue = -Double.MAX_VALUE;
						ArrayList<ArrayRealVector> bestXi = new ArrayList<ArrayRealVector>(); 
						GraphIHT graphIHT = null;
						//System.out.println();
						for(int s : S){
							double B = s - 1 + 0.0D;
							GraphIHT IHT = new GraphIHT(graphsize, edges, edgeCost, s, 1, B, false, c, func, truesubgraph, t, Xi);
							Xi = IHT.x;
							if(bestfuncValue < IHT.funcValueTail){
								bestfuncValue = IHT.funcValueTail;
								graphIHT = IHT;
								if(verboseLevel == 0){
									bestXi.add(Xi);
								}
							}
							
						}
						System.out.println(S);
						System.out.println(new PreRec(graphIHT.resultNodesTail, truesubgraph).toString());
						pr.add(new PreRec(graphIHT.resultNodesTail, truesubgraph));
						X.add(bestXi.get(bestXi.size()-1));
						funcValue.add(bestfuncValue);
						bestGraphs.add(graphIHT);
					
					}
					//Chart chart = new Chart(pr, X.size(), funcValue);
					//chart.PlotChart();
					try{
						
						FileWriter FW = new FileWriter(resultFile, false);
						
						for (int i = 0; i < T; i++) {
							
							PreRec PR = new PreRec(bestGraphs.get(i).resultNodesTail, truesubgraph);

							FW.write(funcValue.get(i) + " "
									+PR.pre + " " + PR.rec + " "
									+ PR.fmeasure);
							FW.write(
									"\n");

						}
						FW.close();
					}catch(IOException e){
						e.printStackTrace();
					}
					
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
		/*if(args == null || args.length == 0){
			new TestLogisticReg(1);
		}else{
			new TestLogisticReg(Integer.parseInt(args[0]));
		}	*/
		new TestLogisticReg(1);
	}
	
	
}
