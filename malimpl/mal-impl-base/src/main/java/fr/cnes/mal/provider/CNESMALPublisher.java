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
package fr.cnes.mal.provider;

import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.provider.MALProvider;
import org.ccsds.moims.mo.mal.provider.MALPublishInteractionListener;
import org.ccsds.moims.mo.mal.provider.MALPublisher;
import org.ccsds.moims.mo.mal.structures.AttributeTypeList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UpdateHeader;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class CNESMALPublisher implements MALPublisher {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
  .getLogger(CNESMALPublisher.class.getName());
  
  private CNESMALProvider provider;
  
  private MALPubSubOperation op;
  
  private IdentifierList domain;
  
  private Identifier networkZone;
  
  private SessionType sessionType;
  
  private Identifier sessionName;
  
  private QoSLevel publishQos;
  
  private NamedValueList supplements;
  
  private Map publishQosProps;
  
  private UInteger publishPriority;
  
  private Long tid;
  
  private MALPublishInteractionListener listener;
  
  CNESMALPublisher(CNESMALProvider provider, MALPubSubOperation op,
    IdentifierList domain, Identifier networkZone,
    SessionType sessionType, Identifier sessionName,
    QoSLevel publishQos, Map publishQosProps,
    UInteger publishPriority) {
    this.provider = provider;
    this.op = op;
    this.domain = domain;
    this.networkZone = networkZone;
    this.sessionType = sessionType;
    this.sessionName = sessionName;
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX &&
        sessionName == null) {
      sessionName = new Identifier("LIVE");
    }
    this.publishQos = publishQos;
    // TODO: missing supplements
    this.supplements = new NamedValueList();
    this.publishQosProps = publishQosProps;
    this.publishPriority = publishPriority;
  }
  
  public IdentifierList getDomain() {
    return domain;
  }

  public Identifier getNetworkZone() {
    return networkZone;
  }

  public SessionType getSessionType() {
    return sessionType;
  }

  public Identifier getSessionName() {
    return sessionName;
  }

  public Long getTid() {
    return tid;
  }

  public void setTid(Long tid) {
    this.tid = tid;
  }

  MALPubSubOperation getOperation() {
    return op;
  }

  public MALPublishInteractionListener getListener() {
    return listener;
  }

  /**
   * Publishes a list of updates.
   *
   * @param updateHeader Published UpdateHeader.
   * @param updateValues The published values of the Update message.
   * @return The MALMessage that has been sent.
   * @throws java.lang.IllegalArgumentException If the parameter
   * ‘updateHeaderList’ is NULL.
   * @throws MALException If a non-MAL error occurs during the initiation
   * message sending.
   * @throws MALInteractionException If a MAL standard error occurs during the
   * initiation message sending.
   */
  public MALMessage publish(UpdateHeader updateHeader, Object... updateObjects) 
      throws MALInteractionException, MALException {
    if (updateHeader == null) throw new IllegalArgumentException("Null UpdateHeader");
    if (updateHeader.getSource() == null) {
      // TODO MAL v2 source should be Identifier
      Identifier source = provider.getDestinationId();
      updateHeader.setSource(source);
    }
    //updateHeader.setTimestamp(new Time(time));
    return provider.publish(op, supplements, publishQosProps, tid,
         updateHeader, updateObjects);
  }

  /**
   * The method enables a provider to synchronously register to its broker.
   *
   * @param keyNames Key Names of the subscriptions that are to be published
   * @param keyTypes Key Types of the subscriptions that are to be published
   * @param listener Listener in charge of receiving the messages PUBLISH
   * ERROR
   * @throws java.lang.IllegalArgumentException If the parameters
   * ‘entityKeyList’ or ‘listener’ are NULL
   * @throws MALException if a non-MAL error occurs during the initiation
   * message sending or the MALPublisher is closed.
   * @throws MALInteractionException if a PUBLISH REGISTER ERROR or other MAL
   * error occurs.
   */
  public void register(IdentifierList keyNames,
      AttributeTypeList keyTypes,
      MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    if (listener == null) throw new IllegalArgumentException("Null MALPublishInteractionListener");
    doRegister(keyNames, keyTypes, listener, false);
  }

  /**
   * The method enables a provider to asynchronously register to its broker.
   *
   * @param keyNames Key Names of the subscriptions that are to be published
   * @param keyTypes Key Types of the subscriptions that are to be published
   * @param listener Listener in charge of receiving the messages PUBLISH
   * REGISTER ACK, PUBLISH REGISTER ERROR and PUBLISH ERROR
   * @return the MALMessage that has been sent
   * @throws java.lang.IllegalArgumentException If the parameter
   * ‘entityKeyList’ or ‘listener’ are NULL
   * @throws MALException if a non-MAL error occurs during the initiation
   * message sending or the MALPublisher is closed.
   * @throws MALInteractionException if a MAL error occurs.
   */
  public MALMessage asyncRegister(IdentifierList keyNames,
      AttributeTypeList keyTypes,
      MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    if (listener == null) throw new IllegalArgumentException("Null MALPublishInteractionListener");
    return doRegister(keyNames, keyTypes, listener, true);
  }
  
  public MALMessage doRegister(IdentifierList keyNames,
      AttributeTypeList keyTypes,
      MALPublishInteractionListener listener,
      boolean async)
      throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALPublisher.doRegister(" + keyNames + ',' + keyTypes + ',' +
          networkZone + ',' + sessionType + ',' + sessionName + ',' +
          publishQos + ',' + publishQosProps + ',' + publishPriority + ',' +
          async + ')');
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX) {
      sessionName = new Identifier("LIVE");
    }
    MALMessage msg = provider.publishRegister(
        op, keyNames, keyTypes, supplements, publishQosProps, publishPriority, async, listener);
    if (tid == null) {
      tid = msg.getHeader().getTransactionId();
    }
    this.listener = listener;
    return msg;
  }

  /**
   * The method enables a provider to synchronously deregister from its
   * broker.
   *
   * @throws MALInteractionException if a MAL standard error occurs during the
   * initiation message sending.
   * @throws MALException if a non-MAL error occurs
   */
  public void deregister() throws MALInteractionException, MALException {
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX) {
      sessionName = new Identifier("LIVE");
    }
    provider.publishDeregister(op, supplements, publishQosProps, publishPriority, false, null);
  }

  /**
   * The method enables a provider to asynchronously deregister from its
   * broker.
   *
   * @param listener Listener in charge of receiving the messages PUBLISH
   * DEREGISTER ACK
   * @return the MALMessage that has been sent
   * @throws java.lang.IllegalArgumentException If the parameter ‘listener’ is
   * NULL
   * @throws MALException if a non-MAL error occurs during the initiation
   * message sending or the MALPublisher is closed.
   * @throws MALInteractionException if a MAL error occurs.
   */
  public MALMessage asyncDeregister(MALPublishInteractionListener listener) 
      throws MALInteractionException, MALException {
    if (listener == null) throw new IllegalArgumentException("Null MALPublishInteractionListener");
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX) {
      sessionName = new Identifier("LIVE");
    }
    MALMessage msg = provider.publishDeregister(op, supplements, publishQosProps, publishPriority, true, listener);
    return msg;
  }

  /**
   * The method releases the resources owned by this MALPublisher.
   *
   * @throws MALException if an error occurs.
   */
  public void close() throws MALException {
    provider.closePublisher(this);
  }

  /**
   * Return the MALProvider that created this MALPublisher.
   *
   * @return The Provider.
   */
  public MALProvider getProvider() {
    return provider;
  }

  @Override
  public String toString() {
    return "CNESMALPublisher [op=" + op + ", domain=" + domain
        + ", networkZone=" + networkZone + ", sessionType=" + sessionType
        + ", sessionName=" + sessionName + ", publishQos=" + publishQos
        + ", publishQosProps=" + publishQosProps + ", publishPriority="
        + publishPriority + ", tid=" + tid + "]";
  }
}
