package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Miranda Cheung
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }

    @Test
    public void testInvertChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('B', p.invert('A'));
        assertEquals('D', p.invert('B'));
        assertEquals('A', p.invert('C'));
        assertEquals('C', p.invert('D'));
        assertEquals(1, p.invert(0));
    }

    @Test
    public void testPermuteChar() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertEquals('A', p.permute('B'));
        assertEquals('B', p.permute('D'));
        assertEquals('C', p.permute('A'));
        assertEquals('D', p.permute('C'));
        assertEquals(0, p.permute(1));

        String al = "(HIG)(NF)(L)";
        Permutation p1 = new Permutation(al, new Alphabet("HILFNGR"));
        assertEquals('I', p1.permute('H'));
        assertEquals('G', p1.permute('I'));
        assertEquals('H', p1.permute('G'));
        assertEquals('L', p1.permute('L'));
        assertEquals('N', p1.permute('F'));
        assertEquals('F', p1.permute('N'));
        assertEquals('R', p1.permute('R'));

    }

    @Test
    public void testAlphabet() {
        Alphabet al = new Alphabet("ABCD");
        Permutation p = new Permutation("(BACD)", al);
        assertEquals(al, p.alphabet());
    }

    @Test
    public void testDerangement() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        assertTrue(p.derangement());
        Permutation p1 = new Permutation("(S)", new Alphabet("S"));
        assertFalse(p1.derangement());
    }

    @Test(expected = EnigmaException.class)
    public void testNotInAlphabet() {
        Permutation p = new Permutation("(BACD)", new Alphabet("ABCD"));
        p.invert('F');
    }
    /*@Test
    public void testCycles() {
        String cycle = "(ABCD) (EFGH)";
        Permutation p = new Permutation(cycle, new Alphabet());
        System.out.println(p._cycles);
        p.addCycle("LMNO");
        System.out.println(p._cycles);
    }*/
}
