package fr.barrow.go4lunch.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewViewHolder> {


    @NonNull
    @Override
    public ListViewAdapter.ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ListViewViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ListViewViewHolder extends RecyclerView.ViewHolder {

        public ListViewViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
