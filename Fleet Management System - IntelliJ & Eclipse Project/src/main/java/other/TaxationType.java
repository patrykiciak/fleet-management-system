package other;

public enum TaxationType {
    STUDENT("STUDENT"), NOT_WORKING_ANYWHERE_ELSE("NOT_WORKING_ANYWHERE_ELSE"), WORKING_SOMEWHERE_ELSE("WORKING_SOMEWHERE_ELSE");

    private String taxationType;

    TaxationType(String type) {
        taxationType = type;
    }

    public String getTaxationType() {
        return taxationType;
    }

    public static TaxationType getFromLabel(String label) {
        switch (label) {
            case "STUDENT": return STUDENT;
            case "WORKING_SOMEWHERE_ELSE": return WORKING_SOMEWHERE_ELSE;
            case "NOT_WORKING_ANYWHERE_ELSE": default: return NOT_WORKING_ANYWHERE_ELSE;
        }
    }
}