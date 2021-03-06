package mtas.codec.util.collector;

import java.util.HashMap;
import java.util.TreeSet;
import mtas.codec.util.CodecUtil;

/**
 * The Class MtasDataItemLongBasic.
 */
class MtasDataItemLongBasic extends MtasDataItemBasic<Long, Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new mtas data item long basic.
   *
   * @param valueSum
   *          the value sum
   * @param valueN
   *          the value n
   * @param sub
   *          the sub
   * @param statsItems
   *          the stats items
   * @param sortType
   *          the sort type
   * @param sortDirection
   *          the sort direction
   * @param errorNumber
   *          the error number
   * @param errorList
   *          the error list
   * @param sourceNumber
   *          the source number
   */
  public MtasDataItemLongBasic(Long valueSum, long valueN,
      MtasDataCollector<?, ?> sub, TreeSet<String> statsItems, String sortType,
      String sortDirection, int errorNumber, HashMap<String, Integer> errorList,
      int sourceNumber) {
    super(valueSum, valueN, sub, statsItems, sortType, sortDirection,
        errorNumber, errorList, new MtasDataLongOperations(), sourceNumber);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Override
  public int compareTo(MtasDataItem<Long, Double> o) {
    int compare = 0;
    if (o instanceof MtasDataItemLongBasic) {
      MtasDataItemLongBasic to = (MtasDataItemLongBasic) o;
      NumberComparator c1 = getComparableValue();
      NumberComparator c2 = to.getComparableValue();
      compare = (c1 != null && c2 != null) ? c1.compareTo(c2.getValue()) : 0;
    }
    return sortDirection.equals(CodecUtil.SORT_DESC) ? -1 * compare : compare;
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.collector.MtasDataItem#getCompareValue()
   */
  @Override
  public NumberComparator<Long> getCompareValue1() {
    switch (sortType) {
    case CodecUtil.STATS_TYPE_N:
      return new NumberComparator<Long>(valueN);
    case CodecUtil.STATS_TYPE_SUM:
      return new NumberComparator<Long>(valueSum);
    default:
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.collector.MtasDataItem#getCompareValue2()
   */
  @Override
  public NumberComparator<Double> getCompareValue2() {
    switch (sortType) {
    case CodecUtil.STATS_TYPE_MEAN:
      return new NumberComparator<Double>(getValue(sortType));
    default:
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  public String toString() {
    return this.getClass().getSimpleName() + "[" + valueSum + "," + valueN
        + "]";
  }

}
