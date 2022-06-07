package fr.barrow.go4lunch;


import static org.junit.Assert.assertEquals;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fr.barrow.go4lunch.data.Repository;
import fr.barrow.go4lunch.data.RestaurantRepository;
import fr.barrow.go4lunch.data.UserRepository;
import fr.barrow.go4lunch.ui.viewmodels.MyViewModel;

public class ViewModelUnitTesting {

    private MyViewModel mMyViewModel;

    @Mock
    private Context mContext;
    @Mock
    public Repository mRepository;
    @Mock
    public UserRepository mUserRepository;
    @Mock
    public RestaurantRepository mRestaurantRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        //mMyViewModel = new MyViewModel(mRepository, mRestaurantRepository, mUserRepository, mContext);
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }


}
