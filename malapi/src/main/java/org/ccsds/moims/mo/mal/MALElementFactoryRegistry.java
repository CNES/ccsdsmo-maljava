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

/**
 * A {@code MALElementFactoryRegistry} maps
 * absolute short forms to {@code MALElementFactory} instances.
 */
public class MALElementFactoryRegistry {
  
  private Hashtable<Object, MALElementFactory> elementFactories;
  
  /**
   * Constructs an element factory registry.
   */
  public MALElementFactoryRegistry() {
    elementFactories = new Hashtable<Object, MALElementFactory>();
  }
  
  /**
   * Maps an absolute short form to an element factory.
   * @param shortForm the absolute short form of the element created by the registered factory
   * @param elementFactory the element factory
   * @throws IllegalArgumentException if the short form or the element factory is {@code null}
   */
  public void registerElementFactory(Object shortForm, MALElementFactory elementFactory) throws IllegalArgumentException {
    if (shortForm == null) throw new IllegalArgumentException("Null short form");
    if (elementFactory == null) throw new IllegalArgumentException("Null element factory");
    elementFactories.put(shortForm, elementFactory);
  }
  
  /**
   * Removes the short form and its corresponding element factory.
   * This method does nothing if the short form is not registered.
   * @param shortForm the short form to remove
   * @return true if the specified short form is found and removed
   * @throws IllegalArgumentException if the short form is {@code null}
   */
  public boolean deregisterElementFactory(Object shortForm) throws IllegalArgumentException {
    if (shortForm == null) throw new IllegalArgumentException("Null short form");
    return (elementFactories.remove(shortForm) != null);
  }

  /**
   * Returns the factory of the element which short form is specified.
   * @param shortForm the short form of the element factory to lookup
   * @return the factory of the element which short form and version are specified.
   * @throws IllegalArgumentException if the short form is {@code null}
   */
  public MALElementFactory lookupElementFactory(Object shortForm) throws IllegalArgumentException {
    if (shortForm == null) throw new IllegalArgumentException("Null short form");
    MALElementFactory factory = elementFactories.get(shortForm);
    return factory;
  }
}
