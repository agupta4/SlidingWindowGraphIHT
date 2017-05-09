package edu.cs.albany.cs.TestLogisticReg;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import edu.albany.cs.Interface.Function;
import edu.albany.cs.OnlineGraphIHT.GraphIHT;
import edu.albany.cs.base.APDMInputFormat;
import edu.albany.cs.base.PreRec;
import edu.albany.cs.scoreFuncs.FuncType;
import edu.albany.cs.scoreFuncs.ScoreFuncFactory;


public class ModifiedLogisticReg {
	private String APDMfile;
	private int verbose = 0;
	public ModifiedLogisticReg(String file){
		this.APDMfile = file;
		this.run();
	}
	
	private void run(){
		File file = new File(this.APDMfile);
		APDMInputFormat apdm = new APDMInputFormat(file);
		//int T = Integer.parseInt(file.getName().split("_")[1].split(".txt")[0]);	//Number of Snaphots
		double[][] weights = apdm.data.matrix;
		int[] bt = apdm.data.bt;
		int[] y = apdm.data.yt;
		ArrayList<Integer[]> edges = apdm.data.intEdges;
		ArrayList<Double> edgeCosts = apdm.data.identityEdgeCosts;
		int[] truesubgrah = apdm.data.trueSubGraphNodes;
		int graphsize = apdm.data.numNodes;
		double[] b = new double[graphsize];
		Arrays.fill(b, 1.0D);
		Function func = ScoreFuncFactory.getFunc(FuncType.LRReg2, b, weights, y, bt);
		//System.out.println(graphsize*0.1);
		int[] S = new int[]{(int) (graphsize * 0.05), (int) (graphsize * 0.1), (int) (graphsize * 0.15), (int) (graphsize * 0.2),(int) (graphsize * 0.25), (int) (graphsize * 0.3), (int) (graphsize * 0.35), (int) (graphsize * 0.4),(int) (graphsize * 0.45), (int) (graphsize * 0.5)};
		
		//for(int i: S){System.out.println(i);}
		double[] c = this.getWeight(weights, 0, graphsize);
		double funcvalue = 0.0;
        
		for(int s: S){
			long start = System.currentTimeMillis();
			double B = s - 1 + 0.0D;
			GraphIHT GraphIHT = new GraphIHT(graphsize, edges, edgeCosts, s, 1, B, true, c, func, truesubgrah, 0, null);
			//ArrayRealVector X = GraphIHT.x;
			funcvalue = GraphIHT.funcValue;
			
			PreRec PR = new PreRec(GraphIHT.resultNodesTail, truesubgrah);
			System.out.println("Sparsity: "+ s);
			System.out.println(PR.toString());
			System.out.println("Function value: "+funcvalue);
			long end = System.currentTimeMillis();
			try{
				FileWriter fw = new FileWriter(new File("./output/BWSN/preRec-LR"),true);
				fw.write(PR.pre + " " + PR.rec+ " " + PR.fmeasure + " " + (end-start)/1e3  + "\n");
				fw.flush();
				fw.close();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		
	}
	public double[] getWeight(double[][] weights, int t, int graphsize){
		double[] c = new double[graphsize];
		
		for(int x = 0; x < graphsize; x++){
			c[x] = weights[x][t];
		}
		
		return c;
	}
	public static void main(String[] args){
		new ModifiedLogisticReg("./data/BWSN/LRtestGraphs/APDM-GridData-100_800_10_20.0_100_0.0-1.txt");
	}
}
