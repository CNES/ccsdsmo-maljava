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
package org.ccsds.moims.mo.mal.accesscontrol;

import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.MALException;

/**
 * A {@code MALAccessControlFactory} enables the MAL layer 
 * to instantiate and configure a {@code MALAccessControl}.
 */
public abstract class MALAccessControlFactory {
  
  public static final String FACTORY_CLASS = "org.ccsds.moims.mo.mal.accesscontrol.factory.class";
  
  private static Hashtable<String, Class> factoryClasses;
  
  /**
   * Registers the class of a specific {@code MALAccessControlFactory}.
   * @param factoryClass the factory class to register
   * @throws IllegalArgumentException if the specified class does not extend {@code MALAccessControlFactory}
   */
  public static void registerFactoryClass(Class factoryClass) {
    if (! MALAccessControlFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Not compliant: " + factoryClass.getName());
    if (factoryClasses == null) factoryClasses = new Hashtable<String, Class>();
    factoryClasses.put(factoryClass.getName(), factoryClass);
  }
  
  /**
   * Deregisters the class of a specific {@code MALAccessControlFactory}.
   * @param factoryClass the factory class to deregister
   * @throws IllegalArgumentException if the specified class does not extend {@code MALAccessControlFactory}
   */
  public static void deregisterFactoryClass(Class factoryClass) {
    if (! MALAccessControlFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Not compliant: " + factoryClass.getName());
    if (factoryClasses != null) {
      factoryClasses.remove(factoryClass.getName());
    }
  }
  
  /**
   * Creates a {@code MALAccessControlFactory}.
   * The environment property <code>org.ccsds.moims.mo.mal.accesscontrol.factory.class</code>
   * specifies the class name of the {@code MALAccessControlFactory} to instantiate.
   * @return the newly created MAL access control factory
   * @throws MALException if no MAL access control factory can be created
   * @throws MALException if no MALAccessControlFactory can be returned 
   */
  public static MALAccessControlFactory newFactory() throws MALException {
    try {
      String factoryClassName = System.getProperty(FACTORY_CLASS);
      if (factoryClassName == null) {
        throw new MALException("System property '" + FACTORY_CLASS + "' not found");
      }
      Class factoryClass = null;
      if (factoryClasses != null) {
        factoryClass = factoryClasses.get(factoryClassName);
      }
      if (factoryClass == null) {
        factoryClass = Class.forName(factoryClassName);
      }
      return (MALAccessControlFactory) factoryClass.newInstance();
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  /**
   * Creates a {@code MALAccessControl}.
   * @param properties configuration properties
   * @return the newly created {@code MALAccessControl}
   * @throws MALException if no {@code MALAccessControl} can be returned 
   */
  public abstract MALAccessControl createAccessControl(Map properties) throws MALException;

}
