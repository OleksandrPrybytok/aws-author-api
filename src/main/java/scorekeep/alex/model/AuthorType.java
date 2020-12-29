package scorekeep.alex.model;

/**
 * @author Alexander Pribytok
 * Date: 16.12.2020.
 */
public enum AuthorType {
    POET, NOVELIST, DETECTIVIST;

    /**
     * The method parses a string in order to convert it to the enum value.
     * Pasing is case insensitive.
     *
     * @param value - the value of the author type.
     * @return a particular AuthorType value.
     */
    public static AuthorType parseString(String value) {
        return valueOf(value.toUpperCase());
    }
}
