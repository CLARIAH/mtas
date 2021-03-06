package mtas.parser.cql.util;

import org.apache.lucene.search.spans.SpanQuery;

/**
 * The Class MtasCQLParserGroupCondition.
 */
public class MtasCQLParserGroupCondition {

  /** The condition. */
  private SpanQuery condition;

  /** The field. */
  private String field;

  /**
   * Instantiates a new mtas cql parser group condition.
   *
   * @param field
   *          the field
   * @param condition
   *          the condition
   */
  public MtasCQLParserGroupCondition(String field, SpanQuery condition) {
    this.field = field;
    this.condition = condition;
  }

  /**
   * Field.
   *
   * @return the string
   */
  public String field() {
    return field;
  }

  /**
   * Gets the query.
   *
   * @return the query
   */
  public SpanQuery getQuery() {
    return condition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object object) {
    if (object == null) {
      return false;
    } else if (object instanceof MtasCQLParserGroupCondition) {
      MtasCQLParserGroupCondition condition = (MtasCQLParserGroupCondition) object;
      // basic checks
      if (!field.equals(condition.field)) {
        return false;
      } else {
        if (!this.condition.equals(condition)) {
          return false;
        }
        return true;
      }
    } else {
      return false;
    }
  }
}
