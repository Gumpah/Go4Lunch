package fr.barrow.go4lunch;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import fr.barrow.go4lunch.data.UserRepository;

public class UserRepositoryUnitTesting {

    private UserRepository mUserRepository;

    @Before
    public void setUp() {
       //mUserRepository = new UserRepository();
    }

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }
}
