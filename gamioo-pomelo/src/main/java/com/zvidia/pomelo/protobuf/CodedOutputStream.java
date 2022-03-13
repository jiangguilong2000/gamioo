// Protocol Buffers - Google's data interchange format
// Copyright 2008 Google Inc.  All rights reserved.
// http://code.google.com/p/protobuf/
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are
// met:
//
//     * Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
//     * Redistributions in binary form must reproduce the above
// copyright notice, this list of conditions and the following disclaimer
// in the documentation and/or other materials provided with the
// distribution.
//     * Neither the name of Google Inc. nor the names of its
// contributors may be used to endorse or promote products derived from
// this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
// "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
// LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
// A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
// OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
// SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
// LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
// DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
// THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
// OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
package com.zvidia.pomelo.protobuf;

import java.io.*;

/**
 * Encodes and writes protocol message fields.
 *
 * <p>This class contains two kinds of methods:  methods that write specific
 * protocol message constructs and field types and methods that write low-level values (e.g.
 * {@link #writeRawVarint32} and {@link #writeRawBytes}).  If you are
 * writing encoded protocol messages, you should use the former methods, but if
 * you are writing some other format of your own design, use the latter.
 *
 * <p>This class is totally unsynchronized.
 *
 * @author kneton@google.com Kenton Varda
 */
public final class CodedOutputStream {
    private ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    public byte[] toBytes(){
        return buffer.toByteArray();
    }

    // -----------------------------------------------------------------

    /** Write a {@code double} field to the stream. */
    public void writeDoubleNoTag(final double value) {
        writeRawLittleEndian64(Double.doubleToRawLongBits(value));
    }

    /** Write a {@code float} field to the stream. */
    public void writeFloatNoTag(final float value) {
        writeRawLittleEndian32(Float.floatToRawIntBits(value));
    }

    /** Write a {@code uint64} field to the stream. */
    public void writeUInt64NoTag(final long value) throws IOException {
        writeRawVarint64(value);
    }

    /** Write an {@code int64} field to the stream. */
    public void writeInt64NoTag(final long value) {
        writeRawVarint64(value);
    }

    /** Write an {@code int32} field to the stream. */
    public void writeInt32NoTag(final int value){
        if (value >= 0) {
            writeRawVarint32(value);
        } else {
            // Must sign-extend.
            writeRawVarint64(value);
        }
    }

    /** Write a {@code fixed64} field to the stream. */
    public void writeFixed64NoTag(final long value) throws IOException {
        writeRawLittleEndian64(value);
    }

    /** Write a {@code fixed32} field to the stream. */
    public void writeFixed32NoTag(final int value) throws IOException {
        writeRawLittleEndian32(value);
    }

    /** Write a {@code bool} field to the stream. */
    public void writeBoolNoTag(final boolean value) throws IOException {
        writeRawByte(value ? 1 : 0);
    }

    /** Write a {@code string} field to the stream. */
    public void writeStringNoTag(final String value) throws IOException {
        // Unfortunately there does not appear to be any way to tell Java to encode
        // UTF-8 directly into our buffer, so we have to let it create its own byte
        // array and then copy.
        final byte[] bytes = value.getBytes("UTF-8");
        writeRawVarint32(bytes.length);
        writeRawBytes(bytes);
    }

    /** Write a {@code uint32} field to the stream. */
    public void writeUInt32NoTag(final int value) throws IOException {
        writeRawVarint32(value);
    }

    /**
     * Write an enum field to the stream.  Caller is responsible
     * for converting the enum value to its numeric value.
     */
    public void writeEnumNoTag(final int value) throws IOException {
        writeInt32NoTag(value);
    }

    /** Write an {@code sfixed32} field to the stream. */
    public void writeSFixed32NoTag(final int value) throws IOException {
        writeRawLittleEndian32(value);
    }

    /** Write an {@code sfixed64} field to the stream. */
    public void writeSFixed64NoTag(final long value) throws IOException {
        writeRawLittleEndian64(value);
    }

    /** Write an {@code sint32} field to the stream. */
    public void writeSInt32NoTag(final int value) {
        writeRawVarint32(encodeZigZag32(value));
    }

    /** Write an {@code sint64} field to the stream. */
    public void writeSInt64NoTag(final long value) throws IOException {
        writeRawVarint64(encodeZigZag64(value));
    }

    // =================================================================

    /** Write a single byte. */
    public void writeRawByte(final byte value) {
        buffer.write(value);
    }

    /** Write a single byte, represented by an integer value. */
    public void writeRawByte(final int value) {
        writeRawByte((byte) value);
    }

    /** Write an array of bytes. */
    public void writeRawBytes(final byte[] value) {
        writeRawBytes(value, 0, value.length);
    }

    /** Write part of an array of bytes. */
    public void writeRawBytes(final byte[] value, int offset, int length){
        buffer.write(value, offset, length);
    }

    /**
     * Encode and write a varint.  {@code value} is treated as
     * unsigned, so it won't be sign-extended if negative.
     */
    public void writeRawVarint32(int value) {
        while (true) {
            if ((value & ~0x7F) == 0) {
                writeRawByte(value);
                return;
            } else {
                writeRawByte((value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    /**
     * Compute the number of bytes that would be needed to encode a varint.
     * {@code value} is treated as unsigned, so it won't be sign-extended if
     * negative.
     */
    public static int computeRawVarint32Size(final int value) {
        if ((value & (0xffffffff <<  7)) == 0) return 1;
        if ((value & (0xffffffff << 14)) == 0) return 2;
        if ((value & (0xffffffff << 21)) == 0) return 3;
        if ((value & (0xffffffff << 28)) == 0) return 4;
        return 5;
    }

    /** Encode and write a varint. */
    public void writeRawVarint64(long value) {
        while (true) {
            if ((value & ~0x7FL) == 0) {
                writeRawByte((int)value);
                return;
            } else {
                writeRawByte(((int)value & 0x7F) | 0x80);
                value >>>= 7;
            }
        }
    }

    /** Compute the number of bytes that would be needed to encode a varint. */
    public static int computeRawVarint64Size(final long value) {
        if ((value & (0xffffffffffffffffL <<  7)) == 0) return 1;
        if ((value & (0xffffffffffffffffL << 14)) == 0) return 2;
        if ((value & (0xffffffffffffffffL << 21)) == 0) return 3;
        if ((value & (0xffffffffffffffffL << 28)) == 0) return 4;
        if ((value & (0xffffffffffffffffL << 35)) == 0) return 5;
        if ((value & (0xffffffffffffffffL << 42)) == 0) return 6;
        if ((value & (0xffffffffffffffffL << 49)) == 0) return 7;
        if ((value & (0xffffffffffffffffL << 56)) == 0) return 8;
        if ((value & (0xffffffffffffffffL << 63)) == 0) return 9;
        return 10;
    }

    /** Write a little-endian 32-bit integer. */
    public void writeRawLittleEndian32(final int value) {
        writeRawByte((value      ) & 0xFF);
        writeRawByte((value >>  8) & 0xFF);
        writeRawByte((value >> 16) & 0xFF);
        writeRawByte((value >> 24) & 0xFF);
    }

    public static final int LITTLE_ENDIAN_32_SIZE = 4;

    /** Write a little-endian 64-bit integer. */
    public void writeRawLittleEndian64(final long value) {
        writeRawByte((int)(value      ) & 0xFF);
        writeRawByte((int)(value >>  8) & 0xFF);
        writeRawByte((int)(value >> 16) & 0xFF);
        writeRawByte((int)(value >> 24) & 0xFF);
        writeRawByte((int)(value >> 32) & 0xFF);
        writeRawByte((int)(value >> 40) & 0xFF);
        writeRawByte((int)(value >> 48) & 0xFF);
        writeRawByte((int)(value >> 56) & 0xFF);
    }

    public static final int LITTLE_ENDIAN_64_SIZE = 8;

    /**
     * Encode a ZigZag-encoded 32-bit value.  ZigZag encodes signed integers
     * into values that can be efficiently encoded with varint.  (Otherwise,
     * negative values must be sign-extended to 64 bits to be varint encoded,
     * thus always taking 10 bytes on the wire.)
     *
     * @param n A signed 32-bit integer.
     * @return An unsigned 32-bit integer, stored in a signed int because
     *         Java has no explicit unsigned support.
     */
    public static int encodeZigZag32(final int n) {
        // Note:  the right-shift must be arithmetic
        return (n << 1) ^ (n >> 31);
    }

    /**
     * Encode a ZigZag-encoded 64-bit value.  ZigZag encodes signed integers
     * into values that can be efficiently encoded with varint.  (Otherwise,
     * negative values must be sign-extended to 64 bits to be varint encoded,
     * thus always taking 10 bytes on the wire.)
     *
     * @param n A signed 64-bit integer.
     * @return An unsigned 64-bit integer, stored in a signed int because
     *         Java has no explicit unsigned support.
     */
    public static long encodeZigZag64(final long n) {
        // Note:  the right-shift must be arithmetic
        return (n << 1) ^ (n >> 63);
    }
}
