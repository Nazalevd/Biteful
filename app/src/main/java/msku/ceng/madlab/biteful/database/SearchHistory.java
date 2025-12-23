package msku.ceng.madlab.biteful.database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "search_history")
public class SearchHistory {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String query;
    public long timestamp;

    public SearchHistory(String query, long timestamp) {
        this.query = query;
        this.timestamp = timestamp;
    }
}