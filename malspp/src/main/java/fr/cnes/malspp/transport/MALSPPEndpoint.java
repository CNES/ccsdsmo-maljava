/*******************************************************************************
 * MIT License
 * 
 * Copyright (c) 2017 CNES
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
  *******************************************************************************/
package fr.cnes.malspp.transport;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransmitMultipleErrorException;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacket;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacketHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.binary.BufferOutputStream;
import fr.cnes.encoding.binary.OutputStreamEncoder;
import fr.cnes.malspp.transport.MALSPPTransport.QualifiedAPID;

public class MALSPPEndpoint implements MALEndpoint {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MALSPPEndpoint.class.getName());

  private MALSPPTransport transport;
  
  private URI uri;

  private Integer apid;
  
  private Integer endpointId;
  
  private int apidQualifier;

  private Map qosProperties;

  private MALMessageListener listener;

  private Integer packetType;

  public MALSPPEndpoint(MALSPPTransport transport, int apidQualifier, int apid,
      Integer endpointId, Map qosProperties) throws MALException {
    this.transport = transport;
    this.apid = apid;
    this.endpointId = endpointId;
    this.apidQualifier = apidQualifier;
    this.qosProperties = qosProperties;
    uri = new URI(MALSPPHelper.toUri(transport.getProtocol(), apidQualifier,
        apid, endpointId));
    
    Boolean isTc;
    if (qosProperties != null) {
      isTc = (Boolean) qosProperties.get(MALSPPTransport.IS_TC_PACKET);
    } else {
      isTc = null;
    }
    if (isTc == null) {
      packetType = transport.getDefaultPacketType();
    } else if (isTc) {
      packetType = 1;
    } else {
      packetType = 0;
    }
  }

  public Map getQosProperties() {
    return qosProperties;
  }

  public void close() throws MALException {
    // TODO Auto-generated method stub

  }

  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, Long transactionId, Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage, Map qosProperties,
      Object... body) throws IllegalArgumentException, MALException {
    MALSPPMessageHeader header = new MALSPPMessageHeader(uri, authenticationId,
        uriTo, timestamp, qoSlevel, priority, domain, networkZone, session,
        sessionName, op.getInteractionType(), interactionStage, transactionId,
        op.getService().getArea().getNumber(), op.getService().getNumber(),
        op.getNumber(), op.getService().getArea().getVersion(), isErrorMessage);
    Object[] bodyArray = new Object[body.length];
    for (int i = 0; i < body.length; i++) {
      bodyArray[i] = body[i];
    }

    MALSPPMessageBody messageBody;
    if (isErrorMessage.booleanValue()) {
      messageBody = new MALSPPErrorBody(bodyArray);
    } else if (op.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
      switch (interactionStage.getValue()) {
      case MALPubSubOperation._REGISTER_STAGE:
        messageBody = new MALSPPRegisterBody(bodyArray);
        break;
      case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
        messageBody = new MALSPPPublishRegisterBody(bodyArray);
        break;
      case MALPubSubOperation._PUBLISH_STAGE:
        messageBody = new MALSPPPublishBody(bodyArray);
        break;
      case MALPubSubOperation._NOTIFY_STAGE:
        messageBody = new MALSPPNotifyBody(bodyArray);
        break;
      case MALPubSubOperation._DEREGISTER_STAGE:
        messageBody = new MALSPPDeregisterBody(bodyArray);
        break;
      default:
        messageBody = new MALSPPMessageBody(bodyArray);
      }
    } else {
      messageBody = new MALSPPMessageBody(bodyArray);
    }
    return new MALSPPMessage(header, messageBody, qosProperties);
  }

  public MALMessage createMessage(Blob authenticationId, URI uriTo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet serviceVersion,
      Boolean isErrorMessage, Map qosProperties, Object... body)
      throws IllegalArgumentException, MALException {
    MALArea messageArea = MALContextFactory.lookupArea(serviceArea, serviceVersion);
    if (messageArea == null) {
      throw new MALException("Unexpected area number: " + serviceArea);
    }

    MALService messageService = messageArea.getServiceByNumber(
        service);
    if (messageService == null) {
      throw new MALException("Unexpected service number: " + service);
    }

    MALOperation messageOperation = messageService
        .getOperationByNumber(operation);
    if (messageOperation == null) {
      throw new MALException("Unexpected operation number: " + operation);
    }

    return createMessage(authenticationId, uriTo, timestamp, qoSlevel,
        priority, domain, networkZone, session, sessionName, transactionId,
        isErrorMessage, messageOperation, interactionStage, qosProperties, body);

  }

  public URI getURI() {
    return uri;
  }
  
  private void throwTransmitError(MALMessageHeader header, String errorMsg,
      Map properties) throws MALTransmitErrorException {
    throw new MALTransmitErrorException(header, new MALStandardError(
        MALHelper.INTERNAL_ERROR_NUMBER, new Union(errorMsg)), properties);
  }

  public void sendMessage(MALMessage msg) throws MALTransmitErrorException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.sendMessage(" + msg + ')');    
    MALMessageHeader header = msg.getHeader();
    
    if (header.getIsErrorMessage() == null) {
      throwTransmitError(header, "Null field 'Is Error'", msg.getQoSProperties());
    } else if (header.getAreaVersion() == null) {
      throwTransmitError(header, "null field 'Area version'", msg.getQoSProperties());
    } else if (header.getAuthenticationId() == null) {
      throwTransmitError(header, "null field 'Authentication Id'", msg.getQoSProperties());
    } else if (header.getDomain() == null) {
      throwTransmitError(header, "null field 'Domain'", msg.getQoSProperties());
    } else if (header.getInteractionStage() == null) {
      throwTransmitError(header, "null field 'Interaction Stage'", msg.getQoSProperties());
    } else if (header.getInteractionType() == null) {
      throwTransmitError(header, "null field 'Interaction Type'", msg.getQoSProperties());
    } else if (header.getNetworkZone() == null) {
      throwTransmitError(header, "null field 'Network Zone'", msg.getQoSProperties());
    } else if (header.getOperation() == null) {
      throwTransmitError(header, "null field 'Operation'", msg.getQoSProperties());
    } else if (header.getPriority() == null) {
      throwTransmitError(header, "null field 'Priority'", msg.getQoSProperties());
    } else if (header.getQoSlevel() == null) {
      throwTransmitError(header, "null field 'QoSlevel'", msg.getQoSProperties());
    } else if (header.getService() == null) {
      throwTransmitError(header, "null field 'Service'", msg.getQoSProperties());
    } else if (header.getServiceArea() == null) {
      throwTransmitError(header, "null field 'Service Area'", msg.getQoSProperties());
    } else if (header.getSession() == null) {
      throwTransmitError(header, "null field 'Session'", msg.getQoSProperties());
    } else if (header.getSessionName() == null) {
      throwTransmitError(header, "null field 'Session Name'", msg.getQoSProperties());
    } else if (header.getTimestamp() == null) {
      throwTransmitError(header, "null field 'Timestamp'", msg.getQoSProperties());
    } else if (header.getURIFrom() == null) {
      throwTransmitError(header, "null field 'URI From'", msg.getQoSProperties());
    } else if (header.getURITo() == null) {
      throwTransmitError(header, "null field 'URI To'", msg.getQoSProperties());
    } 
    
    try {
      MALEndpoint destinationEndpoint = transport.getEndpoint(header.getURITo());
      if (destinationEndpoint != null) {
        if (logger.isLoggable(BasicLevel.DEBUG))
          logger.log(BasicLevel.DEBUG, "Local message sending");
        ((MALSPPEndpoint) destinationEndpoint).onMessage(header, msg.getBody(), msg.getQoSProperties());
      } else {
        transport.sendSpacePacket(header, packetType, qosProperties,
            msg.getQoSProperties(), msg.getBody());
      }
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.WARN))
        logger.log(BasicLevel.WARN, "", exc);
      throw new MALTransmitErrorException(header, new MALStandardError(
          MALHelper.INTERNAL_ERROR_NUMBER, new Union(exc.toString())),
          msg.getQoSProperties());
    }
  }

  public void setMessageListener(MALMessageListener listener)
      throws MALException {
    this.listener = listener;
  }

  void onException(MALException exc) {
    listener.onInternalError(null, exc);
  }

  void onMessage(MALMessageHeader malHeader, MALMessageBody body, Map msgProperties) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.onMessage(" + malHeader
          + ',' + body + ',' + msgProperties + ')');
    MALMessage msg = new MALSPPMessage(malHeader, body, msgProperties);
    listener.onMessage(this, msg);
  }

  public static String toString(Identifier id) {
    if (id == null)
      return null;
    else
      return id.getValue();
  }

  public static String[] createStringArray(IdentifierList idList) {
    String[] res = new String[idList.size()];
    for (int i = 0; i < res.length; i++) {
      Identifier id = idList.get(i);
      res[i] = toString(id);
    }
    return res;
  }

  public String getLocalName() {
    // TODO Auto-generated method stub
    return null;
  }

  public void sendMessages(MALMessage[] msgList)
      throws MALTransmitMultipleErrorException {
    Vector transmitExceptionList = null;
    for (MALMessage msg : msgList) {
      try {
        sendMessage(msg);
      } catch (MALTransmitErrorException exc) {
        if (transmitExceptionList == null) {
          transmitExceptionList = new Vector();
        }
        transmitExceptionList.addElement(exc);
      }
    }
    if (transmitExceptionList != null) {
      MALTransmitErrorException[] transmitExceptions = 
          new MALTransmitErrorException[transmitExceptionList.size()];
      transmitExceptionList.copyInto(transmitExceptions);
      throw new MALTransmitMultipleErrorException(transmitExceptions);
    }
  }

  public void startMessageDelivery() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.startMessageDelivery()");
    //transport.addEndpoint(this);
  }

  public void stopMessageDelivery() throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.stopMessageDelivery()");
    //transport.removeEndpoint(uri);
  }

  public MALMessage createMessage(Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, Long transactionId, Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage, Map qosProperties,
      MALEncodedBody encodedBody) throws IllegalArgumentException, MALException {
    throw new MALException("Not yet implemented");
  }

  public MALMessage createMessage(Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel, UInteger priority,
      IdentifierList domain, Identifier networkZone, SessionType session,
      Identifier sessionName, InteractionType interactionType,
      UOctet interactionStage, Long transactionId, UShort serviceArea,
      UShort service, UShort operation, UOctet serviceVersion,
      Boolean isErrorMessage, Map qosProperties, MALEncodedBody encodedBody)
      throws IllegalArgumentException, MALException {
    throw new MALException("Not yet implemented");
  }
  
  void segmentReceived(MALSPPMessageHeader malHeader, int sequenceFlags,
      MALSPPSecondaryHeader secHeader, 
      SegmentationContext segmentContext, 
      byte[] content, int offset, int length,
      AppConfiguration appConf) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.segmentReceived(" + secHeader + ')');
    long arrivalTime = System.currentTimeMillis();
    
    segmentContext.addSegment(new Segment(sequenceFlags, secHeader
        .getSegmentCounter(), content, offset, length, arrivalTime));
    
    List<Segment> segments = segmentContext.getSegments();
    
    // 1- Remove timed out packets
    int i = 0;
    while (i < segments.size()) {
      Segment s = segments.get(i);
      if (s.getArrivalTime() + transport.getTimeout() < arrivalTime) {
        segments.remove(i);
      } else {
        i++;
      }
    }

    // 2- Insert the arrived packet in the sequence
    int start = -1;
    i = 0;
    Segment previousSegment = null;
    boolean startFound = false;
    while (i < segments.size()) {
      Segment s = segments.get(i);
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "segment=" + s);

      if (startFound) {
        long previousIndex = previousSegment.getSegmentIndex();
        if (s.getSequenceFlags() == 0) {
          if (s.getSegmentIndex() != (previousIndex + 1)) {
            if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG,
                  "Missing segment: " + s.getSegmentIndex() + " != "
                      + (previousIndex + 1));
            startFound = false;
          }
          // Else this is the continuation of a sequence: continue...
        } else if (s.getSequenceFlags() == 2) {
          // This is the end of a sequence
          if (s.getSegmentIndex() == (previousIndex + 1)) {
            // A full sequence has been detected
            deliverSegmentSequence(segmentContext, start, i, appConf, secHeader);
          } else if (logger.isLoggable(BasicLevel.DEBUG)) {
            logger.log(BasicLevel.DEBUG,
                "Missing final segment: " + s.getSegmentIndex() + " != "
                    + (previousIndex + 1));
          }
          // Try to find a new start in both cases
          startFound = false;
        } else if (s.getSequenceFlags() == 1) {
          // This is a new start
          start = i;
        }
      } else {
        if (s.getSequenceFlags() == 1) {
          // This is the start of a sequence
          start = i;
          startFound = true;
        }
      }
      
      previousSegment = s;
      i++;
    }
  }
  
  private void deliverSegmentSequence(SegmentationContext segmentContext,
      int start, int end, AppConfiguration appConf,
      MALSPPSecondaryHeader secHeader) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.deliverSegmentSequence(" + secHeader + ')');
    
    List<Segment> segments = segmentContext.getSegments();

    int encodedBodySize = 0;
    for (int i = start; i <= end; i++) {
      Segment s = segments.get(i);
      encodedBodySize += s.getLength();
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "encodedBodySize=" + encodedBodySize);

    byte[] encodedBody = new byte[encodedBodySize];
    int position = 0;
    for (int i = start; i <= end; i++) {
      Segment s = segments.remove(start);
      System.arraycopy(s.getContent(), s.getOffset(), encodedBody, position,
          s.getLength());
      position += s.getLength();
    }

    deliverMessage(segmentContext.getMalHeader(), encodedBody, 0,
        encodedBodySize, appConf);
    segmentContext.clear();
  }
  
  public void deliverMessage(MALSPPMessageHeader malHeader,
      byte[] encodedBody, int offset, int length, AppConfiguration appConf) throws Exception {
    ByteArrayInputStream bais = new ByteArrayInputStream(encodedBody, offset,
        length);
    
    MALArea area = MALContextFactory.lookupArea(malHeader.getServiceArea(),
        malHeader.getAreaVersion());
    MALService service = area.getServiceByNumber(malHeader.getService());
    MALOperation op = service.getOperationByNumber(malHeader.getOperation());

    Map qosProperties = new Hashtable();
    MALEncodingContext encodingContext = new MALEncodingContext(malHeader, op,
        0, getQosProperties(), qosProperties);

    MALElementStreamFactory elementStreamFactory = appConf
        .getElementStreamFactory();

    MALSPPMessageBody messageBody;
    if (malHeader.getIsErrorMessage().booleanValue()) {
      messageBody = new MALSPPErrorBody(encodingContext,
          elementStreamFactory, bais);
    } else if (malHeader.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
      switch (malHeader.getInteractionStage().getValue()) {
      case MALPubSubOperation._REGISTER_STAGE:
        messageBody = new MALSPPRegisterBody(encodingContext,
            elementStreamFactory, bais);
        break;
      case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
        messageBody = new MALSPPPublishRegisterBody(encodingContext,
            elementStreamFactory, bais);
        break;
      case MALPubSubOperation._PUBLISH_STAGE:
        messageBody = new MALSPPPublishBody(encodingContext,
            elementStreamFactory, bais);
        break;
      case MALPubSubOperation._NOTIFY_STAGE:
        messageBody = new MALSPPNotifyBody(encodingContext,
            elementStreamFactory, bais);
        break;
      case MALPubSubOperation._DEREGISTER_STAGE:
        messageBody = new MALSPPDeregisterBody(encodingContext,
            elementStreamFactory, bais);
        break;
      default:
        messageBody = new MALSPPMessageBody(encodingContext,
            elementStreamFactory, bais);
      }
    } else {
      messageBody = new MALSPPMessageBody(encodingContext,
          elementStreamFactory, bais);
    }
    
    onMessage(malHeader, messageBody, qosProperties);
  }
  
  public static class PacketCounter {
    
    private long value;

    public PacketCounter(long value) {
      super();
      this.value = value;
    }
      
    public long getValue() {
      return value;
    }

    public void increment() {
      value++;
    }
      
  }
  
}
