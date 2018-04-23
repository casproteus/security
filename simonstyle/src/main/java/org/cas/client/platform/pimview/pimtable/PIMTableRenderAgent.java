package org.cas.client.platform.pimview.pimtable;

import java.awt.Color;

public interface PIMTableRenderAgent {
	public Color getBackgroundAtRow(int row);
	public Color getForegroundAtRow(int row);
}
