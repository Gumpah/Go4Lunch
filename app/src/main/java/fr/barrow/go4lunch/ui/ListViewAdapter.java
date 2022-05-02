package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.RestaurantListViewItemBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

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
        return new ListViewViewHolder(RestaurantListViewItemBinding.inflate(inflater, parent, false), mContext);
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
        private MyViewModel mMyViewModel;

        public ListViewViewHolder(RestaurantListViewItemBinding b, Context context) {
            super(b.getRoot());
            binding = b;
            mMyViewModel = new ViewModelProvider((ViewModelStoreOwner) context, MyViewModelFactory.getInstance(context)).get(MyViewModel.class);
        }

        void bind(Restaurant restaurant) {
            binding.textViewRestaurantName.setText(restaurant.getName());
            binding.textViewRestaurantAddress.setText(restaurant.getAddress());
            Glide.with(binding.getRoot())
                    .load(restaurant.getUrlPicture())
                    .centerCrop()
                    .placeholder(R.drawable.backgroundblurred)
                    .into(binding.imageViewRestaurantPhoto);
            binding.textViewRestaurantDistance.setText(getDistance(restaurant.getPosition(), mMyViewModel.getLocation()));
            setupStarsRating(restaurant);
        }

        void setClickListener(String restaurantId) {
            binding.constraintLayoutRestaurantListItem.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), RestaurantDetailsActivity.class);
                intent.putExtra("RESTAURANT_ID", restaurantId);
                v.getContext().startActivity(intent);
            });
        }

        String getDistance(LatLng position1, Location position2) {
            Location startPoint= new Location("locationA");
            startPoint.setLatitude(position1.latitude);
            startPoint.setLongitude(position1.longitude);

            double distance = startPoint.distanceTo(position2);
            long distanceLong = Math.round(distance/10.0) * 10;
            return (String.valueOf(distanceLong) + 'm');
        }

        void setupStarsRating(Restaurant restaurant) {
            switch (restaurant.getRating()) {
                case 1:
                    binding.imageViewStar3.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    binding.imageViewStar3.setVisibility(View.VISIBLE);
                    binding.imageViewStar2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    binding.imageViewStar3.setVisibility(View.VISIBLE);
                    binding.imageViewStar2.setVisibility(View.VISIBLE);
                    binding.imageViewStar1.setVisibility(View.VISIBLE);
                    break;
                default:
                    binding.imageViewStar3.setVisibility(View.INVISIBLE);
                    binding.imageViewStar2.setVisibility(View.INVISIBLE);
                    binding.imageViewStar1.setVisibility(View.INVISIBLE);
                    break;
            }
        }
    }
}
