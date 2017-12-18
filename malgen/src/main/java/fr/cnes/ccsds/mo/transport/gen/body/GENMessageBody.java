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

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.MALOperationStage;
import org.ccsds.moims.mo.mal.encoding.MALElementInputStream;
import org.ccsds.moims.mo.mal.encoding.MALElementStreamFactory;
import org.ccsds.moims.mo.mal.encoding.MALEncodingContext;
import org.ccsds.moims.mo.mal.transport.MALEncodedBody;
import org.ccsds.moims.mo.mal.transport.MALEncodedElement;
import org.ccsds.moims.mo.mal.transport.MALMessageBody;

/**
 * Implementation of the MALMessageBody interface.
 */
public class GENMessageBody implements MALMessageBody {

	private byte[] encodedBody;

	private List elements;

	private MALEncodingContext msgCtx;

	private MALElementStreamFactory elementStreamFactory;

	private MALElementInputStream meis;

	/**
	 * Constructor.
	 */
	public GENMessageBody(List elements) {
		this.elements = elements;
	}

	/**
	 * Constructor.
	 */
	public GENMessageBody(
			byte[] encodedBody,
			MALEncodingContext msgCtx,
			MALElementStreamFactory elementStreamFactory) {
		this.encodedBody = encodedBody;
		this.msgCtx = msgCtx;
		this.elementStreamFactory = elementStreamFactory;
		elements = new ArrayList();
	}


	@Override
	public int getElementCount() {
		if (msgCtx != null) {
			MALOperationStage stage = msgCtx.getOperation().getOperationStage(msgCtx.getHeader().getInteractionStage());
			return stage.getElementShortForms().length;
		} else {
			return elements.size();
		}
	}

	protected List getBodyElements() throws MALException {
		return elements;
	}

	private MALElementInputStream getElementInputStream() throws MALException {
		if (meis == null) {
			ByteArrayInputStream bais = new ByteArrayInputStream(encodedBody);
			meis = elementStreamFactory.createInputStream(bais);
		}
		return meis;
	}

	public Object getBodyElement(int index, Object element) throws MALException {
		if (elements.size() < index + 1) {
			int i = elements.size();
			MALElementInputStream meis = getElementInputStream();
			while (elements.size() < index + 1) {
				msgCtx.setBodyElementIndex(i);
				Object bodyElement = meis.readElement(element, msgCtx);
				elements.add(bodyElement);
				i++;
			}
		}
		return elements.get(index); 
	}

	public MALEncodedElement getEncodedBodyElement(int index) throws MALException {
		throw new MALException("Not available");
	}

	public MALEncodedBody getEncodedBody() throws MALException {
		throw new MALException("Not yet implemented");
	}
}
