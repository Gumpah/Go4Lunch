package fr.barrow.go4lunch.ui.listrestaurant;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.data.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.databinding.ListRestaurantSearchItemBinding;
import fr.barrow.go4lunch.ui.ClickCallback;

public class AutocompleteSearchAdapter extends RecyclerView.Adapter<AutocompleteSearchAdapter.AutocompleteSearchViewHolder> {

    private ArrayList<RestaurantAutocomplete> mRestaurantList;
    public ListRestaurantFragment mListRestaurantFragment;
    private Context mContext;
    public ClickCallback mCallback;

    public AutocompleteSearchAdapter(ArrayList<RestaurantAutocomplete> restaurants, ListRestaurantFragment listRestaurantFragment, ClickCallback callback) {
        mRestaurantList = restaurants;
        mListRestaurantFragment = listRestaurantFragment;
        mCallback = callback;
    }

    @NonNull
    @Override
    public AutocompleteSearchAdapter.AutocompleteSearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mContext = parent.getContext();
        return new AutocompleteSearchAdapter.AutocompleteSearchViewHolder(ListRestaurantSearchItemBinding.inflate(inflater, parent, false));
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

        private ListRestaurantSearchItemBinding binding;

        public AutocompleteSearchViewHolder(ListRestaurantSearchItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(RestaurantAutocomplete restaurant, Context context) {
            binding.textViewSearchListRestaurantName.setText(restaurant.getName());
            if (restaurant.getDistance() != null && !restaurant.getDistance().equals("")) {
                String distanceText = restaurant.getDistance() + context.getString(R.string.distance_meters_unit);
                binding.textViewSearchListRestaurantDistance.setText(distanceText);
            }
            if (restaurant.getAddress() != null) {
                binding.textViewSearchListRestaurantAddress.setText(restaurant.getAddress());
            }
        }

        void setClickListener(String restaurantId, ClickCallback callback) {
            binding.constraintLayoutRestaurantListItem.setOnClickListener(v -> {
                callback.myClickCallback(restaurantId);
            });
        }
    }
}
