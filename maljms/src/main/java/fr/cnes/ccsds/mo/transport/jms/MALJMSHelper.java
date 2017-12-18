package fr.cnes.ccsds.mo.transport.jms;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.jms.Message;

import org.ccsds.moims.mo.mal.MALArea;
import org.ccsds.moims.mo.mal.MALContextFactory;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.MALService;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.EntityRequest;
import org.ccsds.moims.mo.mal.structures.EntityRequestList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Subscription;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateType;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.ccsds.mo.transport.gen.body.GENDeregisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENErrorBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENMessageBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENNotifyBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishRegisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENRegisterBody;

public class MALJMSHelper {
  
  public final static Logger logger = 
      fr.dyade.aaa.common.Debug.getLogger(MALJMSHelper.class.getName());
  
  public static final String AUTHENTICATION_ID_HEADER_FIELD_NAME = "authenticationId";
  public static final String DOMAIN_HEADER_FIELD_NAME = "domain";
  public static final String NETWORK_ZONE_HEADER_FIELD_NAME = "networkZone";
  public static final String SESSION_HEADER_FIELD_NAME = "session";
  public static final String SESSION_NAME_HEADER_FIELD_NAME = "sessionName";
  public static final String INTERACTION_TYPE_HEADER_FIELD_NAME = "interactionType";
  public static final String INTERACTION_STAGE_HEADER_FIELD_NAME = "interactionStage";
  public static final String TRANSACTION_ID_HEADER_FIELD_NAME = "transactionId";
  public static final String AREA_HEADER_FIELD_NAME = "area";
  public static final String SERVICE_HEADER_FIELD_NAME = "service";
  public static final String VERSION_HEADER_FIELD_NAME = "version";
  public static final String OPERATION_HEADER_FIELD_NAME = "operation";
  public static final String QOS_LEVEL_HEADER_FIELD_NAME = "qosLevel";
  public static final String TIMESTAMP_HEADER_FIELD_NAME = "timestamp";
  public static final String IS_ERROR_HEADER_FIELD_NAME = "isError";
  public static final String FROM_HEADER_FIELD_NAME = "from";
  
  public static final String TOPIC_URI = "topic";
  
  public static final String FIRST_SUBKEY_FIELD_NAME = "first";
  public static final String SECOND_SUBKEY_FIELD_NAME = "second";
  public static final String THIRD_SUBKEY_FIELD_NAME = "third";
  public static final String FOURTH_SUBKEY_FIELD_NAME = "fourth";
  
  public static final String UPDATE_TYPE_FIELD_NAME = "update";

  public static final String QUEUE_URI_PREFIX = "maljms://queue.";
  public static final String TOPIC_URI_PREFIX = "maljms://topic.";
  
  public static String getQueueName(URI uri) {
    // removes the prefix "malamqp://queue."
    return uri.getValue().substring(TOPIC_URI_PREFIX.length());
  }
  
  public static URI getQueueUri(String name) {
    return new URI(QUEUE_URI_PREFIX + name);
  }
  
  public static String getTopicName(URI uri) {
    // removes the prefix "maljms://topic."
    return uri.getValue().substring(15);
  }
  
  public static URI getTopicUri(String name) {
    return new URI(TOPIC_URI_PREFIX + name);
  }
  
  public static String domainToString(IdentifierList domainId) {
    if (domainId.size() == 0) return "";
    String res = domainId.get(0).getValue();
    for (int i = 1; i < domainId.size(); i++) {
      res = res + '.' + domainId.get(i).getValue();
    }
    return res;
  }
  
  public static IdentifierList stringToDomain(String domainIdS) {
    IdentifierList domainId = new IdentifierList();
    StringTokenizer st = new StringTokenizer(domainIdS, ".");
    while (st.hasMoreTokens()) {
      String domainElement = st.nextToken();
      domainId.add(new Identifier(domainElement));
    }
    return domainId;
  }
  
  public static boolean isTransportLevelBroker(URI uri) {
    return uri.getValue().startsWith(TOPIC_URI_PREFIX);
  }
  
  public static String getMessageSelector(MALMessageHeader header, Subscription subscription) {
    EntityRequestList entityRequests = subscription.getEntities();
    StringBuffer buf = new StringBuffer();
    buf.append("(");
    for (int i = 0; i < entityRequests.size(); i++) {
      if (i > 0) buf.append(" OR ");
      
      EntityRequest entityRequest = (EntityRequest) entityRequests.get(i);
      boolean ooc = entityRequest.getOnlyOnChange().booleanValue();
      EntityKeyList entityKeyList = entityRequest.getEntityKeys();;
      String selector = getMessageSelector(header, entityKeyList,
          entityRequest.getSubDomain(), entityRequest.getAllAreas()
              .booleanValue(), entityRequest.getAllServices().booleanValue(),
          entityRequest.getAllOperations().booleanValue(), ooc);
      buf.append("(");
      buf.append(selector);
      buf.append(")");
    }
    buf.append(")");
    return buf.toString();
  }
  
  public static void setPublishJmsProperties(MALMessageHeader header,
      UpdateHeader update, Message jmsMsg) throws Exception {
    jmsMsg.setIntProperty(UPDATE_TYPE_FIELD_NAME, update.getUpdateType()
        .getOrdinal());
    jmsMsg.setStringProperty(FIRST_SUBKEY_FIELD_NAME, update.getKey()
        .getFirstSubKey().getValue());
    if (update.getKey().getSecondSubKey() != null) {
    jmsMsg.setLongProperty(SECOND_SUBKEY_FIELD_NAME, update.getKey()
        .getSecondSubKey());
    }
    if (update.getKey().getThirdSubKey() != null) {
    jmsMsg.setLongProperty(THIRD_SUBKEY_FIELD_NAME, update.getKey()
        .getThirdSubKey());
    }
    if (update.getKey().getFourthSubKey() != null) {
    jmsMsg.setLongProperty(FOURTH_SUBKEY_FIELD_NAME, update.getKey()
        .getFourthSubKey());
    }
  }
  
  public static String getMessageSelector(MALMessageHeader header, 
      EntityKeyList keys, 
      IdentifierList subDomain,
      boolean allAreas,
      boolean allServices,
      boolean allOperations,
      boolean ooc) {
    StringBuffer buf = new StringBuffer();
    
    buf.append(DOMAIN_HEADER_FIELD_NAME);
    buf.append(" LIKE '");
    buf.append(MALJMSHelper.domainToString(header.getDomain()));
    if (subDomain != null) {
      appendSubdomainIdentifier(subDomain, buf);
    }
    buf.append("'");
    
    buf.append(" AND ");
    
    buf.append(SESSION_HEADER_FIELD_NAME);
    buf.append(" = ");
    buf.append(header.getSession().getOrdinal());
   
    buf.append(" AND ");
    
    buf.append(SESSION_NAME_HEADER_FIELD_NAME);
    buf.append(" = '");
    buf.append(header.getSessionName().getValue());
    buf.append("'");
    
    if (! allAreas) {
      buf.append(" AND ");
      
      buf.append(AREA_HEADER_FIELD_NAME);
      buf.append(" = ");
      buf.append(header.getServiceArea());
    }
    
    if (! allServices) {
      buf.append(" AND ");
      
      buf.append(SERVICE_HEADER_FIELD_NAME);
      buf.append(" = ");
      buf.append(header.getService());
    }
    
    if (!allOperations) {
      buf.append(" AND ");

      buf.append(OPERATION_HEADER_FIELD_NAME);
      buf.append(" = ");
      buf.append(header.getOperation());
    }
    
    if (ooc) {
      buf.append(" AND ");

      buf.append(UPDATE_TYPE_FIELD_NAME);
      buf.append(" <> ");
      buf.append(UpdateType._UPDATE_INDEX);
    }
    
    buf.append(" AND ");
    
    buf.append('(');
    
    int keyIndex = 0;
    for (EntityKey key : keys) {
      
      if (keyIndex > 0) buf.append(" OR ");
      
      buf.append('(');

      if (!key.getFirstSubKey().getValue().equals("*")) {
        buf.append(FIRST_SUBKEY_FIELD_NAME);
        appendKeyValue(key.getFirstSubKey(), buf);
      }

      if (key.getSecondSubKey() == null || key.getSecondSubKey() != 0L) {
        buf.append(" AND ");

        buf.append(SECOND_SUBKEY_FIELD_NAME);
        appendKeyValue(key.getSecondSubKey(), buf);
      }

      if (key.getThirdSubKey() == null || key.getThirdSubKey() != 0L) {
        buf.append(" AND ");

        buf.append(THIRD_SUBKEY_FIELD_NAME);
        appendKeyValue(key.getThirdSubKey(), buf);
      }

      if (key.getFourthSubKey() == null || key.getFourthSubKey() != 0L) {
        buf.append(" AND ");

        buf.append(FOURTH_SUBKEY_FIELD_NAME);
        appendKeyValue(key.getFourthSubKey(), buf);
      }
      
      buf.append(')');
      
      keyIndex++;
    }
    
    buf.append(')');
    
    return buf.toString();
  }
  
  public static void appendKeyValue(Identifier id, StringBuffer buf) {
    if (id == null) {
      buf.append(" IS NULL");
    } else {
      buf.append(" = '").append(id.getValue()).append("'");
    }
  }
  
  public static void appendKeyValue(Long id, StringBuffer buf) {
    if (id == null) {
      buf.append(" IS NULL");
    } else {
      buf.append(" = ").append(id);
    }
  }
  
  public static void appendSubdomainIdentifier(IdentifierList domainId, StringBuffer buf) {
    for (int i = 0; i < domainId.size(); i++) {
      Identifier subId = (Identifier) domainId.get(i);
      if (subId.getValue().equals("*")) {
        buf.append('%');
        // '*' ends the subdomain
        return;
      } else {
        buf.append('.');
        buf.append(subId.getValue());
      }
    }
  }
  
  public static void malToAMQP(String id, StringBuffer buf) {
    if (id == null || id.equals("NULL")) {
      // Insert empty string
      //buf.append("");
    } else if (id.equals("*")) {
      buf.append('*');
    } else {
      buf.append(id);
    }
  }
  
  public static void malToAMQP(Integer id, StringBuffer buf) {
    if (id == null || id.equals("NULL")) {
      // Insert empty string
      //buf.append("");
    } else if (id.intValue() == 0) {
      buf.append('*');
    } else {
      buf.append(id);
    }
  }

  public static MALMessageHeader getMALHeader(Message msg, MALService service, URI uriTo) throws Exception {
    MALJMSMessageHeader header = new MALJMSMessageHeader();
    String authenticationIdS = msg.getStringProperty(AUTHENTICATION_ID_HEADER_FIELD_NAME);
    if (authenticationIdS != null) {
      byte[] authenticationId = authenticationIdS.getBytes();
      header.setAuthenticationId(new Blob(authenticationId));
    }
    String domainIdS = msg.getStringProperty(DOMAIN_HEADER_FIELD_NAME);
    if (domainIdS != null) {
      IdentifierList domainId = MALJMSHelper.stringToDomain(domainIdS.toString());
      header.setDomain(domainId);
    }
    String networkZoneS = msg.getStringProperty(NETWORK_ZONE_HEADER_FIELD_NAME);
    if (networkZoneS != null) {
      header.setNetworkZone(new Identifier(networkZoneS.toString()));
    }
    Integer sessionTypeI = msg.getIntProperty(SESSION_HEADER_FIELD_NAME);
    header.setSession(SessionType.fromOrdinal(sessionTypeI.intValue()));
    String sessionNameS = msg.getStringProperty(SESSION_NAME_HEADER_FIELD_NAME);
    header.setSessionName(new Identifier(sessionNameS.toString()));
    Integer interactionTypeI = msg.getIntProperty(INTERACTION_TYPE_HEADER_FIELD_NAME);
    header.setInteractionType(InteractionType.fromOrdinal(interactionTypeI.intValue()));
    Integer interactionStageI = msg.getIntProperty(INTERACTION_STAGE_HEADER_FIELD_NAME);
    header.setInteractionStage(new UOctet(interactionStageI.byteValue()));
    Long transactionId = msg.getLongProperty(TRANSACTION_ID_HEADER_FIELD_NAME);
    header.setTransactionId(transactionId);
    
    if (service == null) {
      Integer areaI = msg.getIntProperty(AREA_HEADER_FIELD_NAME);
      Integer versionI = msg.getIntProperty(VERSION_HEADER_FIELD_NAME);
      MALArea area = MALContextFactory.lookupArea(new UShort(areaI.intValue()), new UOctet(versionI.shortValue()));
      Integer serviceI = msg.getIntProperty(SERVICE_HEADER_FIELD_NAME);
      service = area.getServiceByNumber(new UShort(serviceI.intValue()));
    }
    
    header.setServiceArea(service.getArea().getNumber());
    header.setService(service.getNumber());
    header.setAreaVersion(service.getArea().getVersion());
    Integer operationI = msg.getIntProperty(MALJMSHelper.OPERATION_HEADER_FIELD_NAME);
    header.setOperation(service.getOperationByNumber(new UShort(operationI.intValue())).getNumber());
    Integer qosI = msg.getIntProperty(QOS_LEVEL_HEADER_FIELD_NAME);
    header.setQoSlevel(QoSLevel.fromOrdinal(qosI.intValue()));
    header.setURIFrom(MALJMSHelper.getQueueUri(msg.getStringProperty(MALJMSHelper.FROM_HEADER_FIELD_NAME)));
    String timestampS = msg.getStringProperty(MALJMSHelper.TIMESTAMP_HEADER_FIELD_NAME);
    header.setTimestamp(new Time((Long.parseLong(timestampS.toString()))));
    //header.setTimestamp(new Time(props.timestamp.getTime()));
    
    if (interactionTypeI.intValue() == InteractionType._PUBSUB_INDEX) {
      if (interactionStageI.intValue() == MALPubSubOperation._PUBLISH_STAGE) {
        String topicUri = msg.getStringProperty(TOPIC_URI);
        if (topicUri != null) {
          uriTo = new URI(topicUri);
        }
      }
    }
    
    header.setURITo(uriTo);
    header.setPriority(new UInteger(msg.getJMSPriority()));
    Integer isErrorI = msg.getIntProperty(MALJMSHelper.IS_ERROR_HEADER_FIELD_NAME);
    if (isErrorI.intValue() == 0) {
      header.setIsErrorMessage(Boolean.FALSE);
    } else {
      header.setIsErrorMessage(Boolean.TRUE);
    }
    return header;
  }
  
  public static Hashtable getMALQoSProperties(Message msg) throws Exception {
    Hashtable res = new Hashtable();
    long expiration = msg.getJMSExpiration();
    int ttl = (int) (expiration - msg.getJMSTimestamp());
    res.put("timeToLive", new Integer(ttl));
    return res;
  }
  
  public static MALException createMALException(String errorMsg) {
    return new MALException(errorMsg);
  }
  
  public static MALMessageBody createMessageBody(MALMessageHeader header, List bodyElements) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALAMQPHelper.createMessageBody(" + header + ',' + bodyElements + ')');
    if (header.getIsErrorMessage().booleanValue()) {
      return new GENErrorBody(bodyElements);
    } else if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
      switch (header.getInteractionStage().getValue()) {
      case MALPubSubOperation._REGISTER_STAGE:
        return new GENRegisterBody(bodyElements);
      case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
        return new GENPublishRegisterBody(bodyElements);
      case MALPubSubOperation._PUBLISH_STAGE:
        return new GENPublishBody(bodyElements);
      case MALPubSubOperation._NOTIFY_STAGE:
        return new GENNotifyBody(bodyElements);
      case MALPubSubOperation._DEREGISTER_STAGE:
        return new GENDeregisterBody(bodyElements);
      default:
        return new GENMessageBody(bodyElements);
      }
    } else {
      return new GENMessageBody(bodyElements);
    }
  }
  
  public static MALMessageBody createMessageBody(
      byte[] encodedBody, MALEncodingContext msgCtx,
      MALElementStreamFactory elementStreamFactory) {
    MALMessageHeader header = msgCtx.getHeader();
    if (header.getIsErrorMessage().booleanValue()) {
      return new GENErrorBody(encodedBody, msgCtx, elementStreamFactory);
    } else if (header.getInteractionType().getOrdinal() == InteractionType._PUBSUB_INDEX) {
      switch (header.getInteractionStage().getValue()) {
      case MALPubSubOperation._REGISTER_STAGE:
        return new GENRegisterBody(encodedBody, msgCtx, elementStreamFactory);
      case MALPubSubOperation._PUBLISH_REGISTER_STAGE:
        return new GENPublishRegisterBody(encodedBody, msgCtx, elementStreamFactory);
      case MALPubSubOperation._PUBLISH_STAGE:
        return new GENPublishBody(encodedBody, msgCtx, elementStreamFactory);
      case MALPubSubOperation._NOTIFY_STAGE:
        return new GENNotifyBody(encodedBody, msgCtx, elementStreamFactory);
      case MALPubSubOperation._DEREGISTER_STAGE:
        return new GENDeregisterBody(encodedBody, msgCtx, elementStreamFactory);
      default:
        return new GENMessageBody(encodedBody, msgCtx, elementStreamFactory);
      }
    } else {
      return new GENMessageBody(encodedBody, msgCtx, elementStreamFactory);
    }
  }
}
