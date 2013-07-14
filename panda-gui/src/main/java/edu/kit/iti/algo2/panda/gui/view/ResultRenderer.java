package edu.kit.iti.algo2.panda.gui.view;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

public class ResultRenderer extends DefaultListCellRenderer {
	private static final long serialVersionUID = -4461696202264213887L;
	
	public void setWidthTo(Component parent) {
		int preferredWidth = parent.getWidth() - 20;
		View view = (View) getClientProperty(BasicHTML.propertyKey);
		if(view != null) {
			view.setSize(preferredWidth, 0);

			int preferredHeight = (int) Math.ceil(view.getPreferredSpan(View.Y_AXIS));
			
			setPreferredSize(new Dimension(preferredWidth, preferredHeight));
		}
	}
	
	@Override
	public Component getListCellRendererComponent(final JList<? extends Object> list,
			Object value, int index, boolean isSelected, boolean cellHasFocus) {
		super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		setWidthTo(list);
		return this;
	}

}
