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
package org.ccsds.moims.mo.mal;

import java.util.Hashtable;
import java.util.Map;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UInteger;
import org.ccsds.moims.mo.mal.structures.UOctet;
import org.ccsds.moims.mo.mal.structures.UShort;

/**
 * A {@code MALContextFactory} enables a MAL client to instantiate and configure MAL contexts.
 */
public abstract class MALContextFactory {
  
  public static final String MAL_FACTORY_CLASS =
    "org.ccsds.moims.mo.mal.factory.class";
  
  private static Hashtable<AreaNameKey, MALArea> areaNameRepository;
  
  private static Hashtable<AreaNumberKey, MALArea> areaNumberRepository;
  
  private static Hashtable<UInteger, Identifier> errorRepository;
  
  private static MALElementFactoryRegistry elementFactoryRegistry;
  
  private static Hashtable<String, Class> factoryClasses;
  
  static {
    areaNameRepository = new Hashtable<AreaNameKey, MALArea>();
    areaNumberRepository = new Hashtable<AreaNumberKey, MALArea>();
    errorRepository= new Hashtable<UInteger, Identifier>();
    try {
      elementFactoryRegistry = new MALElementFactoryRegistry();
      MALHelper.init(elementFactoryRegistry);
    } catch (MALException e) {
      throw new RuntimeException(e.toString());
    }
  }
  
  /**
   * Returns the element factory registry.
   * @return the element factory registry
   */
  public static MALElementFactoryRegistry getElementFactoryRegistry() {
    return elementFactoryRegistry;
  }
  
  /**
   * Registers the class of a specific {@code MALContextFactory}.
   * @param factoryClass the factory class to register
   * @throws IllegalArgumentException if the specified class does not extend {@code MALContextFactory}
   */
  public static void registerFactoryClass(Class factoryClass) throws IllegalArgumentException {
    if (! MALContextFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Class '" + factoryClass.getName() + 
          "' does not extend " + MALContextFactory.class.getName());
    if (factoryClasses == null) factoryClasses = new Hashtable<String, Class>();
    factoryClasses.put(factoryClass.getName(), factoryClass);
  }
  
  /**
   * Deregisters the class of a specific {@code MALContextFactory}.
   * @param factoryClass the factory class to deregister
   * @throws IllegalArgumentException if the specified class does not extend {@code MALContextFactory}
   */
  public static void deregisterFactoryClass(Class factoryClass) throws IllegalArgumentException {
    if (! MALContextFactory.class.isAssignableFrom(factoryClass))
      throw new IllegalArgumentException("Class '" + factoryClass.getName() + 
          "' does not extend " + MALContextFactory.class.getName());
    if (factoryClasses != null) {
      factoryClasses.remove(factoryClass.getName());
    }
  }
  
  /**
   * Creates a specific {@code MALContextFactory}.
   * The environment property <code>org.ccsds.moims.mo.mal.factory.class</code>
   * specifies the class name of the {@code MALContextFactory} to instantiate.
   * @return the newly created MAL context factory
   * @throws MALException if no {@code MALContextFactory} can be created
   */
  public static MALContextFactory newFactory() throws MALException {
    String malfactoryClassName = System.getProperty(MAL_FACTORY_CLASS);
    if (malfactoryClassName == null) {
      throw new MALException("System property '" + MAL_FACTORY_CLASS + "' not found");
    }
    Class factoryClass = null;
    if (factoryClasses != null) {
      factoryClass = factoryClasses.get(malfactoryClassName);
    }
    try {
      if (factoryClass == null) {
        factoryClass = (Class) Class.forName(malfactoryClassName);
      }
      return (MALContextFactory) factoryClass.newInstance();
    } catch (Exception e) {
      throw new MALException(e.toString(), e);
    }
  }
  
  /**
   * Registers an area.
   * @param area the area to register
   * @throws IllegalArgumentException if the specified area is null
   * @throws MALException if an area having the same name and version has already been registered
   * and if the registered area is not the same object as the area to register 
   */
  public static void registerArea(MALArea area) throws IllegalArgumentException, MALException {
    if (area == null) throw new IllegalArgumentException("Null area");
    AreaNumberKey numberKey = new AreaNumberKey(area.getNumber(), area.getVersion());
    AreaNameKey nameKey = new AreaNameKey(area.getName().getValue(), area.getVersion());
    synchronized (areaNumberRepository) {
      MALArea initialArea = (MALArea) areaNumberRepository.get(numberKey);
      if (initialArea != null && initialArea != area) {
        throw new MALException("Area already registered: " + area.getNumber());
      }
      initialArea = (MALArea) areaNameRepository.get(nameKey);
      if (initialArea != null && initialArea != area) {
        throw new MALException("Area already registered: " + area.getName());
      }
      areaNumberRepository.put(numberKey, area);
      areaNameRepository.put(nameKey, area);
    }
  }
  
  /**
   * Returns the area which name and version are specified,
   * or {@code null} if no area is found for the name and the version.
   * @param areaName the name of the area to lookup
   * @param areaVersion the version of the area to lookup
   * @return the area which name and version are specified.
   * @throws IllegalArgumentException if the specified area name is null
   */
  public static MALArea lookupArea(Identifier areaName, UOctet areaVersion) throws IllegalArgumentException {
    if (areaName == null) throw new IllegalArgumentException("Null area name");
    if (areaVersion == null) throw new IllegalArgumentException("Null version");
    return areaNameRepository.get(new AreaNameKey(areaName.getValue(), areaVersion));
  }
  
  /**
   * Returns the area which number and version are specified,
   * or {@code null} if no area is found for the number and the version.
   * @param areaNumber the number of the area to lookup
   * @param areaVersion the version of the area to lookup
   * @return the area which number and version are specified.
   * @throws IllegalArgumentException if the specified area number is null
   */
  public static MALArea lookupArea(UShort areaNumber, UOctet areaVersion) throws IllegalArgumentException {
    if (areaNumber == null) throw new IllegalArgumentException("Null area number");
    if (areaVersion == null) throw new IllegalArgumentException("Null version");
    AreaNumberKey key = new AreaNumberKey(areaNumber, areaVersion);
    MALArea area = areaNumberRepository.get(key);
    return area;
  }
  
  /**
   * Returns the name of the error which number is specified.
   * @param errorNumber the number of the error
   * @return the name of the error which number is specified
   */
  public static Identifier lookupError(UInteger errorNumber) throws IllegalArgumentException {
    if (errorNumber == null) throw new IllegalArgumentException("Null error number");
    return errorRepository.get(errorNumber);
  }
  
  /**
   * Maps the specified <code>errorNumber</code> to the specified
   * <code>errorName</code>.
   * @param errorNumber the number of the error
   * @param errorName the name of the error
   * @throws IllegalArgumentException if the specified error number or error name is null
   * @throws MALException if an error having the same number is already registered 
   */
  public static void registerError(UInteger errorNumber, Identifier errorName)
    throws IllegalArgumentException, MALException {
    if (errorNumber == null) throw new IllegalArgumentException("Null error number");
    if (errorName == null) throw new IllegalArgumentException("Null error name");
    Identifier alreadyRegisteredError = errorRepository.get(errorNumber);
    if (alreadyRegisteredError != null) {
      throw new MALException("Error '" + errorNumber + "' already registered: " + alreadyRegisteredError);
    }
    errorRepository.put(errorNumber, errorName);
  }

  /**
   * Creates a MAL context.
   * @return the newly created MAL context
   * @throws MALException if no MAL context can be created
   */
  public abstract MALContext createMALContext(Map properties) throws MALException;

  static class AreaNameKey {
    private String name;
    private UOctet version;
    
    public AreaNameKey(String name, UOctet version) {
      super();
      this.name = name;
      this.version = version;
    }
    
    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((name == null) ? 0 : name.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      AreaNameKey other = (AreaNameKey) obj;
      if (name == null) {
        if (other.name != null)
          return false;
      } else if (!name.equals(other.name))
        return false;
      if (version == null) {
        if (other.version != null)
          return false;
      } else if (!version.equals(other.version))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "AreaNameKey [name=" + name + ", version=" + version + "]";
    }
  }
  
  static class AreaNumberKey {
    private UShort number;
    private UOctet version;
    
    public AreaNumberKey(UShort number, UOctet version) {
      super();
      this.number = number;
      this.version = version;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((number == null) ? 0 : number.hashCode());
      result = prime * result + ((version == null) ? 0 : version.hashCode());
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
      AreaNumberKey other = (AreaNumberKey) obj;
      if (number == null) {
        if (other.number != null)
          return false;
      } else if (!number.equals(other.number))
        return false;
      if (version == null) {
        if (other.version != null)
          return false;
      } else if (!version.equals(other.version))
        return false;
      return true;
    }

    @Override
    public String toString() {
      return "AreaNumberKey [number=" + number + ", version=" + version + "]";
    }
  }
  
}
