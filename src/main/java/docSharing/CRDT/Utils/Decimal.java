//package docSharing.CRDT.Utils;
//
//import docSharing.CRDT.Identifier;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
//public class Decimal {
//
//
//    public static List<Integer> fromPosition(List<Identifier> pos) {
//        List<Integer> decimal =  new ArrayList<>();
//        for(Identifier i : pos) {
//            decimal.add(i.getDigit());
//        }
//
//        return decimal;
//    }
//
//    public static List<Integer> substractGreaterThan(List<Integer> dec2, List<Integer> dec1) {
//            ArrayList<Integer> result = new ArrayList<Integer>();
//            int carry = 0;
//            int i = dec2.size() - 1;
//            int j = dec1.size() - 1;
//            while (i >= 0 || j >= 0) {
//                int x = i >= 0 ? dec2.get(i) : 0;
//                int y = j >= 0 ? dec1.get(j) : 0;
//                int diff = x - y - carry;
//                if (diff < 0) {
//                    diff += 10;
//                    carry = 1;
//                } else {
//                    carry = 0;
//                }
//                result.add(diff);
//                i--;
//                j--;
//            }
//            Collections.reverse(result);
//            while (result.size() > 1 && result.get(0) == 0) {
//                result.remove(0);
//            }
//
//            return result;
//        }
//
//    public static List<Integer> increment(List<Integer> dec1, List<Integer> delta) {
//
//    }
//}
