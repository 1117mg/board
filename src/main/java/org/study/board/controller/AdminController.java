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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        // 카테고리 번호를 키로 하고 UserCtgAuth 객체를 값으로 하는 맵 생성
        Map<Integer, UserCtgAuth> authMap = new HashMap<>();
        for (Category category : categories) {
            authMap.put(category.getCtgNo(), new UserCtgAuth());
        }
        for (UserCtgAuth auth : auths) {
            authMap.put(auth.getCtgNo(), auth);
        }

        model.addAttribute("user", user);
        model.addAttribute("authMap", authMap);
        model.addAttribute("categories", categories);
        return "thymeleaf/admin/user_auths";
    }

    @PostMapping("/user/{idx}/auths")
    public String updateUserAuth(@PathVariable long idx, @RequestParam Map<String, String> params) {
        List<UserCtgAuth> auths = new ArrayList<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("read_") || entry.getKey().startsWith("write_")) {
                String[] parts = entry.getKey().split("_");
                int ctgNo = Integer.parseInt(parts[1]);
                boolean canRead = "read".equals(parts[0]) && "on".equals(entry.getValue());
                boolean canWrite = "write".equals(parts[0]) && "on".equals(entry.getValue());

                UserCtgAuth auth = auths.stream()
                        .filter(a -> a.getCtgNo() == ctgNo)
                        .findFirst()
                        .orElseGet(() -> {
                            UserCtgAuth newAuth = new UserCtgAuth();
                            newAuth.setCtgNo(ctgNo);
                            auths.add(newAuth);
                            return newAuth;
                        });

                if (parts[0].equals("read")) {
                    auth.setCanRead(canRead);
                } else if (parts[0].equals("write")) {
                    auth.setCanWrite(canWrite);
                }
            }
        }

        adminService.updateUserAuth(idx, auths);
        return "redirect:/admin/user-list";
    }
}