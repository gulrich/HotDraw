/*
 * @(#)InsertIntoDrawingVisitor.java
 *
 * Project:		JHotdraw - a GUI framework for technical drawings
 *				http://www.jhotdraw.org
 *				http://jhotdraw.sourceforge.net
 * Copyright:	� by the original author(s) and all contributors
 * License:		Lesser GNU Public License (LGPL)
 *				http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.standard;

import java.util.HashSet;
import java.util.Set;

import org.jhotdraw.framework.Drawing;
import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureChangeListener;
import org.jhotdraw.framework.FigureEnumeration;
import org.jhotdraw.framework.FigureVisitor;
import org.jhotdraw.framework.Handle;

/**
 * @author  Wolfram Kaiser <mrfloppy@sourceforge.net>
 * @version <$CURRENT_VERSION$>
 */
public class InsertIntoDrawingVisitor implements FigureVisitor {
	private Set<Figure> myInsertedFigures;
	private Drawing myDrawing;

	public InsertIntoDrawingVisitor(Drawing newDrawing) {
		myInsertedFigures = new HashSet<Figure>();
		setDrawing(newDrawing);
	}

	private void setDrawing(Drawing newDrawing) {
		myDrawing = newDrawing;
	}

	protected Drawing getDrawing() {
		return myDrawing;
	}

	public void visitFigure(Figure hostFigure) {
		if (!myInsertedFigures.contains(hostFigure) && !getDrawing().includes(hostFigure)) {
			Figure addedFigure = getDrawing().add(hostFigure);
			myInsertedFigures.add(addedFigure);
		}
	}

	public void visitHandle(Handle hostHandle) {
	}

	public void visitFigureChangeListener(FigureChangeListener hostFigureChangeListener) {
//		hostFigureChangeListener.visit(this);
	}

	public FigureEnumeration getInsertedFigures() {
		return new FigureEnumerator(myInsertedFigures);
	}
}
