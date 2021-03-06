package fr.barrow.go4lunch.ui.restaurantdetails;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.RestaurantDetailsWorkmatesItemBinding;
import fr.barrow.go4lunch.data.model.UserStateItem;

public class RestaurantDetailsWorkmatesAdapter extends RecyclerView.Adapter<RestaurantDetailsWorkmatesAdapter.ViewHolder> {

    private ArrayList<UserStateItem> mUsersList;

    public RestaurantDetailsWorkmatesAdapter (ArrayList<UserStateItem> users) {
        mUsersList = users;
    }

    @NonNull
    @Override
    public RestaurantDetailsWorkmatesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(RestaurantDetailsWorkmatesItemBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantDetailsWorkmatesAdapter.ViewHolder holder, int position) {
        holder.bind(mUsersList.get(position));
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

        private RestaurantDetailsWorkmatesItemBinding binding;

        public ViewHolder(RestaurantDetailsWorkmatesItemBinding b) {
            super(b.getRoot());
            binding = b;
        }

        void bind(UserStateItem user) {
            String textViewUser = (user.getUsername() + binding.getRoot().getResources().getString(R.string.workmate_is_joining));
            binding.textViewUserNameList.setText(textViewUser);
            if (user.getUrlPicture() != null) {
                Glide.with(binding.getRoot())
                        .load(user.getUrlPicture())
                        .error(R.drawable.backgroundblurred)
                        .centerCrop()
                        .circleCrop()
                        .into(binding.imageViewUserAvatarList);
            } else {
                Glide.with(binding.getRoot())
                        .load(R.drawable.ic_person)
                        .apply(RequestOptions.circleCropTransform())
                        .into(binding.imageViewUserAvatarList);
            }
        }
    }
}
