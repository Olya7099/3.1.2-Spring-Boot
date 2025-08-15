package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.entity.Role;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface RoleService {
    List<Role> findAll();
    Set<Role> findSetByNames(List<String> roleNames);
    Optional<Role> findById(Long aLong);

    public Set<Role> findRolesByIds(List<Long> rolesIds);
}
