package Service;

import java.io.Serializable;

public class Request implements Serializable {

    private final RequestMethod method;

    public String getFileName() {
        return fileName;
    }

    public int getFileId() {
        return fileId;
    }

    private final String fileName;

    private final int fileId;

    private byte[] data;

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public Request(RequestMethod method) {
        this(method, null, 0);
    }

    public Request(RequestMethod method, String fileName, int fileId) {
        this.method = method;
        this.fileName = fileName;
        this.fileId = fileId;
    }

    public RequestMethod getMethod() {
        return method;
    }
}
