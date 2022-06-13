package fr.barrow.go4lunch.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.RestaurantListViewItemBinding;
import fr.barrow.go4lunch.model.Restaurant;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.ClickCallback;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewViewHolder> {

    private ArrayList<Restaurant> mRestaurantList;
    private ArrayList<UserStateItem> mUsersList;
    public ListViewFragment mListViewFragment;
    public Date now;
    public Context mContext;
    public ClickCallback mCallback;
    public Location mLocation;

    public ListViewAdapter(ArrayList<Restaurant> restaurants, ListViewFragment listViewFragment, ClickCallback callback, Location location) {
        mRestaurantList = restaurants;
        mListViewFragment = listViewFragment;
        Calendar n = Calendar.getInstance();
        n.set(Calendar.HOUR_OF_DAY, 20);
        n.set(Calendar.MINUTE, 0);
        n.set(Calendar.MINUTE, 0);
        now = n.getTime();
        mCallback = callback;
        mLocation = location;
        mUsersList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ListViewAdapter.ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ListViewViewHolder(RestaurantListViewItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ListViewViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.bind(restaurant, mLocation);
        holder.setClickListener(restaurant.getId(), mCallback);
        holder.initUserCount(mUsersList, restaurant);
        holder.setupClosingTime(restaurant, now);
    }

    @Override
    public int getItemCount() {
        return mRestaurantList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<Restaurant> list) {
        mRestaurantList = (ArrayList<Restaurant>) list;
        notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setDataUsers(List<UserStateItem> users) {
        mUsersList = (ArrayList<UserStateItem>) users;
        notifyDataSetChanged();
    }

    public static class ListViewViewHolder extends RecyclerView.ViewHolder {

        private RestaurantListViewItemBinding binding;

        public ListViewViewHolder(RestaurantListViewItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(Restaurant restaurant, Location location) {
            binding.textViewListRestaurantName.setText(restaurant.getName());
            binding.textViewListRestaurantAddress.setText(restaurant.getAddress());
            if (restaurant.getUrlPicture() != null) {
                Glide.with(binding.getRoot())
                        .load(restaurant.getUrlPicture())
                        .centerCrop()
                        .placeholder(R.drawable.backgroundblurred)
                        .into(binding.imageViewListRestaurantPhoto);
            } else {
                Glide.with(binding.getRoot())
                        .load(R.drawable.backgroundblurred)
                        .centerCrop()
                        .into(binding.imageViewListRestaurantPhoto);
            }
            binding.textViewListRestaurantDistance.setText(getDistance(restaurant.getPosition(), location));
            setupStarsRating(restaurant);
        }

        void setClickListener(String restaurantId, ClickCallback callback) {
            binding.constraintLayoutRestaurantListItem.setOnClickListener(v -> {
                callback.myClickCallback(restaurantId);
            });
        }

        String getDistance(LatLng position1, Location position2) {
            if (position1 != null && position2 != null) {
                Location startPoint= new Location("locationA");
                startPoint.setLatitude(position1.latitude);
                startPoint.setLongitude(position1.longitude);

                double distance = startPoint.distanceTo(position2);
                long distanceLong = Math.round(distance/10.0) * 10;
                return (String.valueOf(distanceLong) + 'm');
            } else {
                return ("Erreur");
            }
        }

        private void initUserCount(ArrayList<UserStateItem> users, Restaurant restaurant) {
            ArrayList<UserStateItem> list = new ArrayList<>();
            for (UserStateItem u : users) {
                if (u.getPickedRestaurant().equals(restaurant.getId())) list.add(u);
            }
            if (list.size() > 0) {
                binding.imageViewListWorkmatesIcon.setVisibility(View.VISIBLE);
                String workmateCount = ('(' + String.valueOf(list.size()) + ')');
                binding.textViewListWorkmatesNumber.setText(workmateCount);
            } else {
                binding.imageViewListWorkmatesIcon.setVisibility(View.INVISIBLE);
                binding.textViewListWorkmatesNumber.setText("");
            }
        }

        void setupStarsRating(Restaurant restaurant) {
            binding.imageViewListStar3.setVisibility(View.INVISIBLE);
            binding.imageViewListStar2.setVisibility(View.INVISIBLE);
            binding.imageViewListStar1.setVisibility(View.INVISIBLE);
            switch (restaurant.getRating()) {
                case 1:
                    binding.imageViewListStar3.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    binding.imageViewListStar3.setVisibility(View.VISIBLE);
                    binding.imageViewListStar2.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    binding.imageViewListStar3.setVisibility(View.VISIBLE);
                    binding.imageViewListStar2.setVisibility(View.VISIBLE);
                    binding.imageViewListStar1.setVisibility(View.VISIBLE);
                    break;
                default:
                    binding.imageViewListStar3.setVisibility(View.INVISIBLE);
                    binding.imageViewListStar2.setVisibility(View.INVISIBLE);
                    binding.imageViewListStar1.setVisibility(View.INVISIBLE);
                    break;
            }
        }

        void setupClosingTime(Restaurant restaurant, Date now) {
            if (restaurant.getClosingTimeDate() == null) {
                binding.textViewListRestaurantClosingTime.setText(R.string.restaurant_Closed);
                binding.textViewListRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.redClosingTime, null));
            } else if (restaurant.getOpeningTimeDate() != null) {

                Date opening = restaurant.getOpeningTimeDate();

                Date closing = restaurant.getClosingTimeDate();

                long diffClosing  = closing.getTime() - now.getTime();
                long diffInMinutesClosing = TimeUnit.MILLISECONDS.toMinutes(diffClosing);

                long diffOpening  = now.getTime() - opening.getTime();
                long diffInMinutesOpening = TimeUnit.MILLISECONDS.toMinutes(diffOpening);
                if (diffInMinutesClosing <= 0 || diffInMinutesOpening < 0) {
                    binding.textViewListRestaurantClosingTime.setText(R.string.restaurant_Closed);
                    binding.textViewListRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.redClosingTime, null));
                } else if (diffInMinutesClosing <= 30) {
                    binding.textViewListRestaurantClosingTime.setText(R.string.closing_Soon);
                    binding.textViewListRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.redClosingTime, null));
                } else {
                    String closingTimeHour = restaurant.getClosingTime().substring(0,2);
                    String closingTimeMinute = restaurant.getClosingTime().substring(2,4);
                    String closingTimeFormat = (binding.getRoot().getResources().getString(R.string.open_Until)  + closingTimeHour + "h" + closingTimeMinute);
                    binding.textViewListRestaurantClosingTime.setText(closingTimeFormat);
                    binding.textViewListRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.grey, null));
                }
            }
        }
    }
}
