//
// $Id$
// @author Yves Lafon <ylafon@w3.org>
//
// (c) COPYRIGHT 2010  World Wide Web Consortium (MIT, ERCIM, Keio University)
// Please first read the full copyright statement at
// http://www.w3.org/Consortium/Legal/copyright-software-19980720

package org.w3c.css.properties.css;

import org.w3c.css.parser.CssStyle;
import org.w3c.css.properties.css3.Css3Style;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;

/**
 * http://www.w3.org/TR/2009/CR-css3-background-20091217/#the-background-origin
 * <p/>
 * Name: 	background-origin
 * Value: 	&lt;bg-origin&gt; [ , &lt;bg-origin&gt; ]*
 * Initial: 	padding-box
 * Applies to: 	all elements
 * Inherited: 	no
 * Percentages: 	N/A
 * Media: 	visual
 * Computed value: 	same as specified value
 * <p/>
 * For elements rendered as a single box, specifies the background positioning
 * area. For elements rendered as multiple boxes (e.g., inline boxes on several
 * lines, boxes on several pages) specifies which boxes 'box-decoration-break'
 * operates on to determine the background positioning area(s).
 * <p/>
 * &lt;bg-origin&gt; = border-box | padding-box | content-box
 */

public class CssBackgroundOrigin extends CssProperty {

    Object value;


    /**
     * Create a new CssBackgroundClip
     */
    public CssBackgroundOrigin() {
    }

    /**
     * Create a new CssBackgroundClip
     *
     * @param expression The expression for this property
     * @throws org.w3c.css.util.InvalidParamException
     *          Incorrect value
     */
    public CssBackgroundOrigin(ApplContext ac, CssExpression expression,
                               boolean check) throws InvalidParamException {
        throw new InvalidParamException("unrecognize", ac);
    }

    public CssBackgroundOrigin(ApplContext ac, CssExpression expression)
            throws InvalidParamException {
        this(ac, expression, false);
    }

    /**
     * Add this property to the CssStyle
     *
     * @param style The CssStyle
     */
    public void addToStyle(ApplContext ac, CssStyle style) {
        // TODO FIXME -> in CssStyle
        if (((Css3Style) style).cssBackgroundOrigin != null)
            style.addRedefinitionWarning(ac, this);
        ((Css3Style) style).cssBackgroundOrigin = this;
    }

    /**
     * Get this property in the style.
     *
     * @param style   The style where the property is
     * @param resolve if true, resolve the style to find this property
     */
    public CssProperty getPropertyInStyle(CssStyle style, boolean resolve) {
        if (resolve) {
            return ((Css3Style) style).getCssBackgroundOrigin();
        } else {
            return ((Css3Style) style).cssBackgroundOrigin;
        }
    }

    /**
     * Compares two properties for equality.
     *
     * @param property The other property.
     */
    public boolean equals(CssProperty property) {
        return (property instanceof CssBackgroundOrigin &&
                value.equals(((CssBackgroundOrigin) property).value));
    }

    /**
     * Returns the name of this property
     */
    public final String getPropertyName() {
        return "background-origin";
    }

    /**
     * Returns the value of this property
     */
    public Object get() {
        return value;
    }

    public void set(Object val) {
        value = val;
    }

    /**
     * Returns true if this property is "softly" inherited
     */
    public boolean isSoftlyInherited() {
        return inherit.equals(value);
    }

    /**
     * Returns a string representation of the object
     */
    public String toString() {

        return value.toString();
    }

    /**
     * Is the value of this property a default value
     * It is used by all macro for the function <code>print</code>
     */
    public boolean isDefault() {
        return false;
    }

}