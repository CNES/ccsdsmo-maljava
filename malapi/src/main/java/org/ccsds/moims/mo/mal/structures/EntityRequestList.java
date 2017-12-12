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

public final class EntityRequestList extends java.util.ArrayList<org.ccsds.moims.mo.mal.structures.EntityRequest> implements org.ccsds.moims.mo.mal.structures.CompositeList<org.ccsds.moims.mo.mal.structures.EntityRequest> {

  public static final Integer TYPE_SHORT_FORM = new Integer(-24);

  public static final Long SHORT_FORM = new Long(281475010265064L);

  public EntityRequestList() {}

  public EntityRequestList(int size) {
    super(size);
  }

  public void encode(org.ccsds.moims.mo.mal.MALEncoder encoder) throws org.ccsds.moims.mo.mal.MALException {
    org.ccsds.moims.mo.mal.MALListEncoder listEncoder = encoder.createListEncoder(this);
    for (int i = 0; i < size(); i++) {
      org.ccsds.moims.mo.mal.structures.EntityRequest element = (org.ccsds.moims.mo.mal.structures.EntityRequest) get(i);
      listEncoder.encodeNullableElement(element);
    }
  }

  public org.ccsds.moims.mo.mal.structures.Element decode(org.ccsds.moims.mo.mal.MALDecoder decoder) throws org.ccsds.moims.mo.mal.MALException {
    org.ccsds.moims.mo.mal.MALListDecoder listDecoder = decoder.createListDecoder(this);
    while (listDecoder.hasNext()) {
      add((org.ccsds.moims.mo.mal.structures.EntityRequest)listDecoder.decodeNullableElement(new org.ccsds.moims.mo.mal.structures.EntityRequest()));
    }
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

  public org.ccsds.moims.mo.mal.structures.Element createElement() { 
    return new org.ccsds.moims.mo.mal.structures.EntityRequestList();
  }

}
