package mtas.parser.cql.util;

import mtas.parser.cql.ParseException;

/**
 * The Class MtasCQLParserDefaultPrefixCondition.
 */
public class MtasCQLParserDefaultPrefixCondition
    extends MtasCQLParserWordCondition {

  /**
   * Instantiates a new mtas cql parser default prefix condition.
   *
   * @param field
   *          the field
   * @param prefix
   *          the prefix
   * @param value
   *          the value
   * @throws ParseException
   *           the parse exception
   */
  public MtasCQLParserDefaultPrefixCondition(String field, String prefix,
      String value) throws ParseException {
    super(field, TYPE_AND);
    if (prefix == null) {
      throw new ParseException("no default prefix defined");
    } else {
      addPositiveQuery(new MtasCQLParserWordQuery(field, prefix, value));
    }
  }

}
