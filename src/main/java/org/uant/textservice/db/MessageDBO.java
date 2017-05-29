package org.uant.textservice.db;

public class MessageDBO {
    private int hash;
    private String sender;
    private String body;
    private String response;
    private boolean validResource;
    private boolean validRequest;
    private boolean sent;
    private boolean processed;
    private long timestamp;

    public MessageDBO() {}

    public MessageDBO(int hash, String sender, String body, String response, boolean validResource, boolean validRequest, boolean sent, boolean processed, long timestamp) {
        this.hash = hash;
        this.sender = sender;
        this.body = body;
        this.response = response;
        this.validResource = validResource;
        this.validRequest = validRequest;
        this.sent = sent;
        this.processed = processed;
        this.timestamp = timestamp;
    }

    public int getHash() {
        return this.hash;
    }

    public void setHash(int hash) {
        this.hash = hash;
    }

    public String getSender() {
        return this.sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getBody() {
        return this.body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getResponse() {
        return this.response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public boolean getValidResource() {
        return this.validResource;
    }

    public void setValidResource(boolean validResource) {
        this.validResource =  validResource;
    }

    public boolean getValidRequest() {
        return this.validRequest;
    }

    public void setValidRequest(boolean validRequest) {
        this.validRequest =  validRequest;
    }

    public boolean getSent() {
        return this.sent;
    }

    public void setSent(boolean sent) {
        this.sent = sent;
    }

    public boolean getProcessed() {
        return this.processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
