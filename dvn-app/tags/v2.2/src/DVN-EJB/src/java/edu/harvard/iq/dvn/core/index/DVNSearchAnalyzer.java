package edu.harvard.iq.dvn.core.index;

import org.apache.lucene.analysis.*;

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.util.Version;

public class DVNSearchAnalyzer extends Analyzer {

  private boolean replaceInvalidAcronym = false;
  


  public TokenStream tokenStream(String fieldName, Reader reader) {
/* It seems that LUCENE_CURRENT is ultimately what we should use, but there is
 an ominous warning in the source code about using it -- using LUCENE_29 for now, will look into this further */
//    StandardTokenizer tokenStream = new StandardTokenizer(Version.LUCENE_CURRENT,reader);
    StandardTokenizer tokenStream = new StandardTokenizer(Version.LUCENE_29,reader);
    tokenStream.setMaxTokenLength(maxTokenLength);
    TokenStream result = new StandardFilter(tokenStream);
    result = new LowerCaseFilter(result);
    result = new PorterStemFilter(result);
    return result;
  }

  private static final class SavedStreams {
    StandardTokenizer tokenStream;
    TokenStream filteredTokenStream;
  }

  public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;

  private int maxTokenLength = DEFAULT_MAX_TOKEN_LENGTH;


  public void setMaxTokenLength(int length) {
    maxTokenLength = length;
  }
    
  public int getMaxTokenLength() {
    return maxTokenLength;
  }
  
  public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
    SavedStreams streams = (SavedStreams) getPreviousTokenStream();
    if (streams == null) {
      streams = new SavedStreams();
      setPreviousTokenStream(streams);
//      streams.tokenStream = new StandardTokenizer(Version.LUCENE_CURRENT, reader);
      streams.tokenStream = new StandardTokenizer(Version.LUCENE_29, reader);
      streams.filteredTokenStream = new StandardFilter(streams.tokenStream);
      streams.filteredTokenStream = new LowerCaseFilter(streams.filteredTokenStream);
      streams.filteredTokenStream = new PorterStemFilter(streams.filteredTokenStream);
    } else {
      streams.tokenStream.reset(reader);
    }
    streams.tokenStream.setMaxTokenLength(maxTokenLength);
    
    return streams.filteredTokenStream;
  }

  public boolean isReplaceInvalidAcronym() {
    return replaceInvalidAcronym;
  }

  public void setReplaceInvalidAcronym(boolean replaceInvalidAcronym) {
    this.replaceInvalidAcronym = replaceInvalidAcronym;
  }
}
