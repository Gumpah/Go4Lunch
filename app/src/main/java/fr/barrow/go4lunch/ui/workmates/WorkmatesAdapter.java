package fr.barrow.go4lunch.ui.workmates;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.WorkmatesViewItemBinding;
import fr.barrow.go4lunch.data.model.UserStateItem;
import fr.barrow.go4lunch.ui.ClickCallback;

public class WorkmatesAdapter extends RecyclerView.Adapter<WorkmatesAdapter.ViewHolder> {

    private ArrayList<UserStateItem> mUsersList;
    public ClickCallback mCallback;

    public WorkmatesAdapter(ArrayList<UserStateItem> usersList, ClickCallback callback) {
        mUsersList = usersList;
        mCallback = callback;
    }

    @NonNull
    @Override
    public WorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new WorkmatesAdapter.ViewHolder(WorkmatesViewItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WorkmatesAdapter.ViewHolder holder, int position) {
        UserStateItem user = mUsersList.get(position);
        String pickedRestaurantId = user.getPickedRestaurant();
        String restaurantName = null;
        if (pickedRestaurantId != null && mUsersList.get(position).getPickedRestaurantName() != null) restaurantName = mUsersList.get(position).getPickedRestaurantName();
        holder.bind(mUsersList.get(position), restaurantName, pickedRestaurantId, mCallback);
    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setData(List<UserStateItem> users) {
        mUsersList = (ArrayList<UserStateItem>) users;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private WorkmatesViewItemBinding binding;

        public ViewHolder(WorkmatesViewItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(UserStateItem user, String restaurantName, String restaurantId, ClickCallback callback) {
            setTextViewAndClickListener(user.getUsername(), restaurantName, restaurantId, callback);
            if (user.getUrlPicture() != null) {
                Glide.with(binding.getRoot())
                        .load(user.getUrlPicture())
                        .error(R.drawable.backgroundblurred)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.imageViewWorkmateIcon);
            } else {
                Glide.with(binding.getRoot())
                        .load(R.drawable.ic_person)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imageViewWorkmateIcon);
            }

        }

        void setTextViewAndClickListener(String username, String restaurantName, String restaurantId, ClickCallback callback) {
            String textView = (username + binding.getRoot().getResources().getString(R.string.workmate_noPickedRestaurant));
            binding.textViewWorkmateChoice.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.greyText, null));
            binding.textViewWorkmateChoice.setTypeface(binding.textViewWorkmateChoice.getTypeface(), Typeface.ITALIC);
            if (restaurantName != null && restaurantId != null) {
                textView = (username + binding.getRoot().getResources().getString(R.string.workmate_hasRestaurantPick) + restaurantName);
                binding.textViewWorkmateChoice.setTextColor(ResourcesCompat.getColor(binding.getRoot().getResources(), R.color.black, null));
                binding.textViewWorkmateChoice.setTypeface(null, Typeface.NORMAL);
                binding.constraintLayoutContainer.setOnClickListener(v -> {
                    callback.myClickCallback(restaurantId);
                });
            } else {
                binding.constraintLayoutContainer.setClickable(false);
                binding.constraintLayoutContainer.setOnClickListener(v -> {
                });
            }
            binding.textViewWorkmateChoice.setText(textView);
        }
    }
}
