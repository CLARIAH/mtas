package mtas.search.spans;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import mtas.codec.util.CodecInfo;
import mtas.search.similarities.MtasSimScorer;
import org.apache.lucene.codecs.FieldsProducer;
import org.apache.lucene.index.LeafReader;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.index.Terms;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.similarities.Similarity.SimScorer;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.lucene.search.spans.SpanWeight;
import org.apache.lucene.search.spans.Spans;

/**
 * The Class MtasSpanMatchAllQuery.
 */
public class MtasSpanMatchAllQuery extends SpanQuery {

  /** The field. */
  private String field;

  /** The query name. */
  private static String QUERY_NAME = "mtasSpanMatchAllQuery";

  /**
   * Instantiates a new mtas span match all query.
   *
   * @param field
   *          the field
   */
  public MtasSpanMatchAllQuery(String field) {
    this.field = field;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.SpanQuery#getField()
   */
  @Override
  public String getField() {
    return field;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.lucene.search.spans.SpanQuery#createWeight(org.apache.lucene.
   * search.IndexSearcher, boolean)
   */
  @Override
  public SpanWeight createWeight(IndexSearcher searcher, boolean needsScores)
      throws IOException {
    return new SpanAllWeight(searcher, null);
  }

  /**
   * The Class SpanAllWeight.
   */
  public class SpanAllWeight extends SpanWeight {

    /**
     * Instantiates a new span all weight.
     *
     * @param searcher
     *          the searcher
     * @param termContexts
     *          the term contexts
     * @throws IOException
     *           Signals that an I/O exception has occurred.
     */
    public SpanAllWeight(IndexSearcher searcher,
        Map<Term, TermContext> termContexts) throws IOException {
      super(MtasSpanMatchAllQuery.this, searcher, termContexts);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.search.spans.SpanWeight#extractTermContexts(java.util.
     * Map)
     */
    @Override
    public void extractTermContexts(Map<Term, TermContext> contexts) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.search.spans.SpanWeight#getSpans(org.apache.lucene.
     * index.LeafReaderContext,
     * org.apache.lucene.search.spans.SpanWeight.Postings)
     */
    @Override
    public Spans getSpans(LeafReaderContext context, Postings requiredPostings)
        throws IOException {
      try {
        // get leafreader
        LeafReader r = context.reader();
        // get delegate
        Boolean hasMethod = true;
        while (hasMethod) {
          hasMethod = false;
          Method[] methods = r.getClass().getMethods();
          for (Method m : methods) {
            if (m.getName().equals("getDelegate")) {
              hasMethod = true;
              r = (LeafReader) m.invoke(r, (Object[]) null);
              break;
            }
          }
        }
        // get fieldsproducer
        Method fpm = r.getClass().getMethod("getPostingsReader",
            (Class<?>[]) null);
        FieldsProducer fp = (FieldsProducer) fpm.invoke(r, (Object[]) null);
        // get MtasFieldsProducer using terms
        Terms t = fp.terms(field);
        if (t == null) {
          return new MtasSpanMatchNone(field);
        } else {
          CodecInfo mtasCodecInfo = CodecInfo.getCodecInfoFromTerms(t);
          return new MtasSpanMatchAll(mtasCodecInfo, field);
        }
      } catch (Exception e) {
        throw new IOException("Can't get reader");
      }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.lucene.search.Weight#extractTerms(java.util.Set)
     */
    @Override
    public void extractTerms(Set<Term> terms) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.apache.lucene.search.spans.SpanWeight#getSimScorer(org.apache.lucene.
     * index.LeafReaderContext)
     */
    @Override
    public SimScorer getSimScorer(LeafReaderContext context) {
      return new MtasSimScorer();
    }

  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.lucene.search.spans.SpanTermQuery#toString(java.lang.String)
   */
  @Override
  public String toString(String field) {
    StringBuilder buffer = new StringBuilder();
    buffer.append(QUERY_NAME + "([])");
    return buffer.toString();
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.Query#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    final MtasSpanMatchAllQuery that = (MtasSpanMatchAllQuery) obj;
    return field.equals(that.field);
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.Query#hashCode()
   */
  @Override
  public int hashCode() {
    int h = QUERY_NAME.hashCode();
    h = (h * 7) ^ field.hashCode();
    return h;
  }

}
