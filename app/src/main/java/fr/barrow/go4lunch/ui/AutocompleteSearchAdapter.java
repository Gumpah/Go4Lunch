package fr.barrow.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.RestaurantSearchItemBinding;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.utils.ClickCallback;

public class AutocompleteSearchAdapter extends RecyclerView.Adapter<AutocompleteSearchAdapter.AutocompleteSearchViewHolder> {

    private ArrayList<RestaurantAutocomplete> mRestaurantList;
    public ListViewFragment mListViewFragment;
    private Context mContext;
    public ClickCallback mCallback;

    public AutocompleteSearchAdapter(ArrayList<RestaurantAutocomplete> restaurants, ListViewFragment listViewFragment, ClickCallback callback) {
        mRestaurantList = restaurants;
        mListViewFragment = listViewFragment;
        mCallback = callback;
    }

    @NonNull
    @Override
    public AutocompleteSearchAdapter.AutocompleteSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mContext = parent.getContext();
        return new AutocompleteSearchAdapter.AutocompleteSearchViewHolder(RestaurantSearchItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AutocompleteSearchAdapter.AutocompleteSearchViewHolder holder, int position) {
        RestaurantAutocomplete restaurant = mRestaurantList.get(position);
        holder.bind(restaurant, mContext);
        holder.setClickListener(restaurant.getPlaceId(), mCallback);
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<RestaurantAutocomplete> list) {
        mRestaurantList = (ArrayList<RestaurantAutocomplete>) list;
        notifyDataSetChanged();
    }

    public static class AutocompleteSearchViewHolder extends RecyclerView.ViewHolder {

        private RestaurantSearchItemBinding binding;

        public AutocompleteSearchViewHolder(RestaurantSearchItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(RestaurantAutocomplete restaurant, Context context) {
            binding.textViewSearchListRestaurantName.setText(restaurant.getName());
            if (restaurant.getDistance() != null && !restaurant.getDistance().equals("")) {
                String distanceText = restaurant.getDistance() + context.getString(R.string.distance_meters_unit);
                binding.textViewSearchListRestaurantDistance.setText(distanceText);
            }
        }

        void setClickListener(String restaurantId, ClickCallback callback) {
            binding.constraintLayoutRestaurantListItem.setOnClickListener(v -> {
                callback.myClickCallback(restaurantId);
            });
        }
    }
}
