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

/** The Subscription structure is used when subscribing for updates using the PUBSUB interaction pattern. It contains a single identifier that identifies the subscription being defined and a set of entities being requested. */
public final class Subscription implements org.ccsds.moims.mo.mal.structures.Composite {

  public static final Integer TYPE_SHORT_FORM = new Integer(23);

  public static final Long SHORT_FORM = new Long(281474993487895L);

  /** The identifier of this subscription. **/
  private org.ccsds.moims.mo.mal.structures.Identifier subscriptionId;

  /** The list of entities that are being subscribed for by this identified subscription. **/
  private org.ccsds.moims.mo.mal.structures.EntityRequestList entities;

  public Subscription() { }

  public Subscription(org.ccsds.moims.mo.mal.structures.Identifier subscriptionId, org.ccsds.moims.mo.mal.structures.EntityRequestList entities) {
    if (subscriptionId == null) throw new IllegalArgumentException("Null field 'subscriptionId'");
    if (entities == null) throw new IllegalArgumentException("Null field 'entities'");
    this.subscriptionId = subscriptionId;
    this.entities = entities;
  }

  /** The identifier of this subscription. **/
  public org.ccsds.moims.mo.mal.structures.Identifier getSubscriptionId() {
    return subscriptionId;
  }

  /** The identifier of this subscription. **/
  public void setSubscriptionId(org.ccsds.moims.mo.mal.structures.Identifier subscriptionId) {
    if (subscriptionId == null) throw new IllegalArgumentException("Null field 'subscriptionId'");
    this.subscriptionId = subscriptionId;
  }

  /** The list of entities that are being subscribed for by this identified subscription. **/
  public org.ccsds.moims.mo.mal.structures.EntityRequestList getEntities() {
    return entities;
  }

  /** The list of entities that are being subscribed for by this identified subscription. **/
  public void setEntities(org.ccsds.moims.mo.mal.structures.EntityRequestList entities) {
    if (entities == null) throw new IllegalArgumentException("Null field 'entities'");
    this.entities = entities;
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    encoder.encodeIdentifier(subscriptionId);
    encoder.encodeElement(entities);
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    subscriptionId = decoder.decodeIdentifier();
    entities = (org.ccsds.moims.mo.mal.structures.EntityRequestList) decoder.decodeElement(new org.ccsds.moims.mo.mal.structures.EntityRequestList());
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
    result = prime * result + ((subscriptionId == null) ? 0 : subscriptionId.hashCode());
    result = prime * result + ((entities == null) ? 0 : entities.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (obj instanceof Subscription) {
      Subscription other = (Subscription) obj;
      if (subscriptionId == null) {
        if (other.subscriptionId != null) return false;
      } else {
        if (! subscriptionId.equals(other.subscriptionId)) return false;
      }
      if (entities == null) {
        if (other.entities != null) return false;
      } else {
        if (! entities.equals(other.entities)) return false;
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
    buf.append(",subscriptionId");
    buf.append('=');
    buf.append(subscriptionId);
    buf.append(",entities");
    buf.append('=');
    buf.append(entities);
    buf.append(')');
    return buf.toString();
  }

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.Subscription();
  }

}