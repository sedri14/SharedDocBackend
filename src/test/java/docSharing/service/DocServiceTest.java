package docSharing.service;

import docSharing.CRDT.Identifier;
import docSharing.entities.Document;
import docSharing.entities.User;
import docSharing.enums.INodeType;
import docSharing.repository.DocRepository;
import org.checkerframework.checker.units.qual.C;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
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

    public static final int ELEPHANT = 0;

    public static final int CHEETAH = 1;

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
    @DisplayName("Alloc a new position given that head digits are different")
    public void alloc_generatingANewPositionBetween_HeadDigitsAreDifferent() {
        List<Identifier> before = createIdentifiers(3, ELEPHANT, 1, CHEETAH);
        List<Identifier> after = createIdentifiers(3, ELEPHANT, 2, CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("Alloc a new position given that head digits are the same and sites are different")
    public void alloc_generatingANewPositionBetween_HeadDigitsSameSitesDifferent() {
        List<Identifier> before = createIdentifiers(1, ELEPHANT);
        List<Identifier> after = createIdentifiers(1, CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("Alloc a new position given that head digits and site ids are the same")
    public void alloc_generatingANewPositionBetween_HeadDigitsAndSitesSame() {
        List<Identifier> before = createIdentifiers(1, ELEPHANT);
        List<Identifier> after = createIdentifiers(1, ELEPHANT, 6, ELEPHANT, 3, CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("general test")
    public void alloc_generatingANewPositionBetween_test() {
        List<Identifier> before = createIdentifiers(1, ELEPHANT, 2, ELEPHANT, 3, ELEPHANT, 6, ELEPHANT);
        List<Identifier> after = createIdentifiers(1, CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        printPositionsInOrder(before, newPos, after);

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
