package org.freedom.cluster;

import org.freedom.util.StringUtil;

import java.util.*;

/**
 * Created by chaolin on 2017/4/20.
 */
public class Cluster {
    private Cluster left;
    private Cluster right;
    private List<AiaProject> aiaProjects=new ArrayList<>();
    private double distance;
    private Map<String,Integer> titles;
    private Map<String,Double> labels;
    public Cluster(Cluster left, Cluster right) {
        super();
        this.left = left;
        this.right = right;
        aiaProjects.addAll(left.getAiaProjects());
        aiaProjects.addAll(right.getAiaProjects());
    }
    public Cluster(AiaProject aia) {
        super();
        left=null;
        right=null;
        aiaProjects.add(aia);
    }

    public void generateLabel(){

        Map<String,Double> clusterVector=new HashMap<>();
        for(int i=0;i<aiaProjects.size();i++){
            Map<String,Double> vector=aiaProjects.get(i).getVector();
            for(String mutation : vector.keySet()){
                if(clusterVector.get(mutation)==null){
                    clusterVector.put(mutation, vector.get(mutation));
                }else{
                    clusterVector.put(mutation,clusterVector.get(mutation)+vector.get(mutation));
                }
            }
        }
        labels=sortMapByValue(clusterVector);
    }
    public void generateTitle(){
        Map<String,Integer> map=new HashMap<String,Integer>();
        for(int i=0;i<aiaProjects.size();i++){
            addMap(map, StringUtil.segmentWord(aiaProjects.get(i).getTitle()));
        }
        titles=sortMapByValue(map);
    }

    private void addMap(Map<String,Integer> map,Map<String,Integer> other){
        for(Map.Entry<String, Integer> entry:other.entrySet()){
            if(map.containsKey(entry.getKey())){
                map.put(entry.getKey(), entry.getValue()+map.get(entry.getKey()));
            }else{
                map.put(entry.getKey(), entry.getValue());
            }
        }
    }

    private <K,V extends Comparable<V>> Map<K,V> sortMapByValue(Map<K,V> map){
        List<Map.Entry<K,V>> list=new ArrayList<>(map.entrySet());
        Collections.sort(list,new Comparator<Map.Entry<K, V>>() {
            @Override
            public int compare(java.util.Map.Entry<K, V> o1, java.util.Map.Entry<K, V> o2) {
                // TODO Auto-generated method stub
                return o2.getValue().compareTo(o1.getValue());
            }

        });
        Map<K,V> sortMap=new LinkedHashMap<>();
        for(int i=0;i<list.size();i++){
            sortMap.put(list.get(i).getKey(), list.get(i).getValue());
        }
        return sortMap;
    }

    public String[] getFirstLabel(int num){
        if(num<=0) return null;
        if(num>getLabels().entrySet().size()){
            num=getLabels().entrySet().size();
        }
        String[] strs=new String[num];
        int i =0;
        for(String str:getLabels().keySet()){
            if(i>=num){
                break;
            }
            strs[i]=str;
            i++;
        }
        return strs;
    }

    public String[] getFirstTitle(int num){
        if(num<=0) return null;
        if(num>getTitles().entrySet().size()){
            num=getTitles().entrySet().size();
        }
        String[] strs=new String[num];
        int i =0;
        for(String str:getTitles().keySet()){
            if(i>=num){
                break;
            }
            strs[i]=str;
            i++;
        }
        return strs;
    }

    public static List<AiaProject> inOrder(Cluster c){
        List<AiaProject> list=new ArrayList<>();
        if(c.getAiaProjects().size()==1){
            list.add(c.getAiaProjects().get(0));
        }else if(c.getAiaProjects().size()>1){
            list.addAll(inOrder(c.getLeft()));
            list.addAll(inOrder(c.getRight()));
        }
        return list;
    }


    public Cluster getLeft() {
        return left;
    }
    public Cluster getRight() {
        return right;
    }

    public List<AiaProject> getAiaProjects() {
        return aiaProjects;
    }
    public double getDistance() {
        return distance;
    }
    public void setDistance(double distance) {
        this.distance = distance;
    }
    public Map<String, Integer> getTitles() {
        if(titles==null){
            generateTitle();
        }
        return titles;
    }
    public void setTitles(Map<String, Integer> titles) {
        if(titles==null){
            generateTitle();
        }
        this.titles = titles;
    }
    public Map<String, Double> getLabels() {
        if(labels==null){
            generateLabel();
        }
        return labels;
    }
    public void setLabels(Map<String, Double> labels) {
        this.labels = labels;
    }
    public String toString(){
        StringBuilder sb=new StringBuilder();
        if(left==null&&right==null){
            sb.append(aiaProjects.get(0).getName()+"-"+aiaProjects.get(0).getTitle());
        }else{
            sb.append("[");
            sb.append(left.toString());
            sb.append(",");
            sb.append(right.toString());
            sb.append("]");
        }
        return sb.toString();
    }
}
