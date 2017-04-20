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
import java.text.ParseException;
import java.util.List;

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

import org.freedom.cluster.AiaProject;
import org.freedom.cluster.Cluster;
import org.freedom.cluster.Clustering;
import org.freedom.cluster.NestestClustering;

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
//			badBox.removeAll();
			messageBox.removeAll();
			filterBox.removeAll();
			clusterBox.removeAll();
			long start=System.currentTimeMillis();
			if (isNew) {
				System.out.println("aia解析中...");
				clusterAnalysis = new NestestClustering(fileTf.getText(),"D:\\test\\aiaTitle.properties");
			}
			System.out.println("aia聚类中...");
			List<Cluster> clusters;
			double threshold=0.0;
			if(thresholdrb.isSelected()){
				threshold=thresholdSlider.getValue();
				clusters = clusterAnalysis.startAnalysis(threshold);
				
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
			messageBox.add(new JLabel("聚类耗时: "+time+" ms"));
			messageBox.add(Box.createVerticalStrut(10));
			messageBox.add(new JLabel("簇的数目: "+clusters.size()));
			messageBox.add(Box.createVerticalStrut(10));
			if (badAia.size() > 0) {
				messageBox.add(new JLabel("无效的Aia文件:"));
//				badBox.add(new JLabel("不规范的Aia文件:"));
//				badBox.add(Box.createVerticalStrut(10));
				messageBox.add(Box.createVerticalStrut(10));
				JPanel errorPanel = new JPanel(new GridLayout(badAia.size()/10+1, 10));
				for (AiaProject aia : badAia) {
					errorPanel.add(new JLabel(" " + aia.getName() + " "));
				}
			}
//			for (int i = 0; i < clusters.size(); i++) {
//				clusterBox.add(new JLabel("类 "+i+" (共包含有"+clusters.get(i).getAiaProjects().size()+"个Aia)"));
//				clusterBox.add(Box.createVerticalStrut(10));
//				JPanel clusterPanel = new JPanel(new GridLayout(clusters.get(i).getAiaProjects().size()/10+1, 10));
//				for (AiaProject aia : clusters.get(i).getAiaProjects()) {
//					clusterPanel.add(new JLabel(" " + aia.getName() + " "));
//					clusterPanel.setBackground(Color.YELLOW);
//				}
//				if (clusters.get(i).getAiaProjects().size() > 1) {
//					JButton btn = new JButton("查看聚类");
//					btn.addActionListener(new DisplayTree(clusters.get(i)));
//					clusterPanel.add(btn);
//				}
//				clusterBox.add(clusterPanel);
//				clusterBox.add(Box.createVerticalStrut(20));
//			}
//			if(threshold-0.0<0.001){
//				for(Cluster cluster:clusters){
//					if(cluster.getDistance()>threshold){
//						threshold=cluster.getDistance();
//					}
//				}
//			}
//			clusterBox.add(new ClusterPanel(clusters, threshold));
			clusterBox.add(new ClusterTree(clusters));
			isNew=false;
			startBtn.setText("开始聚类");
			startBtn.setEnabled(true);
			resultBox.revalidate();
		}
	}
	
//	/**
//	 * 监听器,展示聚类后簇的树形结构
//	 * @author FREEDOM
//	 *
//	 */
//	class DisplayTree implements ActionListener{
//		private Cluster cluster;
//		public DisplayTree(Cluster cluster) {
//			super();
//			this.cluster = cluster;
//		}
//		@Override
//		public void actionPerformed(ActionEvent e) {
//			final JFrame frame = new JFrame("聚类树形展示");
//			TreePanel tp = new TreePanel(TreePanel.CHILD_ALIGN_RELATIVE);
//			tp.setTree(cluster);
//			frame.add(new JScrollPane(tp,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),BorderLayout.CENTER);
//			frame.setSize(800, 600);
//			frame.setVisible(true);
//			frame.addWindowListener(new WindowAdapter() {
//				@Override
//				public void windowClosing(WindowEvent e) {
//					frame.dispose();
//				}
//			});
//		}
//		
//	}
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
