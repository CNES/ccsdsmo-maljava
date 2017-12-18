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

import java.util.ArrayList;
import java.util.Hashtable;

import org.ccsds.moims.mo.mal.MALHelper;
import org.ccsds.moims.mo.mal.MALPubSubOperation;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.Union;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.ccsds.moims.mo.mal.transport.MALMessageListener;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.ReturnListener;

import fr.cnes.ccsds.mo.transport.gen.body.GENErrorBody;

public class InternalErrorListener implements ReturnListener {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(InternalErrorListener.class.getName());
  
  private MALMessageListener listener;
  
  private MALAMQPEndPoint endpoint;

  public InternalErrorListener (MALMessageListener listener,
      MALAMQPEndPoint endpoint) {
    this.listener = listener;
    this.endpoint = endpoint;
  }
  
  public void handleBasicReturn(int replyCode, String replyText, String exchange, String routingKey,
      BasicProperties properties, byte[] body) {
    if (logger.isLoggable(BasicLevel.WARN))
      logger.log(BasicLevel.WARN, "InternalErrorListener.handleBasicReturn(" + replyCode + ',' + replyText + ','
          + exchange + ',' + routingKey + ',' + properties + ')');
    MALMessageHeader receivedHeader = MALAMQPHelper.getMALHeader(properties, exchange, routingKey, null);
    Hashtable qosProperties = MALAMQPHelper.getMALQoSProperties(properties);

    int interactionType = receivedHeader.getInteractionType().getOrdinal();
    short stage = receivedHeader.getInteractionStage().getValue();
    switch (interactionType) {
    case InteractionType._PUBSUB_INDEX:
      if (stage == MALPubSubOperation._REGISTER_STAGE ||
          stage == MALPubSubOperation._DEREGISTER_STAGE) {
        returnErrorMessage(receivedHeader, qosProperties, (byte) (stage + 1), replyText);
      } else {
        listener.onInternalError(endpoint, new Exception(replyText));
      }
      break;
    default:
      if (stage == 1) {
        returnErrorMessage(receivedHeader, qosProperties, (byte) (stage + 1), replyText);
      } else {
        listener.onInternalError(endpoint, new Exception(replyText));
      }
    }
  }
  
  private void returnErrorMessage(MALMessageHeader receivedHeader, 
      Hashtable qosProperties, byte stage, String replyText) {
    URI uriFrom = receivedHeader.getURIFrom();
    URI uriTo = receivedHeader.getURITo();
    receivedHeader.setURIFrom(uriTo);
    receivedHeader.setURITo(uriFrom);
    receivedHeader.setInteractionStage(new UOctet(stage));
    receivedHeader.setIsErrorMessage(Boolean.TRUE);
    ArrayList elements = new ArrayList();
    elements.add(MALHelper.INTERNAL_ERROR_NUMBER);
    elements.add(new Union(replyText));
    GENErrorBody errorBody = new GENErrorBody(elements);
    MALAMQPMessage errorMsg = new MALAMQPMessage(receivedHeader, errorBody, qosProperties);
    listener.onMessage(endpoint, errorMsg);
  }
}
