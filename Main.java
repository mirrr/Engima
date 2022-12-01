package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.Scanner;
import java.util.NoSuchElementException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;


import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Miranda Cheung
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {
        _M = readConfig();
        String setting = "";
        if (_input.hasNext("[*]")) {
            setting = _input.nextLine();
        } else {
            throw error("settings line not found");
        }
        processPt2(setting);
        while (_input.hasNextLine()) {
            String s = _input.nextLine();
            if (s.contains("*")) {
                String temp = s.replaceAll(" ", "");
                if (temp.substring(0, 1).equals("*")) {
                    while (s.substring(0, 1).equals(" ")) {
                        s = s.substring(1);
                    }
                    processPt2(s);
                } else {
                    throw error("message cannot contain *");
                }
            } else {
                printMessageLine(s);
            }
        }
    }

    /** Sets the settings, an extension of process().
     * @param setting contains the settings line
     */
    public void processPt2(String setting) {
        while (setting.substring(0, 1).equals(" ")) {
            setting = setting.substring(1);
        }
        if (setting.length() < 1 || !setting.substring(0, 1).equals("*")) {
            throw error("first character must be *");
        }
        if (_M == null) {
            throw error("Machine M must be initialized");
        }
        setUp(_M, setting);
        while (_input.hasNext("[^*].*")) {
            String msg = _input.nextLine().replaceAll(" ", "");
            printMessageLine(msg);
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {
            String chars = "", next = "";
            if (_config.hasNext()) {
                chars = _config.next();
            }
            _alphabet = new Alphabet(chars);
            int numRotors = 0, pawls = 0;
            if (!_config.hasNextInt()) {
                throw error("need int for numRotors");
            }
            numRotors = _config.nextInt();
            if (!_config.hasNextInt()) {
                throw error("need int for numPawls");
            }
            pawls = _config.nextInt();
            while (_config.hasNext()) {
                _allRotors.add(readRotor());
            }
            return new Machine(_alphabet, numRotors, pawls, _allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {
            Rotor res = null;
            String name = "", rotorType = "", notches = "", cycles = "";
            if (_config.hasNext()) {
                name = _config.next();
            }
            if (_config.hasNext()) {
                rotorType = _config.next();
                if (rotorType.length() > 1) {
                    notches = rotorType.substring(1);
                    rotorType = rotorType.substring(0, 1);
                }
            }
            while (_config.hasNext("(\\([^()*]+\\))+")) {
                cycles += _config.next();
            }
            if (rotorType.equals("N")) {
                res = new FixedRotor(name, new Permutation(cycles, _alphabet));
            }
            if (rotorType.equals("M")) {
                Permutation p = new Permutation(cycles, _alphabet);
                res = new MovingRotor(name, p, notches);
            }
            if (rotorType.equals("R")) {
                res = new Reflector(name, new Permutation(cycles, _alphabet));
            }
            return res;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        settings = settings.substring(1);
        ArrayList<String> rotors = new ArrayList<String>();
        String word = ""; int posCycles = 0;
        while (posCycles < settings.length()) {
            String letter = settings.substring(posCycles, posCycles + 1);
            if (letter.equals(" ")) {
                if (!word.isBlank()) {
                    rotors.add(word);
                    word = "";
                }
            } else if (letter.equals("(")) {
                break;
            } else {
                word += letter;
            }
            posCycles++;
        }
        rotors.add(word); int posSet = -1;
        for (posSet = 0; posSet < rotors.size(); posSet++) {
            boolean found = false;
            ArrayList<Rotor> alRtrs = new ArrayList<>(_allRotors);
            for (Rotor thing: alRtrs) {
                String x = rotors.get(posSet);
                String tN = thing.name();
                if (tN.equals(x)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                break;
            }
        }
        String[] usedRotors = new String[posSet];
        for (int i = 0; i < posSet; i++) {
            String temp = rotors.get(i);
            for (int k = 0; k < i; k++) {
                if (temp.equals(usedRotors[k])) {
                    throw error("cannot repeat rotors used");
                }
            }
            usedRotors[i] = temp;
        }
        if (usedRotors.length != _M.numRotors()) {
            throw error("must have numRotors() number of rotors");
        }
        M.insertRotors(usedRotors); word = rotors.get(posSet);
        for (int i = posSet + 1; i < rotors.size(); i++) {
            word += " " + rotors.get(i);
        }
        int wlen = word.replaceAll(" ", "").length();
        int rlen = usedRotors.length - 1;
        if (wlen == rlen || wlen == rlen * 2) {
            M.setRotors(word);
        } else {
            throw error("rotor.set() must have either N-1 or N*2-1 settings");
        }
        findPlugboardCycles(M, settings, posCycles);
    }

    /** Finds and sets plugboard.
     * @param M is the machine
     * @param setting is the cycles remaining
     * @param posCycles is the position of the cycles
     */
    public void findPlugboardCycles(Machine M, String setting, int posCycles) {
        String s = setting.substring(posCycles);
        boolean started = false;
        boolean hasWords = false;
        for (int i = 0; i < s.length(); i++) {
            String curr = s.substring(i, i + 1);
            if (curr.equals("(")) {
                if (!started) {
                    started = true;
                } else {
                    throw error("previous cycle not yet closed");
                }
            } else if (curr.equals(")")) {
                if (started) {
                    if (!hasWords) {
                        throw error("no letters found in cycle");
                    }
                    started = false;
                    hasWords = false;
                } else {
                    throw error("new cycle hasnt started");
                }
            } else if (_alphabet.contains(s.charAt(i))) {
                if (!started) {
                    throw error("cannot have letter outside of cycle");
                } else {
                    hasWords = true;
                }
            } else if (curr.equals(" ") && hasWords) {
                throw error("cannot have space inside a cycle");
            } else if (!curr.equals(" ")) {
                throw error("character unknown, does not belong in cycles");
            }
        }
        M.setPlugboard(new Permutation(s, _alphabet));

    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg.length() == 0) {
            _output.print("\n");
            return;
        }
        int grpD = (msg.length() / 5) * 5;
        String temp = _M.convert(msg), res = ""; int count = 0;
        for (int i = 0; i < grpD; i += 5) {
            res += temp.substring(i, i + 5) + " ";
        }
        if (grpD != msg.length()) {
            res += temp.substring(grpD);
        } else {
            res = res.substring(0, res.length() - 1);
        }
        _output.print(res + "\n");
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;

    /** All rotors given in config. */
    private Collection<Rotor> _allRotors = new HashSet<>();

    /** Enigma Machine configured via file. */
    private Machine _M;
}
