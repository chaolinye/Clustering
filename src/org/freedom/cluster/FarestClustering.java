package org.freedom.cluster;

import java.io.InputStream;
import java.util.List;

public class FarestClustering extends HierarchicalClustering{
	public FarestClustering(List<AiaProject> aiaProjects) {
		super(aiaProjects);
		// TODO Auto-generated constructor stub
	}
	public FarestClustering(String path,String titleFile) {
		super(path,titleFile);
		// TODO Auto-generated constructor stub
	}

	public FarestClustering(InputStream file) {
		super(file);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 求解两个簇之间的距离,选择最远的两个项目之间的距离
	 */
	@Override
	protected double getClusterDistance(Cluster a, Cluster b) {
		// TODO Auto-generated method stub
		double max = Double.MIN_VALUE;
		List<AiaProject> aiaProjectsA = a.getAiaProjects();
		List<AiaProject> aiaProjectsB = b.getAiaProjects();
		for (int i = 0; i < aiaProjectsA.size(); i++) {
			for (int j = 0; j < aiaProjectsB.size(); j++) {
				double temp = getAiaDistance(aiaProjectsA.get(i), aiaProjectsB.get(j));
				if (temp > max) {
					max = temp;
				}
			}
		} 
		return max;
	}
}
