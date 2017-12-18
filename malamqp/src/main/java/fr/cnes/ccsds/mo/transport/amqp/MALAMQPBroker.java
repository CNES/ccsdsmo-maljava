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
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALTransmitErrorListener;

public class MALAMQPBroker implements MALBrokerBinding {
  
  private URI uri;
  
  private Blob authenticationId;
  
  public MALAMQPBroker(URI uri, Blob authenticationId) {
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

  @Override
  public MALMessage sendNotify(MALOperation operation, URI subscriber,
      Long transactionId, IdentifierList domainId, Identifier networkZone,
      SessionType sessionType, Identifier sessionName, QoSLevel notifyQos,
      Map notifyQosProps, UInteger notifyPriority, Identifier subscriptionId,
      UpdateHeaderList updateHeaderList, List... updateList)
      throws MALInteractionException, MALException {
    throw MALAMQPHelper.createMALException("Invalid call");
  }

  @Override
  public MALMessage sendNotifyError(MALOperation operation, URI subscriber,
      Long transactionId, IdentifierList domainId, Identifier networkZone,
      SessionType sessionType, Identifier sessionName, QoSLevel notifyQos,
      Map notifyQosProps, UInteger notifyPriority, MALStandardError error)
      throws MALInteractionException, MALException {
    throw MALAMQPHelper.createMALException("Invalid call");
  }

  @Override
  public MALMessage sendPublishError(MALOperation operation, URI publisher,
      Long transactionId, IdentifierList domainId, Identifier networkZone,
      SessionType sessionType, Identifier sessionName, QoSLevel qos,
      Map qosProps, UInteger priority, MALStandardError error)
      throws MALInteractionException, MALException {
    throw MALAMQPHelper.createMALException("Invalid call");
  }

  @Override
  public MALTransmitErrorListener getTransmitErrorListener()
      throws MALException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void setTransmitErrorListener(MALTransmitErrorListener arg0)
      throws MALException {
    // TODO Auto-generated method stub
    
  }
}
