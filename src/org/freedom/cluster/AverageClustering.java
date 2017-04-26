package org.freedom.cluster;

import java.util.List;

/**
 * 平均距离层次聚类
 */
public class AverageClustering extends HierarchicalClustering{

	public AverageClustering(List<AiaProject> aiaProjects) {
		super(aiaProjects);
		// TODO Auto-generated constructor stub
	}

	public AverageClustering(String path, String titleFile) {
		super(path, titleFile);
		// TODO Auto-generated constructor stub
	}

	public AverageClustering(List<AiaProject> aiaProjects,double[][] dis){
		super(aiaProjects,dis);
	}
	
	/**
	 * 求解两个簇之间的距离,选择所有项目之间的平均距离
	 */
	@Override
	protected double calculateClusterDistance(Cluster a, Cluster b) {
		// TODO Auto-generated method stub
		List<AiaProject> aiaProjectsA = a.getAiaProjects();
		List<AiaProject> aiaProjectsB = b.getAiaProjects();
		double sum=0.0;
		for (int i = 0; i < aiaProjectsA.size(); i++) {
			for (int j = 0; j < aiaProjectsB.size(); j++) {
				double temp = getAiaDistance(aiaProjectsA.get(i), aiaProjectsB.get(j));
				sum+=temp;
			}
		} 
		return sum/(aiaProjectsA.size()*aiaProjectsB.size());
	}

}
