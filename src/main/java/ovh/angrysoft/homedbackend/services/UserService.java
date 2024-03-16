package ovh.angrysoft.homedbackend.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import ovh.angrysoft.homedbackend.configurations.UsersList;
import ovh.angrysoft.homedbackend.models.User;

@Service
public class UserService {
    private List<String> users;

    // this is temporary solution
    UserService(UsersList usersList) {
        this.users = new ArrayList<>();
        this.users = List.of(usersList.users().split(","));
    }

    public Optional<User> findByEmail(String email) {
        Optional<User> result = Optional.empty();
        if (users.contains(email))
            result = Optional.of(new User(email));

        return result;
    }

}
