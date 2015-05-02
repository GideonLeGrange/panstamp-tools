package me.legrange.panstamp.gui.model;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * An extension of PlainDocument that restricts the text to doubles in a range.
 * @author gideon
 */
public class DoubleDocument extends PlainDocument {

    public DoubleDocument(double min, double max) {
        super();
        this.min = min;
        this.max = max;
    }
    

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        if (str.length() > 0) {
            if (str.startsWith("-") && min >= 0) { // special case to suppress "-0"
                return;
            }
            String val = getText(0, getLength());
            if (offs == 0) {
                val = str + val;
            } else {
                val = val.substring(0, offs) + str + val.substring(offs);
            }
            try {
                double newVal = Double.valueOf(val);
                if ((newVal >= min) && (newVal <= max)) {
                    super.insertString(offs, str, a); 
                }
            } catch (NumberFormatException e) {
            }
        }
    }

    private final double min;
    private final double max;

}
