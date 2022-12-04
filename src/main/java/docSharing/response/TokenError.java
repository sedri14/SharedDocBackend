package docSharing.response;

public enum TokenError {

    INVALID_TOKEN("Invalid token");

    private final String text;

    TokenError(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }


}
