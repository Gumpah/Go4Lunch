package fr.barrow.go4lunch.ui;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.WorkmatesViewItemBinding;
import fr.barrow.go4lunch.model.User;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private ArrayList<UserStateItem> mUsersList;
    private Context mContext;
    private MyViewModel mMyViewModel;
    private WorkmatesFragment workmatesFragment;
    private String apiKey;

    public WorkmatesAdapter(ArrayList<UserStateItem> usersList, WorkmatesFragment workmatesFragment) {
        this.workmatesFragment = workmatesFragment;
        mUsersList = usersList;
        mMyViewModel = new ViewModelProvider(workmatesFragment, MyViewModelFactory.getInstance(workmatesFragment.getContext())).get(MyViewModel.class);
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        mContext = parent.getContext();
        apiKey = mContext.getString(R.string.MAPS_API_KEY);
        return new WorkmatesAdapter.ViewHolder(WorkmatesViewItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int position) {
        UserStateItem user = mUsersList.get(position);
        String pickedRestaurantId = user.getPickedRestaurant();
        String restaurantName = null;
        if (pickedRestaurantId != null && mUsersList.get(position).getPickedRestaurantName() != null) restaurantName = mUsersList.get(position).getPickedRestaurantName();
        boolean isRestaurantInList = mMyViewModel.getRestaurantFromId(pickedRestaurantId) != null;
        holder.bind(mUsersList.get(position), restaurantName, pickedRestaurantId, isRestaurantInList, mMyViewModel, apiKey);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private WorkmatesViewItemBinding binding;

        public ViewHolder(WorkmatesViewItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(UserStateItem user, String restaurantName, String restaurantId, boolean isRestaurantInList, MyViewModel viewModel, String apiKey) {
            setTextViewAndClickListener(user.getUsername(), restaurantName, restaurantId, isRestaurantInList, viewModel, apiKey);
            Glide.with(binding.getRoot())
                    .load(user.getUrlPicture())
                    .error(R.drawable.backgroundblurred)
                    .centerCrop()
                    .circleCrop()
                    .into(binding.imageViewWorkmateIcon);
        }

        void setTextViewAndClickListener(String username, String restaurantName, String restaurantId, boolean isRestaurantInList, MyViewModel viewModel, String apiKey) {
            String textView = (username + binding.getRoot().getResources().getString(R.string.workmate_noPickedRestaurant));
            binding.textViewWorkmateChoice.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.greyText, null));
            if (restaurantName != null && restaurantId != null) {
                textView = (username + binding.getRoot().getResources().getString(R.string.workmate_hasRestaurantPick) + restaurantName);
                binding.textViewWorkmateChoice.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.black, null));
                binding.constraintLayoutContainer.setOnClickListener(v -> {
                    Intent intent = new Intent(v.getContext(), RestaurantDetailsActivity.class);
                    intent.putExtra("RESTAURANT_ID", restaurantId);
                    v.getContext().startActivity(intent);
                });
            }
            binding.textViewWorkmateChoice.setText(textView);
        }
    }
}
