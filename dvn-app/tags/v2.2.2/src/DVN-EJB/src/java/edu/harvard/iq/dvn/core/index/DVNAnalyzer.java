package edu.harvard.iq.dvn.core.index;

import org.apache.lucene.analysis.*;

import java.io.IOException;
import java.io.Reader;

public class DVNAnalyzer extends Analyzer {

  public TokenStream tokenStream(String fieldName, Reader reader) {
    Tokenizer tokenStream = new DVNTokenizer(reader);
    TokenStream result = new LowerCaseFilter(tokenStream);
    result = new PorterStemFilter(tokenStream);
    return result;
  }

  private static final class SavedStreams {
    Tokenizer tokenStream;
    TokenStream filteredTokenStream;
  }


    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        SavedStreams streams = (SavedStreams) getPreviousTokenStream();
        if (streams == null) {
            streams = new SavedStreams();
            setPreviousTokenStream(streams);
            streams.tokenStream = new DVNTokenizer(reader);
            streams.filteredTokenStream = new LowerCaseFilter(streams.tokenStream);
            streams.filteredTokenStream = new PorterStemFilter(streams.filteredTokenStream);
        } else {
            streams.tokenStream.reset(reader);
        }

        return streams.filteredTokenStream;
    }

}
