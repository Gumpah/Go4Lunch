package fr.barrow.go4lunch.ui;

import android.content.Intent;
import android.location.Location;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
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
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class ListViewAdapter extends RecyclerView.Adapter<ListViewAdapter.ListViewViewHolder> {

    private ArrayList<Restaurant> mRestaurantList;
    public MyViewModel mMyViewModel;
    public ListViewFragment listViewFragment;
    public Date now;

    public ListViewAdapter(ArrayList<Restaurant> restaurants, ListViewFragment listViewFragment) {
        mRestaurantList = restaurants;
        this.listViewFragment = listViewFragment;
        mMyViewModel = new ViewModelProvider(listViewFragment, MyViewModelFactory.getInstance(listViewFragment.getContext())).get(MyViewModel.class);
        Calendar n = Calendar.getInstance();
        n.set(Calendar.HOUR_OF_DAY, 20);
        n.set(Calendar.MINUTE, 0);
        now = n.getTime();
    }

    @NonNull
    @Override
    public ListViewAdapter.ListViewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ListViewViewHolder(RestaurantListViewItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ListViewAdapter.ListViewViewHolder holder, int position) {
        Restaurant restaurant = mRestaurantList.get(position);
        holder.bind(restaurant, mMyViewModel.getLocation());
        holder.setClickListener(restaurant.getId());
        mMyViewModel.getUsersWhoPickedARestaurant().observe(listViewFragment.requireActivity(), users -> {
            holder.initUserCount(users, restaurant, mMyViewModel.getCurrentUser().getUid());
        });
        holder.setupClosingTime(restaurant, now);
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

        void bind(Restaurant restaurant, Location location) {
            binding.textViewRestaurantName.setText(restaurant.getName());
            binding.textViewRestaurantAddress.setText(restaurant.getAddress());
            Glide.with(binding.getRoot())
                    .load(restaurant.getUrlPicture())
                    .centerCrop()
                    .placeholder(R.drawable.backgroundblurred)
                    .into(binding.imageViewRestaurantPhoto);
            binding.textViewRestaurantDistance.setText(getDistance(restaurant.getPosition(), location));
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
            if (position2 != null) {
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


        private void initUserCount(List<UserStateItem> users, Restaurant restaurant, String currentUserUid) {
            ArrayList<UserStateItem> list = new ArrayList<>();
            for (UserStateItem u : users) {
                if (u.getPickedRestaurant().equals(restaurant.getId()) && !u.getUid().equals(currentUserUid)) list.add(u);
            }
            if (list.size() > 0) {
                binding.imageViewWorkmatesIcon.setVisibility(View.VISIBLE);
                String workmateCount = ('(' + String.valueOf(list.size()) + ')');
                binding.textViewWorkmatesNumber.setText(workmateCount);
            } else {
                binding.imageViewWorkmatesIcon.setVisibility(View.INVISIBLE);
                binding.textViewWorkmatesNumber.setText("");
            }
        }

        void setupStarsRating(Restaurant restaurant) {
            binding.imageViewStar3.setVisibility(View.INVISIBLE);
            binding.imageViewStar2.setVisibility(View.INVISIBLE);
            binding.imageViewStar1.setVisibility(View.INVISIBLE);
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

        void setupClosingTime(Restaurant restaurant, Date now) {
            if (restaurant.getClosingTimeDate() == null) {
                binding.textViewRestaurantClosingTime.setText(R.string.restaurant_Closed);
                binding.textViewRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.redClosingTime, null));
            } else if (restaurant.getOpeningTimeDate() != null) {

                Date opening = restaurant.getOpeningTimeDate();

                Date closing = restaurant.getClosingTimeDate();

                long diffClosing  = closing.getTime() - now.getTime();
                long diffInMinutesClosing = TimeUnit.MILLISECONDS.toMinutes(diffClosing);

                long diffOpening  = now.getTime() - opening.getTime();
                long diffInMinutesOpening = TimeUnit.MILLISECONDS.toMinutes(diffOpening);
                System.out.println("TEMPS - " + restaurant.getName() + " : " + "now > " + "2000" + " / " + "closing > " + restaurant.getClosingTime() + " // " + diffInMinutesClosing + " // " + diffInMinutesOpening);
                if (diffInMinutesClosing <= 0 || diffInMinutesOpening < 0) {
                    binding.textViewRestaurantClosingTime.setText(R.string.restaurant_Closed);
                    binding.textViewRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.redClosingTime, null));
                } else if (diffInMinutesClosing <= 30) {
                    binding.textViewRestaurantClosingTime.setText(R.string.closing_Soon);
                    binding.textViewRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.redClosingTime, null));
                } else {
                    String closingTimeHour = restaurant.getClosingTime().substring(0,2);
                    String closingTimeMinute = restaurant.getClosingTime().substring(2,4);
                    String closingTimeFormat = (binding.getRoot().getResources().getString(R.string.open_Until)  + closingTimeHour + "h" + closingTimeMinute);
                    binding.textViewRestaurantClosingTime.setText(closingTimeFormat);
                    binding.textViewRestaurantClosingTime.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.grey, null));
                }
            }
        }
    }
}
