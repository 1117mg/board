package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.Category;
import org.study.board.dto.User;
import org.study.board.dto.UserCtgAuth;
import org.study.board.service.AdminService;
import org.study.board.service.UserService;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/user-list")
    public String userList(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "thymeleaf/admin/user_list";
    }

    @GetMapping("/user/{idx}/auths")
    public String userAuths(@PathVariable long idx, Model model) {
        User user = userService.findById(idx);
        if (user == null) {
            return "redirect:/admin/user-list";
        }

        List<UserCtgAuth> auths = adminService.getUserAuth(idx);
        List<Category> categories = userService.getAllCategories();
        model.addAttribute("user", user);
        model.addAttribute("auths", auths);
        model.addAttribute("categories", categories);
        return "thymeleaf/admin/user_auths";
    }

    @PostMapping("/user/{idx}/auths")
    public String updateUserAuth(@PathVariable long idx, @ModelAttribute("auths") List<UserCtgAuth> auths) {
        adminService.updateUserAuth(idx, auths);
        return "redirect:/admin/user-list";
    }
}