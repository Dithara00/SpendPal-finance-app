package com.example.spendpal;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class IncomeAdapter extends RecyclerView.Adapter<IncomeAdapter.IncomeViewHolder> {

    private List<IncomeModel> incomeList;
    private OnIncomeItemClickListener listener;

    public interface OnIncomeItemClickListener {
        void onEditClick(IncomeModel income, int position);
        void onDeleteClick(IncomeModel income, int position);
    }

    public IncomeAdapter(List<IncomeModel> incomeList, OnIncomeItemClickListener listener) {
        this.incomeList = incomeList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_income, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        IncomeModel income = incomeList.get(position);
        holder.tvTitle.setText(income.getTitle());
        holder.tvAmount.setText("Rs. " + String.format("%.2f", income.getAmount()));

        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(income, position));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(income, position));

    }

    @Override
    public int getItemCount() {
        return incomeList.size();
    }

    public static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvAmount;
        View btnEdit, btnDelete;

        public IncomeViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvIncomeTitle);
            tvAmount = itemView.findViewById(R.id.tvIncomeAmount);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}

