package com.latihan.food;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AdapterMenuDibeli extends RecyclerView.Adapter<AdapterMenuDibeli.HolderMenuDibeli> {

    Context context;
    List<ModelCartDetail> cartDetailList;
    List<ModelMenu> menuList;

    public AdapterMenuDibeli(Context context, List<ModelCartDetail> cartDetailList, List<ModelMenu> menuList) {
        this.context = context;
        this.cartDetailList = cartDetailList;
        this.menuList = menuList;
    }

    @NonNull
    @Override
    public AdapterMenuDibeli.HolderMenuDibeli onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.invoice, parent, false);
        return new HolderMenuDibeli(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterMenuDibeli.HolderMenuDibeli holder, int position) {
        holder.tvName.setText(menuList.get(position).getName());
        holder.tvPrice.setText(String.valueOf(menuList.get(position).getPrice()));
        holder.tvQty.setText(String.valueOf(cartDetailList.get(position).getQty()));
        holder.tvSubtotal.setText(String.valueOf(cartDetailList.get(position).getSubtotal()));
    }

    @Override
    public int getItemCount() {
        return cartDetailList != null ? cartDetailList.size() : 0;
    }

    public class HolderMenuDibeli extends RecyclerView.ViewHolder {

        TextView tvName, tvPrice, tvQty, tvSubtotal;

        public HolderMenuDibeli(@NonNull View itemView) {
            super(itemView);

            tvName =  itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQty = itemView.findViewById(R.id.tvQty);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotal);
        }
    }
}
