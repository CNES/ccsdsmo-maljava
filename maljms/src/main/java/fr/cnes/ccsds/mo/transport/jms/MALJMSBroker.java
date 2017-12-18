package fr.cnes.ccsds.mo.transport.jms;

import java.util.List;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.MALStandardError;
import org.ccsds.moims.mo.mal.broker.MALBrokerBinding;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;

public class MALJMSBroker implements MALBrokerBinding {
  
  private URI uri;
  
  private Blob authenticationId;
  
  public MALJMSBroker(URI uri, Blob authenticationId) {
    this.uri = uri;
    this.authenticationId = authenticationId;
  }

  public void close() throws MALException {
    // Do nothing
  }

  public URI getURI() {
    return uri;
  }

  public Blob getAuthenticationId() {
    return authenticationId;
  }

  public void startMessageDelivery() throws MALException {
    // Do nothing
  }

  public MALMessage sendNotify(MALOperation operation, URI subscriber,
      Long transactionId, IdentifierList domainId, Identifier networkZone,
      SessionType sessionType, Identifier sessionName, QoSLevel notifyQos,
      Map notifyQosProps, UInteger notifyPriority, Identifier subscriptionId,
      UpdateHeaderList updateHeaderList, List... updateList)
      throws MALInteractionException, MALException {
    // TODO Auto-generated method stub
    return null;
  }

  public MALMessage sendNotifyError(MALOperation operation, URI subscriber,
      Long transactionId, IdentifierList domainId, Identifier networkZone,
      SessionType sessionType, Identifier sessionName, QoSLevel notifyQos,
      Map notifyQosProps, UInteger notifyPriority, MALStandardError error)
      throws MALInteractionException, MALException {
    // TODO Auto-generated method stub
    return null;
  }

  public MALMessage sendPublishError(MALOperation operation, URI publisher,
      Long transactionId, IdentifierList domainId, Identifier networkZone,
      SessionType sessionType, Identifier sessionName, QoSLevel qos,
      Map qosProps, UInteger priority, MALStandardError error)
      throws MALInteractionException, MALException {
    // TODO Auto-generated method stub
    return null;
  }

  public void setTransmitErrorListener(MALTransmitErrorListener listener)
      throws MALException {
    // TODO Auto-generated method stub
    
  }

  public MALTransmitErrorListener getTransmitErrorListener()
      throws MALException {
    // TODO Auto-generated method stub
    return null;
  }

}
