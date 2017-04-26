package org.freedom.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import mdsj.MDSJ;
import org.freedom.cluster.*;
import org.jfree.ui.RefineryUtilities;

public class ClusterDisplay extends JFrame {

	private JButton fileBtn;
	private JTextField fileTf;
	private JRadioButton thresholdrb;
	private JLabel thresholdLable;
	private JSlider thresholdSlider;
	private JRadioButton numrb;
	private JFormattedTextField numtf;
	private JButton startBtn;
	private boolean isNew;
	private Box badBox;
	private Box messageBox;
	private Box filterBox;
	private Box clusterBox;
	private Box resultBox;

	private Clustering clusterAnalysis;

	private Clustering clustering2D;

	private int clusterNum;

	public ClusterDisplay(String title) throws HeadlessException {
		super(title);
		init();
	}
	
	/**
	 * 初始化布局,控件
	 */
	private void init() {
		/*
		 * 开始面板
		 */
		Box startBox = Box.createVerticalBox();
		
		Box fileBox = Box.createHorizontalBox();
		fileBtn = new JButton("选择聚类项目路径");
		fileTf = new JTextField();
		fileBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				isNew = true;
				JFileChooser jf = new JFileChooser();
//				jf.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				jf.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
				jf.setFileFilter(new FileFilter() {
					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return null;
					}
					
					@Override
					public boolean accept(File f) {
						// TODO Auto-generated method stub
						if(f.isDirectory()) return true;
						if(f.getName().toLowerCase().endsWith(".zip")) return true;
						return false;
					}
				});
				int result = jf.showOpenDialog(ClusterDisplay.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					fileTf.setText(jf.getSelectedFile().getAbsolutePath());
				}
			}
		});
		fileBox.add(Box.createHorizontalGlue());
		fileBox.add(fileBtn);
		fileBox.add(fileTf);
		fileBox.add(Box.createHorizontalGlue());
		
		//参数面板
		Box paramBox=Box.createVerticalBox();
		JLabel paramLabel=new JLabel("选择和设置聚类参数");
		Box thresholdBox = Box.createHorizontalBox();
		thresholdrb=new JRadioButton("");
		thresholdLable = new JLabel("距离阈值:50");
		thresholdSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 50);
		thresholdSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				thresholdLable.setText("距离阈值:" + thresholdSlider.getValue());
			}
		});
//		thresholdBox.add(Box.createHorizontalGlue());
		thresholdBox.add(Box.createHorizontalStrut(20));
		thresholdBox.add(thresholdrb);
		thresholdBox.add(thresholdLable);
		thresholdBox.add(thresholdSlider);
		thresholdBox.add(Box.createHorizontalGlue());
		
		Box numBox=Box.createHorizontalBox();
		numrb=new JRadioButton("");
		JLabel numLabel=new JLabel("簇的个数     ");
		numtf=new JFormattedTextField(new java.text.DecimalFormat("#0"));
		numtf.setMaximumSize(new Dimension(100, 20));
		numtf.setPreferredSize(new Dimension(50, 20));
		numtf.setText("100");
		numtf.addKeyListener(new java.awt.event.KeyAdapter() {
	           public void keyReleased(java.awt.event.KeyEvent evt) {
	               String old = numtf.getText();
	               JFormattedTextField.AbstractFormatter formatter = numtf.getFormatter();
	               if (!old.equals("")) { 
	                   if (formatter != null) {
	                       String str = numtf.getText();
	                       try {
	                           long page = (Long) formatter.stringToValue(str);
	                           numtf.setText(page + "");
	                       } catch (ParseException pe) {
	                    	   numtf.setText("100");//解析异常直接将文本框中值设置为100
	                       }
	                   }
	               }
	           }
	       });
//		numBox.add(Box.createHorizontalGlue());
		numBox.add(Box.createHorizontalStrut(20));
		numBox.add(numrb);
		numBox.add(numLabel);
//		numBox.add(Box.createHorizontalStrut(20));
		numBox.add(numtf);
		numBox.add(Box.createHorizontalGlue());
		
		ButtonGroup bg=new ButtonGroup();
		bg.add(thresholdrb);
		bg.add(numrb);
		thresholdrb.setSelected(true);
		
		paramBox.add(Box.createVerticalStrut(10));
		paramBox.add(paramLabel);
		paramBox.add(Box.createVerticalStrut(10));
		paramBox.add(thresholdBox);
		paramBox.add(Box.createVerticalStrut(10));
		paramBox.add(numBox);

		startBtn = new JButton("开始聚类");
		startBtn.addActionListener(new StartListener());
		startBox.add(Box.createVerticalStrut(10));
		startBox.add(fileBox);
		startBox.add(Box.createVerticalStrut(10));
		startBox.add(paramBox);
		startBox.add(Box.createVerticalStrut(10));
		startBox.add(startBtn);
		startBox.add(Box.createVerticalStrut(10));
		this.add(startBox, BorderLayout.NORTH);
		
		/*
		 * 聚类结果面板
		 */
		resultBox = Box.createVerticalBox();
		messageBox=Box.createVerticalBox();
		filterBox=Box.createVerticalBox();
		badBox = Box.createVerticalBox();
		clusterBox = Box.createVerticalBox();
		resultBox.add(Box.createVerticalStrut(10));
		resultBox.add(messageBox);
		resultBox.add(Box.createVerticalStrut(10));
		resultBox.add(filterBox);
		resultBox.add(Box.createVerticalStrut(10));
		resultBox.add(new JScrollPane(clusterBox, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
		this.add(resultBox, BorderLayout.CENTER);
	}
	/**
	 * 监听器,启动聚类器,开始聚类 
	 * @author FREEDOM
	 *
	 */
	class StartListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			if (fileTf.getText().isEmpty())
				return;
			startBtn.setEnabled(false);
			messageBox.removeAll();
			filterBox.removeAll();
			clusterBox.removeAll();
			long start=System.currentTimeMillis();
			if (isNew) {
				System.out.println("aia解析中...");
				clusterAnalysis = new AverageClustering(fileTf.getText(),"D:\\test\\aiaTitle.properties");
				clustering2D=null;
			}
			System.out.println("aia聚类中...");
			List<Cluster> clusters;
			double threshold=0.0;
			if(thresholdrb.isSelected()){
				threshold=thresholdSlider.getValue();
				clusters = clusterAnalysis.startAnalysisByThreshold(threshold);
				
			}else{
				clusters =clusterAnalysis.startAnalysisByClusterNumber(Integer.parseInt(numtf.getText()));
			}
			List<AiaProject> badAia = clusterAnalysis.getBadAia();
			System.out.println("bad aia: "+badAia);
			System.out.println("the result of cluster");
			for(Cluster cluster: clusters){
				System.out.println(cluster);
			}
			System.out.println("聚类完成,更新控件...");
			long time=System.currentTimeMillis()-start;
			messageBox.add(new JLabel("Aia项目数: "+clusterAnalysis.getCalculator().getAiaProjects().size()+" 个Aia"));
			messageBox.add(Box.createVerticalStrut(10));
			messageBox.add(new JLabel("聚类耗时: "+time+" ms"));
			messageBox.add(Box.createVerticalStrut(10));
			clusterNum=clusters.size();
			messageBox.add(new JLabel("簇的数目: "+clusterNum));
			messageBox.add(Box.createVerticalStrut(10));
			JButton virualBtn= new JButton("查看聚类分布");
			virualBtn.addActionListener(new VirualListener(clusterAnalysis));
			messageBox.add(virualBtn);
			messageBox.add(Box.createVerticalStrut(10));
			if (badAia.size() > 0) {
				messageBox.add(new JLabel("无效的Aia文件:"));
				messageBox.add(Box.createVerticalStrut(10));
				JPanel errorPanel = new JPanel(new GridLayout(badAia.size()/10+1, 10));
				for (AiaProject aia : badAia) {
					errorPanel.add(new JLabel(" " + aia.getName() + " "));
				}
			}
			clusterBox.add(new ClusterTree(clusters));
			isNew=false;
			startBtn.setText("开始聚类");
			startBtn.setEnabled(true);
			resultBox.revalidate();
			System.out.println("控件更新完成");
		}
	}
	class VirualListener implements ActionListener{
		private double[][][] data;
		private Clustering ca;
		private ScatterFrame scatterFrame;
		public VirualListener(Clustering ca){
			this.ca=ca;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
			if(scatterFrame!=null){
				scatterFrame.setVisible(true);
				return;
			}
			List<AiaProject> aias=new ArrayList<>();
			aias.addAll(ca.getCalculator().getAiaProjects());
			aias.removeAll(ca.getBadAia());
			int num=aias.size();

			double[][] dis=new double[num][num];
			for(int i=0;i<num;i++){
				for(int j=0;j<num;j++){
					if(i==j){
						dis[i][j]=0.0;
					}else{
						dis[i][j]=ca.getAiaDistance(aias.get(i),aias.get(j));
					}
				}
			}
//				double[][] xy= Virualization.classicalMDS(dis);
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
			for(int i=0;i<aias.size();i++){
				Double[] point=new Double[2];
				point[0]=xy[i][0];
				point[1]=xy[i][1];
				points.put(aias.get(i),point);
			}
			if(clustering2D==null) {
				clustering2D= new AverageClustering(aias, disMetrix);
			}
			List<Cluster> r=clustering2D.startAnalysisByClusterNumber(clusterNum);
			double[][][] data=new double[r.size()][][];
			int k=0;
			for(int i=0;i<r.size();i++){
				int size=r.get(i).getAiaProjects().size();
				double[][] temp=new double[size][];
				for(int j=0;j<size;j++){
					Double[] point=points.get(r.get(i).getAiaProjects().get(j));
					temp[j]=new double[]{point[0],point[1]};
				}
				data[i]=temp;
			}

			scatterFrame = new ScatterFrame("Scatter Plot Demo 2",data);
			scatterFrame.pack();
//			RefineryUtilities.centerFrameOnScreen(scatterFrame);
			scatterFrame.setVisible(true);
			scatterFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			scatterFrame.addWindowListener(new WindowAdapter() {
				@Override
				public void windowClosing(WindowEvent e) {
					System.out.println("关闭事件");
					scatterFrame.setVisible(false);
				}
			});
		}
	}
	/**
	 * 程序入口
	 * @param args
	 */
	public static void main(String[] args) {
		ClusterDisplay frame = new ClusterDisplay("聚类工具");
		 frame.setSize(800, 600);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

}
