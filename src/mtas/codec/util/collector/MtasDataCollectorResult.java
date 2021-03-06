package mtas.codec.util.collector;

import java.io.IOException;
import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.SortedMap;
import java.util.TreeMap;

import mtas.codec.util.CodecUtil;
import mtas.codec.util.DataCollector;
import mtas.codec.util.collector.MtasDataItem.NumberComparator;

/**
 * The Class MtasDataCollectorResult.
 *
 * @param <T1>
 *          the generic type
 * @param <T2>
 *          the generic type
 */
public class MtasDataCollectorResult<T1 extends Number & Comparable<T1>, T2 extends Number & Comparable<T2>>
    implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /** The list. */
  SortedMap<String, MtasDataItem<T1, T2>> list;

  /** The item. */
  MtasDataItem<T1, T2> item;

  /** The collector type. */
  String sortType, sortDirection, collectorType;

  /** The last sort value. */
  @SuppressWarnings("rawtypes")
  NumberComparator lastSortValue;

  /** The end key. */
  String startKey, endKey;

  /**
   * Instantiates a new mtas data collector result.
   *
   * @param collectorType
   *          the collector type
   * @param sortType
   *          the sort type
   * @param sortDirection
   *          the sort direction
   * @param basicList
   *          the basic list
   * @param start
   *          the start
   * @param number
   *          the number
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public MtasDataCollectorResult(String collectorType, String sortType,
      String sortDirection, TreeMap<String, MtasDataItem<T1, T2>> basicList,
      Integer start, Integer number) throws IOException {
    this(collectorType, sortType, sortDirection);
    if (sortType == null || sortType.equals(CodecUtil.SORT_TERM)) {
      if (sortDirection == null || sortDirection.equals(CodecUtil.SORT_ASC)) {
        list = basicList;
      } else if (sortDirection.equals(CodecUtil.SORT_DESC)) {
        list = basicList.descendingMap();
      } else {
        throw new IOException("unknown sort direction " + sortDirection);
      }
    } else if (CodecUtil.STATS_TYPES.contains(sortType)) {
      // comperator
      Comparator<String> valueComparator = new Comparator<String>() {
        @Override
        public int compare(String k1, String k2) {
          int compare = basicList.get(k1).compareTo(basicList.get(k2));
          return compare == 0 ? k1.compareTo(k2) : compare;
        }
      };
      SortedMap<String, MtasDataItem<T1, T2>> sortedByValues = new TreeMap<String, MtasDataItem<T1, T2>>(
          valueComparator);
      sortedByValues.putAll(basicList);
      list = sortedByValues;
    } else {
      throw new IOException("unknown sort type " + sortType);
    }
    int listStart = start == null ? 0 : start;
    if (number == null || (start == 0 && number >= list.size())) {
      // do nothing, full list is ok
    } else if (listStart < list.size() && number > 0) {
      // subset
      String boundaryEndKey = null;
      int counter = 0;
      MtasDataItem<T1, T2> previous = null;
      for (String key : list.keySet()) {
        if (listStart == counter) {
          startKey = key;
        } else if (listStart + number <= counter) {
          if (sortType.equals(CodecUtil.SORT_TERM)) {
            endKey = key;
            boundaryEndKey = key;
            break;
          } else if (previous != null) {
            if (previous.compareTo(list.get(key)) != 0) {
              break;
            } else {
              boundaryEndKey = key;
            }
          } else {
            endKey = key;
            boundaryEndKey = key;
            previous = list.get(key);
          }
        } else {
          endKey = key;
        }
        counter++;
      }
      list = list.subMap(startKey, boundaryEndKey);
    } else {
      list = new TreeMap<String, MtasDataItem<T1, T2>>();
    }
    if (list.size() > 0 && sortType != null) {
      lastSortValue = list.get(list.lastKey()).getComparableValue();
    }
  }

  /**
   * Instantiates a new mtas data collector result.
   *
   * @param collectorType
   *          the collector type
   * @param item
   *          the item
   */
  public MtasDataCollectorResult(String collectorType,
      MtasDataItem<T1, T2> item) {
    this(collectorType, null, null);
    this.item = item;
  }

  /**
   * Instantiates a new mtas data collector result.
   *
   * @param collectorType
   *          the collector type
   * @param sortType
   *          the sort type
   * @param sortDirection
   *          the sort direction
   */
  public MtasDataCollectorResult(String collectorType, String sortType,
      String sortDirection) {
    list = null;
    item = null;
    lastSortValue = null;
    this.collectorType = collectorType;
    this.sortType = sortType;
    this.sortDirection = sortDirection;
  }

  /**
   * Gets the list.
   *
   * @return the list
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public final SortedMap<String, MtasDataItem<T1, T2>> getList()
      throws IOException {
    return getList(true);
  }

  /**
   * Gets the list.
   *
   * @param reduce
   *          the reduce
   * @return the list
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public final SortedMap<String, MtasDataItem<T1, T2>> getList(boolean reduce)
      throws IOException {
    if (collectorType.equals(DataCollector.COLLECTOR_TYPE_LIST)) {
      if (reduce && startKey != null && endKey != null) {
        return list.subMap(startKey, endKey);
      } else {
        return list;
      }
    } else {
      throw new IOException("type " + collectorType + " not supported");
    }
  }

  /**
   * Gets the comparator list.
   *
   * @return the comparator list
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("rawtypes")
  public final LinkedHashMap<String, NumberComparator> getComparatorList()
      throws IOException {
    if (collectorType.equals(DataCollector.COLLECTOR_TYPE_LIST)) {
      LinkedHashMap<String, NumberComparator> comparatorList = new LinkedHashMap<String, NumberComparator>();
      for (String key : list.keySet()) {
        comparatorList.put(key, list.get(key).getComparableValue());
      }
      return comparatorList;
    } else {
      throw new IOException("type " + collectorType + " not supported");
    }
  }

  /**
   * Gets the last sort value.
   *
   * @return the last sort value
   */
  @SuppressWarnings("rawtypes")
  public final NumberComparator getLastSortValue() {
    return lastSortValue;
  }

  /**
   * Gets the data.
   *
   * @return the data
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public final MtasDataItem<T1, T2> getData() throws IOException {
    if (collectorType.equals(DataCollector.COLLECTOR_TYPE_DATA)) {
      return item;
    } else {
      throw new IOException("type " + collectorType + " not supported");
    }
  }

}
