package msku.ceng.madlab.biteful.database;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Database(entities = {CartItem.class, Order.class, Favorite.class, SearchHistory.class}, version = 4)

public abstract class BitefulDatabase extends RoomDatabase {
    public abstract BitefulDao bitefulDao();

    private static volatile BitefulDatabase INSTANCE;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(4);

    public static BitefulDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BitefulDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    BitefulDatabase.class, "biteful_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}