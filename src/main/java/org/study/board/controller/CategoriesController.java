/*
package org.study.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.study.board.dto.Category;
import org.study.board.service.AdminService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
public class CategoriesController {

    private final AdminService adminService;

    @GetMapping("/aside")
    public String getCategories(Model model, Authentication authentication) {
        List<Category> categories = adminService.getAllCategories();

        // 부모-자식 관계로 카테고리 매핑
        Map<Integer, List<Category>> subCategoriesMap = categories.stream()
                .filter(category -> category.getCtgPno() != null)
                .collect(Collectors.groupingBy(category -> Integer.parseInt(category.getCtgPno())));

        model.addAttribute("categories", categories);
        model.addAttribute("subCategoriesMap", subCategoriesMap);

        if (authentication != null) {
            model.addAttribute("user", authentication.getPrincipal());
        }

        return "thymeleaf/aside";
    }
}
*/
