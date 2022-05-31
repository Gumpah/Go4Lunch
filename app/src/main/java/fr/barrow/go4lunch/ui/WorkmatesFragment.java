package fr.barrow.go4lunch.ui;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import fr.barrow.go4lunch.R;
import fr.barrow.go4lunch.databinding.FragmentWorkmatesBinding;
import fr.barrow.go4lunch.model.UserStateItem;
import fr.barrow.go4lunch.utils.MyViewModelFactory;

public class WorkmatesFragment extends Fragment implements SearchView.OnQueryTextListener {

    private FragmentWorkmatesBinding binding;
    private MyViewModel mMyViewModel;
    private RecyclerView mRecyclerView;
    private ArrayList<UserStateItem> mUsers = new ArrayList<>();
    private ArrayList<UserStateItem> mUsersCopy = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentWorkmatesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        configureViewModel();
        initRecyclerView(view);
        initUsersData();
        setHasOptionsMenu(true);
        return view;
    }

    private void initRecyclerView(View view) {
        mRecyclerView = binding.recyclerview;
        LinearLayoutManager layoutManager = new LinearLayoutManager(view.getContext());
        mRecyclerView.setLayoutManager(layoutManager);
        WorkmatesAdapter mAdapter = new WorkmatesAdapter(mUsers);
        mRecyclerView.setAdapter(mAdapter);
    }

    public void initUsersData() {
        mMyViewModel.getAllUsers().observe(requireActivity(), list -> {
            if (list != null && !list.isEmpty() && mRecyclerView.getAdapter() != null) {
                mUsers.clear();
                mUsers.addAll(list);
                mUsersCopy.clear();
                mUsersCopy.addAll(list);
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        });
    }

    public void filter(String text) {
        if (mUsers != null && mRecyclerView.getAdapter() != null) {
            mUsers.clear();
            if(text.isEmpty()){
                mUsers.addAll(mUsersCopy);
            } else {
                text = text.toLowerCase();
                for(UserStateItem item: mUsersCopy){
                    if(item.getUsername().toLowerCase().contains(text)){
                        mUsers.add(item);
                    }
                }
            }
            mRecyclerView.getAdapter().notifyDataSetChanged();
        }
    }

    public void configureViewModel() {
        mMyViewModel = new ViewModelProvider(this, MyViewModelFactory.getInstance(requireActivity())).get(MyViewModel.class);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        SearchManager searchManager =
                (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setOnQueryTextListener(this);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        filter(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        filter(newText);
        return true;
    }
}