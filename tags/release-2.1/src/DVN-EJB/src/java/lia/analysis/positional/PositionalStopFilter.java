package lia.analysis.positional;

import org.apache.lucene.util.AttributeSource;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.PositionIncrementAttribute;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;
import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.CharArraySet;
import java.io.IOException;

public class PositionalStopFilter extends TokenFilter {
  private CharArraySet stopWords;
  private PositionIncrementAttribute posIncrAttr;
  private TermAttribute termAttr;

  public PositionalStopFilter(TokenStream in, CharArraySet stopWords) {
    super(in);
    this.stopWords = stopWords;
    posIncrAttr = (PositionIncrementAttribute) addAttribute(PositionIncrementAttribute.class);
    termAttr = (TermAttribute) addAttribute(TermAttribute.class);
  }

  public final boolean incrementToken() throws IOException {
    int increment = 0;
    while(input.incrementToken()) {
      if (!stopWords.contains(termAttr.termBuffer(), 0, termAttr.termLength())) {
        posIncrAttr.setPositionIncrement(posIncrAttr.getPositionIncrement() + increment);
        return true;
      }

      increment += posIncrAttr.getPositionIncrement();
    }

    return false;
  }
}
