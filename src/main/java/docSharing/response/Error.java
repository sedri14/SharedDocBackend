package docSharing.response;

public enum Error {

    UNVALID_TOKEN("Unvalid token");

    private final String text;

    Error(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }


}
