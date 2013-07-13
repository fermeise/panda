package edu.kit.iti.algo2.panda.gui.view;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class ResultRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -4461696202264213887L;
	
	public Dimension getPreferredSize(String html, boolean width, int prefSize) {
		setText(html);
		View view = (View) getClientProperty(BasicHTML.propertyKey);
		view.setSize(width?prefSize:0,width?0:prefSize);
		
		float w = view.getPreferredSpan(View.X_AXIS);
		float h = view.getPreferredSpan(View.Y_AXIS);
		
		return new java.awt.Dimension((int) Math.ceil(w), (int) Math.ceil(h));
	}
	@Override
	public Component getListCellRendererComponent(JList<? extends Object> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		if (value instanceof String) {
			this.setPreferredSize(getPreferredSize((String)value, true, list.getWidth()));
		}
		return this;
	}

}
