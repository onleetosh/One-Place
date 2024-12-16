package org.yearup.data.mysql;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yearup.models.*;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class MySqlProfileDaoTest extends BaseDaoTestClass{
    private MySqlProfileDao dao;
    private MySqlUserDao userDao;

    @BeforeEach
    public void setup()
    {
        dao = new MySqlProfileDao(dataSource);
        userDao = new MySqlUserDao(dataSource);
    }

    @Test
    public void testCreateProfile(){
        // arrange
        User user = new User();
        Profile profile = new Profile();

        user.setUsername("Tosh");
        user.setPassword("password");
        user.setAuthorities("ROLE_USER");

        User getNewUser = userDao.create(user); // Insert user and get generated user_id

        profile.setUserId(getNewUser.getId());
        profile.setFirstName("Dan");
        profile.setLastName("Cook");
        profile.setPhone("914-929-9999");
        profile.setEmail("goback@sells.org");
        profile.setAddress("123 Lala Land");
        profile.setCity("New World");
        profile.setState("NY");
        profile.setZip("12232A");

        // act
        Profile getNewProfile = dao.create(profile);

        // assert
        assertNotNull(getNewProfile, "The created profile should not be null");
        assertEquals(profile.getUserId(), getNewProfile.getUserId(), "User ID should match");
        assertEquals(profile.getFirstName(), getNewProfile.getFirstName(), "First name should match");
        assertEquals(profile.getLastName(), getNewProfile.getLastName(), "Last name should match");
        assertEquals(profile.getPhone(), getNewProfile.getPhone(), "Phone should match");
        assertEquals(profile.getEmail(), getNewProfile.getEmail(), "Email should match");
    }

    @Test
    public void testGetProfileById(){
        // Arrange
        int setUserId = 1;
        // Act
        Profile profile = dao.getProfileById(setUserId);

        // Assert
        assertNotNull(profile, "The profile should not be null");
        assertEquals(setUserId, profile.getUserId(), "User ID should match");
        assertEquals("Joe", profile.getFirstName(), "First name should match the database record");
        assertEquals("Joesephus", profile.getLastName(), "Last name should match the database record");
        assertEquals("800-555-1234", profile.getPhone(), "Phone should match the database record");
        assertEquals("joejoesephus@email.com", profile.getEmail(), "Email should match the database record");
    }

    @Test
    public void testUpdateProfile(){
        // Arrange
        Profile setProfile = dao.getProfileById(1);
        assertNotNull(setProfile, "Profile to be updated should exist.");

        setProfile.setFirstName("Joseph");
        setProfile.setCity("Houston");

        // Act
        dao.updateProfile(setProfile);
        Profile getUpdatedProfile = dao.getProfileById(1);

        // Assert
        assertNotNull(getUpdatedProfile, "The updated profile should not be null.");
        assertEquals("Joseph", getUpdatedProfile.getFirstName(), "First name should be updated.");
        assertEquals("Houston", getUpdatedProfile.getCity(), "City should be updated.");
    }

}
