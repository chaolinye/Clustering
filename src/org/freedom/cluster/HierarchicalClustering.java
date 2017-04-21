package org.freedom.cluster;

import java.io.InputStream;
import java.util.*;

/**
 * Created by chaolin on 2017/4/20.
 * 层次聚类
 */
public abstract class HierarchicalClustering extends Clustering{

    private Map<Cluster,Map<Cluster,Double>> clusterDisMap=new HashMap<>();

    private LinkedList<Cluster> stack = new LinkedList<>();

//    class Stack<T>{
//        private LinkedList<T> list;
//        public Stack(){
//            list=new LinkedList<>();
//        }
//        public void push(T e){
//            list.addFirst(e);
//        }
//        public T pop(){
//            return list.poll();
//        }
//        public T top(){
//            return list.peek();
//        }
//        public T get(int i){
//            return list.get(i);
//        }
//    }

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
        for(int i=0;i<originalClusters.size();i++){
            Map<Cluster,Double> map = new HashMap<>();
            for(int j=i+1;j<originalClusters.size();j++){
                double dis=calculateClusterDistance(originalClusters.get(i),originalClusters.get(j));
                map.put(originalClusters.get(j),dis);
            }
            clusterDisMap.put(originalClusters.get(i),map);
        }
        return originalClusters;
    }
    /**
     * 计算两个簇之间的距离
     *
     * @param a
     * @param b
     * @return
     */
    protected abstract double calculateClusterDistance(Cluster a, Cluster b);

    private double getClusterDistance(Cluster a,Cluster b){
        Double dis=clusterDisMap.get(a).get(b);
        if(dis==null){
            dis=clusterDisMap.get(b).get(a);
        }
        if(dis==null){
            dis=calculateClusterDistance(a,b);
            clusterDisMap.get(a).put(b,dis);
        }
        return dis;
    }



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
            // add
            clusterDisMap.remove(clusters.get(mergeIndexA));
            clusterDisMap.remove(clusters.get(mergeIndexB));
            clusters.remove(mergeIndexB);
            clusters.remove(mergeIndexA);
            clusters.add(mergeIndexA, newCluster);
            // add
            for(int i=0;i<mergeIndexA;i++){
                clusterDisMap.get(clusters.get(i)).put(newCluster,calculateClusterDistance(clusters.get(i),newCluster));
            }
            Map<Cluster,Double> map=new HashMap<>();
            for(int i=mergeIndexA+1;i<clusters.size();i++){
                map.put(clusters.get(i),calculateClusterDistance(newCluster,clusters.get(i)));
            }
            clusterDisMap.put(newCluster,map);
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
        Random random=new Random();
        List<Cluster> resultClusters=new ArrayList<>();

        while (clusters.size() + stack.size() > 1) {
            if(stack.size()==0){
                int index=random.nextInt(clusters.size());
                stack.push(clusters.get(index));
                clusters.remove(index);
            }
            Cluster top=stack.peek();
            double min = Double.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < clusters.size(); i++) {
                double temp=getClusterDistance(top,clusters.get(i));
                if (temp < min) {
                    min = temp;
                    minIndex = i;
                }
            }
            double dis=Double.MAX_VALUE;
            if(stack.size()>1){
                 dis=getClusterDistance(top,stack.get(1));
            }
            if (min < dis ){
                stack.push(clusters.get(minIndex));
                clusters.remove(minIndex);
            }else {
                stack.poll();
                Cluster next=stack.poll();
                if(dis>threshold){
                    resultClusters.add(top);
                    resultClusters.add(next);
                }else{
                    Cluster newCluster=new Cluster(next,top);

                    newCluster.setDistance(dis);
                    Map<Cluster,Double> map=new HashMap<>();
                    for(int i=0;i<clusters.size();i++){
                        map.put(clusters.get(i),calculateClusterDistance(newCluster,clusters.get(i)));
                    }
                    if(stack.size()>0){
                        map.put(stack.peek(),calculateClusterDistance(newCluster,stack.peek()));
                    }
                    clusterDisMap.put(newCluster,map);
                    clusterDisMap.remove(top);
                    clusterDisMap.remove(next);
                    stack.push(newCluster);
                }

            }
        }
        if(stack.size()>0){
            resultClusters.addAll(stack);
        }
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return resultClusters;
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
    public static List<Cluster> divideByNumber(Cluster cluster,int num){
        List<Cluster> result=new ArrayList<>();
        if(cluster.getAiaProjects().size()==1){
            result.add(cluster);
            System.out.println("FSDFwejfj;w");
            return result;
        }
        PriorityQueue<Cluster> queue = new PriorityQueue<>(new Comparator<Cluster>() {
            @Override
            public int compare(Cluster o1, Cluster o2) {
                return o2.getDistance()>o1.getDistance()?1:-1;
            }
        });
        queue.offer(cluster);
        while(result.size()+queue.size()<num&&queue.size()!=0){
            Cluster c=queue.poll();
            if(c.getLeft().getAiaProjects().size()==1){
                result.add(c.getLeft());
            }else{
                queue.offer(c.getLeft());
            }
            if(c.getRight().getAiaProjects().size()==1){
                result.add(c.getRight());
            }else{
                queue.offer(c.getRight());
            }
        }
        result.addAll(queue);
        return result;
    }

    @Override
    public List<Cluster> startAnalysisByClusterNumber(int num) {
        // TODO Auto-generated method stub
        System.out.println("start cluster by cluster number");
        long startTime = System.currentTimeMillis();
        List<Cluster> clusters = initialCluster(aiaProjects);
        Random random=new Random();

        while (clusters.size() + stack.size() > 1) {
            if(stack.size()==0){
                int index=random.nextInt(clusters.size());
                stack.push(clusters.get(index));
                clusters.remove(index);
            }
            Cluster top=stack.peek();
            double min = Double.MAX_VALUE;
            int minIndex = -1;
            for (int i = 0; i < clusters.size(); i++) {
                double temp=getClusterDistance(top,clusters.get(i));
                if (temp < min) {
                    min = temp;
                    minIndex = i;
                }
            }
            double dis=Double.MAX_VALUE;
            if(stack.size()>1){
                dis=getClusterDistance(top,stack.get(1));
            }
            if (min < dis ){
                stack.push(clusters.get(minIndex));
                clusters.remove(minIndex);
            }else {
                stack.poll();
                Cluster next=stack.poll();
                Cluster newCluster=new Cluster(next,top);
                newCluster.setDistance(dis);
                Map<Cluster,Double> map=new HashMap<>();
                for(int i=0;i<clusters.size();i++){
                    map.put(clusters.get(i),calculateClusterDistance(newCluster,clusters.get(i)));
                }
                if(stack.size()>0){
                    map.put(stack.peek(),calculateClusterDistance(newCluster,stack.peek()));
                }
                clusterDisMap.put(newCluster,map);
                clusterDisMap.remove(top);
                clusterDisMap.remove(next);
                stack.push(newCluster);
            }
        }
        List<Cluster> resultClusters=divideByNumber(stack.peek(),num);

        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return resultClusters;
    }
}