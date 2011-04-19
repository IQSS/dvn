package edu.harvard.iq.dvn.core.index;

import java.io.Reader;
import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.analysis.LetterTokenizer;

public final class DVNTokenizer extends CharTokenizer {
  /** Construct a new DVNTokenizer. */
  public DVNTokenizer(Reader in) {
    super(in);
  }

    protected char normalize(char c) {
        boolean t = Character.isLetterOrDigit(c);
        if (t) {
            return c;
        } else {
            return '\0';
        }
    }

    protected boolean isTokenChar(char c){
      return Character.isLetterOrDigit(c);
  }
}
