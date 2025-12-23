package msku.ceng.madlab.biteful;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import msku.ceng.madlab.biteful.database.SearchHistory;
import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder> {

    private List<SearchHistory> historyList;
    private OnHistoryClickListener listener;

    public interface OnHistoryClickListener {
        void onItemClick(String query);
        void onDeleteClick(SearchHistory history);
    }

    public SearchHistoryAdapter(List<SearchHistory> historyList, OnHistoryClickListener listener) {
        this.historyList = historyList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_search_history, parent, false);
        return new HistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SearchHistory item = historyList.get(position);
        holder.tvText.setText(item.query);

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item.query));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(item));
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    static class HistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvText;
        ImageView btnDelete;

        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvHistoryText);
            btnDelete = itemView.findViewById(R.id.btnDeleteHistory);
        }
    }
}