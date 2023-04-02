package docSharing.service;

import docSharing.CRDT.Identifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;

import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class DocServiceTest {

    @InjectMocks
    private DocService docService;


    @BeforeEach
    void setUp() {

    }

    @Test
    void addVal_givenIdentifier_getNewIdentifier() {
        List<Identifier> p = createIdentifiersList(9,60);
        List<Identifier> expected = createIdentifiersList(9,62);
        int valToAdd = 2;

        assertIterableEquals(expected,docService.addVal(p,valToAdd));
    }

    @Test
    void subVal_givenIdentifier_getNewIdentifier() {
        List<Identifier> q = createIdentifiersList(9,65);
        List<Identifier> expected = createIdentifiersList(9,60);
        int valToSub = 5;

        assertIterableEquals(expected,docService.subVal(q,valToSub));
    }

    @Test
    void calculateInterval_givenTwoIdentifiers_getNumOfAvailableSpots(){

    }

    //Utils
    private List<Identifier> createIdentifiersList(int... numbers) {
        List<Identifier> res = new ArrayList<>(numbers.length);
        for (int n : numbers) {
            res.add(new Identifier(n));
        }

        return res;
    }

}
