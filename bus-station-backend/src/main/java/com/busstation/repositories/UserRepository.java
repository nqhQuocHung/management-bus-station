package com.busstation.repositories;

import com.busstation.pojo.User;
import java.util.List;
import java.util.Map;


public interface UserRepository {
    User getUserByUserName(String username);
    void saveUser(User newUser);
    boolean isEmailExist(String email);
    List<User> listActiveUsers();
    List<User> findActiveUsersByRoleId(Long roleId);
    User getUserById(Long userId);
    void changeRole(User user);
    User update(User user);
    long countUsersByRoleId(Long roleId);

    User  getUserByEmail(String email);
}
