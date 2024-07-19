package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.study.board.dto.*;
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

    @GetMapping("/users")
    public String userList(User user, Model model, Principal principal, @RequestParam(defaultValue = "1") int page) {
        int total = adminService.cntUser();

        // 페이징
        PaginateDto paginate = new PaginateDto(7, 3);
        paginate.setPageNo(page);
        paginate.setTotalSize(total);
        paginate._calc();

        user.setPageNo(page);
        user.setPageSize(paginate.getPageSize());
        user.setPageOffset(paginate.getPageOffset());

        int offset = (page - 1) * paginate.getPageSize();
        List<User> users = adminService.getAllUsersWithPagination(offset, paginate.getPageSize());

        List<Category> categories = adminService.getAllCategories();

        // 부모-자식 관계로 카테고리 매핑
        Map<Integer, List<Category>> subCategoriesMap = categories.stream()
                .filter(category -> category.getCtgPno() != null)
                .collect(Collectors.groupingBy(category -> Integer.parseInt(category.getCtgPno())));

        model.addAttribute("categories", categories);
        model.addAttribute("subCategoriesMap", subCategoriesMap);

        // 사용자 이름 설정
        if (principal != null) {
            String username = principal.getName();

            if (principal instanceof UsernamePasswordAuthenticationToken) {
                Object principalObj = ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
                // sns유저일 경우
                // 배열 안의 gno와 일치하는 idx를 가진 사용자(연동된 사용자)의 이름을 출력
                if (principalObj instanceof SnsUser) {
                    SnsUser snsUser = (SnsUser) principalObj;
                    Long gno = snsUser.getGno();
                    user = userService.findById(gno);
                    if (user != null) {
                        username = user.getUsername();
                    }
                }
            }

            user = userService.findByName(username);

            model.addAttribute("username", username);
            model.addAttribute("user", user);
        }

        model.addAttribute("paginate", paginate);
        model.addAttribute("users", users);
        model.addAttribute("categories", categories);
        return "thymeleaf/admin/user_main";
    }

    @GetMapping("/user/{userId}")
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
        return "redirect:/admin/users";
    }

    @GetMapping("/deleteUser/{idx}")
    public String deleteUser(@PathVariable Long idx){
        User user = userService.findById(idx);
        adminService.deleteUser(user);
        return "redirect:/admin/users";
    }

}
