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
package fr.cnes.mal.provider;

import org.ccsds.moims.mo.mal.MALException;
import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.ccsds.moims.mo.mal.structures.IdentifierList;
import org.objectweb.util.monolog.api.Logger;

public class StructureHelper {
  
  public final static Logger logger = fr.dyade.aaa.common.Debug
  .getLogger(StructureHelper.class.getName());
  
  public static String[] createStringArray(IdentifierList idList) {
    if (idList == null) return new String[0];
    String[] res = new String[idList.size()];
    for (int i = 0; i < res.length; i++) {
      Identifier id = idList.get(i);
      if (id != null) {
        res[i] = id.getValue();
      } else {
        res[i] = null;
      }
    }
    return res;
  }
  
  public static String toString(Identifier id) {
    if (id == null) return null;
    else return id.getValue();
  }
  
  public static String toString(Integer id) {
    if (id == null) return null;
    else return "" + id.intValue();
  }
  
  public static String toString(Long id) {
    if (id == null) return null;
    else return "" + id.longValue();
  }
  
  public static String[] createStringArray(EntityKey entityKey) {
    String[] res = new String[4];
    res[0] = toString(entityKey.getFirstSubKey());
    res[1] = toString(entityKey.getSecondSubKey());
    res[2] = toString(entityKey.getThirdSubKey());
    res[3] = toString(entityKey.getFourthSubKey());
    return res;
  }
  
  public static IdentifierList createDomain(String[] stringList) {
    IdentifierList domain = new IdentifierList();
    for (int i = 0; i < stringList.length; i++) {
      domain.add(new Identifier(stringList[i]));
    }
    return domain;
  }
  
  public static MALException createException(Exception exc) {
    return new MALException(exc.toString(), exc);
  }
  
  public static EntityKey createEntityKey(String[] key) {
    EntityKey res = new EntityKey(
        createIdentifier(key[0]), 
        createLong(key[1]), 
        createLong(key[2]), 
        createLong(key[3]));
    return res;
  }
  
  public static Identifier createIdentifier(String key) {
    if (key == null) {
      return null;
    } else {
      return new Identifier(key);
    }
  }
  
  public static Long createLong(String key) {
    if (key == null) {
      return null;
    } else {
      return new Long(Long.parseLong(key));
    }
  }
  
}
