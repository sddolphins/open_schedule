package helpers;

public class StatusMessage {
    public static final String SUCCESS = "success";
    public static final String WARNING = "warning";
    public static final String ERROR = "error";

    public String status;
    public String message;
    public String detail;

    public StatusMessage(String status, String message, String detail) {
        this.status = status;
        this.message = message;
        this.detail = detail;
    }
}