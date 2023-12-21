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

class PublisherKey implements Serializable {
  private Identifier uri;
  private DomainKey domainKey;
  
  private void readObject(java.io.ObjectInputStream is)
      throws IOException, ClassNotFoundException {
    uri = new Identifier(is.readUTF());
    domainKey = (DomainKey) is.readObject();
  }
  
  private void writeObject(java.io.ObjectOutputStream os)
      throws IOException {
    os.writeUTF(uri.getValue());
    os.writeObject(domainKey);
  }
  
  public PublisherKey(Identifier uri, DomainKey domainKey) {
    super();
    this.uri = uri;
    this.domainKey = domainKey;
  }

  public Identifier getUri() {
    return uri;
  }

  public void setUri(Identifier uri) {
    this.uri = uri;
  }

  public DomainKey getDomainKey() {
    return domainKey;
  }

  public void setDomainKey(DomainKey domainKey) {
    this.domainKey = domainKey;
  }

  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result
        + ((domainKey == null) ? 0 : domainKey.hashCode());
    result = prime * result + ((uri == null) ? 0 : uri.hashCode());
    return result;
  }

  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    PublisherKey other = (PublisherKey) obj;
    if (domainKey == null) {
      if (other.domainKey != null)
        return false;
    } else if (!domainKey.equals(other.domainKey))
      return false;
    if (uri == null) {
      if (other.uri != null)
        return false;
    } else if (!uri.equals(other.uri))
      return false;
    return true;
  }
  
  public String toString() {
    return '(' + super.toString() + 
    ",uri=" + uri +
    ",domainKey=" + domainKey + ')';
  }
}