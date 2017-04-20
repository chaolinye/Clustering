package org.freedom.cluster;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chaolin on 2017/4/20.
 */
public abstract class HierarchicalClustering extends Clustering{

    public HierarchicalClustering(String path,String titleFile) {
        super(path,titleFile);
        // TODO Auto-generated constructor stub
    }

    public HierarchicalClustering(List<AiaProject> aiaProjects) {
        super(aiaProjects);
        // TODO Auto-generated constructor stub
    }
    public HierarchicalClustering(InputStream file) {
        super(file);
        // TODO Auto-generated constructor stub
    }

    /**
     * 初始化簇
     * @param aiaProjects
     * @return
     */
    protected List<Cluster> initialCluster(List<AiaProject> aiaProjects) {
        List<Cluster> originalClusters = new ArrayList<Cluster>();
        for (int i = 0; i < aiaProjects.size(); i++) {
            AiaProject aia = aiaProjects.get(i);
            if (aia == null || !aia.isValid()) {
                badAia.add(aia);
            } else {
                Cluster cluster = new Cluster(aiaProjects.get(i));
                originalClusters.add(cluster);
            }
        }
        return originalClusters;
    }
    /**
     * 求解两个簇之间的距离
     *
     * @param a
     * @param b
     * @return
     */
    protected abstract double getClusterDistance(Cluster a, Cluster b);

    /**
     * 合并簇
     *
     * @param clusters
     * @param mergeIndexA
     * @param mergeIndexB
     */
    protected Cluster mergeCluster(List<Cluster> clusters, int mergeIndexA, int mergeIndexB) {
        if (mergeIndexA != mergeIndexB) {
            if (mergeIndexA > mergeIndexB) {
                int tmp = mergeIndexA;
                mergeIndexA = mergeIndexB;
                mergeIndexB = tmp;
            }
            Cluster newCluster = new Cluster(clusters.get(mergeIndexA), clusters.get(mergeIndexB));
            clusters.remove(mergeIndexB);
            clusters.remove(mergeIndexA);
            clusters.add(mergeIndexA, newCluster);
            return newCluster;
        }
        return null;
    }

    /**
     * 层次聚类算法
     * @param threshold
     * @return
     */
    @Override
    public List<Cluster> startAnalysis(double threshold) {
        System.out.println("start cluster");
        long startTime = System.currentTimeMillis();
        List<Cluster> clusters = initialCluster(aiaProjects);
        while (clusters.size() > 1) {
            double min = Double.MAX_VALUE;
            int mergeIndexA = 0;
            int mergeIndexB = 0;
            for (int i = 0; i < clusters.size() - 1; i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double tempDis = getClusterDistance(clusters.get(i), clusters.get(j));
                    if (tempDis < min) {
                        min = tempDis;
                        mergeIndexA = i;
                        mergeIndexB = j;
                    }
                }
            }
            if (min > threshold)
                break;
            Cluster newCluster=mergeCluster(clusters, mergeIndexA, mergeIndexB);
            newCluster.setDistance(min);
        }
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return clusters;
    }

    public List<Cluster> agglomerateClustering(List<Cluster> clusters,double threshold){
        System.out.println("start cluster");
        long startTime = System.currentTimeMillis();
        List<Cluster> newClusters=new ArrayList<>(clusters);
        while (newClusters.size() > 1) {
            double min = Double.MAX_VALUE;
            int mergeIndexA = 0;
            int mergeIndexB = 0;
            for (int i = 0; i < newClusters.size() - 1; i++) {
                for (int j = i + 1; j < newClusters.size(); j++) {
                    double tempDis = getClusterDistance(newClusters.get(i), newClusters.get(j));
                    if (tempDis < min) {
                        min = tempDis;
                        mergeIndexA = i;
                        mergeIndexB = j;
                    }
                }
            }
            if (min > threshold)
                break;
            Cluster newCluster=mergeCluster(newClusters, mergeIndexA, mergeIndexB);
            newCluster.setDistance(min);
        }
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return newClusters;
    }

    public static List<Cluster> divisiveClustering(List<Cluster> clusters,double threshold){
        System.out.println("start cluster");
        long startTime = System.currentTimeMillis();
        List<Cluster> newClusters=new ArrayList<>();
        for(int i=0;i<clusters.size();i++){
            newClusters.addAll(divide(clusters.get(i),threshold));
        }
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return newClusters;
    }
    public static List<Cluster> divide(Cluster cluster,double threshold){
        List<Cluster> clusters=new ArrayList<>();
        if(cluster.getLeft()==null){
            clusters.add(cluster);
        }else if(cluster.getDistance()<=threshold){
            clusters.add(cluster);
        }else {
            clusters.addAll(divide(cluster.getLeft(),threshold));
            clusters.addAll(divide(cluster.getRight(),threshold));
        }
        return clusters;
    }

    @Override
    public List<Cluster> startAnalysisByClusterNumber(int num) {
        // TODO Auto-generated method stub
        System.out.println("start cluster by cluster number");
        long startTime = System.currentTimeMillis();
        List<Cluster> clusters = initialCluster(aiaProjects);
        while (clusters.size() > num) {
            double min = Double.MAX_VALUE;
            int mergeIndexA = 0;
            int mergeIndexB = 0;
            for (int i = 0; i < clusters.size() - 1; i++) {
                for (int j = i + 1; j < clusters.size(); j++) {
                    double tempDis = getClusterDistance(clusters.get(i), clusters.get(j));
                    if (tempDis < min) {
                        min = tempDis;
                        mergeIndexA = i;
                        mergeIndexB = j;
                    }
                }
            }
            Cluster newCluster=mergeCluster(clusters, mergeIndexA, mergeIndexB);
            newCluster.setDistance(min);
        }
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return clusters;
    }
}