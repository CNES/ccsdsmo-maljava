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

public class Segment {
  
  private int sequenceFlags;
  
  private long segmentIndex;
  
  private byte[] content;
  
  private int offset;
  
  private int length;
  
  private long arrivalTime;
  
  public Segment(int sequenceFlags, long segmentIndex, byte[] content,
      int offset, int length, long arrivalTime) {
    this.sequenceFlags = sequenceFlags;
    this.segmentIndex = segmentIndex;
    this.content = content;
    this.offset = offset;
    this.length = length;
    this.arrivalTime = arrivalTime;
  }

  public long getArrivalTime() {
    return arrivalTime;
  }

  public int getSequenceFlags() {
    return sequenceFlags;
  }

  public long getSegmentIndex() {
    return segmentIndex;
  }

  public byte[] getContent() {
    return content;
  }

  public int getOffset() {
    return offset;
  }

  public int getLength() {
    return length;
  }

  @Override
  public String toString() {
    return "Segment [sequenceFlags=" + sequenceFlags + ", segmentIndex="
        + segmentIndex + ", offset=" + offset + ", length=" + length + "]";
  }
  
}