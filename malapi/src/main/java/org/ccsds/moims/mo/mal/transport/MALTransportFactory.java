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

import java.lang.reflect.Constructor;
import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALContext;
import org.ccsds.moims.mo.mal.MALException;

/**
 * A {@code MALTransportFactory} enables a MAL client to instantiate and configure a {@code MALTransport}.
 * This class is abstract and must be extended by a specific factory class.
 */
public abstract class MALTransportFactory {

  public static final String FACTORY_PROP_NAME_PREFIX = 
    "org.ccsds.moims.mo.mal.transport.protocol";
  
  private static Hashtable<String, Class> factoryClasses;
  
  /**
   * Registers the class of a specific {@code MALTransportFactory}.
   * @param factoryClass the factory to register
   * @throws IllegalArgumentException if the specified class does not extend {@code MALTransportFactory}
   */
  public static void registerFactoryClass(Class factoryClass) throws IllegalArgumentException {
    if (! MALTransportFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Not compliant: " + factoryClass.getName());
    if (factoryClasses == null) factoryClasses = new Hashtable<String, Class>();
    factoryClasses.put(factoryClass.getName(), factoryClass);
  }
  
  /**
   * Deregisters the class of a specific {@code MALTransportFactory}.
   * @param factoryClass the factory to deregister
   * @throws IllegalArgumentException if the specified class does not extend {@code MALTransportFactory}
   */
  public static void deregisterFactoryClass(Class factoryClass) throws IllegalArgumentException {
    if (! MALTransportFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Not compliant: " + factoryClass.getName());
    if (factoryClasses != null) {
      factoryClasses.remove(factoryClass.getName());
    }
  }
  
  private String protocol;
  
  /**
   * Creates a new {@code MALTransportFactory}.
   * @param protocol name of the transport protocol
   * @return a newly created transport factory
   * @throws IllegalArgumentException if the protocol is {@code null}
   * @throws MALException if an error occurs
   */
  public static MALTransportFactory newFactory(String protocol) 
    throws IllegalArgumentException, MALException {
    if (protocol == null) throw new IllegalArgumentException("Null protocol");
    String propName = FACTORY_PROP_NAME_PREFIX + '.' + protocol;
    String className = System.getProperty(propName);
    if (className == null)
      throw new MALException("Unknown protocol: " + protocol);
    Class factoryClass = null;
    if (factoryClasses != null) {
      factoryClass = factoryClasses.get(className);
    }
    try {
      if (factoryClass == null) {
        factoryClass = Class.forName(className);
      }
      Constructor factoryConstructor = factoryClass.getConstructor(new Class[] {String.class});
      MALTransportFactory factory = (MALTransportFactory) factoryConstructor.newInstance(new Object[] {protocol});
      return factory;
    } catch (Exception exc) {
      throw new MALException(exc.toString(), exc);
    }
  }
  
  /**
   * Constructs a {@code MALTransportFactory} with a protocol name.
   * @param protocol name of the transport protocol.
   */
  public MALTransportFactory(String protocol) {
    this.protocol = protocol;
  }
  
  /**
   * Returns the protocol name.
   * @return the protocol name.
   */
  public String getProtocol() {
    return protocol;
  }
  
  /**
   * Creates a new transport.
   * @param malContext {@code MALContext} that owns the {@code MALTransport} to create
   * @param properties configuration properties
   * @return a newly created transport
   * @throws MALException if an error occurs
   */
  public abstract MALTransport createTransport(MALContext malContext, Map properties) 
    throws MALException;

}
