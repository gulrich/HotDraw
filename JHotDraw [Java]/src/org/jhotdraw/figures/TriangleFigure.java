/*
 * @(#)TriangleFigure.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.figures;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.GeneralPath;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jhotdraw.framework.Connector;
import org.jhotdraw.framework.Handle;
import org.jhotdraw.framework.HandleEnumeration;
import org.jhotdraw.standard.BoxHandleKit;
import org.jhotdraw.standard.HandleEnumerator;
import org.jhotdraw.util.StorableInput;
import org.jhotdraw.util.StorableOutput;

/**
 * An triangle figure.
 *
 * @version <$CURRENT_VERSION$>
 */
public class TriangleFigure extends AttributeFigure {

	private static final long serialVersionUID = 3383263140602976509L;
	
	private Rectangle fDisplayBox;

	
	public TriangleFigure() {
		this(new Point(0,0), new Point(0,0));
	}

	public TriangleFigure(Point origin, Point corner) {
		basicDisplayBox(origin,corner);
	}

	public HandleEnumeration handles() {
		List<Handle> handles = new ArrayList<Handle>();
		BoxHandleKit.addHandles(this, handles);
		return new HandleEnumerator(handles);
	}

	public void basicDisplayBox(Point origin, Point corner) {
		fDisplayBox = new Rectangle(origin);
		fDisplayBox.add(corner);
	}

	public Rectangle displayBox() {
		return new Rectangle(
			fDisplayBox.x,
			fDisplayBox.y,
			fDisplayBox.width,
			fDisplayBox.height);
	}

	protected void basicMoveBy(int x, int y) {
		fDisplayBox.translate(x,y);
	}

	public void drawBackground(Graphics g) {
		Rectangle r = displayBox();
		Graphics2D g2d = (Graphics2D)g;
		GeneralPath path = new GeneralPath();
		
		path.moveTo(r.x+r.width/2.0, r.y);
		path.lineTo(r.x, r.y+r.height);
		path.lineTo(r.x+r.width, r.y+r.height);
		path.closePath();
		
		g2d.fill(path);
	}

	public void drawFrame(Graphics g) {
		Rectangle r = displayBox();
		Graphics2D g2d = (Graphics2D)g;
		GeneralPath path = new GeneralPath();
		
		path.moveTo(r.x+r.width/2.0, r.y);
		path.lineTo(r.x, r.y+r.height);
		path.lineTo(r.x+r.width, r.y+r.height);
		path.closePath();
		
		g2d.draw(path);
	}

	public Insets connectionInsets() {
		Rectangle r = fDisplayBox;
		int cx = r.width/2;
		int cy = r.height/2;
		return new Insets(cy, cx, cy, cx);
	}

	public Connector connectorAt(int x, int y) {
		return new ChopEllipseConnector(this);
	}

	public void write(StorableOutput dw) {
		super.write(dw);
		dw.writeInt(fDisplayBox.x);
		dw.writeInt(fDisplayBox.y);
		dw.writeInt(fDisplayBox.width);
		dw.writeInt(fDisplayBox.height);
	}

	public void read(StorableInput dr) throws IOException {
		super.read(dr);
		fDisplayBox = new Rectangle(
			dr.readInt(),
			dr.readInt(),
			dr.readInt(),
			dr.readInt());
	}
}
