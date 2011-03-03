package de.powerisdnmonitor.capi.util;

import java.io.Serializable;
import java.util.zip.CRC32;

/**
 *  This class is a helper class for PIMCapi implementation
 *
 *@author     Thilo Brandt
 *@created    17. August 2002
 */
public class ByteArray implements Cloneable, Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
     *  generates an hexadecimal presentation of a byte array. The characters
     *  'a' to 'f' are written in lower case (<CODE>Interger.valueOf(String s,int radix)</CODE>
     *  compatible). This method uses a 'fast' implemenatation instead of
     *  calling <CODE>Integer.toHexString()</CODE>
     *
     *@param  binArray    the byte array
     *@param  beginIndex  first byte to be written to output string
     *@param  endIndex    first byte not to be written
     *@param  separator   string to be inserted between the hexadecimal
     *      presentation of the bytes
     *@return             lower case hexadecimal string
     */

    public static String hexString(byte[] binArray, int beginIndex, int endIndex, String separator) {
        int l = endIndex - beginIndex;
        int sl = separator.length();
        StringBuffer sb = new StringBuffer(l * (sl + 2));
        int b;
        boolean is_sep = sl > 0;
        for (; beginIndex < endIndex; ) {
            b = (int) binArray[beginIndex++] & 255;
            sb.append(_nibbles.charAt(b >> 4));
            sb.append(_nibbles.charAt(b & 15));
            if (is_sep && beginIndex < endIndex) {
                sb.append(separator);
            }
        }
        return new String(sb);
    }


    /**
     *  generates an octal presentation of a byte array. This method uses a
     *  'fast' implemenatation instead of calling <CODE>Integer.toOctalString()</CODE>
     *
     *@param  binArray    the byte array
     *@param  beginIndex  first byte to be written to output string
     *@param  endIndex    first byte not to be written
     *@param  separator   string to be inserted between the octal presentation
     *      of the bytes
     *@return             octal string
     */

    public static String octalString(byte[] binArray, int beginIndex, int endIndex, String separator) {
        int l = endIndex - beginIndex;
        int sl = l == 0 ? 0 : separator.length();
        StringBuffer sb = new StringBuffer(l * (sl + 3));
        int b;
        boolean is_sep = sl > 0;
        for (; beginIndex < endIndex; ) {
            b = (int) binArray[beginIndex++] & 255;
            sb.append(b >> 6);
            sb.append((b >> 3) & 7);
            sb.append(b & 7);
            if (is_sep && beginIndex < endIndex) {
                sb.append(separator);
            }
        }
        return new String(sb);
    }


    /**
     *  generates an hexadecimal presentation of a byte array. The characters
     *  'a' to 'f' are written in lower case (<CODE>Interger.valueOf(String s,int radix)</CODE>
     *  compatible). This method uses a 'fast' implemenatation instead of
     *  calling <CODE>Integer.toHexString()</CODE>
     *
     *@param  binArray    the byte array
     *@param  beginIndex  first byte to be written to output string
     *@param  endIndex    first byte not to be written
     *@return             lower case hexadecimal string
     */

    public static String hexString(byte[] binArray, int beginIndex, int endIndex) {
        return hexString(binArray, beginIndex, endIndex, "");
    }


    /**
     *  generates an hexadecimal presentation of a byte array. The characters
     *  'a' to 'f' are written in lower case (<CODE>Interger.valueOf(String s,int radix)</CODE>
     *  compatible). This method uses a 'fast' implemenatation instead of
     *  calling <CODE>Integer.toHexString()</CODE>
     *
     *@param  binArray   the byte array
     *@param  separator  string to be inserted between the hexadecimal
     *      presentation of the bytes
     *@return            lower case hexadecimal string
     */

    public static String hexString(byte[] binArray, String separator) {
        return hexString(binArray, 0, binArray.length, separator);
    }


    /**
     *  generates an hexadecimal presentation of a byte array. The characters
     *  'a' to 'f' are written in lower case (<CODE>Interger.valueOf(String s,int radix)</CODE>
     *  compatible). This method uses a 'fast' implemenatation instead of
     *  calling <CODE>Integer.toHexString()</CODE>
     *
     *@param  binArray  the byte array
     *@return           lower case hexadecimal string
     */

    public static String hexString(byte[] binArray) {
        return hexString(binArray, 0, binArray.length, "");
    }


    /**
     *  returns the little endian <CODE>byte[]</CODE>-presentation of a
     *  numerical value. This means the first byte in the array to hold the bits
     *  2^0 to 2^7 of the <CODE>value</CODE> and so on.
     *
     *@param  value     the numeric value to be splitted in bytes
     *@param  numBytes  the number of bytes in the resulting array. It is not
     *      checked if the given value 'fits' this array.
     *@return           the byte-array
     */

    public static byte[] lowOrderBytes(int value, int numBytes) {
        long lvalue;
        byte ba[] = new byte[numBytes];
        int l;
        lvalue = (value < 0) ? 0x100000000L + value : value;
        for (l = 0; l < numBytes; l++) {
            ba[l] = (byte) (lvalue & 255);
            lvalue >>= 8;
        }
        return ba;
    }


    /**
     *  returns the little endian <CODE>byte[4]</CODE>-presentation of a
     *  numerical value. This means the first byte in the array to hold the bits
     *  2^0 to 2^7 of the <CODE>value</CODE> and so on.
     *
     *@param  value  the numeric value to be splitted in 4 bytes
     *@return        the array of 4 bytes
     */

    public static byte[] lowOrderBytes(int value) {
        return lowOrderBytes(value, 4);
    }


    /**
     *  returns the big endian <CODE>byte[]</CODE>-presentation of a numerical
     *  value. This means the first byte in the array to hold <CODE>value&gt;&gt;(8*(numBytes-1))</CODE>
     *  and so on.
     *
     *@param  value     the numeric value to be splitted in bytes
     *@param  numBytes  the number of bytes in the resulting array. It is not
     *      checked if the given value 'fits' this array.
     *@return           the byte-array
     */

    public static byte[] highOrderBytes(int value, int numBytes) {
        long lvalue;
        byte ba[] = new byte[numBytes];
        int l;
        lvalue = (value < 0) ? 0x100000000L + value : value;
        for (l = numBytes - 1; l >= 0; l--) {
            ba[l] = (byte) (lvalue & 255);
            lvalue >>= 8;
        }
        return ba;
    }


    /**
     *  returns the big endian <CODE>byte[4]</CODE>-presentation of a numerical
     *  value. This means the first byte in the array to hold <CODE>value&gt;&gt;24</CODE>
     *  and so on.
     *
     *@param  value  the numeric value to be splitted in 4 bytes
     *@return        the array of 4 bytes
     */

    public static byte[] highOrderBytes(int value) {
        return highOrderBytes(value, 4);
    }


    /**
     *  copies selected bytes from one array to another. Source and target may
     *  be the same array, even if there is an intersection area.
     *
     *@param  source       the source array
     *@param  srcIndex     the index of the first byte in the source array
     *@param  count        the number of bytes to be copied
     *@param  target       the target array
     *@param  targetIndex  the index where the first byte is to be placed in the
     *      target array
     */

    public static void copyBytes(byte[] source, int srcIndex, int count, byte[] target, int targetIndex) {
        int lauf;
        //if (source != target || srcIndex <= targetIndex) {
        
        // 2010/01/02: fix due to http://sourceforge.net/projects/jcapi/forums/forum/105670/topic/3505082
        if (source != target || srcIndex >= targetIndex) {
            for (lauf = 0; lauf < count; lauf++) {
                target[targetIndex + lauf] = source[lauf + srcIndex];
            }
        } else {
            for (lauf = count - 1; lauf >= 0; lauf--) {
                target[targetIndex + lauf] = source[lauf + srcIndex];
            }
        }
    }


    /**
     *  copies all bytes from one array to another. Source and target may be the
     *  same array, even if there is an intersection area.
     *
     *@param  source       the source array
     *@param  target       the target array
     *@param  targetIndex  the index where the first byte is to be placed in the
     *      target array
     */

    public static void copyBytes(byte[] source, byte[] target, int targetIndex) {
        copyBytes(source, 0, source.length, target, targetIndex);
    }


    /**
     *  copies selected bytes from one array to the first bytes of another.
     *  Source and target may be the same array, even if there is an
     *  intersection area.
     *
     *@param  source    the source array
     *@param  srcIndex  the index of the first byte in the source array
     *@param  count     the number of bytes to be copied
     *@param  target    the target array
     */

    public static void copyBytes(byte[] source, int srcIndex, int count, byte[] target) {
        copyBytes(source, srcIndex, count, target, 0);
    }


    /**
     *  copies all bytes from one array to another.
     *
     *@param  source  the source array
     *@param  target  the target array
     */

    public static void copyBytes(byte[] source, byte[] target) {
        copyBytes(source, 0, source.length, target, 0);
    }


    /**
     *  returns a new array with selected bytes from the given array.
     *
     *@param  source    the source array
     *@param  srcIndex  the index of the first byte in the source array
     *@param  count     the number of bytes to be copied
     *@return           a new array with the selected bytes
     */

    public static byte[] getBytes(byte[] source, int srcIndex, int count) {
        int lauf;
        byte neu[] = new byte[count];
        for (lauf = 0; lauf < count; lauf++) {
            neu[lauf] = source[srcIndex + lauf];
        }
        return neu;
    }


    /**
     *  returns a new array with selected bytes from the given array.
     *
     *@param  source    the source array
     *@param  srcIndex  the index of the first byte in the source array
     *@return           a new array with the selected bytes
     */

    public static byte[] getBytes(byte[] source, int srcIndex) {
        return getBytes(source, srcIndex, source.length - srcIndex);
    }


    /**
     *  returns the numerical value of the big endian <CODE>byte[]</CODE>
     *  -presentation. This means the first byte in the array to be interpreted
     *  as <CODE>returnValue&gt;&gt;(8*(numBytes-1))</CODE> and so on.
     *
     *@param  source    the array containig the big endian presentation
     *@param  srcIndex  the index of the first (most significant) byte
     *@param  count     the number of bytes to be interpreted
     *@return           the integer value
     */

    public static int getHighOrderInt(byte[] source, int srcIndex, int count) {
        int val = 0;
        for (count += srcIndex; srcIndex < count; srcIndex++) {
            val = (val << 8) + (255 & (int) source[srcIndex]);
        }
        return val;
    }


    /**
     *  returns the numerical value of the little endian <CODE>byte[]</CODE>
     *  -presentation. This means the first byte in the array to be interpreted
     *  as the bits 2^0 to 2^7 of the return value and so on.
     *
     *@param  source    the array containig the little endian presentation
     *@param  srcIndex  the index of the first (least significant) byte
     *@param  count     the number of bytes to be interpreted
     *@return           the integer value
     */

    public static int getLowOrderInt(byte[] source, int srcIndex, int count) {
        int val = 0;
        for (count += srcIndex; srcIndex < count; ) {
            val = (val << 8) + (255 & (int) source[--count]);
        }
        return val;
    }


    /**
     *  parses a string of hexadecimal digits into an new byte array. The string
     *  may be upper or lower case; white spaces at the beginning or end will be
     *  ignored.
     *
     *@param  hex  Description of the Parameter
     *@return      the array of bytes
     */

    public static byte[] parseHex(String hex) {
        String work = hex.toLowerCase().trim();
        byte ba[] = new byte[work.length() / 2];
        int lauf = 0;
        for (; lauf < ba.length; lauf++) {
            ba[lauf] = (byte) Integer.parseInt(work.substring(lauf << 1, (lauf << 1) + 2), 16);
        }
        return ba;
    }


    /**
     *  compares two arrays of bytes.
     *
     *@param  ba1  the first byte array
     *@param  ba2  the second byte array
     *@return      the index of the first difference, i.e. length of arrays if
     *      equal.
     */

    public static int compare(byte ba1[], byte ba2[]) {
        int lauf = 0;
        int max = (ba1.length > ba2.length) ? ba2.length : ba1.length;
        while (lauf < max && ba1[lauf] == ba2[lauf]) {
            lauf++;
        }
        return lauf;
    }


    // Suche nach Knuth-Morris-Pratt
    /**
     *  searches the occurance of a byte array inside another. This implements
     *  the algorithm of Knuth-Morris-Pratt.
     *
     *@param  byteArray    the array to search in
     *@param  searchArray  the array to search for. If this is empty the result
     *      will be the value of <CODE>beginIndex</CODE>.
     *@param  beginIndex   the position in <CODE>byteArray</CODE> to start
     *      search
     *@param  reverse      <CODE>true</CODE> for reverse search
     *@return              the position of the first (<CODE>reverse=false</CODE>
     *      ) or last (<CODE>reverse=true</CODE>) occurance of the searched
     *      bytes or -1 for no match.
     */

    public static int search(byte byteArray[], byte searchArray[], int beginIndex, boolean reverse) {
        int i = 0;
        int j = -1;
        int la = byteArray.length - 1;
        int ls = searchArray.length - 1;
        int next[] = new int[searchArray.length + 1];
        if (searchArray.length == 0) {
            return beginIndex;
        }
        if (byteArray.length == 0) {
            return -1;
        }
        // next-Array aufbauen
        next[0] = -1;
        while (i < searchArray.length) {
            if (j < 0 || (reverse ? searchArray[ls - i] == searchArray[ls - j] : searchArray[i] == searchArray[j])) {
                next[++i] = ++j;
            } else {
                j = next[j];
            }
        }
        // suchen
        i = reverse ? la - beginIndex : beginIndex;
        j = 0;
        while (j < searchArray.length && i < byteArray.length) {
            if (j < 0 || (reverse ? byteArray[la - i] == searchArray[ls - j] : byteArray[i] == searchArray[j])) {
                i++;
                j++;
            } else {
                j = next[j];
            }
        }
        return (j < searchArray.length) ? -1 : reverse ? byteArray.length - i : i - searchArray.length;
    }


    /**
     *  searches the occurance of a byte array inside another. This implements
     *  the algorithm of Knuth-Morris-Pratt.
     *
     *@param  byteArray    the array to search in
     *@param  searchArray  the array to search for. If this is empty the result
     *      will be the value of <CODE>beginIndex</CODE>.
     *@param  beginIndex   the position in <CODE>byteArray</CODE> to start
     *      search
     *@return              the position of the first occurance of the searched
     *      bytes or -1 for no match.
     */

    public static int search(byte byteArray[], byte searchArray[], int beginIndex) {
        return search(byteArray, searchArray, beginIndex, false);
    }


    /**
     *  searches the occurance of a byte array inside another. This implements
     *  the algorithm of Knuth-Morris-Pratt.
     *
     *@param  byteArray    the array to search in
     *@param  searchArray  the array to search for. If this is empty the result
     *      will be 0.
     *@return              the position of the first occurance of the searched
     *      bytes or -1 for no match.
     */

    public static int search(byte byteArray[], byte searchArray[]) {
        return search(byteArray, searchArray, 0);
    }


    // Konstruktoren ------------------------------------------------------------------------------

    /**
     *  creates an empty ByteArray.
     */

    public ByteArray() {
        _ba = new byte[0];
    }


    /**
     *  creates an ByteArray with the specified number of elements.
     *
     *@param  count  number of elements
     */

    public ByteArray(int count) {
        _ba = new byte[count];
    }


    /**
     *  creates an ByteArray "around" the given array of bytes.
     *
     *@param  ba  array of bytes. This is <B>not</B> copied but referenced as
     *      object.
     */

    public ByteArray(byte[] ba) {
        _ba = ba;
    }


    /**
     *  creates an ByteArray with the byte array presentation of the given
     *  string.
     *
     *@param  s  the string to get the bytes from.
     */

    public ByteArray(String s) {
        _ba = s.getBytes();
    }


    /**
     *  creates an ByteArray with a copy of some elements of another ByteArray.
     *
     *@param  BA          the ByteArray to get the bytes from.
     *@param  beginIndex  the first byte to be copied.
     *@param  count       the number of bytes to be copied.
     */

    public ByteArray(ByteArray BA, int beginIndex, int count) {
        _ba = getBytes(BA._ba, beginIndex, count);
    }


    /**
     *  creates an ByteArray with a copy the last elements of another ByteArray.
     *
     *@param  BA          the ByteArray to get the bytes from.
     *@param  beginIndex  the first byte to be copied.
     */

    public ByteArray(ByteArray BA, int beginIndex) {
        _ba = getBytes(BA._ba, beginIndex);
    }


    // Instanzmethoden -----------------------------------------------------------------------------

    /**
     *  returns the number of bytes in this ByteArray
     *
     *@return    the number of bytes.
     */

    public int length() {
        return _ba.length;
    }


    /**
     *  generates an hexadecimal presentation of the ByteArray. The characters
     *  'a' to 'f' are written in lower case (<CODE>Interger.valueOf(String s,int radix)</CODE>
     *  compatible). This method uses a 'fast' implemenatation instead of
     *  calling <CODE>Integer.toHexString()</CODE>
     *
     *@return    lower case hexadecimal string
     */

    public String toHexString() {
        return hexString(_ba);
    }


    /**
     *  generates an hexadecimal presentation of the ByteArray. The characters
     *  'a' to 'f' are written in lower case (<CODE>Interger.valueOf(String s,int radix)</CODE>
     *  compatible). This method uses a 'fast' implemenatation instead of
     *  calling <CODE>Integer.toHexString()</CODE>
     *
     *@param  separator  string to be inserted between the hexadecimal
     *      presentation of the bytes
     *@return            lower case hexadecimal string
     */

    public String toHexString(String separator) {
        return hexString(_ba, separator);
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public String toString() {
        return new String(_ba);
    }


    /**
     *  returns the byte array capsulated by this ByteArray (as reference). This
     *  doesn't copy the array, i.e. changes to the returned array will change
     *  the ByteArray object!
     *
     *@return    the array of bytes.
     */

    public byte[] getBytes() {
        return _ba;
    }


    /**
     *  creates a new ByteArray of a copy of this and the given ByteArray.
     *
     *@param  BA  the bytes of this ByteArray will be put at the end of the new
     *      ByteArray
     *@return     the new ByteArray.
     */

    public ByteArray concat(ByteArray BA) {
        byte na[] = new byte[_ba.length + BA._ba.length];
        copyBytes(_ba, na);
        copyBytes(BA._ba, na, _ba.length);
        return new ByteArray(na);
    }


    /**
     *  creates a new ByteArray of a copy of this and the given bytes.
     *
     *@param  ba  this bytes will be put at the end of the new ByteArray
     *@return     the new ByteArray.
     */

    public ByteArray concat(byte[] ba) {
        byte na[] = new byte[_ba.length + ba.length];
        copyBytes(_ba, na);
        copyBytes(ba, na, _ba.length);
        return new ByteArray(na);
    }


    /**
     *  returns the byte at the given index.
     *
     *@param  index  the position of the byte to be returned.
     *@return        the byte at the given index.
     */

    public byte byteAt(int index) {
        return _ba[index];
    }


    /**
     *  searches the occurance of a byte array inside this ByteArray. This
     *  implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search  the array to search for. If this is empty the result will
     *      be 0.
     *@return         the position of the first occurance of the searched bytes
     *      or -1 for no match.
     */

    public int indexOf(byte[] search) {
        return search(_ba, search);
    }


    /**
     *  searches the occurance of the bytes of the given string inside this
     *  ByteArray. This implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search  the string to search for. If this is empty the result will
     *      be 0.
     *@return         the position of the first occurance of the searched bytes
     *      or -1 for no match.
     */

    public int indexOf(String search) {
        return search(_ba, search.getBytes());
    }


    /**
     *  searches the occurance of a byte array inside this ByteArray. This
     *  implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search      the array to search for. If this is empty the result
     *      will be the value of <CODE>beginIndex</CODE>.
     *@param  beginIndex  the position in <CODE>byteArray</CODE> to start search
     *@return             the position of the first occurance of the searched
     *      bytes or -1 for no match.
     */

    public int indexOf(byte[] search, int beginIndex) {
        return search(_ba, search, beginIndex);
    }


    /**
     *  searches the occurance of the bytes of the given string inside this
     *  ByteArray. This implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search      the string to search for. If this is empty the result
     *      will be the value of <CODE>beginIndex</CODE>.
     *@param  beginIndex  the position in <CODE>byteArray</CODE> to start search
     *@return             the position of the first occurance of the searched
     *      bytes or -1 for no match.
     */

    public int indexOf(String search, int beginIndex) {
        return search(_ba, search.getBytes(), beginIndex);
    }


    /**
     *  searches the occurance of a byte inside this ByteArray. This implements
     *  the native algorithm.
     *
     *@param  searchByte  the byte to search for.
     *@return             the position of the first occurance of the searched
     *      byte or -1 for no match.
     */

    public int indexOf(byte searchByte) {
        int i = 0;
        while (i < _ba.length && _ba[i] != searchByte) {
            i++;
        }
        return (i < _ba.length) ? i : -1;
    }


    /**
     *  searches the last occurance of a byte array inside this ByteArray. This
     *  implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search  the array to search for. If this is empty the result will
     *      be 0.
     *@return         the position of the last occurance of the searched bytes
     *      or -1 for no match.
     */

    public int lastIndexOf(byte[] search) {
        return search(_ba, search, _ba.length - 1, true);
    }


    /**
     *  searches the last occurance of the bytes of the given string inside this
     *  ByteArray. This implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search  the string to search for. If this is empty the result will
     *      be 0.
     *@return         the position of the last occurance of the searched bytes
     *      or -1 for no match.
     */

    public int lastIndexOf(String search) {
        return search(_ba, search.getBytes(), _ba.length - 1, true);
    }


    /**
     *  searches the last occurance of a byte array inside this ByteArray. This
     *  implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search      the array to search for. If this is empty the result
     *      will be the value of <CODE>beginIndex</CODE>.
     *@param  beginIndex  the position in <CODE>byteArray</CODE> to start search
     *@return             the position of the last occurance of the searched
     *      bytes or -1 for no match.
     */

    public int lastIndexOf(byte[] search, int beginIndex) {
        return search(_ba, search, beginIndex, true);
    }


    /**
     *  searches the last occurance of the bytes of the given string inside this
     *  ByteArray. This implements the algorithm of Knuth-Morris-Pratt.
     *
     *@param  search      the string to search for. If this is empty the result
     *      will be the value of <CODE>beginIndex</CODE>.
     *@param  beginIndex  the position in <CODE>byteArray</CODE> to start search
     *@return             the position of the last occurance of the searched
     *      bytes or -1 for no match.
     */

    public int lastIndexOf(String search, int beginIndex) {
        return search(_ba, search.getBytes(), beginIndex, true);
    }


    /**
     *  searches the last occurance of a byte inside this ByteArray. This
     *  implements the native algorithm.
     *
     *@param  searchByte  the byte to search for.
     *@return             the position of the last occurance of the searched
     *      byte or -1 for no match.
     */

    public int lastIndexOf(byte searchByte) {
        int i = _ba.length;
        while (i-- > 0 && _ba[i] != searchByte) {
            ;
        }
        return i;
    }


    /**
     *  returns the numerical value of the little endian <CODE>byte[4]</CODE>
     *  -presentation. This means the first selected byte to be interpreted as
     *  the bits 2^0 to 2^7 of the return value and so on.
     *
     *@param  beginIndex  the index of the first (least significant) byte
     *@return             the integer value
     */

    public int lowOrderIntAt(int beginIndex) {
        return getLowOrderInt(_ba, beginIndex, 4);
    }


    /**
     *  returns the numerical value of the little endian <CODE>byte[]</CODE>
     *  -presentation. This means the first selected byte to be interpreted as
     *  the bits 2^0 to 2^7 of the return value and so on.
     *
     *@param  beginIndex  the index of the first (least significant) byte
     *@param  count       the number of bytes to be interpreted
     *@return             the integer value
     */

    public int lowOrderIntAt(int beginIndex, int count) {
        return getLowOrderInt(_ba, beginIndex, count);
    }


    /**
     *  returns the numerical value of the big endian <CODE>byte[4]</CODE>
     *  -presentation. This means the first selected byte to be interpreted as
     *  <CODE>returnValue&gt;&gt;(8*(numBytes-1))</CODE> and so on.
     *
     *@param  beginIndex  the index of the first (most significant) byte
     *@return             the integer value
     */

    public int highOrderIntAt(int beginIndex) {
        return getHighOrderInt(_ba, beginIndex, 4);
    }


    /**
     *  returns the numerical value of the big endian <CODE>byte[]</CODE>
     *  -presentation. This means the first selected byte to be interpreted as
     *  <CODE>returnValue&gt;&gt;(8*(numBytes-1))</CODE> and so on.
     *
     *@param  beginIndex  the index of the first (most significant) byte
     *@param  count       the number of bytes to be interpreted
     *@return             the integer value
     */

    public int highOrderIntAt(int beginIndex, int count) {
        return getHighOrderInt(_ba, beginIndex, count);
    }


    /**
     *  Description of the Method
     *
     *@param  BA  Description of the Parameter
     *@return     Description of the Return Value
     */
    public boolean equals(Object BA) {
        int lauf = 0;
        if (_ba.length != ((ByteArray) BA)._ba.length) {
            return false;
        } else {
            while (lauf < _ba.length && _ba[lauf] == ((ByteArray) BA)._ba[lauf]) {
                lauf++;
            }
            return lauf == _ba.length;
        }
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public int hashCode() {
        _crchash.reset();
        _crchash.update(_ba);
        return (int) _crchash.getValue();
        // alt: hexString(_ba,0,_ba.length).hashCode();
    }


    /**
     *  Description of the Method
     *
     *@return    Description of the Return Value
     */
    public Object clone() {
        return new ByteArray(this, 0);
        // Kopie erzeugen!
    }


    /**
     *@serial
     */
    private byte[] _ba;
    private transient CRC32 _crchash = new CRC32();
    private final static String _nibbles = "0123456789abcdef";
}

