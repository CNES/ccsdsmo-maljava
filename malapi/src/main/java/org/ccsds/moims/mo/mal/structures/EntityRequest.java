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
package org.ccsds.moims.mo.mal.structures;

/** The EntityRequest structure is used when subscribing for updates using the PUBSUB interaction pattern. */
public final class EntityRequest implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(24);

  public static final Long SHORT_FORM = new Long(281474993487896L);

  /** Optional subdomain identifier that is appended to the Message Header domain identifier when requesting entities in a subdomain of this domain. **/
  private org.ccsds.moims.mo.mal.structures.IdentifierList subDomain;

  /** If set to True, then all updates regardless of Area shall be sent. **/
  private Boolean allAreas;

  /** If set to True, then all updates regardless of Service shall be sent. **/
  private Boolean allServices;

  /** If set to True, then all updates regardless of Operation shall be sent. **/
  private Boolean allOperations;

  /** The Boolean denotes that only change updates are to be sent rather than all updates. **/
  private Boolean onlyOnChange;

  /** The list of entities to be monitored. **/
  private org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys;

  public EntityRequest() { }

  public EntityRequest(org.ccsds.moims.mo.mal.structures.IdentifierList subDomain, Boolean allAreas, Boolean allServices, Boolean allOperations, Boolean onlyOnChange, org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys) {
    if (allAreas == null) throw new IllegalArgumentException("Null field 'allAreas'");
    if (allServices == null) throw new IllegalArgumentException("Null field 'allServices'");
    if (allOperations == null) throw new IllegalArgumentException("Null field 'allOperations'");
    if (onlyOnChange == null) throw new IllegalArgumentException("Null field 'onlyOnChange'");
    if (entityKeys == null) throw new IllegalArgumentException("Null field 'entityKeys'");
    this.subDomain = subDomain;
    this.allAreas = allAreas;
    this.allServices = allServices;
    this.allOperations = allOperations;
    this.onlyOnChange = onlyOnChange;
    this.entityKeys = entityKeys;
  }

  /** Optional subdomain identifier that is appended to the Message Header domain identifier when requesting entities in a subdomain of this domain. **/
  public org.ccsds.moims.mo.mal.structures.IdentifierList getSubDomain() {
    return subDomain;
  }

  /** Optional subdomain identifier that is appended to the Message Header domain identifier when requesting entities in a subdomain of this domain. **/
  public void setSubDomain(org.ccsds.moims.mo.mal.structures.IdentifierList subDomain) {
    this.subDomain = subDomain;
  }

  /** If set to True, then all updates regardless of Area shall be sent. **/
  public Boolean getAllAreas() {
    return allAreas;
  }

  /** If set to True, then all updates regardless of Area shall be sent. **/
  public void setAllAreas(Boolean allAreas) {
    if (allAreas == null) throw new IllegalArgumentException("Null field 'allAreas'");
    this.allAreas = allAreas;
  }

  /** If set to True, then all updates regardless of Service shall be sent. **/
  public Boolean getAllServices() {
    return allServices;
  }

  /** If set to True, then all updates regardless of Service shall be sent. **/
  public void setAllServices(Boolean allServices) {
    if (allServices == null) throw new IllegalArgumentException("Null field 'allServices'");
    this.allServices = allServices;
  }

  /** If set to True, then all updates regardless of Operation shall be sent. **/
  public Boolean getAllOperations() {
    return allOperations;
  }

  /** If set to True, then all updates regardless of Operation shall be sent. **/
  public void setAllOperations(Boolean allOperations) {
    if (allOperations == null) throw new IllegalArgumentException("Null field 'allOperations'");
    this.allOperations = allOperations;
  }

  /** The Boolean denotes that only change updates are to be sent rather than all updates. **/
  public Boolean getOnlyOnChange() {
    return onlyOnChange;
  }

  /** The Boolean denotes that only change updates are to be sent rather than all updates. **/
  public void setOnlyOnChange(Boolean onlyOnChange) {
    if (onlyOnChange == null) throw new IllegalArgumentException("Null field 'onlyOnChange'");
    this.onlyOnChange = onlyOnChange;
  }

  /** The list of entities to be monitored. **/
  public org.ccsds.moims.mo.mal.structures.EntityKeyList getEntityKeys() {
    return entityKeys;
  }

  /** The list of entities to be monitored. **/
  public void setEntityKeys(org.ccsds.moims.mo.mal.structures.EntityKeyList entityKeys) {
    if (entityKeys == null) throw new IllegalArgumentException("Null field 'entityKeys'");
    this.entityKeys = entityKeys;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeNullableElement(subDomain);
    encoder.encodeBoolean(allAreas);
    encoder.encodeBoolean(allServices);
    encoder.encodeBoolean(allOperations);
    encoder.encodeBoolean(onlyOnChange);
    encoder.encodeElement(entityKeys);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    subDomain = (org.ccsds.moims.mo.mal.structures.IdentifierList) decoder.decodeNullableElement(new org.ccsds.moims.mo.mal.structures.IdentifierList());
    allAreas = decoder.decodeBoolean();
    allServices = decoder.decodeBoolean();
    allOperations = decoder.decodeBoolean();
    onlyOnChange = decoder.decodeBoolean();
    entityKeys = (org.ccsds.moims.mo.mal.structures.EntityKeyList) decoder.decodeElement(new org.ccsds.moims.mo.mal.structures.EntityKeyList());
    return this;
  }

  public org.ccsds.moims.mo.mal.structures.UShort getAreaNumber() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_NUMBER;
  }

  public org.ccsds.moims.mo.mal.structures.UOctet getAreaVersion() {
    return org.ccsds.moims.mo.mal.MALHelper.MAL_AREA_VERSION;
  }

  public org.ccsds.moims.mo.mal.structures.UShort getServiceNumber() {
    return org.ccsds.moims.mo.mal.MALService.NULL_SERVICE_NUMBER;
  }

  public Long getShortForm() {
    return SHORT_FORM;
  }

  public Integer getTypeShortForm() {
    return TYPE_SHORT_FORM;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((subDomain == null) ? 0 : subDomain.hashCode());
    result = prime * result + ((allAreas == null) ? 0 : allAreas.hashCode());
    result = prime * result + ((allServices == null) ? 0 : allServices.hashCode());
    result = prime * result + ((allOperations == null) ? 0 : allOperations.hashCode());
    result = prime * result + ((onlyOnChange == null) ? 0 : onlyOnChange.hashCode());
    result = prime * result + ((entityKeys == null) ? 0 : entityKeys.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof EntityRequest) {
      EntityRequest other = (EntityRequest) obj;
      if (subDomain == null) {
        if (other.subDomain != null) return false;
      } else {
        if (! subDomain.equals(other.subDomain)) return false;
      }
      if (allAreas == null) {
        if (other.allAreas != null) return false;
      } else {
        if (! allAreas.equals(other.allAreas)) return false;
      }
      if (allServices == null) {
        if (other.allServices != null) return false;
      } else {
        if (! allServices.equals(other.allServices)) return false;
      }
      if (allOperations == null) {
        if (other.allOperations != null) return false;
      } else {
        if (! allOperations.equals(other.allOperations)) return false;
      }
      if (onlyOnChange == null) {
        if (other.onlyOnChange != null) return false;
      } else {
        if (! onlyOnChange.equals(other.onlyOnChange)) return false;
      }
      if (entityKeys == null) {
        if (other.entityKeys != null) return false;
      } else {
        if (! entityKeys.equals(other.entityKeys)) return false;
      }
      return true;
    } else {
      return false;
    }
  }

  public String toString() {
    StringBuffer buf = new StringBuffer();
    buf.append('(');
    buf.append(super.toString());
    buf.append(",subDomain");
    buf.append('=');
    buf.append(subDomain);
    buf.append(",allAreas");
    buf.append('=');
    buf.append(allAreas);
    buf.append(",allServices");
    buf.append('=');
    buf.append(allServices);
    buf.append(",allOperations");
    buf.append('=');
    buf.append(allOperations);
    buf.append(",onlyOnChange");
    buf.append('=');
    buf.append(onlyOnChange);
    buf.append(",entityKeys");
    buf.append('=');
    buf.append(entityKeys);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.EntityRequest();
  }

}