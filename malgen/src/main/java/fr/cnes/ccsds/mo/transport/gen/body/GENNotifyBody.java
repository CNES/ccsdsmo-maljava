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
package fr.cnes.ccsds.mo.transport.gen.body;

import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.structures.Element;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.UpdateHeaderList;
import org.ccsds.moims.mo.mal.transport.MALNotifyBody;

/**
 * Implementation of the MALNotifyBody interface.
 */
public class GENNotifyBody extends GENPublishBody implements MALNotifyBody {
	/**
	 * Constructor.
	 */
	public GENNotifyBody(List<Element> elements) {
		super(elements);
	}

	/**
	 * Constructor.
	 */
	public GENNotifyBody(
			byte[] encodedBody,
			MALEncodingContext msgCtx,
			MALElementStreamFactory elementStreamFactory) {
		super(encodedBody, msgCtx, elementStreamFactory);
	}

	public Identifier getSubscriptionId() throws MALException {
		return (Identifier) getBodyElement(0, new Identifier());
	}

	public UpdateHeaderList getUpdateHeaderList() throws MALException {
		return (UpdateHeaderList) getBodyElement(1, new UpdateHeaderList());
	}

	public List getUpdateList(int listIndex, List updateList) throws MALException {
		return (List) getBodyElement(listIndex + 2, updateList);
	}

	public List[] getUpdateLists(List... updateLists) throws MALException {
		List[] res = new List[getElementCount() - 2];
		for (int i = 0; i < res.length; i++) {
			List toDecode;
			if (updateLists.length > i) {
				toDecode = updateLists[i];
			} else {
				toDecode = null;
			}
			res[i] = (List) getBodyElement(i + 2, toDecode);
		}
		return res;
	}
}
