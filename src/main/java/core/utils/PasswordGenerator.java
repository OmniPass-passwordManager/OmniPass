package core.utils;

import java.util.ArrayList;
import java.util.Collections;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";

    private static final String NUMBERS = "0123456789";

    private static final String SYMBOLS = "!@#$%^&*()-_=+[]{};:,.?";

    private static final String ALL = UPPER + LOWER + NUMBERS + SYMBOLS;

    private static char randomChar(String characters) {
        return characters.charAt(random.nextInt(characters.length()));
    }

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {

        if (length < 4) {
            throw new IllegalArgumentException("Password length must be at least 4.");
        }

        StringBuilder password = new StringBuilder();

        password.append(randomChar(UPPER));
        password.append(randomChar(LOWER));
        password.append(randomChar(NUMBERS));
        password.append(randomChar(SYMBOLS));

        for (int i = 4; i < length; i++) {

            int index = random.nextInt(ALL.length());

            password.append(ALL.charAt(index));

        }

        ArrayList<Character> characters = new ArrayList<>();

        for (int i = 0; i < password.length(); i++) {
            characters.add(password.charAt(i));
        }

        Collections.shuffle(characters, random);

        StringBuilder shuffled = new StringBuilder();

        for (char c : characters) {
            shuffled.append(c);
        }

        return shuffled.toString();
    }
}
