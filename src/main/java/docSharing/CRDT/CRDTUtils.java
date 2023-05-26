package docSharing.CRDT;

import java.util.ArrayList;
import java.util.List;

public class CRDTUtils {
    public static int comparePositions(List<Identifier> p1, List<Identifier> p2) {
        for (int i = 0; i < Math.min(p1.size(), p2.size()); i++) {
            int comp = Identifier.compare(p1.get(i),p2.get(i));
            if (comp != 0) {
                return comp;
            }
        }

        return Integer.compare(p1.size(), p2.size());
    }

    public static List<CharItem> sortByPosition(List<CharItem> content) {
        List<CharItem> sortedContent = new ArrayList<>(content);
        sortedContent.sort((char1, char2) -> comparePositions(char1.getPosition(), char2.getPosition()));

        return sortedContent;
    }

    public static Identifier head(List<Identifier> pos) {
        return pos.size() > 0 ? pos.get(0) : null;
    }

    public static List<Identifier> cons (Identifier head, List<Identifier> rest) {
        List<Identifier> res = new ArrayList<>();
        res.add(head);
        res.addAll(rest);

        return res;
    }

    public static List<Identifier> rest (List<Identifier> pos) {
        if (pos.size() <= 1) {
            return new ArrayList<>();
        }

        return new ArrayList<>(pos.subList(1, pos.size()));
    }

}
