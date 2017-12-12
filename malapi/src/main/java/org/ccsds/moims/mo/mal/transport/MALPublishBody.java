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

import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;

/**
 * A {@code MALPublishBody} represents the body of a 
 * PUBLISH message.
 */
public interface MALPublishBody extends MALMessageBody {

  /**
   * Returns the list of update headers.
   * @return the list of update headers
   * @throws MALException if an error occurs
   */
  public UpdateHeaderList getUpdateHeaderList() throws MALException;
  
  /**
   * Returns the lists of updates.
   * @param updateLists the lists of updates to decode
   * @return the lists of updates
   * @throws MALException if an error occurs
   */
  public List[] getUpdateLists(List... updateLists) throws MALException;
  
  /**
   * Returns the specified list of updates.
   * @param listIndex the index of the update list
   * @param updateList the update list to decode
   * @return the specified list of updates
   * @throws MALException if an error occurs
   */
  public List getUpdateList(int listIndex, List updateList) throws MALException;
  
  /**
   * Returns the number of updates.
   * @return the number of updates
   * @throws MALException if an error occurs
   */
  public int getUpdateCount() throws MALException;
  
  /**
   * Returns an update from a specified list.
   * @param listIndex the index of the update list
   * @param updateIndex the index of the update in the specified list
   * @return an update from a specified list
   * @throws MALException if an error occurs
   */
  public Object getUpdate(int listIndex, int updateIndex) throws MALException;
  
  /**
   * Returns an encoded update from a specified list.
   * @param listIndex the index of the update list
   * @param updateIndex the index of the update in the specified list
   * @return an update from a specified list
   * @throws MALException if an error occurs
   */
  public MALEncodedElement getEncodedUpdate(int listIndex, int updateIndex) throws MALException;
  
}
