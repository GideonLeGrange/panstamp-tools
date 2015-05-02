package me.legrange.panstamp.gui.model;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * An extension of PlainDocument that restricts the text to doubles in a range.
 *
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
            for (char c : val.toCharArray()) {
                switch (c) {
                    case '0':
                    case '1':
                    case '2':
                    case '3':
                    case '4':
                    case '5':
                    case '6':
                    case '7':
                    case '8':
                    case '9':
                        break;
                    case '-':
                        if ((val.indexOf('-') > 0) || val.lastIndexOf('-') != val.indexOf('-')) {
                            return;
                        }
                        break;
                    case '.':
                        if (val.lastIndexOf('.') != val.indexOf('.')) {
                            return;
                        }
                        break;
                    default:
                        return;
                }
            }
            super.insertString(offs, str, a);
        }
    }

    private final double min;
    private final double max;

}
