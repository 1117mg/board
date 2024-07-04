package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.Category;
import org.study.board.dto.User;
import org.study.board.dto.UserCtgAuth;
import org.study.board.service.AdminService;
import org.study.board.service.UserService;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final UserService userService;

    @GetMapping("/user-list")
    public String userList(Model model, Principal principal) {
        List<User> users = adminService.getAllUsers();
        List<Category> categories = adminService.getAllCategories();

        // 부모-자식 관계로 카테고리 매핑
        Map<Integer, List<Category>> subCategoriesMap = categories.stream()
                .filter(category -> category.getCtgPno() != null)
                .collect(Collectors.groupingBy(category -> Integer.parseInt(category.getCtgPno())));

        model.addAttribute("categories", categories);
        model.addAttribute("subCategoriesMap", subCategoriesMap);

        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }

        // 각 사용자별 카테고리별 권한 맵 생성
        /*Map<Long, Map<Integer, UserCtgAuth>> authMap = new HashMap<>();
        for (User user : users) {
            Map<Integer, UserCtgAuth> userAuthMap = new HashMap<>();
            List<UserCtgAuth> auths = adminService.getUserAuth(user.getIdx());
            for (Category category : categories) {
                UserCtgAuth userCtgAuth = auths.stream()
                        .filter(auth -> auth.getCtgNo() == category.getCtgNo())
                        .findFirst()
                        .orElseGet(() -> {
                            UserCtgAuth newAuth = new UserCtgAuth();
                            newAuth.setCtgNo(category.getCtgNo());
                            newAuth.setCanRead(false);
                            newAuth.setCanWrite(false);
                            return newAuth;
                        });
                userAuthMap.put(category.getCtgNo(), userCtgAuth);
            }
            authMap.put(user.getIdx(), userAuthMap);
        }*/

        model.addAttribute("users", users);
        //model.addAttribute("authMap", authMap);
        model.addAttribute("categories", categories);
        return "thymeleaf/admin/user_main";
    }

    @GetMapping("/user-info/{userId}")
    public String userInfo(@PathVariable String userId, Model model) {
        log.info("유저인포 유저아이디: {}", userId);
        User user = userService.getLoginUser(userId);
        if (user == null) {
            log.warn("유저확인불가: {}", userId);
            return "redirect:/login";
        }
        List<Category> categories = adminService.getAllCategories();
        Map<Integer, UserCtgAuth> authMap = new HashMap<>();
        List<UserCtgAuth> auths = adminService.getUserAuth(user.getIdx());

        // 카테고리를 부모-자식 구조로 분류하여 처리
        List<Category> parentCategories = categories.stream()
                .filter(category -> "0".equals(category.getCtgPno()))
                .collect(Collectors.toList());

        for (Category parent : parentCategories) {
            UserCtgAuth parentAuth = auths.stream()
                    .filter(auth -> auth.getCtgNo() == parent.getCtgNo())
                    .findFirst()
                    .orElseGet(() -> {
                        UserCtgAuth newAuth = new UserCtgAuth();
                        newAuth.setCtgNo(parent.getCtgNo());
                        newAuth.setCanRead(false);
                        newAuth.setCanWrite(false);
                        newAuth.setCanDownload(false);
                        return newAuth;
                    });
            authMap.put(parent.getCtgNo(), parentAuth);

            // 자식 카테고리 처리
            List<Category> childCategories = categories.stream()
                    .filter(category -> category.getCtgPno().equals(String.valueOf(parent.getCtgNo())))
                    .collect(Collectors.toList());

            for (Category child : childCategories) {
                UserCtgAuth childAuth = auths.stream()
                        .filter(auth -> auth.getCtgNo() == child.getCtgNo())
                        .findFirst()
                        .orElseGet(() -> {
                            UserCtgAuth newAuth = new UserCtgAuth();
                            newAuth.setCtgNo(child.getCtgNo());
                            newAuth.setCanRead(false);
                            newAuth.setCanWrite(false);
                            newAuth.setCanDownload(false);
                            return newAuth;
                        });
                authMap.put(child.getCtgNo(), childAuth);
            }
        }

        model.addAttribute("user", user);
        model.addAttribute("categories", categories);
        model.addAttribute("authMap", authMap);
        return "thymeleaf/admin/user_info";
    }

    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute User user, @RequestParam Map<String, String> params) {
        long userIdx = user.getIdx();

        adminService.updateUser(user);

        Map<Integer, UserCtgAuth> authMap = new HashMap<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("read_") || entry.getKey().startsWith("write_") || entry.getKey().startsWith("download_")) {
                String[] parts = entry.getKey().split("_");
                int ctgNo = Integer.parseInt(parts[1]);

                UserCtgAuth auth = authMap.getOrDefault(ctgNo, new UserCtgAuth());
                auth.setUserIdx(userIdx);
                auth.setCtgNo(ctgNo);

                if (entry.getKey().startsWith("read_")) {
                    auth.setCanRead("on".equals(entry.getValue()));
                } else if (entry.getKey().startsWith("write_")) {
                    auth.setCanWrite("on".equals(entry.getValue()));
                } else if (entry.getKey().startsWith("download_")) {
                    auth.setCanDownload("on".equals(entry.getValue()));
                }

                authMap.put(ctgNo, auth);
            }
        }

        adminService.updateUserAuth(new ArrayList<>(authMap.values()), userIdx);
        return "redirect:/admin/user-list";
    }

    /*@PostMapping("/user-auth")
    public String updateUserAuth(@RequestParam Map<String, String> params) {
        Map<String, UserCtgAuth> authMap = new HashMap<>();

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (entry.getKey().startsWith("read_") || entry.getKey().startsWith("write_")) {
                String[] parts = entry.getKey().split("_");
                long userIdx = Long.parseLong(parts[1]);
                int ctgNo = Integer.parseInt(parts[2]);
                boolean canRead = "read".equals(parts[0]) && "on".equals(entry.getValue());
                boolean canWrite = "write".equals(parts[0]) && "on".equals(entry.getValue());

                String key = userIdx + "_" + ctgNo;
                UserCtgAuth auth = authMap.getOrDefault(key, new UserCtgAuth());
                auth.setUserIdx(userIdx);
                auth.setCtgNo(ctgNo);
                if ("read".equals(parts[0])) {
                    auth.setCanRead(canRead);
                }
                if ("write".equals(parts[0])) {
                    auth.setCanWrite(canWrite);
                }

                authMap.put(key, auth);
            }
        }

        List<UserCtgAuth> auths = new ArrayList<>(authMap.values());
        adminService.updateUserAuth(auths);
        return "redirect:/admin/user-list";
    }*/

}
