package msku.ceng.madlab.biteful;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrdersAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);

        holder.tvDate.setText(order.getDate());

        holder.tvSummary.setText(order.getItemsSummary());
        holder.tvTotal.setText(String.format("Total: %.2f₺", order.getTotalAmount()));
        holder.tvStatus.setText(order.getStatus());

        String status = order.getStatus();
        if (status != null && status.equalsIgnoreCase("Delivered")) {
            holder.tvStatus.setTextColor(Color.parseColor("#4CAF50"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#E8F5E9"));
        } else {
            holder.tvStatus.setTextColor(Color.parseColor("#E69248"));
            holder.tvStatus.setBackgroundColor(Color.parseColor("#FFF3E0"));
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvStatus, tvSummary, tvTotal;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvSummary = itemView.findViewById(R.id.tvOrderSummary);
            tvTotal = itemView.findViewById(R.id.tvOrderTotal);
        }
    }
}