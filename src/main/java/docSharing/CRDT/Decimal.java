package docSharing.CRDT;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Decimal {

    public static final int BASE = 256;
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

        while (result.size() > 1 && result.get(0) == 0) {
            result.remove(0);
        }

        return result;
    }

    public static List<Integer> increment(List<Integer> n1, List<Integer> delta) {
        int firstNonzeroDigit = delta.indexOf(delta.stream().filter(x -> x != 0).findFirst().orElse(-1));
        List<Integer> inc = new ArrayList<>(delta.subList(0, firstNonzeroDigit));
        inc.add(0);
        inc.add(1);
        List<Integer> v1 = add(n1, inc);
        List<Integer> v2 = v1.get(v1.size() - 1) == 0 ? add(v1, inc) : v1;

        return v2;
    }

    private static List<Integer> add(List<Integer> n1, List<Integer> inc) {
        List<Integer> result = new ArrayList<>();

        int carry = 0;
        int maxLength = Math.max(n1.size(), inc.size());

        for (int i = 0; i < maxLength; i++) {
            int sum = carry;

            if (i < n1.size()) {
                sum += n1.get(i);
            }

            if (i < inc.size()) {
                sum += inc.get(i);
            }

            result.add(sum % 10);
            carry = sum / 10;
        }

        if (carry > 0) {
            result.add(carry);
        }

        return result;
    }

    public static List<Identifier> toIdentifierList(List<Integer> next, List<Identifier> before, List<Identifier> after, int siteId) {
        List<Identifier> result = new ArrayList<>();

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
