package docSharing.CRDT;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Decimal {

    public static final int BASE = 10;

    public static List<Integer> fromIdentifierList(List<Identifier> pos) {
        return pos.stream().map(Identifier::getDigit).collect(Collectors.toList());
    }

    public static List<Integer> substractGreaterThan(List<Integer> n2, List<Integer> n1) {
        int carry = 0;
        int maxLength = Math.max(n2.size(), n1.size());
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < maxLength; i++) {
            int digit2 = i < n2.size() ? n2.get(n2.size() - 1 - i) : 0;
            int digit1 = i < n1.size() ? n1.get(n1.size() - 1 - i) : 0;

            int diff = digit2 - digit1 - carry;

            if (diff < 0) {
                diff += 10;
                carry = 1;
            } else {
                carry = 0;
            }

            result.add(0, diff);
        }

//        while (result.size() > 1 && result.get(0) == 0) {
//            result.remove(0);
//        }

        return result;
    }

    public static List<Integer> increment(List<Integer> n1, List<Integer> delta) {
        if (equalsOne(delta)) {
            return new ArrayList<>(List.of(0,1));
        }

        int firstNonzeroDigit = delta.indexOf(delta.stream().filter(x -> x != 0).findFirst().orElse(-1));
        List<Integer> inc = new ArrayList<>(delta.subList(0, firstNonzeroDigit));
        inc.add(0);
        inc.add(1);
        //add assumes that n1 is bigger in size than inc.
        List<Integer> v1;
        if (n1.size() < inc.size()) {
            v1 = add(inc, n1);
        } else {
            v1 = add(n1, inc);
        }
        List<Integer> v2 = v1.get(v1.size() - 1) == 0 ? add(v1, inc) : v1;

        //try to delete leading zeros.
        int i = 0;
        while (i < v2.size() && v2.get(i) == 0) {
            v2.remove(i);
        }
        return v2;
    }

    private static boolean equalsOne(List<Integer> delta) {
        //delta contains leading zeros
        int sum = delta.stream().mapToInt(Integer::intValue).sum();
        return sum == 1;
    }

    public static List<Integer> add(List<Integer> n1, List<Integer> inc) {
        int n = n1.size(), m = inc.size();
        Integer[] res = new Integer[n + 1];

        int i = n - 1, j = m - 1, k = n;
        int carry = 0, s = 0;

        while (j >= 0) {
            s = n1.get(i) + inc.get(j) + carry;
            res[k] = s % 10;
            carry = s / 10;
            k--;
            i--;
            j--;
        }

        while (i >= 0) {
            s = n1.get(i) + carry;
            res[k] = s % 10;
            carry = s / 10;
            i--;
            k--;
        }

        //in case there's a last carry
        if (carry == 1) {
            res[0] = 1;
            return Arrays.stream(res).collect(Collectors.toList());
        } else {
            return Arrays.stream(res)
                    .collect(Collectors.toList()).subList(1, res.length);
        }
    }

    public static List<Identifier> toIdentifierList(List<Integer> next, List<Identifier> before, List<Identifier> after, int siteId) {
        List<Identifier> result = new ArrayList<>();

        //edge case
        if (Objects.equals(next, new ArrayList<>(List.of(0, 1)))) {
            //copy the before position and concat [1,siteId]
            ArrayList<Identifier> incBefore = new ArrayList<>(before);
            incBefore.add(Identifier.create(1, siteId));
            return incBefore;
        }

        for (int i = 0; i < next.size(); i++) {
            int digit = next.get(i);

            if (i == next.size() - 1) {
                result.add(Identifier.create(digit, siteId));
            } else if (i < before.size() && digit == before.get(i).digit) {
                result.add(Identifier.create(digit, before.get(i).siteId));
            } else if (i < after.size() && digit == after.get(i).digit) {
                result.add(Identifier.create(digit, after.get(i).siteId));
            } else {
                result.add(Identifier.create(digit, siteId));
            }
        }

        return result;
    }
}
