package msku.ceng.madlab.biteful.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface BitefulDao {
    @Insert
    void insertCartItem(CartItem item);

    @Query("SELECT * FROM cart_table")
    List<CartItem> getAllCartItems();

    @Query("DELETE FROM cart_table")
    void clearCart();
    @Insert
    void insertOrder(Order order);

    @Query("SELECT * FROM order_table ORDER BY orderId DESC")
    List<Order> getAllOrders();

    @Insert
    void insertFavorite(Favorite favorite);

    @Query("SELECT * FROM favorite_table")
    List<Favorite> getAllFavorites();

    @Query("DELETE FROM favorite_table WHERE restaurantName = :name")
    void removeFavorite(String name);

    @Query("SELECT EXISTS(SELECT * FROM favorite_table WHERE restaurantName = :name)")
    boolean isFavorite(String name);


    @Query("DELETE FROM favorite_table WHERE restaurantName = :name")
    void deleteFavoriteByName(String name);

    @Query("DELETE FROM cart_table WHERE id = :id")
    void deleteCartItemById(int id);

    @Insert
    void insertSearchHistory(SearchHistory history);

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    List<SearchHistory> getAllSearchHistory();

    @Query("DELETE FROM search_history WHERE id = :id")
    void deleteSearchHistory(int id);

    @Query("DELETE FROM search_history WHERE query = :query")
    void deleteSearchHistoryByQuery(String query);
}