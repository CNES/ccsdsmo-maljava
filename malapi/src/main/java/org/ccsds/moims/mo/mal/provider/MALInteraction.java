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
package org.ccsds.moims.mo.mal.provider;

import java.util.Map;

import org.ccsds.moims.mo.mal.MALOperation;
import org.ccsds.moims.mo.mal.transport.MALMessageHeader;

/**
 * A {@code MALInteraction} represents a MAL interaction on the provider side.
 */
public interface MALInteraction {
  
  /**
   * Returns the header of the message that initiated this
   * interaction.
   * @return the header of the message that initiated this
   * interaction
   */
  public MALMessageHeader getMessageHeader();
  
  /**
   * Returns the operation.
   * called by this interaction.
   * @return the operation called by this interaction.
   */
  public MALOperation getOperation();
  
  /**
   * Sets a QoS property.
   * @param name name of the QoS property
   * @param value value of the QoS property
   */
  public void setQoSProperty(String name, Object value);
  
  /**
   * Returns the value of a QoS property.
   * @param name name of the QoS property
   * @return value of the QoS property
   */
  public Object getQoSProperty(String name);
  
  /**
   * Returns the QoS properties of the message.
   * @return the QoS properties of the message
   */
  public Map getQoSProperties();
  
}
