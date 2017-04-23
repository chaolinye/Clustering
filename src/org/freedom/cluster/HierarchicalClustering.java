package org.freedom.cluster;

import java.io.InputStream;
import java.util.*;

/**
 * Created by chaolin on 2017/4/20.
 * 层次聚类
 */
public abstract class HierarchicalClustering extends Clustering{
    // 簇距离矩阵
    private Map<Cluster,Map<Cluster,Double>> clusterDisMap=new HashMap<>();
    // 层次聚类最后形成的树
    private Cluster tree;

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
     * 指定距离阈值的层次聚类
     * @param threshold
     * @return
     */
    @Override
    public List<Cluster> startAnalysisByThreshold(double threshold) {
        System.out.println("start cluster by threshold");
        long startTime = System.currentTimeMillis();
        if(tree==null){
            generateTree();
        }
        List<Cluster> resultClusters=divideByThreshold(tree,threshold);
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return resultClusters;
    }

    /**
     * 指定簇个数的层次聚类
     * @param num
     * @return
     */
    @Override
    public List<Cluster> startAnalysisByClusterNumber(int num) {
        System.out.println("start cluster by cluster number");
        long startTime = System.currentTimeMillis();
        if(tree==null){
            generateTree();
        }
        List<Cluster> resultClusters=divideByNumber(tree,num);
        System.out.println("end cluster after " + (System.currentTimeMillis() - startTime) + " ms");
        return resultClusters;
    }

    /**
     * 使用Nearest Neighbor Chain改进的凝聚法层次聚类算法实现
     */
    private void generateTree(){
        List<Cluster> clusters = initialCluster(aiaProjects);
        LinkedList<Cluster> stack = new LinkedList<>();
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
        this.tree=stack.poll();
    }

    /**
     * 初始化簇
     * @param aiaProjects
     * @return
     */
    private List<Cluster> initialCluster(List<AiaProject> aiaProjects) {
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

    /**
     * 从簇距离矩阵中获取两个簇之间的距离
     * @param a
     * @param b
     * @return
     */
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
     * 指定距离阈值划分簇
     * @param cluster
     * @param threshold
     * @return
     */
    public static List<Cluster> divideByThreshold(Cluster cluster,double threshold){
        List<Cluster> clusters=new ArrayList<>();
        if(cluster.getAiaProjects().size()==1){
            clusters.add(cluster);
        }else if(cluster.getDistance()<=threshold){
            clusters.add(cluster);
        }else {
            clusters.addAll(divideByThreshold(cluster.getLeft(),threshold));
            clusters.addAll(divideByThreshold(cluster.getRight(),threshold));
        }
        return clusters;
    }

    /**
     * 指定簇个数划分簇
     * @param cluster
     * @param num
     * @return
     */
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
}