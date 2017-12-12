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
import org.ccsds.moims.mo.mal.structures.EntityKeyList;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
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

  public MALMessage publish(UpdateHeaderList updateHeaderList, List... updateLists) 
      throws MALInteractionException, MALException {
    if (updateHeaderList == null) throw new IllegalArgumentException("Null UpdateHeaderList");
    for (int i = 0; i < updateHeaderList.size(); i++) {
      UpdateHeader updateHeader = (UpdateHeader) updateHeaderList.get(i);
      if (updateHeader.getSourceURI() == null) {
        updateHeader.setSourceURI(provider.getURI());
      }
      //updateHeader.setTimestamp(new Time(time));
    }
    return provider.publish(op, domain, networkZone, sessionType, sessionName,
         publishQos, publishQosProps, publishPriority, tid,
         updateHeaderList, updateLists);
  }
  
  public void register(EntityKeyList entityKeys,
      MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    if (entityKeys == null) throw new IllegalArgumentException("Null EntityKeyList");
    if (listener == null) throw new IllegalArgumentException("Null MALPublishInteractionListener");
    doRegister(entityKeys, listener, false);
  }

  public MALMessage asyncRegister(EntityKeyList entityKeys,
      MALPublishInteractionListener listener)
      throws MALInteractionException, MALException {
    if (entityKeys == null) throw new IllegalArgumentException("Null EntityKeyList");
    if (listener == null) throw new IllegalArgumentException("Null MALPublishInteractionListener");
    return doRegister(entityKeys, listener, true);
  }
  
  public MALMessage doRegister(EntityKeyList entityKeys,
      MALPublishInteractionListener listener,
      boolean async)
      throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALPublisher.doRegister(" + entityKeys + ',' +
          networkZone + ',' + sessionType + ',' + sessionName + ',' +
          publishQos + ',' + publishQosProps + ',' + publishPriority + ',' +
          async + ')');
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX) {
      sessionName = new Identifier("LIVE");
    }
    MALMessage msg = provider.publishRegister(
        op, entityKeys, domain, networkZone, sessionType, sessionName,
        publishQos, publishQosProps, publishPriority, async, listener);
    if (tid == null) {
      tid = msg.getHeader().getTransactionId();
    }
    this.listener = listener;
    return msg;
  }

  public void deregister() throws MALInteractionException, MALException {
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX) {
      sessionName = new Identifier("LIVE");
    }
    provider.publishDeregister(op, domain, networkZone, sessionType, sessionName,
        publishQos, publishQosProps, publishPriority, false, null);
  }

  public MALMessage asyncDeregister(MALPublishInteractionListener listener) 
      throws MALInteractionException, MALException {
    if (listener == null) throw new IllegalArgumentException("Null MALPublishInteractionListener");
    if (sessionType.getOrdinal() == SessionType._LIVE_INDEX) {
      sessionName = new Identifier("LIVE");
    }
    MALMessage msg = provider.publishDeregister(op, domain, networkZone, sessionType, sessionName,
        publishQos, publishQosProps, publishPriority, true, listener);
    return msg;
  }

  public void close() throws MALException {
    provider.closePublisher(this);
  }

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
