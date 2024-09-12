package com.busstation.repositories;

import com.busstation.pojo.Role;

public interface RoleRepository {
    Role getRoleByName(String name);
    Role findById(Long id);
}
