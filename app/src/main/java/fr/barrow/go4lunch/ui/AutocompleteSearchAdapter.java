package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Date;

import fr.barrow.go4lunch.databinding.RestaurantListViewItemBinding;
import fr.barrow.go4lunch.databinding.RestaurantSearchItemBinding;
import fr.barrow.go4lunch.model.RestaurantAutocomplete;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class AutocompleteSearchAdapter extends RecyclerView.Adapter<AutocompleteSearchAdapter.AutocompleteSearchViewHolder> {

    private ArrayList<RestaurantAutocomplete> mRestaurantList;
    private Context mContext;
    public MyViewModel mMyViewModel;
    public ListViewFragment listViewFragment;
    public Date now;

    public AutocompleteSearchAdapter(ArrayList<RestaurantAutocomplete> restaurants, ListViewFragment listViewFragment) {
        mRestaurantList = restaurants;
        this.listViewFragment = listViewFragment;
        mMyViewModel = new ViewModelProvider(listViewFragment, MyViewModelFactory.getInstance(listViewFragment.getContext())).get(MyViewModel.class);
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
        holder.bind(restaurant);
        holder.setClickListener(restaurant.getPlaceId());
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    public static class AutocompleteSearchViewHolder extends RecyclerView.ViewHolder {

        private RestaurantSearchItemBinding binding;

        public AutocompleteSearchViewHolder(RestaurantSearchItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(RestaurantAutocomplete restaurant) {
            binding.textViewRestaurantName.setText(restaurant.getName());
            binding.textViewRestaurantDistance.setText(restaurant.getDistance() + "m");
        }

        void setClickListener(String restaurantId) {
            binding.constraintLayoutRestaurantListItem.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RestaurantDetailsActivity.class);
                intent.putExtra("RESTAURANT_ID", restaurantId);
                v.getContext().startActivity(intent);
            });
        }
    }
}
