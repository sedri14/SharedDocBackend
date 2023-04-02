package docSharing.CRDT;

public class Identifier {
    private int digit;

    public Identifier(int digit) {
        this.digit = digit;
    }

    public int getDigit() {
        return digit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Identifier)) return false;

        Identifier that = (Identifier) o;

        return digit == that.digit;
    }

    @Override
    public int hashCode() {
        return digit;
    }

    //    //compare identifiers by digit. In case of equality, compare by site id.
//    public static int compare(Identifier i1, Identifier i2) {
//        if (i1.digit < i2.digit) {
//            return -1;
//        } else if (i1.digit > i2.digit) {
//            return 1;
//        } else {
//            if (i1.site < i2.site) {
//                return -1;
//            } else if (i1.site > i2.site) {
//                return 1;
//            } else {
//                return 0;
//            }
//        }
//    }
}
