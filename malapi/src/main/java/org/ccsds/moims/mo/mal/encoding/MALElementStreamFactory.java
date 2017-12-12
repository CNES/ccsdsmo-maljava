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
package org.ccsds.moims.mo.mal.encoding;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.Blob;

/**
 * A {@code MALElementStreamFactory} enables a MAL client to instantiate and 
 * configure MAL element encoding and decoding streams.
 */
public abstract class MALElementStreamFactory {
  
  public static final String FACTORY_PROP_NAME_PREFIX = 
    "org.ccsds.moims.mo.mal.encoding.protocol";
  
  private static Hashtable<String, Class> factoryClasses;
  
  /**
   * Registers the class of a specific {@code MALElementStreamFactory}.
   * @param factoryClass the factory to register
   * @throws IllegalArgumentException if the specified class does not extend {@code MALElementStreamFactory}
   */
  public static void registerFactoryClass(Class factoryClass) {
    if (! MALElementStreamFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Not compliant: " + factoryClass.getName());
    if (factoryClasses == null) factoryClasses = new Hashtable<String, Class>();
    factoryClasses.put(factoryClass.getName(), factoryClass);
  }
  
  /**
   * Deregisters the class of a specific {@code MALElementStreamFactory}.
   * @param factoryClass the factory to deregister
   * @throws IllegalArgumentException if the specified class does not extend {@code MALElementStreamFactory}
   */
  public static void deregisterFactoryClass(Class factoryClass) {
    if (! MALElementStreamFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Not compliant: " + factoryClass.getName());
    if (factoryClasses != null) {
      factoryClasses.remove(factoryClass.getName());
    }
  }
  
  /**
   * Creates a MAL element stream factory with the specified protocol
   * and configuration properties.
   * @param protocol name of the protocol to be handled
   * @param properties configuration properties
   * @return the newly created MAL element stream factory
   * @throws MALException if no MAL element stream factory can be created
   */
  public static MALElementStreamFactory newFactory(String protocol, Map properties) 
    throws MALException {
    String factoryClassName = System.getProperty(FACTORY_PROP_NAME_PREFIX + '.' + protocol);
    if (factoryClassName == null) {
      throw new MALException("Unknown protocol: " + protocol);
    }
    Class factoryClass = null;
    if (factoryClasses != null) {
      factoryClass = factoryClasses.get(factoryClassName);
    }
    try {
      if (factoryClass == null) {
        factoryClass = Class.forName(factoryClassName);
      }
      MALElementStreamFactory factory = (MALElementStreamFactory) factoryClass.newInstance();
      factory.init(protocol, properties);
      return factory;
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  /**
   * Initializes the factory.
   * @param protocol name of the transport protocol
   * @param properties configuration properties
   * @throws MALException if an internal error occurs
   */
  protected abstract void init(String protocol, Map properties) throws MALException;
  
  /**
   * Creates a {@code MALElementInputStream} from an input stream.
   * @param is input stream used to decode elements
   * @return a newly created {@code MALElementInputStream}
   * @throws IllegalArgumentException if the specified input stream is null
   * @throws MALException if a {@code MALElementInputStream} cannot be created
   */
  public abstract MALElementInputStream createInputStream(InputStream is)
      throws IllegalArgumentException, MALException;
  
  /**
   * Creates a {@code MALElementOutputStream} from an output stream.
   * @param os output stream used to encode elements
   * @return a newly created {@code MALElementOutputStream}
   * @throws IllegalArgumentException if the specified output stream is null
   * @throws MALException if a {@code MALElementOutputStream} cannot be created
   */
  public abstract MALElementOutputStream createOutputStream(OutputStream os)
    throws IllegalArgumentException, MALException;
  
  /**
   * Creates a {@code MALElementInputStream} from a byte array.
   * @param bytes bytes to be decoded
   * @param offset index of the first byte to decode
   * @return a newly created {@code MALElementInputStream}
   * @throws IllegalArgumentException if the specified byte array is null
   * @throws MALException if a {@code MALElementInputStream} cannot be created
   */
  public abstract MALElementInputStream createInputStream(
      byte[] bytes, int offset)
      throws java.lang.IllegalArgumentException, MALException;

  /**
   * Encodes an element array and returns the encoding result as a byte array.
   * @param elements the elements to encode
   * @param ctx the encoding context
   * @return the encoding result
   * @throws MALException if an encoding error occurs
   */
  public abstract Blob encode(Object[] elements, MALEncodingContext ctx)
      throws MALException;

}
