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

import java.util.List;
import java.util.Vector;

public class SegmentationContext {
  
  private MALSPPMessageHeader malHeader;
  
  /**
   * Segments listed in ascending order.
   */
  private List<Segment> segments;

  public SegmentationContext(MALSPPMessageHeader malHeader) {
    super();
    this.malHeader = malHeader;
    segments = new Vector<Segment>();
  }
  
  public MALSPPMessageHeader getMalHeader() {
    return malHeader;
  }

  public List<Segment> getSegments() {
    return segments;
  }
  
  public void clear() {
    segments.clear();
  }

  public void addSegment(Segment segment) {
    int index = 0;
    for (Segment s : segments) {
      if (segment.getSegmentIndex() < s.getSegmentIndex()) {
        break;
      } else {
        index++;
      }
    }
    segments.add(index, segment);
  }
    
}