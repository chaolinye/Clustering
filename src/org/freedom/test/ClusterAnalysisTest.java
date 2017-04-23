package org.freedom.test;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.List;

import org.freedom.cluster.Cluster;
import org.freedom.cluster.Clustering;
import org.freedom.cluster.NestestClustering;

public class ClusterAnalysisTest {
	public static void main(String[] args) {
		Clustering ca=new NestestClustering("D:\\test\\standard","D:\\test\\aiaTitle.properties");
		List<Cluster> result=ca.startAnalysisByThreshold(20);
		System.out.println("bad aia: "+ca.getBadAia());
		System.out.println("the number of cluster is "+result.size());
		System.out.println("the result of cluster");
		for(Cluster cluster: result){
			System.out.println(cluster);
		}
	}
}
