package mtas.solr.handler.component.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.lucene.search.spans.SpanQuery;
import org.apache.solr.common.params.SolrParams;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.handler.component.ShardRequest;
import org.apache.solr.handler.component.ShardResponse;

import mtas.codec.util.CodecComponent.ComponentField;
import mtas.codec.util.CodecComponent.ComponentFields;
import mtas.codec.util.CodecComponent.ComponentGroup;
import mtas.solr.handler.component.MtasSolrSearchComponent;

/**
 * The Class MtasSolrComponentGroup.
 */
public class MtasSolrComponentGroup {

  /** The search component. */
  MtasSolrSearchComponent searchComponent;

  /** The Constant PARAM_MTAS_GROUP. */
  public static final String PARAM_MTAS_GROUP = MtasSolrSearchComponent.PARAM_MTAS
      + ".group";

  /** The Constant NAME_MTAS_GROUP_FIELD. */
  public static final String NAME_MTAS_GROUP_FIELD = "field";

  /** The Constant NAME_MTAS_GROUP_QUERY_TYPE. */
  public static final String NAME_MTAS_GROUP_QUERY_TYPE = "query.type";

  /** The Constant NAME_MTAS_GROUP_QUERY_VALUE. */
  public static final String NAME_MTAS_GROUP_QUERY_VALUE = "query.value";

  /** The Constant NAME_MTAS_GROUP_KEY. */
  public static final String NAME_MTAS_GROUP_KEY = "key";

  /** The Constant NAME_MTAS_GROUP_NUMBER. */
  public static final String NAME_MTAS_GROUP_NUMBER = "number";

  /** The Constant NAME_MTAS_GROUP_GROUPING_LEFT. */
  public static final String NAME_MTAS_GROUP_GROUPING_LEFT = "grouping.left";

  /** The Constant NAME_MTAS_GROUP_GROUPING_RIGHT. */
  public static final String NAME_MTAS_GROUP_GROUPING_RIGHT = "grouping.right";

  /** The Constant NAME_MTAS_GROUP_GROUPING_HIT_INSIDE. */
  public static final String NAME_MTAS_GROUP_GROUPING_HIT_INSIDE = "grouping.hit.inside";

  /** The Constant NAME_MTAS_GROUP_GROUPING_HIT_LEFT. */
  public static final String NAME_MTAS_GROUP_GROUPING_HIT_LEFT = "grouping.hit.left";

  /** The Constant NAME_MTAS_GROUP_GROUPING_HIT_RIGHT. */
  public static final String NAME_MTAS_GROUP_GROUPING_HIT_RIGHT = "grouping.hit.right";

  /** The Constant NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_LEFT. */
  public static final String NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_LEFT = "grouping.hit.insideLeft";

  /** The Constant NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_RIGHT. */
  public static final String NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_RIGHT = "grouping.hit.insideRight";

  /** The Constant NAME_MTAS_GROUP_GROUPING_POSITION. */
  public static final String NAME_MTAS_GROUP_GROUPING_POSITION = "position";

  /** The Constant NAME_MTAS_GROUP_GROUPING_PREFIXES. */
  public static final String NAME_MTAS_GROUP_GROUPING_PREFIXES = "prefixes";

  /** The Constant DEFAULT_NUMBER. */
  private static final int DEFAULT_NUMBER = 10;

  /**
   * Instantiates a new mtas solr component group.
   *
   * @param searchComponent the search component
   */
  public MtasSolrComponentGroup(MtasSolrSearchComponent searchComponent) {
    this.searchComponent = searchComponent;
  }

  /**
   * Prepare.
   *
   * @param rb the rb
   * @param mtasFields the mtas fields
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void prepare(ResponseBuilder rb, ComponentFields mtasFields)
      throws IOException {
    Set<String> ids = MtasSolrResultUtil
        .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP);
    if (ids.size() > 0) {
      int tmpCounter = 0;
      String[] fields = new String[ids.size()];
      String[] queryTypes = new String[ids.size()];
      String[] queryValues = new String[ids.size()];
      String[] keys = new String[ids.size()];
      String[] numbers = new String[ids.size()];
      String[][] groupingLeftPosition = new String[ids.size()][];
      String[][] groupingLeftPrefixes = new String[ids.size()][];
      String[][] groupingRightPosition = new String[ids.size()][];
      String[][] groupingRightPrefixes = new String[ids.size()][];
      String[] groupingHitInsidePrefixes = new String[ids.size()];
      String[][] groupingHitLeftPosition = new String[ids.size()][];
      String[][] groupingHitLeftPrefixes = new String[ids.size()][];
      String[][] groupingHitRightPosition = new String[ids.size()][];
      String[][] groupingHitRightPrefixes = new String[ids.size()][];
      String[][] groupingHitInsideLeftPosition = new String[ids.size()][];
      String[][] groupingHitInsideLeftPrefixes = new String[ids.size()][];
      String[][] groupingHitInsideRightPosition = new String[ids.size()][];
      String[][] groupingHitInsideRightPrefixes = new String[ids.size()][];
      for (String id : ids) {
        fields[tmpCounter] = rb.req.getParams().get(
            PARAM_MTAS_GROUP + "." + id + "." + NAME_MTAS_GROUP_FIELD, null);
        keys[tmpCounter] = rb.req.getParams()
            .get(PARAM_MTAS_GROUP + "." + id + "." + NAME_MTAS_GROUP_KEY,
                String.valueOf(tmpCounter))
            .trim();
        numbers[tmpCounter] = rb.req.getParams().get(
            PARAM_MTAS_GROUP + "." + id + "." + NAME_MTAS_GROUP_NUMBER, null);
        queryTypes[tmpCounter] = rb.req.getParams().get(
            PARAM_MTAS_GROUP + "." + id + "." + NAME_MTAS_GROUP_QUERY_TYPE,
            null);
        queryValues[tmpCounter] = rb.req.getParams().get(
            PARAM_MTAS_GROUP + "." + id + "." + NAME_MTAS_GROUP_QUERY_VALUE,
            null);
        groupingHitInsidePrefixes[tmpCounter] = null;
        // collect
        SortedSet<String> gids;
        String tmpName;
        // collect grouping inside
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE;
        groupingHitInsidePrefixes[tmpCounter] = rb.req.getParams()
            .get(tmpName + "." + NAME_MTAS_GROUP_GROUPING_PREFIXES);
        // collect grouping left
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_LEFT;
        gids = MtasSolrResultUtil.getIdsFromParameters(rb.req.getParams(),
            tmpName);
        groupingLeftPosition[tmpCounter] = new String[gids.size()];
        groupingLeftPrefixes[tmpCounter] = new String[gids.size()];
        prepare(rb.req.getParams(), gids, tmpName,
            groupingLeftPosition[tmpCounter], groupingLeftPrefixes[tmpCounter]);
        // collect grouping right
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_RIGHT;
        gids = MtasSolrResultUtil.getIdsFromParameters(rb.req.getParams(),
            tmpName);
        groupingRightPosition[tmpCounter] = new String[gids.size()];
        groupingRightPrefixes[tmpCounter] = new String[gids.size()];
        prepare(rb.req.getParams(), gids, tmpName,
            groupingRightPosition[tmpCounter],
            groupingRightPrefixes[tmpCounter]);
        // collect grouping hit left
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_HIT_LEFT;
        gids = MtasSolrResultUtil.getIdsFromParameters(rb.req.getParams(),
            tmpName);
        groupingHitLeftPosition[tmpCounter] = new String[gids.size()];
        groupingHitLeftPrefixes[tmpCounter] = new String[gids.size()];
        prepare(rb.req.getParams(), gids, tmpName,
            groupingHitLeftPosition[tmpCounter],
            groupingHitLeftPrefixes[tmpCounter]);
        // collect grouping hit right
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_HIT_RIGHT;
        gids = MtasSolrResultUtil.getIdsFromParameters(rb.req.getParams(),
            tmpName);
        groupingHitRightPosition[tmpCounter] = new String[gids.size()];
        groupingHitRightPrefixes[tmpCounter] = new String[gids.size()];
        prepare(rb.req.getParams(), gids, tmpName,
            groupingHitRightPosition[tmpCounter],
            groupingHitRightPrefixes[tmpCounter]);
        // collect grouping hit inside left
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_LEFT;
        gids = MtasSolrResultUtil.getIdsFromParameters(rb.req.getParams(),
            tmpName);
        groupingHitInsideLeftPosition[tmpCounter] = new String[gids.size()];
        groupingHitInsideLeftPrefixes[tmpCounter] = new String[gids.size()];
        prepare(rb.req.getParams(), gids, tmpName,
            groupingHitInsideLeftPosition[tmpCounter],
            groupingHitInsideLeftPrefixes[tmpCounter]);
        // collect grouping hit inside right
        tmpName = PARAM_MTAS_GROUP + "." + id + "."
            + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_RIGHT;
        gids = MtasSolrResultUtil.getIdsFromParameters(rb.req.getParams(),
            tmpName);
        groupingHitInsideRightPosition[tmpCounter] = new String[gids.size()];
        groupingHitInsideRightPrefixes[tmpCounter] = new String[gids.size()];
        prepare(rb.req.getParams(), gids, tmpName,
            groupingHitInsideRightPosition[tmpCounter],
            groupingHitInsideRightPrefixes[tmpCounter]);

        tmpCounter++;
      }
      String uniqueKeyField = rb.req.getSchema().getUniqueKeyField().getName();
      mtasFields.doGroup = true;
      rb.setNeedDocSet(true);
      for (String field : fields) {
        if (field == null || field.isEmpty()) {
          throw new IOException("no (valid) field in mtas group");
        } else if (!mtasFields.list.containsKey(field)) {
          mtasFields.list.put(field, new ComponentField(field, uniqueKeyField));
        }
      }
      MtasSolrResultUtil.compareAndCheck(keys, fields, NAME_MTAS_GROUP_KEY,
          NAME_MTAS_GROUP_FIELD, true);
      MtasSolrResultUtil.compareAndCheck(queryValues, fields,
          NAME_MTAS_GROUP_QUERY_VALUE, NAME_MTAS_GROUP_FIELD, false);
      MtasSolrResultUtil.compareAndCheck(queryTypes, fields,
          NAME_MTAS_GROUP_QUERY_TYPE, NAME_MTAS_GROUP_FIELD, false);
      for (int i = 0; i < fields.length; i++) {
        ComponentField cf = mtasFields.list.get(fields[i]);
        SpanQuery q = MtasSolrResultUtil.constructQuery(queryValues[i],
            queryTypes[i], fields[i]);
        // minimize number of queries
        if (cf.spanQueryList.contains(q)) {
          q = cf.spanQueryList.get(cf.spanQueryList.indexOf(q));
        } else {
          cf.spanQueryList.add(q);
        }
        String key = (keys[i] == null) || (keys[i].isEmpty())
            ? String.valueOf(i) + ":" + fields[i] + ":" + queryValues[i]
            : keys[i].trim();
        int number = (numbers[i] == null) || (numbers[i].isEmpty())
            ? DEFAULT_NUMBER : Integer.parseInt(numbers[i]);
        mtasFields.list.get(fields[i]).groupList.add(new ComponentGroup(q,
            fields[i], queryValues[i], queryTypes[i], key, number,
            groupingHitInsidePrefixes[i], groupingHitInsideLeftPosition[i],
            groupingHitInsideLeftPrefixes[i], groupingHitInsideRightPosition[i],
            groupingHitInsideRightPrefixes[i], groupingHitLeftPosition[i],
            groupingHitLeftPrefixes[i], groupingHitRightPosition[i],
            groupingHitRightPrefixes[i], groupingLeftPosition[i],
            groupingLeftPrefixes[i], groupingRightPosition[i],
            groupingRightPrefixes[i]));
      }
    }
  }

  /**
   * Prepare.
   *
   * @param solrParams the solr params
   * @param gids the gids
   * @param name the name
   * @param positions the positions
   * @param prefixes the prefixes
   */
  private void prepare(SolrParams solrParams, SortedSet<String> gids,
      String name, String[] positions, String[] prefixes) {
    if (gids.size() > 0) {
      int tmpSubCounter = 0;
      for (String gid : gids) {
        positions[tmpSubCounter] = solrParams.get(
            name + "." + gid + "." + NAME_MTAS_GROUP_GROUPING_POSITION, null);
        prefixes[tmpSubCounter] = solrParams.get(
            name + "." + gid + "." + NAME_MTAS_GROUP_GROUPING_PREFIXES, null);
        tmpSubCounter++;
      }
    }
  }

  /**
   * Modify request.
   *
   * @param rb the rb
   * @param who the who
   * @param sreq the sreq
   */
  public void modifyRequest(ResponseBuilder rb, SearchComponent who,
      ShardRequest sreq) {
    if (sreq.params.getBool(MtasSolrSearchComponent.PARAM_MTAS, false)) {
      if (sreq.params.getBool(PARAM_MTAS_GROUP, false)) {
        if ((sreq.purpose & ShardRequest.PURPOSE_GET_TOP_IDS) != 0) {
          // do nothing
        } else {
          // remove prefix for other requests
          Set<String> keys = MtasSolrResultUtil
              .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP);
          sreq.params.remove(PARAM_MTAS_GROUP);
          Set<String> subKeys;
          for (String key : keys) {
            sreq.params.remove(
                PARAM_MTAS_GROUP + "." + key + "." + NAME_MTAS_GROUP_FIELD);
            sreq.params.remove(
                PARAM_MTAS_GROUP + "." + key + "." + NAME_MTAS_GROUP_KEY);
            sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                + NAME_MTAS_GROUP_QUERY_TYPE);
            sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                + NAME_MTAS_GROUP_QUERY_VALUE);
            subKeys = MtasSolrResultUtil
                .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP + "."
                    + key + "." + NAME_MTAS_GROUP_GROUPING_LEFT);
            for (String subKey : subKeys) {
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_LEFT + "." + subKey + "."
                  + NAME_MTAS_GROUP_GROUPING_POSITION);
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_LEFT + "." + subKey + "."
                  + NAME_MTAS_GROUP_GROUPING_PREFIXES);
            }
            subKeys = MtasSolrResultUtil
                .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP + "."
                    + key + "." + NAME_MTAS_GROUP_GROUPING_RIGHT);
            for (String subKey : subKeys) {
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_RIGHT + "." + subKey + "."
                  + NAME_MTAS_GROUP_GROUPING_POSITION);
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_RIGHT + "." + subKey + "."
                  + NAME_MTAS_GROUP_GROUPING_PREFIXES);
            }
            subKeys = MtasSolrResultUtil
                .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP + "."
                    + key + "." + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE);
            for (String subKey : subKeys) {
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE + "." + subKey + "."
                  + NAME_MTAS_GROUP_GROUPING_POSITION);
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE + "." + subKey + "."
                  + NAME_MTAS_GROUP_GROUPING_PREFIXES);
            }
            subKeys = MtasSolrResultUtil
                .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP + "."
                    + key + "." + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_LEFT);
            for (String subKey : subKeys) {
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_LEFT + "." + subKey
                  + "." + NAME_MTAS_GROUP_GROUPING_POSITION);
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_LEFT + "." + subKey
                  + "." + NAME_MTAS_GROUP_GROUPING_PREFIXES);
            }
            subKeys = MtasSolrResultUtil
                .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_GROUP + "."
                    + key + "." + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_RIGHT);
            for (String subKey : subKeys) {
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_RIGHT + "." + subKey
                  + "." + NAME_MTAS_GROUP_GROUPING_POSITION);
              sreq.params.remove(PARAM_MTAS_GROUP + "." + key + "."
                  + NAME_MTAS_GROUP_GROUPING_HIT_INSIDE_RIGHT + "." + subKey
                  + "." + NAME_MTAS_GROUP_GROUPING_PREFIXES);
            }
          }
        }
      }
    }
  }

  /**
   * Creates the.
   *
   * @param group the group
   * @param encode the encode
   * @return the simple ordered map
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("unchecked")
  public SimpleOrderedMap<Object> create(ComponentGroup group, Boolean encode)
      throws IOException {
    SimpleOrderedMap<Object> mtasGroupResponse = new SimpleOrderedMap<>();
    mtasGroupResponse.add("key", group.key);
    MtasSolrResult data = new MtasSolrResult(group.dataCollector,
        new String[] { group.dataType }, new String[] { group.statsType },
        new TreeSet[] { group.statsItems }, new String[] { group.sortType },
        new String[] { group.sortDirection }, new Integer[] { group.start },
        new Integer[] { group.number }, null);
    if (encode) {
      mtasGroupResponse.add("_encoded_list", MtasSolrResultUtil.encode(data));
    } else {
      mtasGroupResponse.add("list", data);
      MtasSolrResultUtil.rewrite(mtasGroupResponse);
    }
    return mtasGroupResponse;
  }

  /**
   * Finish stage.
   *
   * @param rb the rb
   */
  @SuppressWarnings("unchecked")
  public void finishStage(ResponseBuilder rb) {
    if (rb.req.getParams().getBool(MtasSolrSearchComponent.PARAM_MTAS, false)) {
      if (rb.stage >= ResponseBuilder.STAGE_EXECUTE_QUERY
          && rb.stage < ResponseBuilder.STAGE_GET_FIELDS) {
        for (ShardRequest sreq : rb.finished) {
          if (sreq.params.getBool(MtasSolrSearchComponent.PARAM_MTAS, false)
              && sreq.params.getBool(PARAM_MTAS_GROUP, false)) {
            for (ShardResponse shardResponse : sreq.responses) {
              NamedList<Object> response = shardResponse.getSolrResponse()
                  .getResponse();
              try {
                ArrayList<NamedList<Object>> data = (ArrayList<NamedList<Object>>) response
                    .findRecursive("mtas", "group");
                if (data != null) {
                  MtasSolrResultUtil.decode(data);
                }
              } catch (ClassCastException e) {
                // shouldn't happen
              }
            }
          }
        }
      }
    }
  }

  /**
   * Distributed process.
   *
   * @param rb the rb
   * @param mtasFields the mtas fields
   * @throws IOException Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("unchecked")
  public void distributedProcess(ResponseBuilder rb, ComponentFields mtasFields)
      throws IOException {
    // rewrite
    NamedList<Object> mtasResponse = null;
    try {
      mtasResponse = (NamedList<Object>) rb.rsp.getValues().get("mtas");
      if (mtasResponse != null) {
        ArrayList<Object> mtasResponseGroup;
        try {
          mtasResponseGroup = (ArrayList<Object>) mtasResponse.get("group");
          if (mtasResponseGroup != null) {
            MtasSolrResultUtil.rewrite(mtasResponseGroup);
          }
        } catch (ClassCastException e) {
          mtasResponseGroup = null;
        }
      }
    } catch (ClassCastException e) {
      mtasResponse = null;
    }
  }

}
