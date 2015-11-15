package ch.epfl.sweng.swissaffinity.utilities.network.users;

import ch.epfl.sweng.swissaffinity.users.User;

public interface UserClient {
    User fetchByUsername(String userName) throws UserClientException;

    User fetchByFacebookID(String id) throws UserClientException;
}
