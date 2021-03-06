package mtas.search.spans;

import java.io.IOException;

import mtas.codec.util.CodecInfo;
import mtas.codec.util.CodecInfo.IndexDoc;

import org.apache.lucene.search.spans.SpanCollector;
import org.apache.lucene.search.spans.Spans;

/**
 * The Class MtasSpanPosition.
 */
public class MtasSpanPosition extends Spans {

  /** The field. */
  private String field;

  /** The doc id. */
  private int start, end, minPosition, maxPosition, currentStartPosition,
      currentEndPosition, docId;

  /** The mtas codec info. */
  private CodecInfo mtasCodecInfo;

  /**
   * Instantiates a new mtas span position.
   *
   * @param mtasCodecInfo
   *          the mtas codec info
   * @param field
   *          the field
   * @param start
   *          the start
   * @param end
   *          the end
   */
  public MtasSpanPosition(CodecInfo mtasCodecInfo, String field, int start,
      int end) {
    super();
    this.mtasCodecInfo = mtasCodecInfo;
    this.field = field;
    this.start = start;
    this.end = end;
    minPosition = NO_MORE_POSITIONS;
    maxPosition = NO_MORE_POSITIONS;
    currentStartPosition = NO_MORE_POSITIONS;
    currentEndPosition = NO_MORE_POSITIONS;
    docId = -1;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#nextStartPosition()
   */
  @Override
  public int nextStartPosition() throws IOException {
    if (currentStartPosition < minPosition) {
      currentStartPosition = minPosition;
      currentEndPosition = currentStartPosition + 1;
    } else {
      currentStartPosition++;
      currentEndPosition = currentStartPosition + 1;
      if (currentStartPosition > maxPosition) {
        currentStartPosition = NO_MORE_POSITIONS;
        currentEndPosition = NO_MORE_POSITIONS;
      }
    }
    return currentStartPosition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#startPosition()
   */
  @Override
  public int startPosition() {
    return currentStartPosition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#endPosition()
   */
  @Override
  public int endPosition() {
    return currentEndPosition;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#width()
   */
  @Override
  public int width() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.lucene.search.spans.Spans#collect(org.apache.lucene.search.spans
   * .SpanCollector)
   */
  @Override
  public void collect(SpanCollector collector) throws IOException {

  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#docID()
   */
  @Override
  public int docID() {
    return docId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#nextDoc()
   */
  @Override
  public int nextDoc() throws IOException {
    do {
      IndexDoc indexDoc = mtasCodecInfo.getNextDoc(field, docId);
      if (indexDoc != null) {
        docId = indexDoc.docId;
        minPosition = Math.max(start, indexDoc.minPosition);
        maxPosition = Math.min(end, indexDoc.maxPosition);
        currentStartPosition = -1;
        currentEndPosition = -1;
      } else {
        docId = NO_MORE_DOCS;
        minPosition = NO_MORE_POSITIONS;
        maxPosition = NO_MORE_POSITIONS;
        currentStartPosition = NO_MORE_POSITIONS;
        currentEndPosition = NO_MORE_POSITIONS;
      }
    } while (docId != NO_MORE_DOCS && (minPosition > maxPosition));
    return docId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#advance(int)
   */
  @Override
  public int advance(int target) throws IOException {
    int tmpTarget = target - 1;
    do {
      IndexDoc indexDoc = mtasCodecInfo.getNextDoc(field, tmpTarget);
      if (indexDoc != null) {
        docId = indexDoc.docId;
        minPosition = Math.max(start, indexDoc.minPosition);
        maxPosition = Math.min(end, indexDoc.maxPosition);
        currentStartPosition = -1;
        currentEndPosition = -1;
      } else {
        docId = NO_MORE_DOCS;
        minPosition = NO_MORE_POSITIONS;
        maxPosition = NO_MORE_POSITIONS;
        currentStartPosition = NO_MORE_POSITIONS;
        currentEndPosition = NO_MORE_POSITIONS;
      }
      tmpTarget = docId;
    } while (docId != NO_MORE_DOCS && minPosition > maxPosition);
    return docId;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.DocIdSetIterator#cost()
   */
  @Override
  public long cost() {
    return 0;
  }

  /*
   * (non-Javadoc)
   * 
   * @see org.apache.lucene.search.spans.Spans#positionsCost()
   */
  @Override
  public float positionsCost() {
    return 0;
  }

}
