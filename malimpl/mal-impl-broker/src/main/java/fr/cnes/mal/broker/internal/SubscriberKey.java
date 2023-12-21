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
package fr.cnes.mal.broker.internal;

import java.io.IOException;
import java.io.Serializable;

import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.URI;

class SubscriberKey implements Serializable {
  private Identifier subscriberUri;
  private DomainKey domainKey;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    subscriberUri = new Identifier(is.readUTF());
    domainKey = (DomainKey) is.readObject();
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeUTF(subscriberUri.getValue());
    os.writeObject(domainKey);
  }
  
  public SubscriberKey(Identifier subscriberUri, DomainKey domainKey) {
    super();
    this.subscriberUri = subscriberUri;
    this.domainKey = domainKey;
  }

  public Identifier getSubscriberUri() {
    return subscriberUri;
  }

  public DomainKey getDomainKey() {
    return domainKey;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((domainKey == null) ? 0 : domainKey.hashCode());
    result = prime * result
        + ((subscriberUri == null) ? 0 : subscriberUri.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SubscriberKey other = (SubscriberKey) obj;
    if (domainKey == null) {
      if (other.domainKey != null)
        return false;
    } else if (!domainKey.equals(other.domainKey))
      return false;
    if (subscriberUri == null) {
      if (other.subscriberUri != null)
        return false;
    } else if (!subscriberUri.equals(other.subscriberUri))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SubscriberKey [subscriberUri=" + subscriberUri + ", domainKey="
        + domainKey + "]";
  }
}