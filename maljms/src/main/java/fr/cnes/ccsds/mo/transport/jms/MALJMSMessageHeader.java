package fr.cnes.ccsds.mo.transport.jms;

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

public class MALJMSMessageHeader implements MALMessageHeader {
  
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

  private UOctet AreaVersion; 

  private Boolean IsErrorMessage;

  public MALJMSMessageHeader() {}
  
  public MALJMSMessageHeader(URI uRIFrom, Blob authenticationId, URI uRITo,
      Time timestamp, QoSLevel qoSlevel,
      UInteger priority, IdentifierList domain,
      Identifier networkZone, SessionType session, Identifier sessionName,
      org.ccsds.moims.mo.mal.structures.InteractionType interactionType,
      UOctet interactionStage, Long transactionId,
      UShort serviceArea, UShort service, UShort operation,
      UOctet areaVersion, Boolean isErrorMessage) {
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
    AreaVersion = areaVersion;
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
    return AreaVersion;
  }

  public void setAreaVersion(UOctet areaVersion) {
    AreaVersion = areaVersion;
  }

  public Boolean getIsErrorMessage() {
    return IsErrorMessage;
  }

  public void setIsErrorMessage(Boolean isErrorMessage) {
    IsErrorMessage = isErrorMessage;
  }

  @Override
  public String toString() {
    return "MALAMQPMessageHeader [URIFrom=" + URIFrom + ", AuthenticationId="
        + AuthenticationId + ", URITo=" + URITo + ", Timestamp=" + Timestamp
        + ", QoSlevel=" + QoSlevel + ", Priority=" + Priority + ", Domain="
        + Domain + ", NetworkZone=" + NetworkZone + ", Session=" + Session
        + ", SessionName=" + SessionName + ", InteractionType="
        + InteractionType + ", InteractionStage=" + InteractionStage
        + ", TransactionId=" + TransactionId + ", ServiceArea=" + ServiceArea
        + ", Service=" + Service + ", Operation=" + Operation
        + ", AreaVersion=" + AreaVersion + ", IsErrorMessage="
        + IsErrorMessage + "]";
  }
}
