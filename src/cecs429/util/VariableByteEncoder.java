package cecs429.util;

import java.util.ArrayList;
import java.util.List;
//Reference
// https://nlp.stanford.edu/IR-book/html/htmledition/variable-byte-codes-1.html
public class VariableByteEncoder {
    public static List<Long> VBEncode(List<Integer> numbers) {
        List<Long> bytestream = new ArrayList<Long>();
        long previousNumber = 0;
        for (long each : numbers) {
            bytestream.addAll(encode(each - previousNumber));
            previousNumber = each;
        }
        return bytestream;
    }

    public static List<Long> encode(long n) {
        List<Long> bytes = new ArrayList<Long>(5);
        while (true) {
            bytes.add(0, n % 128);
            if (n < 128) break;
            n /= 128;
        }
        bytes.set(bytes.size() - 1, bytes.get(bytes.size() - 1) + 128);
        return bytes;
    }



    public static List<Long> decode(List<Long> bytestream) {
        long n = 0;
        long prevNum = 0;
        List<Long> numbers = new ArrayList<Long>();
        for (int i = 0; i < bytestream.size(); i++) {
            if (bytestream.get(i) < 128) {
                n = 128 * n + bytestream.get(i);
            } else {
                n = 128 * n + (bytestream.get(i) - 128);
                numbers.add(n + prevNum);
                prevNum = numbers.get(numbers.size() - 1);
                n = 0;
            }
        }
        return numbers;
    }
}
