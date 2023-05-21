package docSharing.service;

import docSharing.CRDT.Identifier;
import docSharing.entities.Document;
import docSharing.entities.User;
import docSharing.enums.INodeType;
import docSharing.repository.DocRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static docSharing.CRDT.CRDTUtils.comparePositions;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DocServiceTest {

    @InjectMocks
    private DocService docService;

    @Mock
    private DocRepository docRepository;

    public static final int WHALE = 0;

    public static final int BUFFALO = 1;

    Document document;

    Document emptyDoc;

    User user;

    @BeforeEach
    void setUp() {
        user = new User("user", "user@gmail.com", "1234");
        document = new Document("document1", INodeType.FILE, LocalDateTime.now(), null, null, user, new ArrayList<>(), LocalDateTime.now());
        emptyDoc = Document.createNewEmptyDocument("empty document", null, user);
    }

    @Test
    public void alloc_generatingANewPositionBetween_DifferentDigits() {
        List<Identifier> before = createIdentifiers(3, WHALE, 1, BUFFALO);
        List<Identifier> after = createIdentifiers(3, WHALE, 2, BUFFALO);

        List<Identifier> newPos = docService.alloc(before, after, WHALE);
        printPositionsInOrder(before, newPos, after);

        assertNotNull(newPos);
        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    public static List<Identifier> createIdentifiers(int... numbers) {

        List<Identifier> identifiers = new ArrayList<>();
        for (int i = 0; i < numbers.length - 1; i += 2) {
            int digit = numbers[i];
            int siteId = numbers[i + 1];
            identifiers.add(Identifier.create(digit, siteId));
        }

        return identifiers;
    }

    //Utils
    //print identifiers in format: before < newPos < after
    private void printPositionsInOrder(List<Identifier> before, List<Identifier> newPos, List<Identifier> after) {
        System.out.println(before + " <<< " + newPos + " <<< " + after);
    }
}
