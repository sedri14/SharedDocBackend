package docSharing.service;

import docSharing.CRDT.Decimal;
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

    @Test
    @DisplayName("general test")
    public void alloc_generatingANewPositionBetween_newPosBiggerSizeThanPrev() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 9,ELEPHANT);
        List<Identifier> after = new ArrayList<>();

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("general test")
    public void alloc_generatingANewPositionBetween_newPosSameSizeAsPrev() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 9,ELEPHANT,1,ELEPHANT);
        List<Identifier> after = new ArrayList<>();

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }
    @Test
    @DisplayName("alloc between same user example 1")
    public void alloc_generatingANewPositionBetween_sameUser_example1() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 9,ELEPHANT);
        List<Identifier> after = createIdentifiers(2,ELEPHANT,1,ELEPHANT,4, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between same user example 2")
    public void alloc_generatingANewPositionBetween_sameUser_example2() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT);
        List<Identifier> after = createIdentifiers(1,ELEPHANT,6,ELEPHANT,3, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between same user example 3")
    public void alloc_generatingANewPositionBetween_sameUser_example3() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 4, ELEPHANT);
        List<Identifier> after = createIdentifiers(1,ELEPHANT,7, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between same user example 4")
    public void alloc_generatingANewPositionBetween_sameUser_example4() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT);
        List<Identifier> after = createIdentifiers(1,ELEPHANT,4, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }
    @Test
    @DisplayName("alloc between same user example 5")
    public void alloc_generatingANewPositionBetween_sameUser_example5() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 6, ELEPHANT, 3, ELEPHANT);
        List<Identifier> after = createIdentifiers(1,ELEPHANT,7, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between same user example 6")
    public void alloc_generatingANewPositionBetween_sameUser_example6() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 6, ELEPHANT, 3, ELEPHANT);
        List<Identifier> after = createIdentifiers(1,ELEPHANT,6, ELEPHANT, 4, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between same user example 7")
    public void alloc_generatingANewPositionBetween_sameUser_example7() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT);
        List<Identifier> after = createIdentifiers(2,ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between same user example 8")
    public void alloc_generatingANewPositionBetween_sameUser_example8() {
        List<Identifier> before = createIdentifiers(9,ELEPHANT, 9, ELEPHANT, 9, ELEPHANT);
        List<Identifier> after = new ArrayList<>();

        List<Identifier> newPos = docService.alloc(before, after, ELEPHANT);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between different users example 1")
    public void alloc_generatingANewPositionBetween_differentUsers_example1() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT, 6, ELEPHANT, 3, CHEETAH);
        List<Identifier> after = createIdentifiers(1,ELEPHANT,7, ELEPHANT);

        List<Identifier> newPos = docService.alloc(before, after, CHEETAH);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between different users example 2")
    public void alloc_generatingANewPositionBetween_differentUsers_example2() {
        List<Identifier> before = createIdentifiers(1,ELEPHANT);
        List<Identifier> after = createIdentifiers(1,CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, CHEETAH);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between different users example 3")
    public void alloc_generatingANewPositionBetween_differentUsers_example3() {
        List<Identifier> before = createIdentifiers(2, ELEPHANT, 1,ELEPHANT);
        List<Identifier> after = createIdentifiers(2, ELEPHANT, 1,CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, CHEETAH);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc between different users example 4")
    public void alloc_generatingANewPositionBetween_differentUsers_example4() {
        List<Identifier> before = createIdentifiers(1, ELEPHANT, 9,ELEPHANT);
        List<Identifier> after = createIdentifiers(1, CHEETAH, 1,CHEETAH);

        List<Identifier> newPos = docService.alloc(before, after, CHEETAH);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    @DisplayName("alloc first char ever")
    public void alloc_generatingFirstChar() {
        List<Identifier> before = new ArrayList<>();
        List<Identifier> after = new ArrayList<>();

        List<Identifier> newPos = docService.alloc(before, after, CHEETAH);
        assertNotNull(newPos);
        before = (before.size() > 0) ? before : new ArrayList<>(List.of(Identifier.create(0,ELEPHANT)));
        after = after.size() > 0 ? after : new ArrayList<>(List.of(Identifier.create(10,ELEPHANT)));
        printPositionsInOrder(before, newPos, after);

        assertTrue(comparePositions(before, newPos) < 0);
        assertTrue(comparePositions(newPos, after) < 0);
    }

    @Test
    public void add_addingNumbersRepresentedAsIntegerArray_noCarry() {
        List<Integer> result = Decimal.add(List.of(3, 2), List.of(9));
        assertEquals(List.of(4,1), result);
    }

    @Test
    public void add_addingNumbersRepresentedAsIntegerArray_Carry() {
        List<Integer> result = Decimal.add(List.of(9, 9), List.of(9));
        assertEquals(List.of(1,0,8), result);
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
