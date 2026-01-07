package msku.ceng.madlab.biteful;

public class SearchHistory {
    private String documentId;
    private String query;
    private long timestamp;

    public SearchHistory() { }

    public SearchHistory(String query, long timestamp) {
        this.query = query;
        this.timestamp = timestamp;
    }

    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}