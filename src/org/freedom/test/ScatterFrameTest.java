package org.freedom.test;

import mdsj.MDSJ;
import org.freedom.cluster.*;
import org.freedom.display.ScatterFrame;
import org.freedom.util.FileUtil;
import org.jfree.ui.RefineryUtilities;

import java.io.*;
import java.util.*;

/**
 * Created by chaolin on 2017/4/24.
 */
public class ScatterFrameTest {
    public static void main1(String[] args)throws Exception{
        Clustering ca=new AverageClustering("D:\\test\\standard","D:\\test\\aiaTitle.properties");
        List<Cluster> result=ca.startAnalysisByClusterNumber(200);
        System.out.println("bad aia: "+ca.getBadAia());
        System.out.println("the number of cluster is "+result.size());
        List<AiaProject> aias=new ArrayList<>();
        for(int i=0;i<result.size();i++){
            aias.addAll(result.get(i).getAiaProjects());
        }
        double[][] dis=new double[aias.size()][aias.size()];
        for(int i=0;i<dis.length;i++){
            for(int j=0;j<dis[i].length;j++){
                if(i==j){
                    dis[i][j]=0.0;
                }else{
                    dis[i][j]=ca.getAiaDistance(aias.get(i),aias.get(j));
                }
            }
        }
        PrintStream ps=new PrintStream("D:\\test\\dis.txt");
        PrintStream ps2=new PrintStream("D:\\test\\dis2.txt");
        for(int i=0;i<dis.length;i++){
            for(int j=0;j<dis[i].length;j++){
                ps.printf("%5.2f#",dis[i][j]);
            }
            ps.println();
        }
        double[][] xy=Virualization.classicalMDS(dis);
        for(int i=0;i<xy.length;i++){
            for(int j=0;j<xy.length;j++){
                double d=Math.sqrt(Math.pow(xy[i][0]-xy[j][0],2)+Math.pow(xy[i][1]-xy[j][1],2));
                ps2.printf("%5.2f#",d);
            }
            ps2.println();
        }
        double[][][] data=new double[result.size()][][];
        int k=0;
        for(int i=0;i<result.size();i++){
            int size=result.get(i).getAiaProjects().size();
            double[][] temp=new double[size][];
            for(int j=0;j<size;j++){
                temp[j]=xy[k++];
            }
            data[i]=temp;
        }
        ScatterFrame scatterplotdemo2 = new ScatterFrame("Scatter Plot Demo 2",data);
        scatterplotdemo2.pack();
        RefineryUtilities.centerFrameOnScreen(scatterplotdemo2);
        scatterplotdemo2.setVisible(true);
    }

    public static void main2(String[] args)throws Exception {
        Clustering ca=new AverageClustering("D:\\test\\standard","D:\\test\\aiaTitle.properties");
//        List<Cluster> result=ca.startAnalysisByClusterNumber(200);
        List<Cluster> result=ca.startAnalysisByThreshold(10);
        System.out.println("bad aia: "+ca.getBadAia());
        System.out.println("the number of cluster is "+result.size());
        List<AiaProject> aias=new ArrayList<>();
        for(int i=0;i<result.size();i++){
            aias.addAll(result.get(i).getAiaProjects());
        }
        double[][] dis=new double[aias.size()][aias.size()];
        for(int i=0;i<dis.length;i++){
            for(int j=0;j<dis[i].length;j++){
                if(i==j){
                    dis[i][j]=0.0;
                }else{
                    dis[i][j]=ca.getAiaDistance(aias.get(i),aias.get(j));
                }
            }
        }
        PrintStream ps=new PrintStream("D:\\test\\dis.txt");
        PrintStream ps2=new PrintStream("D:\\test\\dis2.txt");
        for(int i=0;i<dis.length;i++){
            for(int j=0;j<dis[i].length;j++){
                ps.printf("%5.2f#",dis[i][j]);
            }
            ps.println();
        }
        double[][] xy= MDSJ.classicalScaling(dis);
        for(int i=0;i<dis.length;i++){
            for(int j=0;j<dis.length;j++){
                double d=Math.sqrt(Math.pow(xy[0][i]-xy[0][j],2)+Math.pow(xy[1][i]-xy[1][j],2));
                ps2.printf("%5.2f#",d);
            }
            ps2.println();
        }
        double[][] t=new double[dis.length][2];
        for(int i=0;i<dis.length;i++){
            t[i][0]=xy[0][i];
            t[i][1]=xy[1][i];
        }
        xy=t;
        double[][][] data=new double[result.size()][][];
        int k=0;
        for(int i=0;i<result.size();i++){
            int size=result.get(i).getAiaProjects().size();
            double[][] temp=new double[size][];
            for(int j=0;j<size;j++){
                temp[j]=xy[k++];
            }
            data[i]=temp;
        }
        ScatterFrame scatterplotdemo2 = new ScatterFrame("Scatter Plot Demo 2",data);
        scatterplotdemo2.pack();
        RefineryUtilities.centerFrameOnScreen(scatterplotdemo2);
        scatterplotdemo2.setVisible(true);
    }

    public static void main3(String[] args)throws Exception{
        PrintStream ps=new PrintStream("D:\\test.tsv");
        PrintStream ps2=new PrintStream("D:\\test2.tsv");
        Clustering ca=new AverageClustering("D:\\test\\standard","D:\\test\\aiaTitle.properties");
//        List<Cluster> result=ca.startAnalysisByClusterNumber(200);
        List<Cluster> result=ca.startAnalysisByClusterNumber(50);
        System.out.println("bad aia: "+ca.getBadAia());
        System.out.println("the number of cluster is "+result.size());
        List<String> mutations=new ArrayList<>(ca.getCalculator().getMutationIDF().keySet());

        ps2.println("title\tcolor");
        for(int i=0;i<result.size();i++){
            List<AiaProject> aias=result.get(i).getAiaProjects();
            for(int j=0;j<aias.size();j++){
                AiaProject aia=aias.get(j);
                for(int k=0;k<mutations.size();k++){
                    if(aia.getVector().get(mutations.get(k))==null){
                        ps.print(0+"\t");
                    }else{
                        ps.print(aia.getVector().get(mutations.get(k))+"\t");
                    }
                }
                ps.println();
                ps2.print(aia.getTitle()+"\t");
                ps2.print("color"+i);
                ps2.println();
            }
        }
        ps.close();
        ps2.close();
    }

    public static void main(String[] args) {
        String path="D:\\test\\standard";
        File dir = new File(path);
        Properties prop = new Properties();
        List<AiaProject> aiaProjects=new ArrayList<>();
        try {
            prop.load(new FileInputStream("D:\\test\\aiaTitle.properties"));
        } catch (FileNotFoundException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        if (dir.isDirectory()) {
            List<File> files = FileUtil.findAiaFile(dir);
            System.out.println("start parse");
            long start= System.currentTimeMillis();
            for (int i = 0; i < 500; i++) {
                AiaProject aia=new AiaProject(files.get(i));
                aia.setTitle(prop.getProperty(aia.getName()));
                aiaProjects.add(aia);
            }
            System.out.println("end parse after "+(System.currentTimeMillis()-start)+" ms");
        } else if (path.toLowerCase().endsWith(".zip")) {
            try {
                aiaProjects = FileUtil.UnzipToAiaProjects(new FileInputStream(dir));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println(aiaProjects.size());
        CalculateDistance calculator=new CalculateDistance(aiaProjects);
        double[][] dis=calculator.calculateAllDistance();
        double[][] xy= MDSJ.classicalScaling(dis);
        double[][] disMetrix=new double[dis.length][dis.length];
        for(int i=0;i<dis.length;i++){
            for(int j=0;j<dis.length;j++){
                disMetrix[i][j]=Math.sqrt(Math.pow(xy[0][i]-xy[0][j],2)+Math.pow(xy[1][i]-xy[1][j],2));
            }
        }
        double[][] t=new double[dis.length][2];
        for(int i=0;i<dis.length;i++){
            t[i][0]=xy[0][i];
            t[i][1]=xy[1][i];
        }
        xy=t;
        Map<AiaProject,Double[]> points=new HashMap<>();
        for(int i=0;i<aiaProjects.size();i++){
            Double[] point=new Double[2];
            point[0]=xy[i][0];
            point[1]=xy[i][1];
            points.put(aiaProjects.get(i),point);
        }
        Clustering c=new NestestClustering(aiaProjects,disMetrix);
        List<Cluster> result=c.startAnalysisByClusterNumber(50);
        double[][][] data=new double[result.size()][][];
        int k=0;
        for(int i=0;i<result.size();i++){
            int size=result.get(i).getAiaProjects().size();
            double[][] temp=new double[size][];
            for(int j=0;j<size;j++){
                Double[] point=points.get(result.get(i).getAiaProjects().get(j));
                temp[j]=new double[]{point[0],point[1]};
            }
            data[i]=temp;
        }
        ScatterFrame scatterplotdemo2 = new ScatterFrame("Scatter Plot Demo 2",data);
        scatterplotdemo2.pack();
        RefineryUtilities.centerFrameOnScreen(scatterplotdemo2);
        scatterplotdemo2.setVisible(true);
    }
}
