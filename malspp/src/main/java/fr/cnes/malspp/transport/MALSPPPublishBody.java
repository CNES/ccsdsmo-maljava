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
package fr.cnes.malspp.transport;

import java.io.ByteArrayInputStream;
import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALEncodedElement;
import org.ccsds.moims.mo.mal.transport.MALPublishBody;

public class MALSPPPublishBody extends MALSPPMessageBody implements MALPublishBody {

  public MALSPPPublishBody(Object[] body) {
    super(body);
  }

  public MALSPPPublishBody(MALEncodingContext encodingContext,
      MALElementStreamFactory elementStreamFactory,
      ByteArrayInputStream encodedBody) {
    super(encodingContext, elementStreamFactory, encodedBody);
  }

  public MALEncodedElement getEncodedUpdate(int listIndex, int updateIndex) throws MALException {
    throw new MALException("Not implemented");
  }

  public Object getUpdate(int listIndex, int updateIndex) throws MALException {
    throw new MALException("Not implemented");
  }

  public int getUpdateCount() throws MALException {
    return getUpdateHeaderList().size();
  }

  public UpdateHeaderList getUpdateHeaderList() throws MALException {
    return (UpdateHeaderList) getBodyElement(0, new UpdateHeaderList());
  }

  public List getUpdateList(int listIndex, List updateList) throws MALException {
    return (List) getBodyElement(listIndex + 1, updateList);
  }

  public List[] getUpdateLists(List... updateLists) throws MALException {
    List[] res = new List[getElementCount() - 1];
    for (int i = 0; i < res.length; i++) {
      List toDecode;
      if (updateLists.length > i) {
        toDecode = updateLists[i];
      } else {
        toDecode = null;
      }
      res[i] = (List) getBodyElement(i + 1, toDecode);
    }
    return res;
  }
}
