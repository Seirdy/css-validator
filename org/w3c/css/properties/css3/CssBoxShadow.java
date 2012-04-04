// $Id$
// From Sijtsche de Jong (sy.de.jong@let.rug.nl)
// Rewritten 2012 by Yves Lafon <ylafon@w3.org>
//
// (c) COPYRIGHT 1995-2012  World Wide Web Consortium (MIT, ERCIM, Keio University)
// Please first read the full copyright statement at
// http://www.w3.org/Consortium/Legal/copyright-software-19980720

package org.w3c.css.properties.css3;

import org.w3c.css.properties.css.CssProperty;
import org.w3c.css.util.ApplContext;
import org.w3c.css.util.InvalidParamException;
import org.w3c.css.values.CssExpression;
import org.w3c.css.values.CssIdent;
import org.w3c.css.values.CssLength;
import org.w3c.css.values.CssNumber;
import org.w3c.css.values.CssTypes;
import org.w3c.css.values.CssValue;
import org.w3c.css.values.CssValueList;

import java.util.ArrayList;

import static org.w3c.css.values.CssOperator.COMMA;
import static org.w3c.css.values.CssOperator.SPACE;


public class CssBoxShadow extends org.w3c.css.properties.css.CssBoxShadow {

    public static CssIdent inset;
    Object value;

    static {
        inset = CssIdent.getIdent("inset");
    }

    /**
     * Create new CssBoxShadow
     */
    public CssBoxShadow() {
        value = none;
    }

    /**
     * Create new CssBoxShadow
     *
     * @param expression The expression for this property
     * @throws InvalidParamException Values are incorrect
     */
    public CssBoxShadow(ApplContext ac, CssExpression expression,
                        boolean check) throws InvalidParamException {
        CssExpression single_layer = null;
        ArrayList<CssBoxShadowValue> values;
        CssBoxShadowValue boxShadowValue;

        setByUser();
        CssValue val = expression.getValue();
        char op = expression.getOperator();

        if (expression.getCount() == 1) {
            // it can be only 'none' or 'inherit'
            if (val.getType() == CssTypes.CSS_IDENT) {
                CssIdent ident = (CssIdent) val;
                if (inherit.equals(ident)) {
                    value = inherit;
                    expression.next();
                    return;
                } else if (none.equals(ident)) {
                    value = none;
                    expression.next();
                    return;
                }
            }
            // if it is another ident, or not an ident, fail.
            throw new InvalidParamException("value", expression.getValue(),
                    getPropertyName(), ac);
        }
        // ok, so we have one or multiple layers here...
        values = new ArrayList<CssBoxShadowValue>();

        while (!expression.end()) {
            val = expression.getValue();
            op = expression.getOperator();

            if (single_layer == null) {
                single_layer = new CssExpression();
            }
            single_layer.addValue(val);
            single_layer.setOperator(op);
            expression.next();

            if (!expression.end()) {
                // incomplete value followed by a comma... it's complete!
                if (op == COMMA) {
                    single_layer.setOperator(SPACE);
                    boxShadowValue = check(ac, single_layer, check);
                    values.add(boxShadowValue);
                    single_layer = null;
                } else if ((op != SPACE)) {
                    throw new InvalidParamException("operator",
                            ((new Character(op)).toString()), ac);
                }
            }
        }
        // if we reach the end in a value that can come in pair
        if (single_layer != null) {
            boxShadowValue = check(ac, single_layer, check);
            values.add(boxShadowValue);
        }
        if (values.size() == 1) {
            value = values.get(0);
        } else {
            value = values;
        }

    }

    public CssBoxShadowValue check(ApplContext ac, CssExpression expression,
                                   boolean check)
            throws InvalidParamException {
        if (check && expression.getCount() > 6) {
            throw new InvalidParamException("unrecognize", ac);
        }
        CssValue val;
        char op;
        CssBoxShadowValue value = new CssBoxShadowValue();
        boolean length_ok = true;
        int got_length = 0;

        while (!expression.end()) {
            val = expression.getValue();
            op = expression.getOperator();
            switch (val.getType()) {
                case CssTypes.CSS_NUMBER:
                    val = ((CssNumber) val).getLength();
                case CssTypes.CSS_LENGTH:
                    if (!length_ok) {
                        throw new InvalidParamException("value", val,
                                getPropertyName(), ac);
                    }
                    got_length++;
                    switch (got_length) {
                        case 1:
                            value.horizontal_offset = val;
                            break;
                        case 2:
                            value.vertical_offset = val;
                            break;
                        case 3:
                            CssLength length = (CssLength) val;
                            if (!length.isPositive()) {
                                throw new InvalidParamException("negative-value",
                                        expression.getValue(),
                                        getPropertyName(), ac);

                            }
                            value.blur_radius = length;
                            break;
                        case 4:
                            value.spread_distance = val;
                            break;
                        default:
                            throw new InvalidParamException("value", val,
                                    getPropertyName(), ac);
                    }
                    break;
                case CssTypes.CSS_IDENT:
                    // if we got 2 or 4 length tokens we must not have others
                    if (got_length != 0) {
                        length_ok = false;
                    }
                    CssIdent ident = (CssIdent) val;
                    // checked before, not allowed here
                    if (inherit.equals(ident)) {
                        throw new InvalidParamException("value", val,
                                getPropertyName(), ac);
                    }
                    if (inset.equals(ident)) {
                        value.shadow_mod = inset;
                        // inset can be first or last
                        if ((value.color != null || got_length != 0) &&
                                expression.getRemainingCount() != 1) {
                            // so we got a color, but no length, that's not valid
                            throw new InvalidParamException("unrecognize", ac);
                        }
                        break;
                    }
                    // if not a known ident, it must be a color
                    // and let's use the CSS3 color.
                    CssExpression exp = new CssExpression();
                    exp.addValue(val);
                    CssColor color = new CssColor(ac, exp, check);
                    value.color = (CssValue) color.get();
                    break;
                case CssTypes.CSS_COLOR:
                case CssTypes.CSS_FUNCTION:
                    if (got_length != 0) {
                        length_ok = false;
                    }
                    // this one is a pain... need to remove function for colors.
                    CssExpression fexp = new CssExpression();
                    fexp.addValue(val);
                    CssColor fcolor = new CssColor(ac, fexp, check);
                    value.color = (CssValue) fcolor.get();
                    break;
                default:
                    throw new InvalidParamException("value", val,
                            getPropertyName(), ac);
            }
            if (op != SPACE) {
                throw new InvalidParamException("operator", val,
                        getPropertyName(), ac);
            }
            expression.next();
        }
        // we need 2 or 4 lengthed
        if (got_length != 2 && got_length != 4) {
            throw new InvalidParamException("unrecognize", ac);
        }
        return value;
    }

    public CssBoxShadow(ApplContext ac, CssExpression expression)
            throws InvalidParamException {
        this(ac, expression, false);
    }

    /**
     * Compares two properties for equality.
     *
     * @param property The other property.
     */
    public boolean equals(CssProperty property) {
        return (property instanceof CssBoxShadow &&
                value.equals(((CssBoxShadow) property).value));
    }

    /**
     * Returns the value of this property
     */
    public Object get() {
        return value;
    }

    /**
     * Returns true if this property is "softly" inherited
     */
    public boolean isSoftlyInherited() {
        return (inherit == value);
    }

    /**
     * Returns a string representation of the object
     */
    public String toString() {
        // FIXME TODO
        return value.toString();
    }

    /**
     * Is the value of this property a default value
     * It is used by all macro for the function <code>print</code>
     */
    public boolean isDefault() {
        return none == value;
    }

// placeholder for the different values

    public class CssBoxShadowValue extends CssValueList {

        CssValue horizontal_offset;
        CssValue vertical_offset;
        CssValue blur_radius;
        CssValue spread_distance;
        CssValue color;
        CssValue shadow_mod;

        public boolean equals(CssBoxShadowValue v) {
            // at last!
            return true;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(horizontal_offset).append(' ').append(vertical_offset);
            if (blur_radius != null) {
                sb.append(' ').append(blur_radius).append(' ').append(spread_distance);
            }
            if (color != null) {
                sb.append(' ').append(color);
            }
            if (shadow_mod != null) {
                sb.append(' ').append(shadow_mod);
            }
            return sb.toString();
        }
    }

}
