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
package org.ccsds.moims.mo.mal.transport;

import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.ccsds.moims.mo.mal.structures.InteractionType;
import org.ccsds.moims.mo.mal.structures.QoSLevel;
import org.ccsds.moims.mo.mal.structures.SessionType;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALMessageHeader} represents a MAL message header.
 */
public interface MALMessageHeader {

  public URI getURIFrom();

  public Blob getAuthenticationId();

  public URI getURITo();

  public Time getTimestamp();

  public QoSLevel getQoSlevel();

  public UInteger getPriority();

  public IdentifierList getDomain();

  public Identifier getNetworkZone();

  public SessionType getSession();

  public Identifier getSessionName();

  public InteractionType getInteractionType();

  public UOctet getInteractionStage();

  public Long getTransactionId();

  public UShort getServiceArea();

  public UShort getService();

  public UShort getOperation();

  public UOctet getAreaVersion();

  public Boolean getIsErrorMessage();

  public void setURIFrom(URI uRIFrom);

  public void setAuthenticationId(Blob authenticationId);

  public void setURITo(URI uRITo);

  public void setTimestamp(Time timestamp);

  public void setQoSlevel(QoSLevel qoSlevel);

  public void setPriority(UInteger priority);

  public void setDomain(IdentifierList domain);

  public void setNetworkZone(Identifier networkZone);

  public void setSession(SessionType session);

  public void setSessionName(Identifier sessionName);

  public void setInteractionType(InteractionType interactionType);

  public void setInteractionStage(UOctet interactionStage);

  public void setTransactionId(Long transactionId);

  public void setServiceArea(UShort serviceArea);

  public void setService(UShort service);

  public void setOperation(UShort operation);

  public void setAreaVersion(UOctet areaVersion);

  public void setIsErrorMessage(Boolean isErrorMessage);

}
