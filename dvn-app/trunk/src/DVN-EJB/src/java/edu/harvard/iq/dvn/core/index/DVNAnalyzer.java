/*
   Copyright (C) 2005-2012, by the President and Fellows of Harvard College.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

   Dataverse Network - A web application to share, preserve and analyze research data.
   Developed at the Institute for Quantitative Social Science, Harvard University.
   Version 3.0.
*/
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
