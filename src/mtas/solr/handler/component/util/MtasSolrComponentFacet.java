package mtas.solr.handler.component.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.apache.lucene.document.FieldType.LegacyNumericType;
import org.apache.lucene.search.spans.SpanQuery;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.common.util.SimpleOrderedMap;
import org.apache.solr.handler.component.ResponseBuilder;
import org.apache.solr.handler.component.SearchComponent;
import org.apache.solr.handler.component.ShardRequest;
import org.apache.solr.handler.component.ShardResponse;
import org.apache.solr.schema.FieldType;
import org.apache.solr.schema.IndexSchema;
import org.apache.solr.schema.SchemaField;

import mtas.codec.util.CodecComponent.ComponentFacet;
import mtas.codec.util.CodecComponent.ComponentField;
import mtas.codec.util.CodecComponent.ComponentFields;
import mtas.codec.util.CodecComponent.SubComponentFunction;
import mtas.codec.util.collector.MtasDataCollector;
import mtas.parser.function.ParseException;
import mtas.solr.handler.component.MtasSolrSearchComponent;

/**
 * The Class MtasSolrComponentFacet.
 */
@SuppressWarnings("deprecation")
public class MtasSolrComponentFacet {

  /** The search component. */
  MtasSolrSearchComponent searchComponent;

  /** The Constant PARAM_MTAS_FACET. */
  public static final String PARAM_MTAS_FACET = MtasSolrSearchComponent.PARAM_MTAS
      + ".facet";

  /** The Constant NAME_MTAS_FACET_KEY. */
  public static final String NAME_MTAS_FACET_KEY = "key";

  /** The Constant NAME_MTAS_FACET_FIELD. */
  public static final String NAME_MTAS_FACET_FIELD = "field";

  /** The Constant NAME_MTAS_FACET_QUERY. */
  private static final String NAME_MTAS_FACET_QUERY = "query";

  /** The Constant NAME_MTAS_FACET_BASE. */
  private static final String NAME_MTAS_FACET_BASE = "base";

  /** The Constant SUBNAME_MTAS_FACET_QUERY_TYPE. */
  public static final String SUBNAME_MTAS_FACET_QUERY_TYPE = "type";

  /** The Constant SUBNAME_MTAS_FACET_QUERY_VALUE. */
  public static final String SUBNAME_MTAS_FACET_QUERY_VALUE = "value";

  /** The Constant SUBNAME_MTAS_FACET_BASE_FIELD. */
  public static final String SUBNAME_MTAS_FACET_BASE_FIELD = "field";

  /** The Constant SUBNAME_MTAS_FACET_BASE_TYPE. */
  public static final String SUBNAME_MTAS_FACET_BASE_TYPE = "type";

  /** The Constant SUBNAME_MTAS_FACET_BASE_SORT_TYPE. */
  public static final String SUBNAME_MTAS_FACET_BASE_SORT_TYPE = "sort.type";

  /** The Constant SUBNAME_MTAS_FACET_BASE_SORT_DIRECTION. */
  public static final String SUBNAME_MTAS_FACET_BASE_SORT_DIRECTION = "sort.direction";

  /** The Constant SUBNAME_MTAS_FACET_BASE_NUMBER. */
  public static final String SUBNAME_MTAS_FACET_BASE_NUMBER = "number";

  /** The Constant SUBNAME_MTAS_FACET_BASE_MINIMUM. */
  public static final String SUBNAME_MTAS_FACET_BASE_MINIMUM = "minimum";

  /** The Constant SUBNAME_MTAS_FACET_BASE_MAXIMUM. */
  public static final String SUBNAME_MTAS_FACET_BASE_MAXIMUM = "maximum";

  /** The Constant SUBNAME_MTAS_FACET_BASE_FUNCTION. */
  public static final String SUBNAME_MTAS_FACET_BASE_FUNCTION = "function";

  /** The Constant SUBNAME_MTAS_FACET_BASE_FUNCTION_KEY. */
  public static final String SUBNAME_MTAS_FACET_BASE_FUNCTION_KEY = "key";

  /** The Constant SUBNAME_MTAS_FACET_BASE_FUNCTION_EXPRESSION. */
  public static final String SUBNAME_MTAS_FACET_BASE_FUNCTION_EXPRESSION = "expression";

  /** The Constant SUBNAME_MTAS_FACET_BASE_FUNCTION_TYPE. */
  public static final String SUBNAME_MTAS_FACET_BASE_FUNCTION_TYPE = "type";

  /**
   * Instantiates a new mtas solr component facet.
   *
   * @param searchComponent
   *          the search component
   */
  public MtasSolrComponentFacet(MtasSolrSearchComponent searchComponent) {
    this.searchComponent = searchComponent;
  }

  /**
   * Prepare.
   *
   * @param rb
   *          the rb
   * @param mtasFields
   *          the mtas fields
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void prepare(ResponseBuilder rb, ComponentFields mtasFields)
      throws IOException {
    Set<String> ids = MtasSolrResultUtil
        .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_FACET);
    if (ids.size() > 0) {
      int tmpCounter = 0;
      String tmpValue;
      String[] fields = new String[ids.size()];
      String[] keys = new String[ids.size()];
      String[][] queryTypes = new String[ids.size()][];
      String[][] queryValues = new String[ids.size()][];
      String[][] baseFields = new String[ids.size()][];
      String[][] baseFieldTypes = new String[ids.size()][];
      String[][] baseTypes = new String[ids.size()][];
      String[][] baseSortTypes = new String[ids.size()][];
      String[][] baseSortDirections = new String[ids.size()][];
      Integer[][] baseNumbers = new Integer[ids.size()][];
      Double[][] baseMinima = new Double[ids.size()][];
      Double[][] baseMaxima = new Double[ids.size()][];
      String[][][] baseFunctionExpressions = new String[ids.size()][][];
      String[][][] baseFunctionKeys = new String[ids.size()][][];
      String[][][] baseFunctionTypes = new String[ids.size()][][];
      for (String id : ids) {
        fields[tmpCounter] = rb.req.getParams().get(
            PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_FIELD, null);
        keys[tmpCounter] = rb.req.getParams()
            .get(PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_KEY,
                String.valueOf(tmpCounter))
            .trim();
        Set<String> qIds = MtasSolrResultUtil.getIdsFromParameters(
            rb.req.getParams(),
            PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_QUERY);
        if (qIds.size() > 0) {
          int tmpQCounter = 0;
          queryTypes[tmpCounter] = new String[qIds.size()];
          queryValues[tmpCounter] = new String[qIds.size()];
          for (String qId : qIds) {
            queryTypes[tmpCounter][tmpQCounter] = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_QUERY
                        + "." + qId + "." + SUBNAME_MTAS_FACET_QUERY_TYPE,
                    null);
            queryValues[tmpCounter][tmpQCounter] = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_QUERY
                        + "." + qId + "." + SUBNAME_MTAS_FACET_QUERY_VALUE,
                    null);
            tmpQCounter++;
          }
        } else {
          throw new IOException(
              "no " + NAME_MTAS_FACET_QUERY + " for mtas facet " + id);
        }
        Set<String> bIds = MtasSolrResultUtil.getIdsFromParameters(
            rb.req.getParams(),
            PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE);
        if (bIds.size() > 0) {
          int tmpBCounter = 0;
          baseFields[tmpCounter] = new String[bIds.size()];
          baseFieldTypes[tmpCounter] = new String[bIds.size()];
          baseTypes[tmpCounter] = new String[bIds.size()];
          baseSortTypes[tmpCounter] = new String[bIds.size()];
          baseSortDirections[tmpCounter] = new String[bIds.size()];
          baseNumbers[tmpCounter] = new Integer[bIds.size()];
          baseMinima[tmpCounter] = new Double[bIds.size()];
          baseMaxima[tmpCounter] = new Double[bIds.size()];
          baseFunctionKeys[tmpCounter] = new String[bIds.size()][];
          baseFunctionExpressions[tmpCounter] = new String[bIds.size()][];
          baseFunctionTypes[tmpCounter] = new String[bIds.size()][];
          for (String bId : bIds) {
            baseFields[tmpCounter][tmpBCounter] = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                        + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_FIELD,
                    null);
            baseFieldTypes[tmpCounter][tmpBCounter] = getFieldType(
                rb.req.getSchema(), baseFields[tmpCounter][tmpBCounter]);
            baseTypes[tmpCounter][tmpBCounter] = rb.req.getParams()
                .get(PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                    + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_TYPE, null);
            baseSortTypes[tmpCounter][tmpBCounter] = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                        + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_SORT_TYPE,
                    null);
            baseSortDirections[tmpCounter][tmpBCounter] = rb.req.getParams()
                .get(PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                    + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_SORT_DIRECTION,
                    null);
            tmpValue = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                        + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_NUMBER,
                    null);
            baseNumbers[tmpCounter][tmpBCounter] = tmpValue != null
                ? getPositiveInteger(tmpValue) : null;
            tmpValue = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                        + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_MINIMUM,
                    null);
            baseMinima[tmpCounter][tmpBCounter] = tmpValue != null
                ? getDouble(tmpValue) : null;
            tmpValue = rb.req.getParams()
                .get(
                    PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                        + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_MAXIMUM,
                    null);
            baseMaxima[tmpCounter][tmpBCounter] = tmpValue != null
                ? getDouble(tmpValue) : null;
            Set<String> functionIds = MtasSolrResultUtil.getIdsFromParameters(
                rb.req.getParams(),
                PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE + "."
                    + bId + "." + SUBNAME_MTAS_FACET_BASE_FUNCTION);
            baseFunctionExpressions[tmpCounter][tmpBCounter] = new String[functionIds
                .size()];
            baseFunctionKeys[tmpCounter][tmpBCounter] = new String[functionIds
                .size()];
            baseFunctionTypes[tmpCounter][tmpBCounter] = new String[functionIds
                .size()];
            int tmpSubCounter = 0;
            for (String functionId : functionIds) {
              baseFunctionKeys[tmpCounter][tmpBCounter][tmpSubCounter] = rb.req
                  .getParams()
                  .get(PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                      + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_FUNCTION + "."
                      + functionId + "." + SUBNAME_MTAS_FACET_BASE_FUNCTION_KEY,
                      String.valueOf(tmpSubCounter))
                  .trim();
              baseFunctionExpressions[tmpCounter][tmpBCounter][tmpSubCounter] = rb.req
                  .getParams()
                  .get(PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                      + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_FUNCTION + "."
                      + functionId + "."
                      + SUBNAME_MTAS_FACET_BASE_FUNCTION_EXPRESSION, null);
              baseFunctionTypes[tmpCounter][tmpBCounter][tmpSubCounter] = rb.req
                  .getParams()
                  .get(PARAM_MTAS_FACET + "." + id + "." + NAME_MTAS_FACET_BASE
                      + "." + bId + "." + SUBNAME_MTAS_FACET_BASE_FUNCTION + "."
                      + functionId + "."
                      + SUBNAME_MTAS_FACET_BASE_FUNCTION_TYPE, null);
              tmpSubCounter++;
            }
            tmpBCounter++;
          }
        } else {
          throw new IOException(
              "no " + NAME_MTAS_FACET_BASE + " for mtas facet " + id);
        }
        tmpCounter++;
      }
      String uniqueKeyField = rb.req.getSchema().getUniqueKeyField().getName();
      mtasFields.doFacet = true;
      rb.setNeedDocSet(true);
      for (String field : fields) {
        if (field == null || field.isEmpty()) {
          throw new IOException("no (valid) field in mtas facet");
        } else if (!mtasFields.list.containsKey(field)) {
          mtasFields.list.put(field, new ComponentField(field, uniqueKeyField));
        }
      }
      MtasSolrResultUtil.compareAndCheck(keys, fields, NAME_MTAS_FACET_KEY,
          NAME_MTAS_FACET_FIELD, true);
      for (int i = 0; i < fields.length; i++) {
        ComponentField cf = mtasFields.list.get(fields[i]);
        int queryNumber = queryValues[i].length;
        SpanQuery ql[] = new SpanQuery[queryNumber];
        for (int j = 0; j < queryNumber; j++) {
          SpanQuery q = MtasSolrResultUtil.constructQuery(queryValues[i][j],
              queryTypes[i][j], fields[i]);
          // minimize number of queries
          if (cf.spanQueryList.contains(q)) {
            q = cf.spanQueryList.get(cf.spanQueryList.indexOf(q));
          } else {
            cf.spanQueryList.add(q);
          }
          ql[j] = q;
        }
        String key = (keys[i] == null) || (keys[i].isEmpty())
            ? String.valueOf(i) + ":" + fields[i] : keys[i].trim();
        try {
          mtasFields.list.get(fields[i]).facetList.add(new ComponentFacet(ql,
              fields[i], key, baseFields[i], baseFieldTypes[i], baseTypes[i],
              baseSortTypes[i], baseSortDirections[i], baseNumbers[i],
              baseMinima[i], baseMaxima[i], baseFunctionKeys[i],
              baseFunctionExpressions[i], baseFunctionTypes[i]));
        } catch (ParseException e) {
          throw new IOException(e.getMessage());
        }
      }
    }
  }

  /**
   * Modify request.
   *
   * @param rb
   *          the rb
   * @param who
   *          the who
   * @param sreq
   *          the sreq
   */
  public void modifyRequest(ResponseBuilder rb, SearchComponent who,
      ShardRequest sreq) {
    if (sreq.params.getBool(MtasSolrSearchComponent.PARAM_MTAS, false)) {
      if (sreq.params.getBool(PARAM_MTAS_FACET, false)) {
        if ((sreq.purpose & ShardRequest.PURPOSE_GET_TOP_IDS) != 0) {
          // do nothing
        } else {
          // remove prefix for other requests
          Set<String> keys = MtasSolrResultUtil
              .getIdsFromParameters(rb.req.getParams(), PARAM_MTAS_FACET);
          sreq.params.remove(PARAM_MTAS_FACET);
          for (String key : keys) {
            sreq.params.remove(
                PARAM_MTAS_FACET + "." + key + "." + NAME_MTAS_FACET_FIELD);
            sreq.params.remove(
                PARAM_MTAS_FACET + "." + key + "." + NAME_MTAS_FACET_KEY);
            sreq.params.remove(
                PARAM_MTAS_FACET + "." + key + "." + NAME_MTAS_FACET_QUERY);
            sreq.params.remove(
                PARAM_MTAS_FACET + "." + key + "." + NAME_MTAS_FACET_BASE);
          }
        }
      }
    }
  }

  /**
   * Creates the.
   *
   * @param facet
   *          the facet
   * @param encode
   *          the encode
   * @return the simple ordered map
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public SimpleOrderedMap<Object> create(ComponentFacet facet, Boolean encode)
      throws IOException {
    SimpleOrderedMap<Object> mtasFacetResponse = new SimpleOrderedMap<>();
    mtasFacetResponse.add("key", facet.key);
    HashMap<MtasDataCollector<?, ?>, HashMap<String, MtasSolrResult>> functionData = new HashMap<MtasDataCollector<?, ?>, HashMap<String, MtasSolrResult>>();
    for (int i = 0; i < facet.baseFields.length; i++) {
      if (facet.baseFunctionList[i] != null) {
        for (MtasDataCollector<?, ?> functionDataCollector : facet.baseFunctionList[i]
            .keySet()) {
          SubComponentFunction[] tmpSubComponentFunctionList = facet.baseFunctionList[i]
              .get(functionDataCollector);
          if (tmpSubComponentFunctionList != null) {
            HashMap<String, MtasSolrResult> tmpList = new HashMap<String, MtasSolrResult>();
            for (SubComponentFunction tmpSubComponentFunction : tmpSubComponentFunctionList) {
              tmpList.put(tmpSubComponentFunction.key,
                  new MtasSolrResult(tmpSubComponentFunction.dataCollector,
                      tmpSubComponentFunction.dataType,
                      tmpSubComponentFunction.statsType,
                      tmpSubComponentFunction.statsItems, null));
            }
            functionData.put(functionDataCollector, tmpList);
          }
        }
      }
    }
    MtasSolrResult data = new MtasSolrResult(facet.dataCollector,
        facet.baseDataTypes, facet.baseStatsTypes, facet.baseStatsItems,
        facet.baseSortTypes, facet.baseSortDirections, null, facet.baseNumbers,
        functionData);

    if (encode) {
      mtasFacetResponse.add("_encoded_list", MtasSolrResultUtil.encode(data));
    } else {
      mtasFacetResponse.add("list", data);
      MtasSolrResultUtil.rewrite(mtasFacetResponse);
    }
    return mtasFacetResponse;
  }

  /**
   * Finish stage.
   *
   * @param rb
   *          the rb
   */
  @SuppressWarnings("unchecked")
  public void finishStage(ResponseBuilder rb) {
    if (rb.req.getParams().getBool(MtasSolrSearchComponent.PARAM_MTAS, false)) {
      if (rb.stage >= ResponseBuilder.STAGE_EXECUTE_QUERY
          && rb.stage < ResponseBuilder.STAGE_GET_FIELDS) {
        for (ShardRequest sreq : rb.finished) {
          if (sreq.params.getBool(MtasSolrSearchComponent.PARAM_MTAS, false)
              && sreq.params.getBool(PARAM_MTAS_FACET, false)) {
            for (ShardResponse shardResponse : sreq.responses) {
              NamedList<Object> response = shardResponse.getSolrResponse()
                  .getResponse();
              try {
                ArrayList<NamedList<Object>> data = (ArrayList<NamedList<Object>>) response
                    .findRecursive("mtas", "facet");
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
   * @param rb
   *          the rb
   * @param mtasFields
   *          the mtas fields
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  @SuppressWarnings("unchecked")
  public void distributedProcess(ResponseBuilder rb, ComponentFields mtasFields)
      throws IOException {
    // rewrite
    NamedList<Object> mtasResponse = null;
    try {
      mtasResponse = (NamedList<Object>) rb.rsp.getValues().get("mtas");
      if (mtasResponse != null) {
        ArrayList<Object> mtasResponseFacet;
        try {
          mtasResponseFacet = (ArrayList<Object>) mtasResponse.get("facet");
          if (mtasResponseFacet != null) {
            MtasSolrResultUtil.rewrite(mtasResponseFacet);
          }
        } catch (ClassCastException e) {
          mtasResponseFacet = null;
        }
      }
    } catch (ClassCastException e) {
      mtasResponse = null;
    }
  }

  /**
   * Gets the field type.
   *
   * @param schema
   *          the schema
   * @param field
   *          the field
   * @return the field type
   */
  private String getFieldType(IndexSchema schema, String field) {
    SchemaField sf = schema.getField(field);
    FieldType ft = sf.getType();
    if (ft != null) {
      if (ft.getNumericType() != null) {
        LegacyNumericType nt = ft.getNumericType();
        if (nt.equals(LegacyNumericType.INT)) {
          return ComponentFacet.TYPE_INTEGER;
        } else if (nt.equals(LegacyNumericType.DOUBLE)) {
          return ComponentFacet.TYPE_DOUBLE;
        } else if (nt.equals(LegacyNumericType.LONG)) {
          return ComponentFacet.TYPE_LONG;
        } else if (nt.equals(LegacyNumericType.FLOAT)) {
          return ComponentFacet.TYPE_FLOAT;
        }
      }
    }
    return ComponentFacet.TYPE_STRING;
  }

  /**
   * Gets the positive integer.
   *
   * @param number
   *          the number
   * @return the positive integer
   */
  private int getPositiveInteger(String number) {
    try {
      return Math.max(0, Integer.parseInt(number));
    } catch (NumberFormatException e) {
      return 0;
    }
  }

  /**
   * Gets the double.
   *
   * @param number
   *          the number
   * @return the double
   */
  private Double getDouble(String number) {
    try {
      return Double.parseDouble(number);
    } catch (NumberFormatException e) {
      return null;
    }
  }

}
