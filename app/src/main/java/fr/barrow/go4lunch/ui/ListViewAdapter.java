package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.RestaurantListViewItemBinding;
import fr.barrow.go4lunch.model.Restaurant;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewViewHolder> {

    private ArrayList<Restaurant> mRestaurantList;
    private Context mContext;

    public ListViewAdapter (ArrayList<Restaurant> restaurants) {
        mRestaurantList = restaurants;
    }

    @NonNull
    @Override
    public ListViewAdapter.ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mContext = parent.getContext();
        return new ListViewViewHolder(RestaurantListViewItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ListViewViewHolder holder, int position) {
        holder.bind(mRestaurantList.get(position));
        holder.setClickListener(mRestaurantList.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public static class ListViewViewHolder extends RecyclerView.ViewHolder {

        private RestaurantListViewItemBinding binding;

        public ListViewViewHolder(RestaurantListViewItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(Restaurant restaurant) {
            binding.textViewRestaurantName.setText(restaurant.getName());
            binding.textViewRestaurantAddress.setText(restaurant.getAddress());
            Glide.with(binding.getRoot())
                    .load(restaurant.getUrlPicture())
                    .error(R.drawable.backgroundblurred)
                    .centerCrop()
                    .circleCrop()
                    .into(binding.imageViewRestaurantPhoto);
        }

        void setClickListener(String restaurantId) {
            binding.constraintLayoutRestaurantListItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), RestaurantDetailsActivity.class);
                    intent.putExtra("RESTAURANT_ID", restaurantId);
                    v.getContext().startActivity(intent);
                }
            });
        }
    }
}
