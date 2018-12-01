package wof.component;

import org.apache.commons.lang3.RandomStringUtils;

public class IdGenerator {
    public static String generate() {
        return RandomStringUtils.randomAlphanumeric(12);
    }

    public static String reset() {
        return RandomStringUtils.randomAlphanumeric(32);
    }
}
