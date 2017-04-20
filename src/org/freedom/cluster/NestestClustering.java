package org.freedom.cluster;

import java.util.List;

/**
 * Created by chaolin on 2017/4/20.
 * 最近距离层次聚类
 */
public class NestestClustering extends HierarchicalClustering{

    public NestestClustering(String path,String titleFile) {
        super(path,titleFile);
        // TODO Auto-generated constructor stub
    }

    public NestestClustering(List<AiaProject> aiaProjects) {
        super(aiaProjects);
        // TODO Auto-generated constructor stub
    }

    /**
     * 求解两个簇之间的距离,选择最近的两个项目之间的距离
     *
     * @param a
     * @param b
     * @return
     */
    protected double getClusterDistance(Cluster a, Cluster b) {
        double min = Double.MAX_VALUE;
        List<AiaProject> aiaProjectsA = a.getAiaProjects();
        List<AiaProject> aiaProjectsB = b.getAiaProjects();
        for (int i = 0; i < aiaProjectsA.size(); i++) {
            for (int j = 0; j < aiaProjectsB.size(); j++) {
                double temp = getAiaDistance(aiaProjectsA.get(i), aiaProjectsB.get(j));
                if (temp < min) {
                    min = temp;
                }
            }
        }
        return min;
    }
}

