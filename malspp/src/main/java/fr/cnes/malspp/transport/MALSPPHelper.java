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

import java.io.ByteArrayOutputStream;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALInvokeOperation;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALProgressOperation;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALRequestOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.MALSubmitOperation;
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
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacketHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.binary.BufferDecoder;
import fr.cnes.encoding.binary.BufferOutputStream;
import fr.cnes.encoding.binary.BufferReader;
import fr.cnes.encoding.binary.OutputStreamEncoder;

public class MALSPPHelper {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MALSPPHelper.class.getName());

  public static final UOctet INITIATION_STAGE = new UOctet((byte) 0x01);
  
  public static final UInteger DEFAULT_PRIORITY = new UInteger(0);
  public static final Identifier DEFAULT_NETWORK_ZONE = new Identifier("");
  public static final Identifier DEFAULT_SESSION_NAME = new Identifier("");
  public static final IdentifierList DEFAULT_DOMAIN = new IdentifierList(0);
  public static final Blob DEFAULT_AUTHENTICATION_ID = new Blob(new byte[0]);
  
  public static class SduType {
    public final static int SEND = 0;
    public final static int SUBMIT = 1;
    public final static int SUBMIT_ACK = 2;
    public final static int REQUEST = 3;
    public final static int REQUEST_RESPONSE = 4;
    public final static int INVOKE = 5;
    public final static int INVOKE_ACK = 6;
    public final static int INVOKE_RESPONSE = 7;
    public final static int PROGRESS = 8;
    public final static int PROGRESS_ACK = 9;
    public final static int PROGRESS_UPDATE = 10;
    public final static int PROGRESS_RESPONSE = 11;
    public final static int PUBSUB_REGISTER = 12;
    public final static int PUBSUB_REGISTER_ACK = 13;
    public final static int PUBSUB_PUBLISH_REGISTER = 14;
    public final static int PUBSUB_PUBLISH_REGISTER_ACK = 15;
    public final static int PUBSUB_PUBLISH = 16;
    public final static int PUBSUB_NOTIFY = 17;
    public final static int PUBSUB_DEREGISTER = 18;
    public final static int PUBSUB_DEREGISTER_ACK = 19;
    public final static int PUBSUB_PUBLISH_DEREGISTER = 20;
    public final static int PUBSUB_PUBLISH_DEREGISTER_ACK = 21;
  }

  /*
   * 0 (TM) 1 (TC)
   */
  /* DF: not used any more by the MAL/SPP specification
  public static int getSPType(InteractionType type, UOctet stage,
      boolean isError) throws MALException {
    switch (type.getOrdinal()) {
    case InteractionType._SEND_INDEX:
      return SPType.TC;
    case InteractionType._SUBMIT_INDEX:
    case InteractionType._REQUEST_INDEX:
    case InteractionType._INVOKE_INDEX:
    case InteractionType._PROGRESS_INDEX:
      if (stage.getValue() == 0x01) {
        return SPType.TC;
      } else {
        return SPType.TM;
      }
    case InteractionType._PUBSUB_INDEX:
      if (stage.getValue() == MALPubSubOperation._REGISTER_STAGE) {
        return SPType.TC;
      } else if (stage.getValue() == MALPubSubOperation._REGISTER_ACK_STAGE) {
        return SPType.TM;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
        return SPType.TC;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE) {
        return SPType.TM;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_STAGE) {
        if (isError) {
          return SPType.TM;
        } else {
          return SPType.TC;
        }
      } else if (stage.getValue() == MALPubSubOperation._NOTIFY_STAGE) {
        return SPType.TC;
      } else if (stage.getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
        return SPType.TC;
      } else if (stage.getValue() == MALPubSubOperation._DEREGISTER_ACK_STAGE) {
        return SPType.TM;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
        return SPType.TC;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE) {
        return SPType.TM;
      } else {
        throw new MALException("Unsupported PUB/SUB stage: " + stage.getValue());
      }
    default:
      throw new MALException("Unknown interaction type: " + type.getOrdinal());
    }
  }*/

  public static int getSDUType(InteractionType type, UOctet stage,
      Boolean isError) throws MALException {
    switch (type.getOrdinal()) {
    case InteractionType._SEND_INDEX:
      return SduType.SEND;
    case InteractionType._SUBMIT_INDEX:
      if (stage.getValue() == MALSubmitOperation._SUBMIT_STAGE) {
        return SduType.SUBMIT;
      } else {
        return SduType.SUBMIT_ACK;
      }
    case InteractionType._REQUEST_INDEX:
      if (stage.getValue() == MALRequestOperation._REQUEST_STAGE) {
        return SduType.REQUEST;
      } else {
        return SduType.REQUEST_RESPONSE;
      }
    case InteractionType._INVOKE_INDEX:
      if (stage.getValue() == MALInvokeOperation._INVOKE_STAGE) {
        return SduType.INVOKE;
      } else if (stage.getValue() == MALInvokeOperation._INVOKE_ACK_STAGE) {
        return SduType.INVOKE_ACK;
      } else {
        return SduType.INVOKE_RESPONSE;
      }
    case InteractionType._PROGRESS_INDEX:
      if (stage.getValue() == MALProgressOperation._PROGRESS_STAGE) {
        return SduType.PROGRESS;
      } else if (stage.getValue() == MALProgressOperation._PROGRESS_ACK_STAGE) {
        return SduType.PROGRESS_ACK;
      } else if (stage.getValue() == MALProgressOperation._PROGRESS_UPDATE_STAGE) {
        return SduType.PROGRESS_UPDATE;
      } else {
        return SduType.PROGRESS_RESPONSE;
      }
    case InteractionType._PUBSUB_INDEX:
      if (stage.getValue() == MALPubSubOperation._REGISTER_STAGE) {
        return SduType.PUBSUB_REGISTER;
      } else if (stage.getValue() == MALPubSubOperation._REGISTER_ACK_STAGE) {
        return SduType.PUBSUB_REGISTER_ACK;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
        return SduType.PUBSUB_PUBLISH_REGISTER;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_REGISTER_ACK_STAGE) {
        return SduType.PUBSUB_PUBLISH_REGISTER_ACK;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_STAGE) {
        return SduType.PUBSUB_PUBLISH;
      } else if (stage.getValue() == MALPubSubOperation._NOTIFY_STAGE) {
        return SduType.PUBSUB_NOTIFY;
      } else if (stage.getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
        return SduType.PUBSUB_DEREGISTER;
      } else if (stage.getValue() == MALPubSubOperation._DEREGISTER_ACK_STAGE) {
        return SduType.PUBSUB_DEREGISTER_ACK;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
        return SduType.PUBSUB_PUBLISH_DEREGISTER;
      } else if (stage.getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_ACK_STAGE) {
        return SduType.PUBSUB_PUBLISH_DEREGISTER_ACK;
      } else {
        throw new MALException("Unsupported Pub/Sub stage: " + stage.getValue());
      }
    default:
      throw new MALException("Unknown interaction type: " + type.getOrdinal());
    }
  }

  public static UOctet getInteractionStage(int sduType) throws MALException {
    switch (sduType) {
    case SduType.SEND:
    case SduType.SUBMIT:
    case SduType.REQUEST:
    case SduType.INVOKE:
    case SduType.PROGRESS:
      return INITIATION_STAGE;
    case SduType.PUBSUB_REGISTER:
      return MALPubSubOperation.REGISTER_STAGE;
    case SduType.PUBSUB_DEREGISTER:
      return MALPubSubOperation.DEREGISTER_STAGE;
    case SduType.PUBSUB_PUBLISH_REGISTER:
      return MALPubSubOperation.PUBLISH_REGISTER_STAGE;
    case SduType.PUBSUB_PUBLISH:
      return MALPubSubOperation.PUBLISH_STAGE;
    case SduType.PUBSUB_NOTIFY:
      return MALPubSubOperation.NOTIFY_STAGE;
    case SduType.PUBSUB_PUBLISH_DEREGISTER:
      return MALPubSubOperation.PUBLISH_DEREGISTER_STAGE;
    case SduType.SUBMIT_ACK:
      return MALSubmitOperation.SUBMIT_ACK_STAGE;
    case SduType.REQUEST_RESPONSE:
      return MALRequestOperation.REQUEST_RESPONSE_STAGE;
    case SduType.INVOKE_ACK:
      return MALInvokeOperation.INVOKE_ACK_STAGE;
    case SduType.INVOKE_RESPONSE:
      return MALInvokeOperation.INVOKE_RESPONSE_STAGE;
    case SduType.PROGRESS_ACK:
      return MALProgressOperation.PROGRESS_ACK_STAGE;
    case SduType.PROGRESS_UPDATE:
      return MALProgressOperation.PROGRESS_UPDATE_STAGE;
    case SduType.PROGRESS_RESPONSE:
      return MALProgressOperation.PROGRESS_RESPONSE_STAGE;
    case SduType.PUBSUB_REGISTER_ACK:
      return MALPubSubOperation.REGISTER_ACK_STAGE;
    case SduType.PUBSUB_DEREGISTER_ACK:
      return MALPubSubOperation.DEREGISTER_ACK_STAGE;
    case SduType.PUBSUB_PUBLISH_REGISTER_ACK:
      return MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE;
    case SduType.PUBSUB_PUBLISH_DEREGISTER_ACK:
      return MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE;
    default:
      throw new MALException("Unsupported SDU type: " + sduType);
    }
  }

  public static InteractionType getInteractionType(int sduType)
      throws MALException {
    switch (sduType) {
    case SduType.SEND:
      return InteractionType.SEND;
    case SduType.SUBMIT:
      return InteractionType.SUBMIT;
    case SduType.REQUEST:
      return InteractionType.REQUEST;
    case SduType.INVOKE:
      return InteractionType.INVOKE;
    case SduType.PROGRESS:
      return InteractionType.PROGRESS;
    case SduType.PUBSUB_DEREGISTER:
    case SduType.PUBSUB_PUBLISH_REGISTER:
    case SduType.PUBSUB_PUBLISH:
    case SduType.PUBSUB_NOTIFY:
    case SduType.PUBSUB_REGISTER:
    case SduType.PUBSUB_PUBLISH_DEREGISTER:
      return InteractionType.PUBSUB;
    case SduType.SUBMIT_ACK:
      return InteractionType.SUBMIT;
    case SduType.REQUEST_RESPONSE:
      return InteractionType.REQUEST;
    case SduType.INVOKE_ACK:
    case SduType.INVOKE_RESPONSE:
      return InteractionType.INVOKE;
    case SduType.PROGRESS_ACK:
    case SduType.PROGRESS_UPDATE:
    case SduType.PROGRESS_RESPONSE:
      return InteractionType.PROGRESS;
    case SduType.PUBSUB_REGISTER_ACK:
    case SduType.PUBSUB_DEREGISTER_ACK:
    case SduType.PUBSUB_PUBLISH_DEREGISTER_ACK:
    case SduType.PUBSUB_PUBLISH_REGISTER_ACK:
      return InteractionType.PUBSUB;
    default:
      throw new MALException("Unsupported SDU type: " + sduType);
    }
  }

  /* DF: now the transaction id is mandatory
  public static boolean encodeTransactionId(int spType, int sduType) {
    if (spType == SPType.TC) {
      switch (sduType) {
      case SduType.SEND:
        return false;
      default:
        return true;
      }
    } else {
      return true;
    }
  }*/
  /*
  public static boolean encodeTransmitMultiple(int spType, int sduType) {
    switch (sduType) {
    case SduType.PUBSUB_PUBLISH:
    case SduType.PUBSUB_NOTIFY:
      return true;
    default:
      return false;
    }
  }*/
  
  public static int getSecondaryHeaderFixedPartSize(MALSPPSecondaryHeader ssh,
      AppConfiguration qosConfiguration) throws Exception {
    int encodedSize = 1 + 2 + 2 + 2 + 1 + 1 + 1 + 2 + 8 + 1;

    if (ssh.getSourceIdFlag() > 0) {
      encodedSize += 1;
    }
    
    if (ssh.getDestinationIdFlag() > 0) {
      encodedSize += 1;
    }
    
    // Sequence flags is supposed to be 'unsegmented'
    // So no sequence counter
    
    return encodedSize;
  }
  
  public static byte[] encodeSecondaryHeaderVariablePart(MALSPPSecondaryHeader ssh,
     AppConfiguration qosConfiguration) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALSPPHelper.encodeSecondaryHeaderVariablePart(" + ssh + ')');
    
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamEncoder secondaryHeaderEncoder = new OutputStreamEncoder(baos);
    secondaryHeaderEncoder.setVarintSupported(qosConfiguration.isVarintSupported());
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
          "varintSupported=" + qosConfiguration.isVarintSupported());

    if (ssh.getPriorityFlag() > 0) {
      secondaryHeaderEncoder.writeUnsignedInt((int) ssh.getPriority());
    }
    
    if (ssh.getTimestampFlag() > 0) {
      qosConfiguration.getTimeEncoder().encode(ssh.getTimestamp(), secondaryHeaderEncoder);
    }
    
    if (ssh.getNetworkZoneFlag() > 0) {
      secondaryHeaderEncoder.writeString(ssh.getNetworkZone().getValue());
    }
    
    if (ssh.getSessionNameFlag() > 0) {
      secondaryHeaderEncoder.writeString(ssh.getSessionName().getValue());
    }
 
    if (ssh.getDomainFlag() > 0) {
      IdentifierList idList = ssh.getDomain();
      secondaryHeaderEncoder.writeUnsignedInt(idList.size());
      for (Identifier id : idList) {
        secondaryHeaderEncoder.writeNullableString(id.getValue());
      }
    }
    
    if (ssh.getAuthenticationIdFlag() > 0) {
      secondaryHeaderEncoder.writeByteArray(ssh.getAuthenticationId());
    }
    
    return baos.toByteArray();
  }

  public static byte[] encodeSecondaryHeaderFixedPart(MALSPPSecondaryHeader ssh,
      AppConfiguration qosConfiguration, int encodedSize) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALSPPHelper.encodeSecondaryHeaderFixedPart(" + ssh + ')');
    byte[] bytes = new byte[encodedSize];
    BufferOutputStream buffer = new BufferOutputStream(bytes);
    OutputStreamEncoder secondaryHeaderEncoder = new OutputStreamEncoder(buffer);
    secondaryHeaderEncoder.setVarintSupported(qosConfiguration.isVarintSupported());
    
    int versionNumber = ssh.getMalsppVersion();
    int sduType = ssh.getSduType();

    // version on 3 bits, sduType on 5 bits
    secondaryHeaderEncoder.writeByte((byte) ((versionNumber << 5) | (sduType)));
    
    // 16 bits
    secondaryHeaderEncoder.write16((short) ssh.getArea());
    secondaryHeaderEncoder.write16((short) ssh.getService());
    secondaryHeaderEncoder.write16((short) ssh.getOperation());
    secondaryHeaderEncoder.writeByte((byte) ssh.getAreaVersion());
    
    int isError = ssh.getIsError();
    int qos = ssh.getQos();
    int session = ssh.getSession();
    int secondaryApid = ssh.getSecondaryApid();
    secondaryHeaderEncoder.writeByte((byte) ((isError << 7) | (qos << 5) | (session << 3) | (secondaryApid >>> 8)));
    secondaryHeaderEncoder.writeByte((byte) (secondaryApid >>  0));
    
    int secondaryApidQualifier = ssh.getSecondaryApidQualifier();
    secondaryHeaderEncoder.write16((short) secondaryApidQualifier);
    
    secondaryHeaderEncoder.write64(ssh.getTransactionId());
    
    secondaryHeaderEncoder.writeByte((byte) ((ssh.getSourceIdFlag() << 7)
        | (ssh.getDestinationIdFlag() << 6) | (ssh.getPriorityFlag() << 5)
        | (ssh.getTimestampFlag() << 4) | (ssh.getNetworkZoneFlag() << 3)
        | (ssh.getSessionNameFlag() << 2) | (ssh.getDomainFlag() << 1) | (ssh
        .getAuthenticationIdFlag() << 0)));

    if (ssh.getSourceIdFlag() > 0) {
      secondaryHeaderEncoder.writeByte((byte) ssh.getSourceId().intValue());
    }
    
    if (ssh.getDestinationIdFlag() > 0) {
      secondaryHeaderEncoder.writeByte((byte) ssh.getDestinationId().intValue());
    }
    
    return bytes;
  }

  public static int decodeSecondaryHeaderFixedPart(MALSPPSecondaryHeader ssh,
      byte[] bytes, int offset, int seqFlags, AppConfiguration qosConfiguration) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALSPPHelper.decodeSecondaryHeaderFixedpart("
          + qosConfiguration + ')');
    
    BufferReader bufferReader = new BufferReader(bytes, offset);
    BufferDecoder secondaryHeaderDecoder = new BufferDecoder(bufferReader);
    secondaryHeaderDecoder.setVarintSupported(qosConfiguration.isVarintSupported());

    byte b = secondaryHeaderDecoder.readByte();
    int malsppVersion = (b >>> 5) & 0x07;
    ssh.setMalsppVersion(malsppVersion);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "malsppVersion=" + malsppVersion);
    
    int sduType = b & 0x1F;
    ssh.setSduType(sduType);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "sduType=" + sduType);
    
    int area = secondaryHeaderDecoder.read16() & 0xFFFF;
    ssh.setArea(area);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "area=" + area);
    
    int service = secondaryHeaderDecoder.read16() & 0xFFFF;
    ssh.setService(service);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "service=" + service);
    
    int operation = secondaryHeaderDecoder.read16() & 0xFFFF;
    ssh.setOperation(operation);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "operation=" + operation);
    
    byte areaVersion = secondaryHeaderDecoder.readByte();
    ssh.setAreaVersion(areaVersion);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "areaVersion=" + areaVersion);
    
    b = secondaryHeaderDecoder.readByte();
    int isError = (b >>> 7) & 0x01;
    ssh.setIsError(isError);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "isError=" + isError);
    
    int qos = (b >>> 5) & 0x03;
    ssh.setQos(qos);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "qos=" + qos);
    
    int session = (b >>> 3) & 0x03;
    ssh.setSession(session);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "session=" + session);
    
    int secondaryApid = ((b >>> 0) & 0x07) << 8 | secondaryHeaderDecoder.readByte();
    ssh.setSecondaryApid(secondaryApid);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "secondaryApid=" + secondaryApid);
    
    short secondaryApidQualifier = secondaryHeaderDecoder.read16();
    ssh.setSecondaryApidQualifier(secondaryApidQualifier);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "secondaryApidQualifier=" + secondaryApidQualifier);
    
    long transactionId = secondaryHeaderDecoder.read64();
    ssh.setTransactionId(transactionId);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "transactionId=" + transactionId);
    
    b = secondaryHeaderDecoder.readByte();
    byte sourceIdFlag = (byte) ((b >>> 7) & 0x01);
    ssh.setSourceIdFlag(sourceIdFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "sourceIdFlag=" + sourceIdFlag);
    
    byte destinationIdFlag = (byte) ((b >>> 6) & 0x01);
    ssh.setDestinationIdFlag(destinationIdFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "destinationIdFlag=" + destinationIdFlag);
    
    byte priorityFlag = (byte) ((b >>> 5) & 0x01);
    ssh.setPriorityFlag(priorityFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "priorityFlag=" + priorityFlag);
    
    byte timestampFlag = (byte) ((b >>> 4) & 0x01);
    ssh.setTimestampFlag(timestampFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "timestampFlag=" + timestampFlag);
    
    byte networkZoneFlag = (byte) ((b >>> 3) & 0x01);
    ssh.setNetworkZoneFlag(networkZoneFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "networkZoneFlag=" + networkZoneFlag);
    
    byte sessionNameFlag = (byte) ((b >>> 2) & 0x01);
    ssh.setSessionNameFlag(sessionNameFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "sessionNameFlag=" + sessionNameFlag);
    
    byte domainFlag = (byte) ((b >>> 1) & 0x01);
    ssh.setDomainFlag(domainFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "domainFlag=" + domainFlag);
    
    byte authenticationIdFlag = (byte) ((b >>> 0) & 0x01);
    ssh.setAuthenticationIdFlag(authenticationIdFlag);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "authenticationIdFlag=" + authenticationIdFlag);
    
    if (sourceIdFlag > 0) {
      int sourceId = secondaryHeaderDecoder.readByte() & 0xFF;
      ssh.setSourceId(sourceId);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "sourceId=" + sourceId);
    } else {
      ssh.setSourceId(null);
    }
    
    if (destinationIdFlag > 0) {
      int destinationId = secondaryHeaderDecoder.readByte() & 0xFF;
      ssh.setDestinationId(destinationId);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "destinationId=" + destinationId);
    } else {
      ssh.setDestinationId(null);
    }
    
    // Need to decode 'seqFlags' in the fixed part
    if (seqFlags != 3) {
      // TODO: Should be a long (unsigned int)
      int segmentCounter = secondaryHeaderDecoder.read32();
      ssh.setSegmentCounter(segmentCounter);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "segmentCounter=" + segmentCounter);
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "decoded secondary header (fixed part) = " + ssh);
    
    return bufferReader.getIndex();
  }
  
  public static int decodeSecondaryHeaderVarPart(MALSPPSecondaryHeader ssh,
      byte[] bytes, int offset, AppConfiguration qosConfiguration) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALSPPHelper.decodeSecondaryHeaderVarPart("
          + qosConfiguration + ')');
    
    BufferReader bufferReader = new BufferReader(bytes, offset);
    BufferDecoder secondaryHeaderDecoder = new BufferDecoder(bufferReader);
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
          "varintSupported=" + qosConfiguration.isVarintSupported());

    secondaryHeaderDecoder.setVarintSupported(qosConfiguration.isVarintSupported());
    
    if (ssh.getPriorityFlag() > 0) {
      long priority = secondaryHeaderDecoder.readUnsignedInt();
      ssh.setPriority(priority);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "priority=" + priority);
    }
    
    if (ssh.getTimestampFlag() > 0) {
      long timestamp = qosConfiguration.getTimeDecoder().decode(secondaryHeaderDecoder);
      ssh.setTimestamp(timestamp);
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "timestamp=" + timestamp);
    }
    
    if (ssh.getNetworkZoneFlag() > 0) {
      String networkZone = secondaryHeaderDecoder.readString();
      ssh.setNetworkZone(new Identifier(networkZone));
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "networkZone=" + networkZone);
    }
    
    if (ssh.getSessionNameFlag() > 0) {
      String sessionName = secondaryHeaderDecoder.readString();
      ssh.setSessionName(new Identifier(sessionName));
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "sessionName=" + sessionName);
    }
    
    if (ssh.getDomainFlag() > 0) {
      IdentifierList idList = new IdentifierList();
      int size = secondaryHeaderDecoder.readUnsignedInt();
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "domain size=" + size);
      for (int i = 0; i < size; i++) {
        String s = secondaryHeaderDecoder.readNullableString();
        if (s != null) {
          Identifier id = new Identifier(s);
          idList.add(id);
        } else {
          idList.add(null);
        }
      }
      ssh.setDomain(idList);
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "idList=" + idList);
    }
    
    if (ssh.getAuthenticationIdFlag() > 0) {
      byte[] authenticationId = secondaryHeaderDecoder.readByteArray();
      ssh.setAuthenticationId(authenticationId);
    }
    
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "decoded secondary header (var part) = " + ssh);
    
    return bufferReader.getIndex();
  }
  
  public static String getStringProperty(Map properties, String propName) {
    if (properties == null) return null;
    return (String) properties.get(propName); 
  }

  public static Integer getIntegerProperty(Map properties, String propName) {
    if (properties == null) return null;
    Object propValue = properties.get(propName);
    if (propValue == null) return null;
    if (propValue instanceof Integer) {
      return (Integer) propValue;
    } else {
      Integer intValue = Integer.parseInt((String) propValue);
      properties.put(propValue, intValue);
      return intValue;
    }
  }
  
  public static Boolean getBooleanProperty(Map properties, String propName) {
    if (properties == null) return null;
    Object propValue = properties.get(propName);
    if (propValue == null) return null;
    if (propValue instanceof Boolean) {
      return (Boolean) propValue;
    } else {
      Boolean booleanValue = Boolean.parseBoolean((String) propValue);
      properties.put(propValue, booleanValue);
      return booleanValue;
    }
  }
  
  public static MALTransmitErrorException createTransmitException(MALMessageHeader header, String msg, Map properties) {
    return new MALTransmitErrorException(header, new MALStandardError(MALHelper.INTERNAL_ERROR_NUMBER, new Union(msg)), properties);
  }
  
  public static String toUri(String protocol, int apidQualifier, int apid, Integer endpointId) {
    StringBuffer sb = new StringBuffer();
    sb.append(protocol);
    sb.append(':');
    sb.append(apidQualifier);
    sb.append('/');
    sb.append(apid);
    if (endpointId != null) {
      sb.append('/');
      sb.append(endpointId);
    }
    return sb.toString();
  }
  
  public static Object newInstance(String propName, Map properties) throws MALException {
    String className = (String) properties.get(propName);
    if (className != null) {
      try {
        Class clazz = Class.forName(className);
          return clazz.newInstance();
      } catch (ClassNotFoundException e) {
        throw new MALException("", e);
      } catch (InstantiationException e) {
        throw new MALException("", e);
      } catch (IllegalAccessException e) {
        throw new MALException("", e);
      }
    }
    return null;
  }
  
  public static byte getFlag(String flagName, Map messageProperties,
      Map endpointProperties) {
    if (messageProperties != null) {
      Boolean flag = (Boolean) messageProperties.get(flagName);
      if (flag != null) {
        if (flag)
          return 1;
        else
          return 0;
      }
    }
    if (endpointProperties != null) {
      Boolean flag = (Boolean) endpointProperties.get(flagName);
      if (flag != null) {
        if (flag)
          return 1;
        else
          return 0;
      }
    }
    // Default is true
    return 1;
  }
  
  public static void decodeMessageHeaderFixedPart(
      MALSPPMessageHeader malHeader,
      SpacePacketHeader header,
      MALSPPSecondaryHeader secHeader,
      URI urito, URI urifrom, AppConfiguration qosConfig) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.decodeMessageHeaderFixedPart("
          + header + ',' + secHeader + ',' + urito + ','
          + urifrom + ',' + qosConfig + ')');
    MALArea area = MALContextFactory
        .lookupArea(new UShort(secHeader.getArea()), new UOctet((short) secHeader.getAreaVersion()));
    MALService service = area.getServiceByNumber(
        new UShort(secHeader.getService()));
    MALOperation op = service.getOperationByNumber(new UShort(secHeader
        .getOperation()));
    
    malHeader.setURIFrom(urifrom);
    malHeader.setURITo(urito);
    malHeader.setQoSlevel(QoSLevel.fromOrdinal(secHeader.getQos()));
    malHeader.setSession(SessionType.fromOrdinal(secHeader
        .getSession()));
    malHeader.setInteractionType(MALSPPHelper.getInteractionType(
        secHeader.getSduType()));
    malHeader.setInteractionStage(MALSPPHelper.getInteractionStage(
        secHeader.getSduType()));
    malHeader.setTransactionId(secHeader.getTransactionId());
    malHeader.setServiceArea(service.getArea().getNumber());
    malHeader.setService(service.getNumber());
    malHeader.setOperation(op.getNumber());
    malHeader.setAreaVersion(area.getVersion());
    malHeader.setIsErrorMessage(secHeader.getIsError() == 1 ? true : false);
  }
  
  public static void decodeMessageHeaderVarPart(
      MALSPPMessageHeader malHeader,
      MALSPPSecondaryHeader secHeader,
      AppConfiguration qosConfig) throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.decodeMessageHeaderVarPart("
          + secHeader + ',' + qosConfig + ')');
    
    UInteger priority;
    if (secHeader.getPriorityFlag() == 1) {
      priority = new UInteger(secHeader.getPriority());
    } else {
      priority = qosConfig.getPriority();
      if (priority == null) {
        priority = MALSPPHelper.DEFAULT_PRIORITY;
      }
    }
    
    Time timestamp;
    if (secHeader.getTimestampFlag() == 1) {
      timestamp = new Time(secHeader.getTimestamp());
    } else {
      timestamp = new Time(0);
    }
    
    Identifier networkZone;
    if (secHeader.getNetworkZoneFlag() == 1) {
      networkZone = secHeader.getNetworkZone();
    } else {
      networkZone = qosConfig.getNetworkZone();
      if (networkZone == null) {
        networkZone = MALSPPHelper.DEFAULT_NETWORK_ZONE;
      }
    }
    
    Identifier sessionName;
    if (secHeader.getSessionNameFlag() == 1) {
      sessionName = secHeader.getSessionName();
    } else {
      sessionName = qosConfig.getSessionName();
      if (sessionName == null) {
        sessionName = MALSPPHelper.DEFAULT_SESSION_NAME;
      }
    }
    
    IdentifierList domain;
    if (secHeader.getDomainFlag() == 1) {
      domain = secHeader.getDomain();
    } else {
      domain = qosConfig.getDomain();
      if (domain == null) {
        domain = MALSPPHelper.DEFAULT_DOMAIN;
      }
    }
    
    Blob authenticationId;
    if (secHeader.getAuthenticationIdFlag() == 1) {
      authenticationId = new Blob(secHeader.getAuthenticationId());
    } else {
      authenticationId = qosConfig.getAuthenticationId();
      if (authenticationId == null) {
        authenticationId = MALSPPHelper.DEFAULT_AUTHENTICATION_ID;
      }
    }

    malHeader.setAuthenticationId(authenticationId);
    malHeader.setTimestamp(timestamp);
    malHeader.setPriority(priority);
    malHeader.setDomain(domain);
    malHeader.setNetworkZone(networkZone);
    malHeader.setSessionName(sessionName);
  }
  
  public static UOctet getErrorStage(InteractionType interactionType, 
      UOctet interactionStage) {
    switch (interactionType.getOrdinal()) {
    case InteractionType._SUBMIT_INDEX:
    case InteractionType._REQUEST_INDEX:
    case InteractionType._INVOKE_INDEX:
    case InteractionType._PROGRESS_INDEX:
      if (interactionStage.getValue() == (byte) 0x01) {
        return new UOctet((short) 0x02);
      }
      break;
    case InteractionType._PUBSUB_INDEX:
      if (interactionStage.getValue() == MALPubSubOperation._REGISTER_STAGE) {
        return MALPubSubOperation.REGISTER_ACK_STAGE;
      } else if (interactionStage.getValue() == MALPubSubOperation._DEREGISTER_STAGE) {
        return MALPubSubOperation.DEREGISTER_ACK_STAGE;
      } else if (interactionStage.getValue() == MALPubSubOperation._PUBLISH_REGISTER_STAGE) {
        return MALPubSubOperation.PUBLISH_REGISTER_ACK_STAGE;
      } else if (interactionStage.getValue() == MALPubSubOperation._PUBLISH_DEREGISTER_STAGE) {
        return MALPubSubOperation.PUBLISH_DEREGISTER_ACK_STAGE;
      }
    }
    return null;
  }
  
}