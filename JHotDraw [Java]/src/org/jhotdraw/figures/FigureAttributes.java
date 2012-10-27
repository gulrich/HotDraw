/*
 * @(#)FigureAttributes.java
 *
 * Project:     JHotdraw - a GUI framework for technical drawings
 *              http://www.jhotdraw.org
 *              http://jhotdraw.sourceforge.net
 * Copyright:   � by the original author(s) and all contributors
 * License:     Lesser GNU Public License (LGPL)
 *              http://www.opensource.org/licenses/lgpl-license.html
 */

package org.jhotdraw.figures;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jhotdraw.framework.Figure;
import org.jhotdraw.framework.FigureAttributeConstant;
import org.jhotdraw.util.Storable;
import org.jhotdraw.util.StorableInput;
import org.jhotdraw.util.StorableOutput;

/**
 * A container for a figure's attributes. The attributes are stored
 * as key/value pairs.
 *
 * @see Figure
 *
 * @version <$CURRENT_VERSION$>
 */
public  class   FigureAttributes
		extends Object
		implements Cloneable, Serializable {

	private Map<FigureAttributeConstant, Object> fMap;

	/*
	 * Serialization support.
	 */
	private static final long serialVersionUID = -6886355144423666716L;

	/**
	 * Constructs the FigureAttributes.
	 */
	public FigureAttributes() {
		fMap = new HashMap<FigureAttributeConstant, Object>();
	}

	/**
	 * Gets the attribute with the given name.
	 * @return attribute or null if the key is not defined
	 */
	public Object get(FigureAttributeConstant attributeConstant) {
		return fMap.get(attributeConstant);
	}

	/**
	 * Sets the attribute with the given name and
	 * overwrites its previous value.
	 */
	public void set(FigureAttributeConstant attributeConstant, Object value) {
		if (value != null) {
			fMap.put(attributeConstant, value);
		}
		else {
			fMap.remove(attributeConstant);
		}
	}

	/**
	 * Tests if an attribute is defined.
	 */
	public boolean hasDefined(FigureAttributeConstant attributeConstant) {
		return fMap.containsKey(attributeConstant);
	}

	/**
	 * Clones the attributes.
	 */
   public Object clone() {
		try {
			FigureAttributes a = (FigureAttributes) super.clone();
			a.fMap = new HashMap<FigureAttributeConstant, Object>(fMap);
			return a;
		}
		catch (CloneNotSupportedException e) {
			throw new InternalError();
		}
	}

	/**
	 * Reads the attributes from a StorableInput.
	 * FigureAttributes store the following types directly:
	 * Color, Boolean, String, Int. Other attribute types
	 * have to implement the Storable interface or they
	 * have to be wrapped by an object that implements Storable.
	 * @see Storable
	 * @see #write
	 */
	public void read(StorableInput dr) throws IOException {
		String s = dr.readString();
		if (!s.toLowerCase().equals("attributes")) {
			throw new IOException("Attributes expected");
		}

		fMap = new HashMap<FigureAttributeConstant, Object>();
		int size = dr.readInt();
		for (int i=0; i<size; i++) {
			String key = dr.readString();
			String valtype = dr.readString();
			Object val = null;
			if (valtype.equals("Color")) {
				val = new Color(dr.readInt(), dr.readInt(), dr.readInt());
			}
			else if (valtype.equals("Boolean")) {
				val = new Boolean(dr.readString());
			}
			else if (valtype.equals("String")) {
				val = dr.readString();
			}
			else if (valtype.equals("Int")) {
				val = new Integer(dr.readInt());
			}
			else if (valtype.equals("Storable")) {
				val = dr.readStorable();
			}
			else if (valtype.equals(Figure.POPUP_MENU)) {
				// read String but don't store it
				continue;
			}
			else if (valtype.equals("UNKNOWN")) {
				continue;
			}
			// try to get defined constant
			FigureAttributeConstant attributeConstant = FigureAttributeConstant.getConstant(key);
			set(attributeConstant, val);
		}
	}

	/**
	 * Writes the attributes to a StorableInput.
	 * FigureAttributes store the following types directly:
	 * Color, Boolean, String, Int. Other attribute types
	 * have to implement the Storable interface or they
	 * have to be wrapped by an object that implements Storable.
	 * @see Storable
	 * @see #write
	 */
	public void write(StorableOutput dw) {
		dw.writeString("attributes");

		dw.writeInt(fMap.size());   // number of attributes
		Iterator<FigureAttributeConstant> iter = fMap.keySet().iterator();
		while (iter.hasNext()) {
			FigureAttributeConstant fac = iter.next();
			String attributeName = fac.getName();
			Object attributeValue = fMap.get(fac);

			dw.writeString(attributeName);

			if (attributeValue instanceof String) {
				dw.writeString("String");
				dw.writeString((String)attributeValue);
			}
			else if (attributeValue instanceof Color) {
				writeColor(dw, "Color", (Color)attributeValue);
			}
			else if (attributeValue instanceof Boolean) {
				dw.writeString("Boolean");
				if (((Boolean)attributeValue).booleanValue()) {
					dw.writeString("TRUE");
				}
				else {
					dw.writeString("FALSE");
				}
			}
			else if (attributeValue instanceof Integer) {
				dw.writeString("Int");
				dw.writeInt(((Integer)attributeValue).intValue());
			}
			else if (attributeValue instanceof Storable) {
				dw.writeString("Storable");
				dw.writeStorable((Storable)attributeValue);
			}
			else if (attributeValue instanceof javax.swing.JPopupMenu) {
				dw.writeString(Figure.POPUP_MENU);
			}
			else {
				System.err.println("Unknown attribute: " + attributeValue);
				dw.writeString("UNKNOWN");
			}
		}
	}

	public static void writeColor(StorableOutput dw, String colorName, Color color) {
	   if (color != null) {
			dw.writeString(colorName);
			dw.writeInt(color.getRed());
			dw.writeInt(color.getGreen());
			dw.writeInt(color.getBlue());
		}
	}

	public static Color readColor(StorableInput dr) throws IOException {
		return new Color(dr.readInt(), dr.readInt(), dr.readInt());
	}
}
