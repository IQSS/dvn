package lia.analysis.positional;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenFilter;
import java.util.Set;
import java.io.IOException;

public class PositionalStopFilter extends TokenFilter {
  private Set stopWords;

  public PositionalStopFilter(TokenStream in, Set stopWords) {
    super(in);
    this.stopWords = stopWords;
  }

  public final Token next() throws IOException {
    int increment = 0;
    for (Token token = input.next();
         token != null; token = input.next()) {

      if (!stopWords.contains(token.termText())) {
        token.setPositionIncrement(
            token.getPositionIncrement() + increment);
        return token;
      }

      increment++;
    }

    return null;
  }
}
