package fr.barrow.go4lunch.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fr.barrow.go4lunch.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RestaurantDetailsWorkmatesItem#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RestaurantDetailsWorkmatesItem extends Fragment {

    public RestaurantDetailsWorkmatesItem() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RestaurantDetailsWorkmatesItem.
     */
    public static RestaurantDetailsWorkmatesItem newInstance(String param1, String param2) {
        RestaurantDetailsWorkmatesItem fragment = new RestaurantDetailsWorkmatesItem();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant_details_workmates_item, container, false);
    }
}