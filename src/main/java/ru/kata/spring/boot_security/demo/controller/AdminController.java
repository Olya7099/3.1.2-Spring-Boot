package ru.kata.spring.boot_security.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    private final UserService userService;
    private final RoleService roleService;

    @Autowired
    public AdminController(UserService userService, RoleService roleService) {
        this.userService = userService;
        this.roleService = roleService;
    }

    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> usersList = userService.findAll();
        model.addAttribute("users", usersList);
        return "users-list";
    }

    @GetMapping("/create-user")
    public String createUserPage(Model model) {
        List<Role> roleList = roleService.findAll();
        model.addAttribute("roles", roleList);
        model.addAttribute("user", new User());
        return "create-user";
    }
    @PostMapping("/create-user")
    public String addUser(@ModelAttribute("user") User user,
                          @RequestParam(required = false) List<Long> rolesIds) {
        logger.info("Creating new user: {}", user.getUsername());


        if (rolesIds != null && !rolesIds.isEmpty()) {
            Set<Role> roles = roleService.findRolesByIds(rolesIds);
            user.setRoles(roles);
        }

        userService.save(user);
        logger.info("User created successfully: {}", user.getUsername());
        return "redirect:/admin/users";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam Long id) {
        logger.info("Deleting user with ID: {}", id);
        userService.delete(id);
        logger.info("User deleted successfully: ID {}", id);
        return "redirect:/admin/users";
    }

    @GetMapping("/update-user")
    public String updateUserPage(@RequestParam Long id, Model model) {
        logger.info("Opening update page for user ID: {}", id);
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            model.addAttribute("roles", roleService.findAll());
            return "update-user";
        } else {
            logger.warn("User not found for update, ID: {}", id);
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/update-user")
    public String updateUser(@ModelAttribute("user") User user,
                             @RequestParam(required = false) List<Long> rolesIds) {
        logger.info("Updating user with ID: {}", user.getId());


        Optional<User> existingUserOpt = userService.findById(user.getId());
        if (!existingUserOpt.isPresent()) {
            logger.warn("User not found for update, ID: {}", user.getId());
            return "redirect:/admin/users?error=userNotFound";
        }
        User existingUser = existingUserOpt.get();


        existingUser.setUsername(user.getUsername());
        existingUser.setEmail(user.getEmail());


        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }
        if (rolesIds != null && !rolesIds.isEmpty()) {
            Set<Role> newRoles = roleService.findRolesByIds(rolesIds);
            existingUser.setRoles(newRoles);
            logger.info("Roles updated for user {}: {}", user.getUsername(), newRoles);
        } else {

            existingUser.getRoles().clear();
            logger.info("Roles cleared for user {}", user.getUsername());
        }

        userService.update(existingUser);
        logger.info("User updated successfully: {}", existingUser.getUsername());
        return "redirect:/admin/users";
    }
}




