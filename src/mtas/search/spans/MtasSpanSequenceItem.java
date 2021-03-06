package mtas.search.spans;

import org.apache.lucene.search.spans.SpanQuery;

/**
 * The Class MtasSpanSequenceItem.
 */
public class MtasSpanSequenceItem {

  /** The span query. */
  private SpanQuery spanQuery;

  /** The optional. */
  private boolean optional;

  /**
   * Instantiates a new mtas span sequence item.
   *
   * @param spanQuery
   *          the span query
   * @param optional
   *          the optional
   */
  public MtasSpanSequenceItem(SpanQuery spanQuery, boolean optional) {
    this.spanQuery = spanQuery;
    this.optional = optional;
  }

  /**
   * Gets the query.
   *
   * @return the query
   */
  public SpanQuery getQuery() {
    return spanQuery;
  }

  /**
   * Sets the query.
   *
   * @param spanQuery
   *          the new query
   */
  public void setQuery(SpanQuery spanQuery) {
    this.spanQuery = spanQuery;
  }

  /**
   * Checks if is optional.
   *
   * @return true, if is optional
   */
  public boolean isOptional() {
    return optional;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#clone()
   */
  @Override
  public MtasSpanSequenceItem clone() {
    MtasSpanSequenceItem item = new MtasSpanSequenceItem(spanQuery, optional);
    return item;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object o) {
    if (o instanceof MtasSpanSequenceItem) {
      final MtasSpanSequenceItem that = (MtasSpanSequenceItem) o;
      return spanQuery.equals(that.getQuery())
          && (optional == that.isOptional());
    } else {
      return false;
    }
  }

}
