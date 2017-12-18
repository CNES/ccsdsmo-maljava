/*******************************************************************************
 * Copyright or © or Copr. CNES
 *
 * This software is a computer program whose purpose is to provide a 
 * framework for the CCSDS Mission Operations services.
 *
 * This software is governed by the CeCILL-C license under French law and
 * abiding by the rules of distribution of free software.  You can  use, 
 * modify and/ or redistribute the software under the terms of the CeCILL-C
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info". 
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability. 
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or 
 * data to be ensured and,  more generally, to use and operate it in the 
 * same conditions as regards security. 
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL-C license and that you accept its terms.
 *******************************************************************************/
package fr.cnes.ccsds.mo.transport.amqp;

import java.util.Hashtable;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

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

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.impl.LongString;

import fr.cnes.ccsds.mo.transport.gen.body.GENDeregisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENErrorBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENMessageBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENNotifyBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENPublishRegisterBody;
import fr.cnes.ccsds.mo.transport.gen.body.GENRegisterBody;

public class MALAMQPHelper {
  
  public final static Logger logger = 
      fr.dyade.aaa.common.Debug.getLogger(MALAMQPHelper.class.getName());
  
  public static final String AUTHENTICATION_ID_HEADER_FIELD_NAME = "mal.authenticationId";
  public static final String DOMAIN_HEADER_FIELD_NAME = "mal.domain";
  public static final String NETWORK_ZONE_HEADER_FIELD_NAME = "mal.networkZone";
  public static final String SESSION_HEADER_FIELD_NAME = "mal.session";
  public static final String SESSION_NAME_HEADER_FIELD_NAME = "mal.sessionName";
  public static final String INTERACTION_TYPE_HEADER_FIELD_NAME = "mal.interactionType";
  public static final String INTERACTION_STAGE_HEADER_FIELD_NAME = "mal.interactionStage";
  public static final String TRANSACTION_ID_HEADER_FIELD_NAME = "mal.transactionId";
  public static final String AREA_HEADER_FIELD_NAME = "mal.area";
  public static final String SERVICE_HEADER_FIELD_NAME = "mal.service";
  public static final String VERSION_HEADER_FIELD_NAME = "mal.version";
  public static final String OPERATION_HEADER_FIELD_NAME = "mal.operation";
  public static final String QOS_LEVEL_HEADER_FIELD_NAME = "mal.qosLevel";
  public static final String TIMESTAMP_HEADER_FIELD_NAME = "mal.timestamp";
  public static final String IS_ERROR_HEADER_FIELD_NAME = "mal.isError";
  
  public static String getQueueName(URI uri) {
    // removes the prefix "malamqp://queue."
    return uri.getValue().substring(16);
  }
  
  public static URI getQueueUri(String name) {
    return new URI("malamqp://queue." + name);
  }
  
  public static String getTopicName(URI uri) {
    // removes the prefix "malamqp://topic."
    return uri.getValue().substring(16);
  }
  
  public static URI getTopicUri(String name) {
    return new URI("malamqp://topic." + name);
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
    return uri.getValue().startsWith("malamqp://topic.");
  }
  
  public static String[] getSubscribeRoutingKeys(MALMessageHeader header, Subscription subscription) {
    EntityRequestList entityRequests = subscription.getEntities();
    Vector routingKeys = new Vector();
    for (int i = 0; i < entityRequests.size(); i++) {
      EntityRequest entityRequest = (EntityRequest) entityRequests.get(i);
      boolean ooc = entityRequest.getOnlyOnChange().booleanValue();
      EntityKeyList entityKeyList = entityRequest.getEntityKeys();;
      for (int j = 0; j < entityKeyList.size(); j++) {
        EntityKey key = (EntityKey) entityKeyList.get(j);
        StringBuffer routingKey = new StringBuffer();
        getRoutingKey(header, key, 
            entityRequest.getSubDomain(),
            entityRequest.getAllAreas().booleanValue(),
            entityRequest.getAllServices().booleanValue(),
            entityRequest.getAllOperations().booleanValue(),
            routingKey);
        routingKey.append('.');
        if (ooc) {
          routingKey.append('m');
        } else {
          routingKey.append('*');
        }
        routingKeys.addElement(routingKey.toString());
      }
    }
    String[] res = new String[routingKeys.size()];
    routingKeys.copyInto(res);
    return res;
  }
  
  public static String getPublishRoutingKey(MALMessageHeader header, UpdateHeader update) {
    EntityKey key = update.getKey();
    StringBuffer routingKey = new StringBuffer();
    getRoutingKey(header, key, null, false, false, false, routingKey);
    routingKey.append('.');
    if (update.getUpdateType().getOrdinal() == UpdateType._UPDATE_INDEX) {
      routingKey.append('u');
    } else {
      routingKey.append('m');
    }
    return routingKey.toString();
  }
  
  public static void getRoutingKey(MALMessageHeader header, 
      EntityKey key, 
      IdentifierList subDomain,
      boolean allAreas,
      boolean allServices,
      boolean allOperations,
      StringBuffer buf) {
    getDomainIdentifier(header.getDomain(), buf, false);
    if (subDomain != null) {
      getDomainIdentifier(subDomain, buf, true);
    }
    buf.append('.');
    // Not used any more by the MAL broker checking
    //buf.append(header.getNetworkZone().getValue());
    //buf.append('.');
    buf.append(header.getSession().toString());
    buf.append('.');
    buf.append(header.getSessionName().getValue());
    buf.append('.');
    if (allAreas) {
      buf.append('*');
    } else {
      buf.append(header.getServiceArea().getValue());
    }
    buf.append('.');
    if (allServices) {
      buf.append('*');
    } else {
      buf.append(header.getService().getValue());
    }
    buf.append('.');
    if (allOperations) {
      buf.append('*');
    } else {
      buf.append(header.getOperation());
    }
    buf.append('.');
    malToAMQP(getKeyValue(key.getFirstSubKey()), buf);
    buf.append('.');
    malToAMQP(getKeyValue(key.getSecondSubKey()), buf);
    buf.append('.');
    malToAMQP(getKeyValue(key.getThirdSubKey()), buf);
    buf.append('.');
    malToAMQP(getKeyValue(key.getFourthSubKey()), buf);
  }
  
  public static String getKeyValue(Identifier id) {
    if (id == null) return null;
    else return id.getValue();
  }
  
  public static Long getKeyValue(Long id) {
    if (id == null) return null;
    else return id;
  }
  
  public static void getDomainIdentifier(IdentifierList domainId, StringBuffer buf, boolean isSubDomain) {
    for (int i = 0; i < domainId.size(); i++) {
      Identifier subId = (Identifier) domainId.get(i);
      if (isSubDomain && subId.getValue().equals("*")) {
        buf.append(subId.getValue());
        // '*' ends the subdomain
        return;
      } else {
        if (i == 0) {
          if (isSubDomain) {
            buf.append('o');
          }
        } else {
          buf.append('o');
        }
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
  
  public static void malToAMQP(Long id, StringBuffer buf) {
    if (id == null || id.equals("NULL")) {
      // Insert empty string
      //buf.append("");
    } else if (id.intValue() == 0) {
      buf.append('*');
    } else {
      buf.append(id);
    }
  }

  public static MALMessageHeader getMALHeader(BasicProperties props, 
      String exchangeName, String routingKey, MALService service) {
    MALAMQPMessageHeader header = new MALAMQPMessageHeader();
    LongString authenticationIdS = (LongString) props.headers.get(AUTHENTICATION_ID_HEADER_FIELD_NAME);
    if (authenticationIdS != null) {
      byte[] authenticationId = authenticationIdS.getBytes();
      header.setAuthenticationId(new Blob(authenticationId));
    }
    LongString domainIdS = (LongString) props.headers.get(DOMAIN_HEADER_FIELD_NAME);
    if (domainIdS != null) {
      IdentifierList domainId = MALAMQPHelper.stringToDomain(domainIdS.toString());
      header.setDomain(domainId);
    }
    LongString networkZoneS = (LongString) props.headers.get(NETWORK_ZONE_HEADER_FIELD_NAME);
    if (networkZoneS != null) {
      header.setNetworkZone(new Identifier(networkZoneS.toString()));
    }
    Integer sessionTypeI = (Integer) props.headers.get(SESSION_HEADER_FIELD_NAME);
    header.setSession(SessionType.fromOrdinal(sessionTypeI.intValue()));
    LongString sessionNameS = (LongString) props.headers.get(SESSION_NAME_HEADER_FIELD_NAME);
    header.setSessionName(new Identifier(sessionNameS.toString()));
    Integer interactionTypeI = (Integer) props.headers.get(INTERACTION_TYPE_HEADER_FIELD_NAME);
    header.setInteractionType(InteractionType.fromOrdinal(interactionTypeI.intValue()));
    Integer interactionStageI = (Integer) props.headers.get(INTERACTION_STAGE_HEADER_FIELD_NAME);
    header.setInteractionStage(new UOctet(interactionStageI.byteValue()));
    Integer transactionId = (Integer) props.headers.get(TRANSACTION_ID_HEADER_FIELD_NAME);
    if (transactionId.intValue() < 0) {
      header.setTransactionId(null);
    } else {
      header.setTransactionId(new Long(transactionId.intValue()));
    }
    
    if (service == null) {
      Integer areaI = (Integer) props.headers.get(AREA_HEADER_FIELD_NAME);
      Integer versionI = (Integer) props.headers.get(VERSION_HEADER_FIELD_NAME);
      MALArea area = MALContextFactory.lookupArea(new UShort(areaI.intValue()), new UOctet(versionI.shortValue()));
      Integer serviceI = (Integer) props.headers.get(SERVICE_HEADER_FIELD_NAME);
      service = area.getServiceByNumber(new UShort(serviceI.intValue()));
    }
    
    header.setServiceArea(service.getArea().getNumber());
    header.setService(service.getNumber());
    header.setAreaVersion(service.getArea().getVersion());
    Integer operationI = (Integer) props.headers.get(MALAMQPHelper.OPERATION_HEADER_FIELD_NAME);
    header.setOperation(service.getOperationByNumber(new UShort(operationI.intValue())).getNumber());
    Integer qosI = (Integer) props.headers.get(QOS_LEVEL_HEADER_FIELD_NAME);
    header.setQoSlevel(QoSLevel.fromOrdinal(qosI.intValue()));
    header.setURIFrom(MALAMQPHelper.getQueueUri(props.replyTo));
    LongString timestampS = (LongString) props.headers.get(MALAMQPHelper.TIMESTAMP_HEADER_FIELD_NAME);
    header.setTimestamp(new Time((Long.parseLong(timestampS.toString()))));
    //header.setTimestamp(new Time(props.timestamp.getTime()));
    
    URI uriTo;
    if (interactionTypeI.intValue() == InteractionType._PUBSUB_INDEX) {
      if (interactionStageI.intValue() == MALPubSubOperation._PUBLISH_STAGE) {
        if (exchangeName.length() == 0) {
          uriTo = MALAMQPHelper.getQueueUri(routingKey);
        } else {
          // Publish coming from an AMQP topic exchange
          uriTo = MALAMQPHelper.getTopicUri(exchangeName);
        }
      } else {
        // Other Pub/Sub stages can only be received from
        // a private broker.
        uriTo = MALAMQPHelper.getQueueUri(routingKey);
      }
    } else {
      uriTo = MALAMQPHelper.getQueueUri(routingKey);
    }
    
    header.setURITo(uriTo);
    header.setPriority(new UInteger(props.priority.intValue()));
    Integer isErrorI = (Integer) props.headers.get(MALAMQPHelper.IS_ERROR_HEADER_FIELD_NAME);
    if (isErrorI.intValue() == 0) {
      header.setIsErrorMessage(Boolean.FALSE);
    } else {
      header.setIsErrorMessage(Boolean.TRUE);
    }
    return header;
  }
  
  public static Hashtable getMALQoSProperties(BasicProperties properties) {
    Hashtable res = new Hashtable();
    String expirationS = properties.expiration;
    if (expirationS != null) {
      long expiration = Long.parseLong(expirationS);
      int ttl = (int) (expiration - properties.timestamp.getTime());
      res.put("timeToLive", new Integer(ttl));
    }
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
