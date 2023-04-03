package docSharing.service;

import docSharing.CRDT.CRDT;
import docSharing.CRDT.Identifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.DisplayName;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class DocServiceTest {

    @InjectMocks
    private DocService docService;

    private Map<Integer, Boolean> strategy;


    @BeforeEach
    void setUp() {
        strategy = new HashMap<>();
        strategy.put(1, true);
        strategy.put(2, false);
        strategy.put(3, true);
        strategy.put(4, false);
    }

    @Test
    void addVal_givenIdentifier_getNewIdentifier() {
        List<Identifier> p = createIdentifiersList(9, 60);
        List<Identifier> expected = createIdentifiersList(9, 62);
        int valToAdd = 2;

        assertIterableEquals(expected, docService.addVal(p, valToAdd));
    }

    @Test
    void subVal_givenIdentifier_getNewIdentifier() {
        List<Identifier> p = createIdentifiersList(9,19);
        List<Identifier> q = createIdentifiersList(9, 65);
        List<Identifier> expected = createIdentifiersList(9, 60);
        int valToSub = 5;

        assertIterableEquals(expected, docService.subVal(p, q, valToSub, 2,5));
    }

    @Test
    void subVal_givenIdentifier_lastDigitZero_getNewIdentifier() {
        List<Identifier> p = createIdentifiersList(9,6);
        List<Identifier> q = createIdentifiersList(10,0);
        List<Identifier> expected = createIdentifiersList(9, 61);
        int valToSub = 3;

        assertIterableEquals(expected, docService.subVal(p, q, valToSub, 2,5));
    }

    @Test
    @DisplayName("[9] - [0] = 8")
    void calculateInterval_givenTwoIdentifiers_SameDepth1Base5_getNumOfAvailableSpots() {
        List<Identifier> p = createIdentifiersList(0);
        List<Identifier> q = createIdentifiersList(9);

        assertEquals(8, docService.calculateInterval(p, q, 1, 5));
    }

    @Test
    @DisplayName("[9,60] - [9,51] = 8")
    void calculateInterval_givenTwoIdentifiers_SameDepth2Base5_getNumOfAvailableSpots() {
        List<Identifier> p = createIdentifiersList(9, 51);
        List<Identifier> q = createIdentifiersList(9, 60);

        assertEquals(0, docService.calculateInterval(p, q, 1, 5));
        assertEquals(8, docService.calculateInterval(p, q, 2, 5));
    }

    @Test
    @DisplayName("[10] - [9,60] = 4 (2^6 nodes)")
    void calculateInterval_givenTwoIdentifiersDifferentLevelsBase5_getNumOfAvailableSpots() {
        List<Identifier> p = createIdentifiersList(9, 60);
        List<Identifier> q = createIdentifiersList(10);

        assertEquals(0, docService.calculateInterval(p, q, 1, 5));
        assertEquals(3, docService.calculateInterval(p, q, 2, 5));
    }

    @Test
    @DisplayName("[10] - [9,64] = 0 (2^6 nodes)")
    void calculateInterval_givenTwoIdentifiersDifferentLevelsEdgeBase5_getNumOfAvailableSpots() {
        List<Identifier> p = createIdentifiersList(9, 63);
        List<Identifier> q = createIdentifiersList(10);

        assertEquals(0, docService.calculateInterval(p, q, 1, 5));
        assertEquals(0, docService.calculateInterval(p, q, 2, 5));
    }

    @Test
    @DisplayName("[10] - [9,63,127] = 0 (base 5)")
    void calculateInterval_givenTwoIdentifiers_qDepth1pDepth3_Base5_getNumOfAvailableSpots_noSpots() {
        List<Identifier> p = createIdentifiersList(9, 63, 127);
        List<Identifier> q = createIdentifiersList(10);

        assertEquals(0, docService.calculateInterval(p, q, 1, 5));
        assertEquals(0, docService.calculateInterval(p, q, 2, 5));
        assertEquals(0, docService.calculateInterval(p, q, 3, 5));
    }

    @Test
    @DisplayName("[10] - [9,63,126] = 1 (base 5)")
    void calculateInterval_givenTwoIdentifiers_qDepth1pDepth3_Base5_getNumOfAvailableSpots_oneSpot() {
        List<Identifier> p = createIdentifiersList(9, 63, 126);
        List<Identifier> q = createIdentifiersList(10);

        assertEquals(0, docService.calculateInterval(p, q, 1, 5));
        assertEquals(0, docService.calculateInterval(p, q, 2, 5));
        assertEquals(1, docService.calculateInterval(p, q, 3, 5));
    }

    @Test
    @DisplayName("test alloc function: with p=[0], q=[9] ")
    void alloc_givenTwoPositions_generateNewPositionInBetween_Test1() {
        List<Identifier> p = createIdentifiersList(0);
        List<Identifier> q = createIdentifiersList(9);
        List<Identifier> newPos = docService.alloc(p, q, strategy, (int) CRDT.BASE);
        printPositionsInOrder(p, newPos, q);

        assertNotNull(newPos);
        assertTrue(comparePositions(p, newPos) < 0);
        assertTrue(comparePositions(newPos, q) < 0);
    }

    @Test
    @DisplayName("test alloc function: with p=[9,51], q=[9,60] ")
    void alloc_givenTwoPositions_generateNewPositionInBetween_Test2() {
        List<Identifier> p = createIdentifiersList(9,51);
        List<Identifier> q = createIdentifiersList(9,60);
        List<Identifier> newPos = docService.alloc(p, q, strategy, (int) CRDT.BASE);
        printPositionsInOrder(p, newPos, q);

        assertNotNull(newPos);
        assertTrue(comparePositions(p, newPos) < 0);
        assertTrue(comparePositions(newPos, q) < 0);
    }

    @Test
    @DisplayName("test alloc function: with p=[9,60], q=[10] ")
    void alloc_givenTwoPositions_generateNewPositionInBetween_Test3() {
        List<Identifier> p = createIdentifiersList(9,60);
        List<Identifier> q = createIdentifiersList(10);
        List<Identifier> newPos = docService.alloc(p, q, strategy, (int) CRDT.BASE);
        printPositionsInOrder(p, newPos, q);

        assertNotNull(newPos);
        assertTrue(comparePositions(p, newPos) < 0);
        assertTrue(comparePositions(newPos, q) < 0);
    }


    //Utils
    private List<Identifier> createIdentifiersList(int... numbers) {
        List<Identifier> res = new ArrayList<>(numbers.length);
        for (int n : numbers) {
            res.add(new Identifier(n));
        }

        return res;
    }

    private int comparePositions(List<Identifier> p1, List<Identifier> p2) {

        for (int i = 0; i < Math.min(p1.size(), p2.size()); i++) {
            int comp = Identifier.compare(p1.get(i), p2.get(i));
            if (comp != 0) {
                return comp;
            }

            if (p1.size() < p2.size()) {
                return -1;
            } else if (p1.size() > p2.size()) {
                return 1;
            }
        }

        return 0; //program should never reach this line of code.
    }

    //print identifiers in format: p < w < q
    private void printPositionsInOrder(List<Identifier> p, List<Identifier> w, List<Identifier> q) {
        System.out.println(p + " < " + w + " < " + q);
    }

}
