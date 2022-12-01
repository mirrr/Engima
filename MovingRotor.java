package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Miranda Cheung
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initially in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _notches = notches;
        for (int i = 0; i < _notches.length(); i++) {
            if (!perm.alphabet().contains(_notches.charAt(i))) {
                throw error("notch not in alphabet");
            }
        }
    }

    @Override
    void advance() {
        set(permutation().wrap(setting() + 1));
    }

    @Override
    boolean atNotch() {
        String it = "" + alphabet().toChar(setting());
        return _notches.contains(it);
    }

    @Override
    boolean rotates() {
        return true;
    }

    /** Contains the notches of this rotor. */
    private String _notches;
}
