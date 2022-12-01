package enigma;

import static enigma.EnigmaException.error;

/** An alphabet of encodable characters.  Provides a mapping from characters
 *  to and from indices into the alphabet.
 *  @author Miranda Cheung
 */
class Alphabet {

    /** A new alphabet containing CHARS.  Character number #k has index
     *  K (numbering from 0). No character may be duplicated. */
    Alphabet(String chars) {
        this._chars = chars;
        for (int i = 0; i < chars.length(); i++) {
            if (chars.substring(i + 1).contains("" + toChar(i) + "")) {
                throw new EnigmaException("no repeats in alphabet");
            }
        }
        if (chars.contains("(") || chars.contains(")") || chars.contains("*")) {
            throw error("cannot contain *, ( or ) symbols");
        }
    }

    /** A default alphabet of all upper-case characters. */
    Alphabet() {
        this("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
    }

    /** Returns the size of the alphabet. */
    int size() {
        return _chars.length();
    }

    /** Returns true if CH is in this alphabet. */
    boolean contains(char ch) {
        boolean contained = false;
        for (int i = 0; i < size(); i++) {
            if (toChar(i) == ch) {
                contained = true;
            }
        }
        return contained;
    }

    /** Returns character number INDEX in the alphabet, where
     *  0 <= INDEX < size(). */
    char toChar(int index) {
        if (index >= _chars.length() || index < 0) {
            throw new EnigmaException("index out of bounds");
        }
        return _chars.charAt(index);
    }

    /** Returns the index of character CH which must be in
     *  the alphabet. This is the inverse of toChar(). */
    int toInt(char ch) {
        if (!contains(ch)) {
            throw new EnigmaException("letter does not exist in alphabet");
        }
        int pos = -1;
        for (int i = 0; i < size(); i++) {
            char x = toChar(i);
            if (x == ch) {
                pos = i;
                break;
            }
        }
        return pos;
    }

    /** Returns the alphabet of this machine. */
    String getAlphabetString() {
        return _chars;
    }

    /** Sets the alphabet of this machine.
     * @param chars is letters in the alphabet
     */
    void setAlphabetString(String chars) {
        _chars = chars;
    }

    /** Contains the alphabet of this machine. */
    private String _chars;
}
