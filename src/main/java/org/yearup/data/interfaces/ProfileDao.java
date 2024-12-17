package org.yearup.data.interfaces;


import org.yearup.models.Profile;

public interface ProfileDao
{
    Profile create(Profile profile);

    Profile getProfileById(int id);

    void updateProfile(Profile profile);


}
