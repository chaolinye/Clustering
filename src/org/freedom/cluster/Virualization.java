package org.freedom.cluster;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;
import mdsj.MDSJ;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chaolin on 2017/4/23.
 */
public class Virualization {
    public static double[][] classicalMDS(double[][] d){
        for(int i=0;i<d.length;i++){
            for(int j=0;j<d[0].length;j++){
                d[i][j]=Math.pow(d[i][j],2);
            }
        }
        Matrix D = new Matrix(d);
        Matrix H = new Matrix(d.length,d.length);
        for(int i=0;i<d.length;i++){
            for(int j=0;j<d.length;j++){
                if(i==j){
                    H.set(i,i,1-1.0/d.length);
                }else {
                    H.set(i, j, -1.0/ d.length);
                }
            }
        }
        Matrix B=H.times(H.times(D)).times(-0.5);
        EigenvalueDecomposition r=B.eig();
        double[] eigenvalues=r.getRealEigenvalues();
        Matrix eigenvector=r.getV();
        Matrix values=new Matrix(2,2);
        for(int i=0;i<values.getRowDimension();i++){
            values.set(i,i,Math.sqrt(eigenvalues[i]));
        }
        Matrix vector=new Matrix(eigenvector.getRowDimension(),2);
        for(int i=0;i<vector.getRowDimension();i++){
            for(int j=0;j<vector.getColumnDimension();j++){
                vector.set(i,j,eigenvector.get(i,j));
            }
        }
        Matrix X=vector.times(values);
        return X.getArray();
    }

    public static void main(String[] args) {
        double[][] d={{0,411,213,219,296,397},
                {411,0,204,203,120,152},
                {213,204,0,73,136,245},
                {219,203,73,0,90,191},
                {296,120,136,90,0,109},
                {397,152,245,191,109,0}};
//        double[][] x=classicalMDS(d);
        double[][] x= MDSJ.classicalScaling(d);
        double[][] t=new double[d.length][2];
        for(int i=0;i<d.length;i++){
            t[i][0]=x[0][i];
            t[i][1]=x[1][i];
        }
        x=t;
        for(int i=0;i<x.length;i++){
            for(int j=0;j<x.length;j++){
                double dis=Math.sqrt(Math.pow(x[i][0]-x[j][0],2)+Math.pow(x[i][1]-x[j][1],2));
                System.out.print(dis+", ");
            }
            System.out.println();
        }
    }
}
