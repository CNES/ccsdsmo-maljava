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
import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.encoding.MALElementOutputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEndpoint;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorException;
import org.ccsds.moims.mo.mal.transport.MALTransport;
import org.ccsds.moims.mo.testbed.util.spp.SPPSocket;
import org.ccsds.moims.mo.testbed.util.spp.SPPSocketFactory;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacket;
import org.ccsds.moims.mo.testbed.util.spp.SpacePacketHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.encoding.binary.BufferOutputStream;
import fr.cnes.encoding.binary.Encoder;
import fr.cnes.encoding.binary.JavaTimeDecoder;
import fr.cnes.encoding.binary.JavaTimeEncoder;
import fr.cnes.encoding.binary.TimeDecoder;
import fr.cnes.encoding.binary.TimeEncoder;
import fr.cnes.malspp.transport.MALSPPEndpoint.PacketCounter;
import fr.dyade.aaa.common.Daemon;

public class MALSPPTransport implements MALTransport {

  public final static Logger logger = fr.dyade.aaa.common.Debug
      .getLogger(MALSPPTransport.class.getName());
  
  public static final String CONF_FILE = "fr.cnes.malspp.transport.conf.file";

  public static final String APID_QUALIFIER = "org.ccsds.moims.mo.malspp.apidQualifier";
  
  public static final String APID = "org.ccsds.moims.mo.malspp.apid";
  
  public static final String APPEND_ID_TO_URI = "org.ccsds.moims.mo.malspp.appendIdToUri";
  
  public static final String IS_TC_PACKET = "org.ccsds.moims.mo.malspp.isTcPacket";
  
  public static final String AUTHENTICATION_ID_FLAG = "org.ccsds.moims.mo.malspp.authenticationIdFlag";
  
  public static final String DOMAIN_FLAG = "org.ccsds.moims.mo.malspp.domainFlag";
  
  public static final String NETWORK_ZONE_FLAG = "org.ccsds.moims.mo.malspp.networkZoneFlag";
  
  public static final String PRIORITY_FLAG = "org.ccsds.moims.mo.malspp.priorityFlag";
  
  public static final String SESSION_NAME_FLAG = "org.ccsds.moims.mo.malspp.sessionNameFlag";
  
  public static final String TIMESTAMP_FLAG = "org.ccsds.moims.mo.malspp.timestampFlag";
  
  public static final String DEFAULT_APID_QUALIFIER_PROP = "fr.cnes.malspp.transport.default.apidQualifier";
  
  public static final String DEFAULT_APID_PROP = "fr.cnes.malspp.transport.default.apid";
  
  public static final String DEFAULT_IS_TC_PACKET_PROP = "fr.cnes.malspp.transport.default.isTcPacket";
  
  public final static String TIMEOUT = "fr.cnes.malspp.transport.timeout";
  
  public static final int DEFAULT_APID_QUALIFIER = 247;
  
  public static final int DEFAULT_APID = 0;
  
  public static final int DEFAULT_PACKET_TYPE = 1;
  
  public static final int DEFAULT_TIMEOUT = 10000;

  public static final String DOMAIN = "fr.cnes.malspp.transport.domain";

  public static final String INSTANCE_ID = "fr.cnes.malspp.transport.instanceId";
  
  public static final String INSTANCE_ID_COUNTER = "fr.cnes.malspp.transport.instanceId.counter";

  public static final String NETWORK_ZONE = "fr.cnes.malspp.transport.network.zone";

  public final static String APPL_TIME_ENCODER = "fr.cnes.malspp.transport.appl.time.encoder";

  public final static String APPL_TIME_DECODER = "fr.cnes.malspp.transport.appl.time.decoder";
  
  public static final int MAX_APID_QUALIFIER = 65535;
  
  public static final int MAX_APID = 2046;
  
  public static final int MAX_ENDPOINT_ID = 255;
  
  private SPPSocket spacePacketSocket;

  private Map properties;
  
  private Map<URI, MALSPPEndpoint> uriToEndpoints;

  private Map<String, MALSPPEndpoint> nameToEndpoints;

  private String malName;

  private MALElementStreamFactory elementStreamFactory;

  private ReaderDaemon readerDaemon;

  private String protocol;

  private Integer defaultApid;
  
  private Integer defaultApidQualifier;
  
  private Integer defaultPacketType;

  private Integer instanceIdCounter;

  private AppConfiguration defaultQosConfiguration;

  /**
   * Key format: <QualifiedAPID>/<APID qualifier>
   */
  private HashMap<QualifiedAPID, AppConfiguration> qosConfigurations;
  
  private Integer timeout;
  
  private Hashtable<QualifiedAPID, PacketCounter> sequenceCounters;
  
  private Hashtable<SegmentationKey, PacketCounter> segmentCounters;
  
  private Hashtable<SegmentationKey, SegmentationContext> segmentContexts;
  
  public void init(String protocol, Map properties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALTransport.init(" + properties + ')');
    this.protocol = protocol;
    this.properties = properties;
    try {
      spacePacketSocket = createSocket(properties);
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.ERROR))
        logger.log(BasicLevel.ERROR, "", exc);
      throw new MALException(exc.toString());
    }
    
    uriToEndpoints = new Hashtable<URI, MALSPPEndpoint>();
    nameToEndpoints = new Hashtable<String, MALSPPEndpoint>();
    
    sequenceCounters = new Hashtable<MALSPPTransport.QualifiedAPID, PacketCounter>();
    
    segmentCounters = new Hashtable<SegmentationKey, PacketCounter>();
    segmentContexts = new Hashtable<SegmentationKey, SegmentationContext>();
    
    qosConfigurations = new HashMap<QualifiedAPID, AppConfiguration>();
    
    AppConfigurationParser qosConfigurationParser = new AppConfigurationParser(
        qosConfigurations);
    String confFilePath = MALSPPHelper.getStringProperty(properties, CONF_FILE);
    if (confFilePath != null) {
      if (new File(confFilePath).exists()) {
        try {
          qosConfigurationParser.parse(confFilePath);
        } catch (Exception e) {
          if (logger.isLoggable(BasicLevel.WARN))
            logger.log(BasicLevel.WARN, "", e);
          throw new MALException("Parse error", e);
        }
      } else {
        throw new MALException("Configuration file not found: "
              + confFilePath);
      }
      defaultQosConfiguration = qosConfigurationParser.getDefaultApp();
      if (defaultQosConfiguration == null)
        throw new MALException(
            "At least one MAL/SPP configuration needs to be defined in: "
                + confFilePath);
    } else {
      throw new MALException("MAL/SPP property not defined: " + CONF_FILE);
    }

    malName = (String) properties.get("fr.cnes.mal.name");
    if (malName == null)
      malName = "MAL";

    elementStreamFactory = MALElementStreamFactory.newFactory(protocol,
        properties);
    
    defaultApidQualifier = MALSPPHelper.getIntegerProperty(properties,
        DEFAULT_APID_QUALIFIER_PROP);
    if (defaultApidQualifier == null) {
      defaultApidQualifier = DEFAULT_APID_QUALIFIER;
    }

    defaultApid = MALSPPHelper.getIntegerProperty(properties, DEFAULT_APID_PROP);
    if (defaultApid == null) {
      defaultApid = DEFAULT_APID;
    }
    
    Boolean isTc = MALSPPHelper.getBooleanProperty(properties, DEFAULT_IS_TC_PACKET_PROP);
    if (isTc == null) {
      defaultPacketType = DEFAULT_PACKET_TYPE;
    } else if (isTc) {
      defaultPacketType = 1;
    } else {
      defaultPacketType = 0;
    }

    instanceIdCounter = MALSPPHelper
        .getIntegerProperty(properties, INSTANCE_ID_COUNTER);
    if (instanceIdCounter == null) {
      instanceIdCounter = 0;
    }
    
    timeout = MALSPPHelper
        .getIntegerProperty(properties, TIMEOUT);
    if (timeout == null) {
      timeout = DEFAULT_TIMEOUT;
    }

    readerDaemon = new ReaderDaemon(spacePacketSocket);
    readerDaemon.start();
  }
  
  private PacketCounter getSequenceCount(QualifiedAPID primaryQApid) {
    PacketCounter sequenceCount = sequenceCounters.get(primaryQApid);
    if (sequenceCount == null) {
      sequenceCount = new PacketCounter(0);
    }
    return sequenceCount;
  }

  public Integer getTimeout() {
    return timeout;
  }

  public String getProtocol() {
    return protocol;
  }

  public Integer getDefaultPacketType() {
    return defaultPacketType;
  }

  public MALElementStreamFactory getElementStreamFactory() {
    return elementStreamFactory;
  }

  static SPPSocket createSocket(Map properties) throws Exception {
    SPPSocketFactory socketFactory = SPPSocketFactory.newInstance();
    return socketFactory.createSocket(properties);
  }

  public void close() throws MALException {
    if (readerDaemon != null) {
      readerDaemon.stop();
      readerDaemon = null;
    }
    if (spacePacketSocket != null) {
      try {
        spacePacketSocket.close();
      } catch (Exception e) {
      }
    }
  }

  public synchronized MALEndpoint createEndpoint(String localName,
      Map qosProperties) throws MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALTransport.createEndPoint("
          + localName + ',' + qosProperties + ')');
    
    if (localName != null && nameToEndpoints.get(localName) != null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "endPoints=" + nameToEndpoints);
      throw new MALException("Name already used: " + localName);
    }
    
    Integer apidQualifier = MALSPPHelper.getIntegerProperty(qosProperties, APID_QUALIFIER);
    if (apidQualifier == null) {
      apidQualifier = defaultApidQualifier;
    }
    
    Integer apid = MALSPPHelper.getIntegerProperty(qosProperties, APID);
    if (apid == null) {
      apid = defaultApid;
    }
    
    Boolean appendIdToUri = MALSPPHelper.getBooleanProperty(qosProperties, APPEND_ID_TO_URI);
    if (appendIdToUri == null) {
      appendIdToUri = Boolean.TRUE;
    }

    Integer endpointId;
    if (appendIdToUri) {
      endpointId = MALSPPHelper.getIntegerProperty(qosProperties, INSTANCE_ID);
      if (endpointId == null) {
        endpointId = instanceIdCounter++;
      }
    } else {
      endpointId = null;
    }

    URI uri = new URI(MALSPPHelper.toUri(protocol, apidQualifier, apid, endpointId));
    
    if (uriToEndpoints.get(uri) != null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "uriToEndpoints=" + uriToEndpoints);
      throw new MALException("URI already used: " + uri);
    }

    MALSPPEndpoint endpoint = new MALSPPEndpoint(this, apidQualifier, apid, endpointId,
      qosProperties);
    
    if (localName != null) {
      nameToEndpoints.put(localName, endpoint);
    }
    uriToEndpoints.put(uri, endpoint);
    
    return endpoint;
  }

  public MALBrokerBinding createBroker(MALEndpoint endPoint,
      Blob authenticationId, QoSLevel[] expectedQos, int priorityLevelNumber,
      Hashtable qosProperties) throws MALException {
    throw new MALException("Not implemented");
  }

  /*
  void addEndpoint(MALSPPEndpoint endPoint) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG,
          "SPPMALTransport.addEndPoint(" + endPoint.getURI() + ')');
    endpoints.put(endPoint.getURI().getValue(), endPoint);
  }

  void removeEndpoint(URI endPointUri) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALTransport.removeEndPoint("
          + endPointUri + ')');
    endpoints.remove(endPointUri.getValue());
  }*/

  public void deleteEndpoint(String localName) throws MALException {
    // TODO Auto-generated method stub

  }

  public boolean isSupportedInteractionType(InteractionType type) {
    return (type.getOrdinal() != InteractionType._PUBSUB_INDEX);
  }

  public boolean isSupportedQoSLevel(QoSLevel qos) {
    return true;
  }

  void send(SpacePacket packet)
      throws Exception {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALTransport.send("
          + packet + ')');
    spacePacketSocket.send(packet);
  }

  public String getMBeanName() {
    return "SPPMALTransport-" + spacePacketSocket.getDescription();
  }

  public final String getMALName() {
    return malName;
  }

  public MALEndpoint getEndpoint(String localName) {
    return nameToEndpoints.get(localName);
  }

  public AppConfiguration getAppConfiguration(QualifiedAPID primaryQApid) {
    AppConfiguration qosConfig = qosConfigurations.get(primaryQApid);
    if (qosConfig == null) {
      qosConfig = defaultQosConfiguration;
    }
    return qosConfig;
  }

  class ReaderDaemon extends Daemon {

    private SPPSocket spacePacketSocket;

    protected ReaderDaemon(SPPSocket spacePacketSocket) {
      super("SPPMALTransport.Daemon", logger);
      this.spacePacketSocket = spacePacketSocket;
    }

    protected void close() {

    }

    protected void shutdown() {
      // TODO Auto-generated method stub

    }

    public void run() {
      try {
        loop: while (running) {
          canStop = true;

          SpacePacket packet = spacePacketSocket.receive();
          
          // Check size and trim if necessary
          int packetDataLength = packet.getLength();
          if (packet.getBody().length > packetDataLength) {
            byte[] newBody = new byte[packetDataLength];
            System.arraycopy(packet.getBody(), 0, newBody, 0, packetDataLength);
            packet.setBody(newBody);
          }

          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG,
                "received packet header: " + packet.getHeader());
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "packet length=" + packet.getLength());
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "apid qualifier=" + packet.getApidQualifier());
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "apid=" + packet.getHeader().getApid());

          AppConfiguration appConf = getAppConfiguration(new QualifiedAPID(
              packet.getApidQualifier(), packet.getHeader().getApid()));
          
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "appConf=" + appConf);

          // Decode the secondary header
          // TimeHelper.getBitLength(TimeHelper.CUC_TIME)
          MALSPPSecondaryHeader secHeader = new MALSPPSecondaryHeader();
          int offset = MALSPPHelper.decodeSecondaryHeaderFixedPart(secHeader, packet
              .getBody(), packet.getOffset(), packet.getHeader()
              .getSequenceFlags(), appConf);

          // DF: not in the new MAL/SPP specification
          // MALSPPUserDataField userDataField = new MALSPPUserDataField();
          // offset = MALSPPHelper.decodeUserData(userDataField,
          // packet.getBody(),
          // offset, packet.getHeader().getPacketType(),
          // secHeader.getSduType());

          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "offset=" + offset);

          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, malName
                + " - received packet secondary header: " + secHeader);

          int packetType = packet.getHeader().getPacketType();
          
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, malName
                + " - packetType: " + packetType);
          
          int destApidQualifier;
          int destApid;
          int sourceApidQualifier;
          int sourceApid;
          if (packetType == 0) {
            // TM: primary = source, secondary = dest
            sourceApidQualifier = packet.getApidQualifier();
            sourceApid = packet.getHeader().getApid();
            destApidQualifier = secHeader.getSecondaryApidQualifier();
            destApid = secHeader.getSecondaryApid();
          } else {
            // TC: primary = dest, secondary = source
            destApidQualifier = packet.getApidQualifier();
            destApid = packet.getHeader().getApid();
            sourceApidQualifier = secHeader.getSecondaryApidQualifier();
            sourceApid = secHeader.getSecondaryApid();
          }
          
          String strUriTo = MALSPPHelper.toUri(protocol,
              destApidQualifier, destApid,
              secHeader.getDestinationId());

          String strUriFrom = MALSPPHelper.toUri(protocol,
              sourceApidQualifier, sourceApid,
              secHeader.getSourceId());

          // Create the MAL message header
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "get endpoint " + strUriTo);

          URI uriTo = new URI(strUriTo);
          
          MALSPPMessageHeader malHeader = new MALSPPMessageHeader();
          MALSPPHelper.decodeMessageHeaderFixedPart(malHeader, packet.getHeader(),
              secHeader, uriTo, new URI(strUriFrom), appConf);

          offset = MALSPPHelper.decodeSecondaryHeaderVarPart(secHeader,
              packet.getBody(), offset, appConf);
          MALSPPHelper.decodeMessageHeaderVarPart(malHeader, secHeader, appConf);

          MALSPPEndpoint ep = (MALSPPEndpoint) uriToEndpoints.get(uriTo);
          if (ep == null) {
            // It means that this packet is not expected by this MAL process
            // even if it is listening to the same LDP
            if (logger.isLoggable(BasicLevel.DEBUG))
              logger.log(BasicLevel.DEBUG, malName + ": ignored packet "
                  + strUriTo + " is not handled by this MAL client: "
                  + uriToEndpoints);
            UOctet errorStage = MALSPPHelper
                .getErrorStage(malHeader.getInteractionType(),
                    malHeader.getInteractionStage());
            if (errorStage != null) {
              MALSPPMessageHeader errorHeader = new MALSPPMessageHeader(
                  malHeader.getURITo(), malHeader.getAuthenticationId(),
                  malHeader.getURIFrom(), new Time(System.currentTimeMillis()),
                  malHeader.getQoSlevel(), malHeader.getPriority(),
                  malHeader.getDomain(), malHeader.getNetworkZone(),
                  malHeader.getSession(), malHeader.getSessionName(),
                  malHeader.getInteractionType(), errorStage,
                  malHeader.getTransactionId(), malHeader.getServiceArea(),
                  malHeader.getService(), malHeader.getOperation(),
                  malHeader.getAreaVersion(), Boolean.TRUE);
              sendSpacePacket(errorHeader, packetType, null, null,
                  new MALSPPMessageBody(new Object[] {
                      MALHelper.DESTINATION_UNKNOWN_ERROR_NUMBER, null }));
            }
            continue loop;
          }

          if (packet.getHeader().getSequenceFlags() == 3) {
            ep.deliverMessage(malHeader, packet.getBody(), offset,
                packet.getLength() - offset, appConf);
          } else {
            SegmentationKey sk = new SegmentationKey(malHeader.getInteractionType(),
                malHeader.getTransactionId(), malHeader.getURIFrom(),
                malHeader.getURITo(),  malHeader.getSession(), 
                malHeader.getSessionName(),
                malHeader.getDomain(), malHeader.getNetworkZone(),
                malHeader.getServiceArea(),
                malHeader.getService(), malHeader.getOperation());
            SegmentationContext segmentContext = segmentContexts.get(sk);
            if (segmentContext == null) {
              segmentContext = new SegmentationContext(malHeader);
              segmentContexts.put(sk, segmentContext);
            }
            ep.segmentReceived(malHeader,
                packet.getHeader().getSequenceFlags(), secHeader,
                segmentContext, packet.getBody(), offset, packet.getLength()
                    - offset, appConf);
          }
        }
      } catch (Exception exc) {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, "SPPMALTransport.ReaderDaemon", exc);
        try {
          spacePacketSocket.close();
        } catch (Exception exc2) {
        }
        MALException malExc = new MALException(exc.toString());
        Collection<MALSPPEndpoint> endpoints = uriToEndpoints.values();
        for (MALSPPEndpoint ep : endpoints) {
          ep.onException(malExc);
        }
      } finally {
        if (logger.isLoggable(BasicLevel.WARN))
          logger.log(BasicLevel.WARN, ", exited");
        finish();
      }
    }
  }

  public MALBrokerBinding createBroker(String localName, Blob authenticationId,
      QoSLevel[] expectedQos, int priorityLevelNumber, Hashtable qosProperties)
      throws MALException {
    throw new MALException("Not provided");
  }

  public MALEndpoint getEndpoint(URI uri) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALSPPTransport.getEndpoint(" + uri
          + "), uriToEndpoints=" + uriToEndpoints);
    return uriToEndpoints.get(uri);
  }

  public MALBrokerBinding createBroker(String localName, Blob authenticationId,
      QoSLevel[] expectedQos, UInteger priorityLevelNumber, Map qosProperties)
      throws MALException {
    // TODO Auto-generated method stub
    return null;
  }

  public MALBrokerBinding createBroker(MALEndpoint endPoint,
      Blob authenticationId, QoSLevel[] expectedQos,
      UInteger priorityLevelNumber, Map qosProperties) throws MALException {
    // TODO Auto-generated method stub
    return null;
  }
  
  synchronized void sendSpacePacket(MALMessageHeader malHeader, int packetType, Map qosProperties,
      Map messageProperties, MALMessageBody body) throws MALTransmitErrorException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "SPPMALEndpoint.sendSpacePacket("
          + malHeader + ')');
    try {
      MALArea area = MALContextFactory.lookupArea(malHeader.getServiceArea(), malHeader.getAreaVersion());
      if (area == null) {
        throw new MALException("Unknown area: " + malHeader.getServiceArea());
      }
      MALService service = area.getServiceByNumber(
          malHeader.getService());
      if (service == null) {
        throw new MALException("Unknown service: " + malHeader.getService());
      }
      MALOperation op = service.getOperationByNumber(malHeader.getOperation());
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "area=" + service.getArea());
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "op=" + op);
      
      if (messageProperties != null && messageProperties.size() > 0
          && qosProperties != null) {
        HashMap newQoSProperties = new HashMap();
        newQoSProperties.putAll(qosProperties);
        newQoSProperties.putAll(messageProperties);
        qosProperties = newQoSProperties;
      }

      int packetVersionNumber = 0;
      int dataFieldHeaderFlag = 1;

      int sduType = MALSPPHelper.getSDUType(malHeader.getInteractionType(),
          malHeader.getInteractionStage(), malHeader.getIsErrorMessage());

      MALSPPURI uriFrom = MALSPPURI.parseURI(malHeader.getURIFrom());
      MALSPPURI uriTo = MALSPPURI.parseURI(malHeader.getURITo());
      
      int primaryApidQualifier;
      int primaryApid;
      int secondaryApidQualifier;
      int secondaryApid;
      if (packetType == 0) {
        // TM: primary = source, secondary = dest
        primaryApidQualifier = uriFrom.getApidQualifier();
        primaryApid = uriFrom.getApid();
        secondaryApidQualifier = uriTo.getApidQualifier();
        secondaryApid = uriTo.getApid();
      } else {
        // TC: primary = dest, secondary = source
        primaryApidQualifier = uriTo.getApidQualifier();
        primaryApid = uriTo.getApid();
        secondaryApidQualifier = uriFrom.getApidQualifier();
        secondaryApid = uriFrom.getApid();
      }
      
      QualifiedAPID primaryQApid = new QualifiedAPID(primaryApidQualifier, primaryApid);
      AppConfiguration appConf = getAppConfiguration(primaryQApid);
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "primaryQApid=" + primaryQApid);
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "appConf=" + appConf);
      
      int packetDataFieldSizeLimit = appConf.getPacketDataFieldSizeLimit();

      // Binary 11 means "stand-alone packet"
      int sequenceFlag = 3;
      
      PacketCounter sequenceCount = getSequenceCount(primaryQApid);
      
      SpacePacketHeader sph = new SpacePacketHeader(packetVersionNumber,
          packetType, dataFieldHeaderFlag, primaryApid, sequenceFlag,
          (int) sequenceCount.getValue());
      
      // Sequence counter is incremented later
      
      MALSPPSecondaryHeader spsh = new MALSPPSecondaryHeader();
      spsh.setArea(service.getArea().getNumber().getValue());
      spsh.setSduType(sduType);
      spsh.setIsError(malHeader.getIsErrorMessage().booleanValue() ? 1 : 0);

      spsh.setService(service.getNumber().getValue());
      spsh.setAreaVersion(service.getArea().getVersion().getValue());
      
      spsh.setQos(malHeader.getQoSlevel().getOrdinal());
      spsh.setSession(malHeader.getSession().getOrdinal());
      
      spsh.setSecondaryApidQualifier(secondaryApidQualifier);
      spsh.setSecondaryApid(secondaryApid);
      
      byte priorityFlag = MALSPPHelper.getFlag(MALSPPTransport.PRIORITY_FLAG, messageProperties, qosProperties);
      spsh.setPriorityFlag(priorityFlag);
      byte timestampFlag = MALSPPHelper.getFlag(MALSPPTransport.TIMESTAMP_FLAG, messageProperties, qosProperties);
      spsh.setTimestampFlag(timestampFlag);
      byte networkZoneFlag = MALSPPHelper.getFlag(MALSPPTransport.NETWORK_ZONE_FLAG, messageProperties, qosProperties);
      spsh.setNetworkZoneFlag(networkZoneFlag);
      byte sessionNameFlag = MALSPPHelper.getFlag(MALSPPTransport.SESSION_NAME_FLAG, messageProperties, qosProperties);
      spsh.setSessionNameFlag(sessionNameFlag);
      byte domainFlag = MALSPPHelper.getFlag(MALSPPTransport.DOMAIN_FLAG, messageProperties, qosProperties);
      spsh.setDomainFlag(domainFlag);
      byte authenticationIdFlag = MALSPPHelper.getFlag(MALSPPTransport.AUTHENTICATION_ID_FLAG, messageProperties, qosProperties);
      spsh.setAuthenticationIdFlag(authenticationIdFlag);
      
      if (uriFrom.getEndpointId() == null) {
        spsh.setSourceIdFlag((byte) 0);
      } else {
        spsh.setSourceIdFlag((byte) 1);
        spsh.setSourceId(uriFrom.getEndpointId());
      }
      
      if (uriTo.getEndpointId() == null) {
        spsh.setDestinationIdFlag((byte) 0);
      } else {
        spsh.setDestinationIdFlag((byte) 1);
        spsh.setDestinationId(uriTo.getEndpointId());
      }
      
      spsh.setPriority(malHeader.getPriority().getValue());
      spsh.setNetworkZone(malHeader.getNetworkZone());
      spsh.setSessionName(malHeader.getSessionName());
      spsh.setDomain(malHeader.getDomain());
      spsh.setAuthenticationId(malHeader.getAuthenticationId()
          .getValue());
      
      spsh.setOperation(op.getNumber().getValue());
      Long malTid = malHeader.getTransactionId();
      int tid = malTid.intValue();
      spsh.setTransactionId(tid);
      long timestamp = malHeader.getTimestamp().getValue();
      spsh.setTimestamp(timestamp);

      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "Send packet, secondary header=" + spsh);

      MALEncodingContext msgCtx = new MALEncodingContext(malHeader, op, 0,
          qosProperties, messageProperties);
      
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      MALElementStreamFactory elementStreamFactory = appConf.getElementStreamFactory();
      MALElementOutputStream meos = elementStreamFactory.createOutputStream(baos);
      
      // Encode the body
      for (int i = 0; i < body.getElementCount(); i++) {
        msgCtx.setBodyElementIndex(i);
        Object bodyElement = body.getBodyElement(i, null);
        meos.writeElement(bodyElement, msgCtx);
      }
      meos.flush();
      byte[] encodedBody = baos.toByteArray();
      
      // Compute the size of the secondary header (unsegmented, without the
      // field 'segment counter')
      int secondaryHeaderFixedPartSize = MALSPPHelper
          .getSecondaryHeaderFixedPartSize(spsh, appConf);
      
      byte[] encodedSecHeaderFixedPart = MALSPPHelper
          .encodeSecondaryHeaderFixedPart(spsh, appConf,
              secondaryHeaderFixedPartSize);

      byte[] encodedSecHeaderVarPart = MALSPPHelper
          .encodeSecondaryHeaderVariablePart(spsh, appConf);
      
      // Without segment counter
      int secHeaderSize = secondaryHeaderFixedPartSize
          + encodedSecHeaderVarPart.length;

      byte[] data;
      int wholeSize = secHeaderSize + encodedBody.length;
      
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "wholeSize=" + wholeSize);
      
      if (wholeSize > packetDataFieldSizeLimit) {
        // Segmentation
        SegmentationKey sk = new SegmentationKey(
            malHeader.getInteractionType(), malHeader.getTransactionId(),
            malHeader.getURIFrom(), malHeader.getURITo(),
            malHeader.getSession(), malHeader.getSessionName(),
            malHeader.getDomain(), malHeader.getNetworkZone(),
            malHeader.getServiceArea(), malHeader.getService(),
            malHeader.getOperation());
        
        // Get the segment counter
        // TODO: should be synchronized in case of parallel execution for the
        // same segmentation context
        PacketCounter segmentCounter = segmentCounters.get(sk);
        if (segmentCounter == null) {
          segmentCounter = new PacketCounter(0);
          segmentCounters.put(sk, segmentCounter);
        }
        
        // Add the segment counter field
        secHeaderSize += 4;
        if (!(secHeaderSize < packetDataFieldSizeLimit))
          throw new MALTransmitErrorException(malHeader, new MALStandardError(
              MALHelper.INTERNAL_ERROR_NUMBER, null), messageProperties);

        int maxEncodedLength = packetDataFieldSizeLimit - secHeaderSize;
        int bodyOffset = 0;
        int remainingToEncode = encodedBody.length;
        while (remainingToEncode > 0) { 
          if (bodyOffset == 0) {
            sph.setSequenceFlags(1);
          } else if (remainingToEncode > maxEncodedLength) {
            sph.setSequenceFlags(0);
          } else {
            sph.setSequenceFlags(2);
          }
          
          // TODO: length should be remainingToEncode + secHeaderSize
          // TODO: should be tested
          data = new byte[packetDataFieldSizeLimit];
          BufferOutputStream buffer = new BufferOutputStream(data);
          Encoder encoder = new Encoder(buffer);
          encoder.setVarintSupported(appConf.isVarintSupported());
          
          buffer.write(encodedSecHeaderFixedPart);
          
          // TODO: Segment counter should be typed long (unsigned int)
          encoder.write32((int) segmentCounter.getValue());
          segmentCounter.increment();
          
          buffer.write(encodedSecHeaderVarPart);
          
          int encodedBodyLength;
          if (remainingToEncode < maxEncodedLength) {
            encodedBodyLength = remainingToEncode;
          } else {
            encodedBodyLength = maxEncodedLength;
          }
          
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "encodedBodyLength=" + encodedBodyLength);
          
          buffer.write(encodedBody, bodyOffset, encodedBodyLength);
          
          // Create a new header for every packet
          SpacePacketHeader sph2 = new SpacePacketHeader(sph.getPacketVersionNumber(),
              sph.getPacketType(), sph.getSecondaryHeaderFlag(), sph.getApid(), sph.getSequenceFlags(),
              (int) sequenceCount.getValue());
          
          SpacePacket packet = new SpacePacket(sph2, primaryApidQualifier, data, 0,
              data.length);
          packet.setQosProperties(qosProperties);
          send(packet);
          
          sequenceCount.increment();
          bodyOffset += encodedBodyLength;
          remainingToEncode -= encodedBodyLength;
          if (logger.isLoggable(BasicLevel.DEBUG))
            logger.log(BasicLevel.DEBUG, "bodyOffset=" + bodyOffset);
        }
      } else {
        // No segmentation
        sph.setSequenceFlags(3);
        data = new byte[wholeSize];
        BufferOutputStream buffer = new BufferOutputStream(data);
        buffer.write(encodedSecHeaderFixedPart);
        // No segment counter
        buffer.write(encodedSecHeaderVarPart);
        buffer.write(encodedBody);
        buffer.close();
        SpacePacket packet = new SpacePacket(sph, primaryApidQualifier, data, 0, data.length);
        packet.setQosProperties(qosProperties);
        send(packet);
        sequenceCount.increment();
      }
   
    } catch (Exception exc) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "", exc);
      throw MALSPPHelper.createTransmitException(malHeader, exc.toString(), messageProperties);
    }
  }

  static class QualifiedAPID {

    private int qualifier;
    
    private int apid;

    public QualifiedAPID(int qualifier, int apid) {
      super();
      this.qualifier = qualifier;
      this.apid = apid;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + apid;
      result = prime * result + qualifier;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj)
        return true;
      if (obj == null)
        return false;
      if (getClass() != obj.getClass())
        return false;
      QualifiedAPID other = (QualifiedAPID) obj;
      if (apid != other.apid)
        return false;
      if (qualifier != other.qualifier)
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "QualifiedAPID [qualifier=" + qualifier + ", apid=" + apid + "]";
    }

  }

}
