package me.legrange.panstamp.gui;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * An extension of PlainDocument that restricts the text to integers in a range.
 * @author gideon
 */
public class HexDocument extends PlainDocument {

    public HexDocument(int min, int max) {
        this.min = min;
        this.max = max;
    }

    @Override
    public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
        System.out.printf("offs = %d, str = %s, a = %s\n", offs, str, a);
        if (str.length() > 0) {
            if (str.startsWith("-") && min >= 0) { // special case to suppress "-0"
                return;
            }
            String val = getText(0, getLength());
            if (offs == 0) {
                val = str + offs;
            } else {
                val = val.substring(0, offs) + str + val.substring(offs);
            }
            try {
                int newVal = Integer.parseInt(val, 16);
                if ((newVal >= min) && (newVal <= max)) {
                    super.insertString(offs, str, a); 
                }
            } catch (NumberFormatException e) {
            }
        }
    }
    
    
    

    private final int min;
    private final int max;
    private int value;

}
