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
package fr.cnes.encoding.binary;

import java.util.List;

import org.ccsds.moims.mo.mal.MALDecoder;
import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALListDecoder;
import org.ccsds.moims.mo.mal.structures.Attribute;
import org.ccsds.moims.mo.mal.structures.Blob;
import org.ccsds.moims.mo.mal.structures.Duration;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.FineTime;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.Time;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.ULong;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.URI;
import org.ccsds.moims.mo.mal.structures.UShort;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

public class BinaryListDecoder implements MALListDecoder {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug.getLogger(BinaryListDecoder.class.getName());
  
  private List list;
  
  private int size;
  
  private MALDecoder malDecoder;
  
  BinaryListDecoder(List list, int size, MALDecoder malDecoder) {
    this.list = list;
    this.size = size;
    this.malDecoder = malDecoder;
  }
  
  public boolean hasNext() {
    /*
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALListDecoder.hasNext()");
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "list=" + list);
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "size=" + size);*/
    return list.size() < size;
  }

  public Blob decodeBlob() throws MALException {
    return malDecoder.decodeBlob();
  }

  public Boolean decodeBoolean() throws MALException {
    return malDecoder.decodeBoolean();
  }

  public Duration decodeDuration() throws MALException {
    return malDecoder.decodeDuration();
  }

  public Float decodeFloat() throws MALException {
    return malDecoder.decodeFloat();
  }

  public Double decodeDouble() throws MALException {
    return malDecoder.decodeDouble();
  }

  public Identifier decodeIdentifier() throws MALException {
    return malDecoder.decodeIdentifier();
  }

  public Byte decodeOctet() throws MALException {
    return malDecoder.decodeOctet();
  }

  public Short decodeShort() throws MALException {
    return malDecoder.decodeShort();
  }

  public Integer decodeInteger() throws MALException {
    return malDecoder.decodeInteger();
  }

  public Long decodeLong() throws MALException {
    return malDecoder.decodeLong();
  }

  public String decodeString() throws MALException {
    return malDecoder.decodeString();
  }
  
  public URI decodeURI() throws MALException {
    return malDecoder.decodeURI();
  }

  public Time decodeTime() throws MALException {
    return malDecoder.decodeTime();
  }
  
  public FineTime decodeFineTime() throws MALException {
    return malDecoder.decodeFineTime();
  }
/*
  public Element decodeElement() throws MALException {
    return malDecoder.decodeElement();
  }

  public Enumeration decodeEnumeration(Enumeration enumeration) throws MALException {
    return malDecoder.decodeEnumeration(enumeration);
  }
  */

  public MALListDecoder createListDecoder(List list) throws MALException {
    return malDecoder.createListDecoder(list);
  }
/*
  public Composite decodeComposite(Composite composite) throws MALException {
    return malDecoder.decodeComposite(composite);
  }
*/
  public Attribute decodeAttribute() throws MALException {
    return malDecoder.decodeAttribute();
  }

  public Element decodeElement(Element element) throws MALException {
    return malDecoder.decodeElement(element);
  }

  public Attribute decodeNullableAttribute() throws MALException {
    return malDecoder.decodeNullableAttribute();
  }

  public Blob decodeNullableBlob() throws MALException {
    return malDecoder.decodeNullableBlob();
  }

  public Boolean decodeNullableBoolean() throws MALException {
    return malDecoder.decodeNullableBoolean();
  }

  public Double decodeNullableDouble() throws MALException {
    return malDecoder.decodeNullableDouble();
  }

  public Duration decodeNullableDuration() throws MALException {
    return malDecoder.decodeNullableDuration();
  }

  public Element decodeNullableElement(Element element) throws MALException {
    return malDecoder.decodeNullableElement(element);
  }

  public FineTime decodeNullableFineTime() throws MALException {
    return malDecoder.decodeNullableFineTime();
  }

  public Float decodeNullableFloat() throws MALException {
    return malDecoder.decodeNullableFloat();
  }

  public Identifier decodeNullableIdentifier() throws MALException {
    return malDecoder.decodeNullableIdentifier();
  }

  public Integer decodeNullableInteger() throws MALException {
    return malDecoder.decodeNullableInteger();
  }

  public Long decodeNullableLong() throws MALException {
    return malDecoder.decodeNullableLong();
  }

  public Byte decodeNullableOctet() throws MALException {
    return malDecoder.decodeNullableOctet();
  }

  public Short decodeNullableShort() throws MALException {
    return malDecoder.decodeNullableShort();
  }

  public String decodeNullableString() throws MALException {
    return malDecoder.decodeNullableString();
  }

  public Time decodeNullableTime() throws MALException {
    return malDecoder.decodeNullableTime();
  }

  public UInteger decodeNullableUInteger() throws MALException {
    return malDecoder.decodeNullableUInteger();
  }

  public ULong decodeNullableULong() throws MALException {
    return malDecoder.decodeNullableULong();
  }

  public UOctet decodeNullableUOctet() throws MALException {
    return malDecoder.decodeNullableUOctet();
  }

  public URI decodeNullableURI() throws MALException {
    return malDecoder.decodeNullableURI();
  }

  public UShort decodeNullableUShort() throws MALException {
    return malDecoder.decodeNullableUShort();
  }

  public UInteger decodeUInteger() throws MALException {
    return malDecoder.decodeUInteger();
  }

  public ULong decodeULong() throws MALException {
    return malDecoder.decodeULong();
  }

  public UOctet decodeUOctet() throws MALException {
    return malDecoder.decodeUOctet();
  }

  public UShort decodeUShort() throws MALException {
    return malDecoder.decodeUShort();
  }

  // Note (AF): Added for compatibility with ESA API,not really part of the interface.
  public int size() {
	  return size;
  }
}
