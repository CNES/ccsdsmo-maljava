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
package fr.cnes.mal;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALInteractionException;
import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALMessage;

public interface MessageSender {
  
  public MALMessage sendMessage(MALMessage msg) 
      throws MALInteractionException, MALException;
  
  public void sendMessages(MALMessage[] messages) 
      throws MALInteractionException, MALException;
  
  public MALMessage createMessage(
      Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel,
      UInteger priority, IdentifierList domain,
      Identifier networkZone, SessionType session, Identifier sessionName,
      Long transactionId,
      Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage,
      Map qosProperties, Object... body) throws MALException;
  
  public MALMessage createMessage(
      Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel,
      UInteger priority, IdentifierList domain,
      Identifier networkZone, SessionType session, Identifier sessionName,
      Long transactionId,
      Boolean isErrorMessage,
      MALOperation op, UOctet interactionStage,
      Map qosProperties, MALEncodedBody encodedBody) throws MALException;

}
