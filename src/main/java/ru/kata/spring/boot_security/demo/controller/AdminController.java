package ru.kata.spring.boot_security.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kata.spring.boot_security.demo.entity.Role;
import ru.kata.spring.boot_security.demo.entity.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@RequestMapping("/admin")
public class AdminController {

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
    public String addUser (@ModelAttribute("user") User user, @RequestParam(required = false) List<Long> rolesIds) {
        Set<Role> roles = roleService.findRolesByIds(rolesIds);
        if (roles.isEmpty()) {
            System.out.println("No roles found for the provided IDs: " + rolesIds);
        }
        user.setRoles(roles);
        userService.save(user);
        return "redirect:/admin/users";
    }

    @PostMapping("/delete-user")
    public String deleteUser(@RequestParam Long id) {
        userService.delete(id);
        return "redirect:/admin/users";
    }

    @GetMapping("/update-user")
    public String updateUserPage(@RequestParam Long id, Model model) {
        Optional<User> optionalUser = userService.findById(id);
        if (optionalUser.isPresent()) {
            model.addAttribute("user", optionalUser.get());
            return "update-user";
        } else {
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/update-user")
    public String updateUser(User user) {
        userService.update(user);
        return "redirect:/admin/users";
    }
}
