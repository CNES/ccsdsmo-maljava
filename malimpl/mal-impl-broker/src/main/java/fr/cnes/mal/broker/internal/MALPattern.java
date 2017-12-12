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

import org.ccsds.moims.mo.mal.structures.EntityKey;
import org.ccsds.moims.mo.mal.structures.Identifier;
import org.objectweb.util.monolog.api.BasicLevel;
import org.objectweb.util.monolog.api.Logger;

import fr.dyade.aaa.common.Strings;

public class MALPattern {
  public final static Logger logger = fr.dyade.aaa.common.Debug
  .getLogger(MALPattern.class.getName());
  
  public final static String ALL = "*";

  private static boolean match(Identifier subkey, Identifier pattern) {
    if (pattern == null) {
      if (subkey == null) return true;
      else return false;
    } else if (! pattern.getValue().equals(ALL)) {
      if (subkey == null) return false;
      if (! pattern.getValue().equals(subkey.getValue())) return false;
      else return true;
    } else {
      return true;
    }
  }
  
  private static boolean match(Long subkey, Long pattern) {
    if (pattern == null) {
      if (subkey == null) return true;
      else return false;
    } else if (pattern.intValue() != 0) {
      if (subkey == null) return false;
      if (pattern.intValue() != subkey.intValue()) return false;
      else return true;
    } else {
      return true;
    }
  }

  public static boolean match(EntityKey key, EntityKey pattern) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALPattern[" + Strings.toString(pattern) + 
          "].match(" + key + ')');
    if (! match(key.getFirstSubKey(), pattern.getFirstSubKey())) return false;
    if (! match(key.getSecondSubKey(), pattern.getSecondSubKey())) return false;
    if (! match(key.getThirdSubKey(), pattern.getThirdSubKey())) return false;
    if (! match(key.getFourthSubKey(), pattern.getFourthSubKey())) return false;
 
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "-> MATCHED");
    return true;
  }
  
  public static boolean isAll(Long subkey) {
    if (subkey == null) {
      return false;
    } else {
      return subkey.intValue() == 0;
    }
  }
  
  public static boolean isAll(Identifier subkey) {
    if (subkey == null) {
      return false;
    } else {
      return ALL.equals(subkey.getValue());
    }
  }
  
  public static boolean includes(Long includingSubKey, Long includedSubKey) {
    if (! isAll(includingSubKey)) {
      if (includedSubKey == null) {
        if (includingSubKey != null) return false;
      } else {
        return includedSubKey.equals(includingSubKey);
      }
    }
    return true;
  }
  
  public static boolean includes(Identifier includingSubKey, Identifier includedSubKey) {
    if (! isAll(includingSubKey)) {
      if (includedSubKey == null) {
        if (includingSubKey != null) return false;
      } else {
        return includedSubKey.equals(includingSubKey);
      }
    }
    return true;
  }
  
  public static boolean includes(EntityKey includingPattern, EntityKey includedPattern) {
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "MALPattern[" + Strings.toString(includingPattern) + 
          "].includes(" + Strings.toString(includedPattern) + ')');
    if (! includes(includingPattern.getFirstSubKey(), includedPattern.getFirstSubKey())) return false;
    if (! includes(includingPattern.getSecondSubKey(), includedPattern.getSecondSubKey())) return false;
    if (! includes(includingPattern.getThirdSubKey(), includedPattern.getThirdSubKey())) return false;
    if (! includes(includingPattern.getFourthSubKey(), includedPattern.getFourthSubKey())) return false;
    if (logger.isLoggable(BasicLevel.DEBUG))
      logger.log(BasicLevel.DEBUG, "-> INCLUDES");
    return true;
  }
  
  public static boolean potentialMatch(EntityKey pattern1, EntityKey pattern2) {
    if ((! isAll(pattern1.getFirstSubKey())) && (! includes(pattern2.getFirstSubKey(), pattern1.getFirstSubKey()))) return false;
    if ((! isAll(pattern1.getSecondSubKey())) && (! includes(pattern2.getSecondSubKey(), pattern1.getSecondSubKey()))) return false;
    if ((! isAll(pattern1.getThirdSubKey())) && (! includes(pattern2.getThirdSubKey(), pattern1.getThirdSubKey()))) return false;
    if ((! isAll(pattern1.getFourthSubKey())) && (! includes(pattern2.getFourthSubKey(), pattern1.getFourthSubKey()))) return false;
    return true;
  }
}
