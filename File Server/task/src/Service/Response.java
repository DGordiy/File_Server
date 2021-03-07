package Service;

import java.io.Serializable;

public class Response implements Serializable {

    private final int status;
    private final byte[] data;
    private String message;

    public int getStatus() {
        return status;
    }

    public byte[] getData() {
        return data;
    }

    public Response(int status, byte[] data) {
        this.status = status;
        this.data = data;
    }

    public Response(int status) {
        this(status, null);
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
