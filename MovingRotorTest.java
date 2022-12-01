package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import java.util.HashMap;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author Miranda Cheung
 */
public class MovingRotorTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Rotor rotor;
    private String alpha = UPPER_STRING;

    /** Check that rotor has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkRotor(String testId,
                            String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, rotor.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d (%c)", ci, c),
                         ei, rotor.convertForward(ci));
            assertEquals(msg(testId, "wrong inverse of %d (%c)", ei, e),
                         ci, rotor.convertBackward(ei));
        }
    }

    /** Set the rotor to the one with given NAME and permutation as
     *  specified by the NAME entry in ROTORS, with given NOTCHES. */
    private void setRotor(String name, HashMap<String, String> rotors,
                          String notches) {
        rotor = new MovingRotor(name, new Permutation(rotors.get(name), UPPER),
                                notches);
    }

    /* ***** TESTS ***** */

    @Test
    public void checkRotorAtA() {
        setRotor("I", NAVALA, "");
        checkRotor("Rotor I (A)", UPPER_STRING, NAVALA_MAP.get("I"));
    }

    @Test
    public void checkRotorAdvance() {
        setRotor("I", NAVALA, "");
        rotor.advance();
        checkRotor("Rotor I advanced", UPPER_STRING, NAVALB_MAP.get("I"));
    }

    @Test
    public void checkRotorSet() {
        setRotor("I", NAVALA, "");
        rotor.set(25);
        checkRotor("Rotor I set", UPPER_STRING, NAVALZ_MAP.get("I"));
    }

    @Test
    public void testRotorSetting() {
        Permutation p = new Permutation("(ABCD)", new Alphabet("ABCD"));
        Rotor r = new Rotor("I", p);
        MovingRotor m = new MovingRotor("II", p, "A");
        FixedRotor f = new FixedRotor("III", p);
        assertEquals(0, r.setting());
        assertEquals(0, m.setting());
        assertEquals(0, f.setting());
        r.set(1); m.set(1); f.set(1);
        assertEquals(1, r.setting());
        assertEquals(1, m.setting());
        assertEquals(1, f.setting());
        r.set('B'); m.set('C'); f.set('D');
        assertEquals(1, r.setting());
        assertEquals(2, m.setting());
        assertEquals(3, f.setting());
    }

    @Test(expected = EnigmaException.class)
    public void testReflectorSetting() {
        Permutation p = new Permutation("(ABCD)", new Alphabet("ABCD"));
        Reflector rf = new Reflector("IV", p);
        System.out.println(rf.setting());
        rf.set(3);
    }

    @Test
    public void testAtNotch() {
        Permutation p = new Permutation("(ABCD)", new Alphabet("ABCD"));
        Rotor r = new Rotor("I", p);
        MovingRotor m = new MovingRotor("II", p, "A");
        FixedRotor f = new FixedRotor("III", p);
        Reflector rf = new Reflector("IV", p);
        m.set(2);
        assertFalse(m.atNotch());
        assertFalse(r.atNotch());
        assertFalse(f.atNotch());
        assertFalse(rf.atNotch());
        m.set(0);
        assertTrue(m.atNotch());
        MovingRotor mv = new MovingRotor("V", p, "DA");
        mv.set(3);
        assertTrue(mv.atNotch());

    }

}
