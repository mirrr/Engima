package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Miranda Cheung
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _cycles = cycles;
        for (int i = 0; i < cycles.length(); i++) {
            for (int j = 0; j < cycles.length(); j++) {
                String s = cycles.substring(i, i + 1);
                if (cycles.substring(j, j + 1).equals(s)) {
                    if (!s.equals("(") && !s.equals(")") && !s.equals(" ")) {
                        if (j != i) {
                            throw error("letters in _cycles be duplicates");
                        }
                    }

                }
            }
        }
    }

    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        for (int i = 0; i < cycle.length(); i++) {
            if (_cycles.contains(cycle.substring(i, i + 1))) {
                throw error("_cycles cannot contain duplicates of a letter");
            }
        }
        _cycles += " (" + cycle + ")";

    }

    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        char pp = _alphabet.toChar(p);
        char cc = permute(pp);
        return _alphabet.toInt(cc);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        char cc = _alphabet.toChar(c);
        char pp = invert(cc);
        return _alphabet.toInt(pp);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        if (!_alphabet.contains(p)) {
            throw new EnigmaException("letter does not exist in alphabet");
        }
        char it = p; int startpos = 0;
        for (int i = 0; i < _cycles.length(); i++) {
            if (_cycles.charAt(i) == '(') {
                startpos = i;
            }
            if (_cycles.charAt(i) == p) {
                if (i + 1 < _cycles.length()) {
                    it = _cycles.charAt(i + 1);
                    if (it != ')') {
                        return it;
                    } else {
                        it = _cycles.charAt(startpos + 1);
                    }
                }
            }
        }
        return it;
    }

    /** Return the result of applying the inverse of this permutation to C. */
    char invert(char c) {
        if (!_alphabet.contains(c)) {
            throw new EnigmaException("letter does not exist in alphabet");
        }
        char input = _alphabet.toChar(0);
        for (int i = 0; i < _alphabet.size(); i++) {
            input = _alphabet.toChar(i);
            if (c == permute(input)) {
                break;
            }
        }
        return input;
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        for (int i = 0; i < _alphabet.size(); i++) {
            char x = _alphabet.toChar(i);
            char y = permute(_alphabet.toChar(i));
            if (x == y) {
                return false;
            }
        }
        return true;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** Cycles of this permutation. */
    private String _cycles;
}
