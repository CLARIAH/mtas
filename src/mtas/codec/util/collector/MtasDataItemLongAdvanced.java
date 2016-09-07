package mtas.codec.util.collector;

import java.util.HashMap;
import java.util.TreeSet;
import mtas.codec.util.CodecUtil;

/**
 * The Class MtasDataItemLongAdvanced.
 */
class MtasDataItemLongAdvanced extends MtasDataItemAdvanced<Long, Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instantiates a new mtas data item long advanced.
   *
   * @param valueSum
   *          the value sum
   * @param valueSumOfLogs
   *          the value sum of logs
   * @param valueSumOfSquares
   *          the value sum of squares
   * @param valueMin
   *          the value min
   * @param valueMax
   *          the value max
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
  public MtasDataItemLongAdvanced(Long valueSum, Double valueSumOfLogs,
      Long valueSumOfSquares, Long valueMin, Long valueMax, long valueN,
      MtasDataCollector<?, ?> sub, TreeSet<String> statsItems, String sortType,
      String sortDirection, int errorNumber, HashMap<String, Integer> errorList,
      int sourceNumber) {
    super(valueSum, valueSumOfLogs, valueSumOfSquares, valueMin, valueMax,
        valueN, sub, statsItems, sortType, sortDirection, errorNumber,
        errorList, new MtasDataLongOperations(), sourceNumber);
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
    if (o instanceof MtasDataItemLongAdvanced) {
      MtasDataItemLongAdvanced to = (MtasDataItemLongAdvanced) o;
      NumberComparator c1 = getComparableValue();
      NumberComparator c2 = to.getComparableValue();
      compare = (c1 != null && c2 != null) ? c1.compareTo(c2.getValue()) : 0;
    }
    return sortDirection.equals(CodecUtil.SORT_DESC) ? -1 * compare : compare;
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.collector.MtasDataItem#getCompareValue1()
   */
  @Override
  public NumberComparator<Long> getCompareValue1() {
    switch (sortType) {
    case CodecUtil.STATS_TYPE_SUM:
      return new NumberComparator<Long>(valueSum);
    case CodecUtil.STATS_TYPE_MAX:
      return new NumberComparator<Long>(valueMax);
    case CodecUtil.STATS_TYPE_MIN:
      return new NumberComparator<Long>(valueMin);
    case CodecUtil.STATS_TYPE_SUMSQ:
      return new NumberComparator<Long>(valueSumOfSquares);
    default:
      return null;
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see mtas.codec.util.collector.MtasDataItem#getCompareValue2()
   */
  public NumberComparator<Double> getCompareValue2() {
    switch (sortType) {
    case CodecUtil.STATS_TYPE_SUMOFLOGS:
      return new NumberComparator<Double>(valueSumOfLogs);
    case CodecUtil.STATS_TYPE_MEAN:
      return new NumberComparator<Double>(getValue(sortType));
    case CodecUtil.STATS_TYPE_GEOMETRICMEAN:
      return new NumberComparator<Double>(getValue(sortType));
    case CodecUtil.STATS_TYPE_STANDARDDEVIATION:
      return new NumberComparator<Double>(getValue(sortType));
    case CodecUtil.STATS_TYPE_VARIANCE:
      return new NumberComparator<Double>(getValue(sortType));
    case CodecUtil.STATS_TYPE_POPULATIONVARIANCE:
      return new NumberComparator<Double>(getValue(sortType));
    case CodecUtil.STATS_TYPE_QUADRATICMEAN:
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
