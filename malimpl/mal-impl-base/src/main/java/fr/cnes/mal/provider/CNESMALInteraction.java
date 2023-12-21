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

import java.util.Arrays;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.provider.MALInteraction;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.NamedValueList;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.cnes.mal.CNESMALContext;
import fr.cnes.mal.MessageSender;

public class CNESMALInteraction implements MALInteraction {
  
  public final static Logger logger = 
    fr.dyade.aaa.common.Debug.getLogger(CNESMALInteraction.class.getName());
  
  private MALMessageHeader header;
  
  private MessageSender messageSender;
  
  private MALMessage request;
  
  private MALOperation operation;
  
  private Blob authenticationId;
  
  private UOctet stage;
  
  private boolean failed;
  
  private Map qosProperties;
  
  protected NamedValueList providerSupplements;
  
  public CNESMALInteraction(MALMessageHeader header, 
      MessageSender messageSender, 
      MALMessage request,
      MALOperation operation,
      Blob authenticationId,
      NamedValueList providerSupplements) {
    this.header = header;
    this.messageSender = messageSender;
    this.request = request;
    this.operation = operation;
    this.authenticationId = authenticationId;
    this.providerSupplements = providerSupplements;
    failed = false;
    qosProperties = request.getQoSProperties();
    if (header.getSupplements() == null) {
      if (logger.isLoggable(BasicLevel.DEBUG))
        logger.log(BasicLevel.DEBUG, "CNESMALInteraction null supplements, " + 
            Arrays.toString(new Exception().getStackTrace()));
    }
  }
  
  protected void setStage(UOctet stage) {
    this.stage = stage;
  }
  
  protected final UOctet getStage() {
    return stage;
  }
  
  protected void setFailed(boolean failed) {
    this.failed = failed;
  }
  
  protected final boolean isFailed() {
    return failed;
  }

  public MALMessageHeader getMessageHeader() {
    return header;
  }
  
  public MALOperation getOperation() {
    return operation;
  }
  
  protected MALMessage sendResult(
      InteractionType interactionType, 
      UOctet interactionStage,
      Boolean isError,
      NamedValueList supplements,
      Object... body) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALInteraction.sendResponse(" + 
          interactionType + ',' + interactionStage + ',' + isError + ')');
    MALMessage responseMsg = messageSender.createMessage(
        authenticationId, request.getHeader().getFrom(),
        new Time(System.currentTimeMillis()),
        request.getHeader().getTransactionId(),
        isError,
        operation,
        interactionStage, 
        supplements, qosProperties, body);
    messageSender.sendMessage(responseMsg);
    return responseMsg;
  }
  
  protected MALMessage sendResult(
      InteractionType interactionType, 
      UOctet interactionStage,
      Boolean isError,
      NamedValueList supplements,
      MALEncodedBody encodedBody) throws MALInteractionException, MALException {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "CNESMALInteraction.sendResponse(" + 
          interactionType + ',' + interactionStage + ',' + isError + ')');
    MALMessage responseMsg = messageSender.createMessage(
        authenticationId, request.getHeader().getFrom(),
        new Time(System.currentTimeMillis()),
        request.getHeader().getTransactionId(),
        isError,
        operation,
        interactionStage, 
        supplements, qosProperties, encodedBody);
    messageSender.sendMessage(responseMsg);
    return responseMsg;
  }
  
  protected void checkFailed() throws MALException {
    if (failed) {
      throw CNESMALContext.createException("Failed interaction");
    }
  }

  public void setQoSProperty(String name, Object value) {
    if (name == null) throw new IllegalArgumentException("Null name");
    qosProperties.put(name, value);
  }

  public Object getQoSProperty(String name) {
    if (name == null) throw new IllegalArgumentException("Null name");
    return qosProperties.get(name);
  }

  public Map getQoSProperties() {
    return qosProperties;
  }

}
