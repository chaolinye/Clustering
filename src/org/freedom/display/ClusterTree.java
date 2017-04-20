package org.freedom.display;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.freedom.cluster.AiaProject;
import org.freedom.cluster.Cluster;

public class ClusterTree extends JPanel{
	private List<Cluster> clusters;
	public ClusterTree(List<Cluster> clusters) {
		super();
		this.clusters = clusters;
		this.init();
	}
	private void init(){
		this.setLayout(new BorderLayout());
		DefaultMutableTreeNode top=new DefaultMutableTreeNode("聚类结果");
		List<Cluster> oneClusters=new ArrayList<>();
		for(Cluster c : clusters){
			if (c.getAiaProjects().size() == 1) {
				oneClusters.add(c);
			} else {
				String[] labels = c.getFirstLabel(2);
				String[] titles = c.getFirstTitle(2);
				List<String> text = new ArrayList<>();
				Collections.addAll(text, labels);
				Collections.addAll(text, titles);
				DefaultMutableTreeNode node=new DefaultMutableTreeNode(text.toString() + "(" + c.getAiaProjects().size() + ")");
				for(AiaProject aia: Cluster.inOrder(c)){
					node.add(new DefaultMutableTreeNode(aia.getName() + "---" + aia.getTitle(), false));
				}
				top.add(node);
			}
		}
		for (Cluster cluster : oneClusters) {
			for (AiaProject aia : cluster.getAiaProjects()) {
				top.add(new DefaultMutableTreeNode(aia.getName() + "---" + aia.getTitle(), false));
			}
		}
		JTree tree =new JTree(top);
		tree.setShowsRootHandles(true);
		this.add(tree);
	}

}
