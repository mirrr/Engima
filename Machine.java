package enigma;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Miranda Cheung
 */
class Machine {

    /** A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     *  and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     *  available rotors. */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        if (_numRotors <= 1) {
            throw error("numRotors must be more than 1");
        } else if (_pawls < 0) {
            throw error("pawls cannot be a negative value");
        } else if (_pawls >= _numRotors) {
            throw error("pawls must be less than numRotors");
        } else if (_allRotors.isEmpty()) {
            throw error("allRotors cannot be empty");
        }
    }

    /** Return the number of rotor slots I have. */
    int numRotors() {
        return _numRotors;
    }

    /** Return the number pawls (and thus rotating rotors) I have. */
    int numPawls() {
        return _pawls;
    }

    /** Set my rotor slots to the rotors named ROTORS from my set of
     *  available rotors (ROTORS[0] names the reflector).
     *  Initially, all rotors are set at their 0 setting. */
    void insertRotors(String[] rotors) {
        _usedRotors = new Rotor[numRotors()];
        int i = 0; int movingR = 0;
        for (String name: rotors) {
            for (Rotor r: _allRotors) {
                if (r.name().equals(name)) {
                    _usedRotors[i] = r;
                    i++;
                    if (r.rotates()) {
                        movingR++;
                    }
                }
            }
        }
        if (!_usedRotors[0].reflecting()) {
            throw error("leftmost rotor must be reflector");
        }
        for (int k = numRotors() - _pawls; k < numRotors(); k++) {
            if (!_usedRotors[k].rotates()) {
                throw error("moving rotor at pos " + k + " must have pawl");
            }
        }
        if (movingR != _pawls) {
            throw error("no. of pawls != no. of moving rotors");
        }
    }

    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 characters in my alphabet. The first letter refers
     *  to the leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        while (setting.substring(0, 1).equals(" ")) {
            setting = setting.substring(1);
        }
        int slen = setting.replaceAll(" ", "").length(), rlen = numRotors() - 1;
        if (slen != rlen && slen != rlen * 2) {
            throw new EnigmaException("no. of settings not match numRotors()");
        }
        String s = setting.substring(0, rlen);
        for (int i = 0; i < s.length(); i++) {
            String test = s.substring(i, i + 1);
            boolean found = false;
            for (int k = 0; k < _alphabet.size(); k++) {
                if (test.equals("" + _alphabet.toChar(k))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw error("setting letter not in alphabet");
            }
            _usedRotors[i + 1].set(s.charAt(i));
        }
        setting = setting.substring(rlen).replaceAll(" ", "");
        slen = setting.length();
        if (slen == rlen) {
            setRingstellung(setting);
        } else if (slen != 0) {
            throw new EnigmaException("ringstellung not right num of rotors");
        }
    }

    /** Manages the ringstellung of this machine.
     * @param setting contains the ringstellung settings.
     */
    public void setRingstellung(String setting) {
        for (int i = 0; i < setting.length(); i++) {
            String test = setting.substring(i, i + 1);
            boolean found = false;
            for (int k = 0; k < _alphabet.size(); k++) {
                if (test.equals("" + _alphabet.toChar(k))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                throw error("setting letter not in alphabet");
            }
            char toBe0th = setting.charAt(i);
            Rotor r = _usedRotors[i + 1];
            r.setNew0(_alphabet.toInt(toBe0th));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */
    int convert(int c) {
        whichRotates();
        int res = _plugboard.permute(c);
        for (int i = numRotors() - 1; i >= 0; i--) {
            Rotor r = _usedRotors[i];
            res = r.convertForward(res);
        }
        for (int i = 1; i < numRotors(); i++) {
            Rotor r = _usedRotors[i];
            res = _usedRotors[i].convertBackward(res);
        }
        return _plugboard.permute(res);
    }

    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String res = "";
        for (int i = 0; i < msg.length(); i++) {
            int valInAlpha = _alphabet.toInt(msg.charAt(i));
            int numericVal = convert(valInAlpha);
            res += _alphabet.toChar(numericVal);
        }
        return res;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** Rotates rotors in need of rotating. */
    private void whichRotates() {
        ArrayList<Rotor> toAdvance = new ArrayList<>();
        for (int i = numRotors() - 1; i > 0; i--) {
            if (_usedRotors[i].atNotch()) {
                if (_usedRotors[i - 1].rotates()) {
                    toAdvance.add(_usedRotors[i - 1]);
                    if (!toAdvance.contains(_usedRotors[i])) {
                        toAdvance.add(_usedRotors[i]);
                    }
                }
            }
        }
        if (!toAdvance.contains(_usedRotors[numRotors() - 1])) {
            toAdvance.add(_usedRotors[numRotors() - 1]);
        }
        for (Rotor r: toAdvance) {
            r.advance();
        }
    }

    /** Number of rotors and pawls for this machine. */
    private int _numRotors, _pawls;

    /** Total rotors potentially used in this machine. */
    private Collection<Rotor> _allRotors = new HashSet<>();

    /** Rotors used in this machine. */
    private Rotor[] _usedRotors;

    /** Plugboard of this machine. */
    private Permutation _plugboard;
}
