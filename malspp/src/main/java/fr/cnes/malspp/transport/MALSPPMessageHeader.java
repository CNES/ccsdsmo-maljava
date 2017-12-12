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
package fr.cnes.malspp.transport;

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
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

public class MALSPPMessageHeader implements MALMessageHeader {
  
  private URI URIFrom;

  private Blob AuthenticationId;

  private URI URITo;

  private Time Timestamp;

  private QoSLevel QoSlevel;

  private UInteger Priority;

  private IdentifierList Domain;

  private Identifier NetworkZone;    

  private SessionType Session;

  private Identifier SessionName;    

  private InteractionType InteractionType;    

  private UOctet InteractionStage;   

  private Long TransactionId;

  private UShort ServiceArea;

  private UShort Service;

  private UShort Operation;

  private UOctet ServiceVersion; 

  private Boolean IsErrorMessage;
  
  public MALSPPMessageHeader() {}

  public MALSPPMessageHeader(URI uRIFrom, Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel,
      UInteger priority, IdentifierList domain,
      Identifier networkZone, SessionType session, Identifier sessionName,
      org.ccsds.moims.mo.mal.structures.InteractionType interactionType,
      UOctet interactionStage, Long transactionId,
      UShort serviceArea, UShort service, UShort operation,
      UOctet serviceVersion, Boolean isErrorMessage) {
    URIFrom = uRIFrom;
    AuthenticationId = authenticationId;
    URITo = uRITo;
    Timestamp = timestamp;
    QoSlevel = qoSlevel;
    Priority = priority;
    Domain = domain;
    NetworkZone = networkZone;
    Session = session;
    SessionName = sessionName;
    InteractionType = interactionType;
    InteractionStage = interactionStage;
    TransactionId = transactionId;
    ServiceArea = serviceArea;
    Service = service;
    Operation = operation;
    ServiceVersion = serviceVersion;
    IsErrorMessage = isErrorMessage;
  }

  public URI getURIFrom() {
    return URIFrom;
  }

  public void setURIFrom(URI uRIFrom) {
    URIFrom = uRIFrom;
  }

  public Blob getAuthenticationId() {
    return AuthenticationId;
  }

  public void setAuthenticationId(Blob authenticationId) {
    AuthenticationId = authenticationId;
  }

  public URI getURITo() {
    return URITo;
  }

  public void setURITo(URI uRITo) {
    URITo = uRITo;
  }

  public Time getTimestamp() {
    return Timestamp;
  }

  public void setTimestamp(Time timestamp) {
    Timestamp = timestamp;
  }

  public QoSLevel getQoSlevel() {
    return QoSlevel;
  }

  public void setQoSlevel(QoSLevel qoSlevel) {
    QoSlevel = qoSlevel;
  }

  public UInteger getPriority() {
    return Priority;
  }

  public void setPriority(UInteger priority) {
    Priority = priority;
  }

  public IdentifierList getDomain() {
    return Domain;
  }

  public void setDomain(IdentifierList domain) {
    Domain = domain;
  }

  public Identifier getNetworkZone() {
    return NetworkZone;
  }

  public void setNetworkZone(Identifier networkZone) {
    NetworkZone = networkZone;
  }

  public SessionType getSession() {
    return Session;
  }

  public void setSession(SessionType session) {
    Session = session;
  }

  public Identifier getSessionName() {
    return SessionName;
  }

  public void setSessionName(Identifier sessionName) {
    SessionName = sessionName;
  }

  public InteractionType getInteractionType() {
    return InteractionType;
  }

  public void setInteractionType(InteractionType interactionType) {
    InteractionType = interactionType;
  }

  public UOctet getInteractionStage() {
    return InteractionStage;
  }

  public void setInteractionStage(UOctet interactionStage) {
    InteractionStage = interactionStage;
  }

  public Long getTransactionId() {
    return TransactionId;
  }

  public void setTransactionId(Long transactionId) {
    TransactionId = transactionId;
  }

  public UShort getServiceArea() {
    return ServiceArea;
  }

  public void setServiceArea(UShort serviceArea) {
    ServiceArea = serviceArea;
  }

  public UShort getService() {
    return Service;
  }

  public void setService(UShort service) {
    Service = service;
  }

  public UShort getOperation() {
    return Operation;
  }

  public void setOperation(UShort operation) {
    Operation = operation;
  }

  public UOctet getAreaVersion() {
    return ServiceVersion;
  }

  public void setAreaVersion(UOctet serviceVersion) {
    ServiceVersion = serviceVersion;
  }

  public Boolean getIsErrorMessage() {
    return IsErrorMessage;
  }

  public void setIsErrorMessage(Boolean isErrorMessage) {
    IsErrorMessage = isErrorMessage;
  }

  @Override
  public String toString() {
    return "MALSPPMessageHeader [URIFrom=" + URIFrom + ", AuthenticationId="
        + AuthenticationId + ", URITo=" + URITo + ", Timestamp=" + Timestamp
        + ", QoSlevel=" + QoSlevel + ", Priority=" + Priority + ", Domain="
        + Domain + ", NetworkZone=" + NetworkZone + ", Session=" + Session
        + ", SessionName=" + SessionName + ", InteractionType="
        + InteractionType + ", InteractionStage=" + InteractionStage
        + ", TransactionId=" + TransactionId + ", ServiceArea=" + ServiceArea
        + ", Service=" + Service + ", Operation=" + Operation
        + ", ServiceVersion=" + ServiceVersion + ", IsErrorMessage="
        + IsErrorMessage + "]";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((AuthenticationId == null) ? 0 : AuthenticationId.hashCode());
    result = prime * result + ((Domain == null) ? 0 : Domain.hashCode());
    result = prime * result
        + ((InteractionStage == null) ? 0 : InteractionStage.hashCode());
    result = prime * result
        + ((InteractionType == null) ? 0 : InteractionType.hashCode());
    result = prime * result
        + ((IsErrorMessage == null) ? 0 : IsErrorMessage.hashCode());
    result = prime * result
        + ((NetworkZone == null) ? 0 : NetworkZone.hashCode());
    result = prime * result + ((Operation == null) ? 0 : Operation.hashCode());
    result = prime * result + ((Priority == null) ? 0 : Priority.hashCode());
    result = prime * result + ((QoSlevel == null) ? 0 : QoSlevel.hashCode());
    result = prime * result + ((Service == null) ? 0 : Service.hashCode());
    result = prime * result
        + ((ServiceArea == null) ? 0 : ServiceArea.hashCode());
    result = prime * result
        + ((ServiceVersion == null) ? 0 : ServiceVersion.hashCode());
    result = prime * result + ((Session == null) ? 0 : Session.hashCode());
    result = prime * result
        + ((SessionName == null) ? 0 : SessionName.hashCode());
    result = prime * result + ((Timestamp == null) ? 0 : Timestamp.hashCode());
    result = prime * result
        + ((TransactionId == null) ? 0 : TransactionId.hashCode());
    result = prime * result + ((URIFrom == null) ? 0 : URIFrom.hashCode());
    result = prime * result + ((URITo == null) ? 0 : URITo.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    MALSPPMessageHeader other = (MALSPPMessageHeader) obj;
    if (AuthenticationId == null) {
      if (other.AuthenticationId != null)
        return false;
    } else if (!AuthenticationId.equals(other.AuthenticationId))
      return false;
    if (Domain == null) {
      if (other.Domain != null)
        return false;
    } else if (!Domain.equals(other.Domain))
      return false;
    if (InteractionStage == null) {
      if (other.InteractionStage != null)
        return false;
    } else if (!InteractionStage.equals(other.InteractionStage))
      return false;
    if (InteractionType == null) {
      if (other.InteractionType != null)
        return false;
    } else if (!InteractionType.equals(other.InteractionType))
      return false;
    if (IsErrorMessage == null) {
      if (other.IsErrorMessage != null)
        return false;
    } else if (!IsErrorMessage.equals(other.IsErrorMessage))
      return false;
    if (NetworkZone == null) {
      if (other.NetworkZone != null)
        return false;
    } else if (!NetworkZone.equals(other.NetworkZone))
      return false;
    if (Operation == null) {
      if (other.Operation != null)
        return false;
    } else if (!Operation.equals(other.Operation))
      return false;
    if (Priority == null) {
      if (other.Priority != null)
        return false;
    } else if (!Priority.equals(other.Priority))
      return false;
    if (QoSlevel == null) {
      if (other.QoSlevel != null)
        return false;
    } else if (!QoSlevel.equals(other.QoSlevel))
      return false;
    if (Service == null) {
      if (other.Service != null)
        return false;
    } else if (!Service.equals(other.Service))
      return false;
    if (ServiceArea == null) {
      if (other.ServiceArea != null)
        return false;
    } else if (!ServiceArea.equals(other.ServiceArea))
      return false;
    if (ServiceVersion == null) {
      if (other.ServiceVersion != null)
        return false;
    } else if (!ServiceVersion.equals(other.ServiceVersion))
      return false;
    if (Session == null) {
      if (other.Session != null)
        return false;
    } else if (!Session.equals(other.Session))
      return false;
    if (SessionName == null) {
      if (other.SessionName != null)
        return false;
    } else if (!SessionName.equals(other.SessionName))
      return false;
    if (Timestamp == null) {
      if (other.Timestamp != null)
        return false;
    } else if (!Timestamp.equals(other.Timestamp))
      return false;
    if (TransactionId == null) {
      if (other.TransactionId != null)
        return false;
    } else if (!TransactionId.equals(other.TransactionId))
      return false;
    if (URIFrom == null) {
      if (other.URIFrom != null)
        return false;
    } else if (!URIFrom.equals(other.URIFrom))
      return false;
    if (URITo == null) {
      if (other.URITo != null)
        return false;
    } else if (!URITo.equals(other.URITo))
      return false;
    return true;
  }
}
