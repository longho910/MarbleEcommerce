package com.marble.admin.category;

import com.marble.admin.FileUploadUtil;
import com.marble.admin.user.UserNotFoundException;
import com.marble.common.entity.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class CategoryController {
    @Autowired
    private CategoryService service;

    @GetMapping("/categories")
    public String listAll(@Param("sortDir") String sortDir, Model model) {
        if (sortDir == null || sortDir.isEmpty()) {
            sortDir = "asc";
        }
        List<Category> listCategories = service.listAll(sortDir);

        String reverseSortDir = sortDir.equals("asc") ? "desc" : "asc";

        model.addAttribute("listCategories", listCategories);
        model.addAttribute("reverseSortDir", reverseSortDir);
        model.addAttribute("sortDir", sortDir);

        return "categories/categories";
    }

    @GetMapping("/categories/new")
    public String newCategory(Model model) {
        List<Category> listCategories = service.listCategoriesUsedInForm();
        model.addAttribute("category", new Category());
        model.addAttribute("listCategories", listCategories);
        model.addAttribute("pageTitle", "Create New Category");
        return "categories/category_form";
    }

    @PostMapping("/categories/save")
    public String saveCategory(Category category, @RequestParam("fileImage")
    MultipartFile multipartFile, RedirectAttributes ra) throws IOException {
        if (!multipartFile.isEmpty()) {
            String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
            category.setImage(fileName);

            Category savedCategory = service.save(category);
            String uploadDir = "../category-images/" + savedCategory.getId();

            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
        } else {
            service.save(category);
        }

        ra.addFlashAttribute("message", "The category has been saved successfully!");
        return "redirect:/categories";
    }

    @GetMapping("/categories/edit/{id}")
    public String editCategory(@PathVariable(name = "id") Integer id, Model model,
                           RedirectAttributes redirectAttributes) {
        try {
            Category category = service.get(id);
            List<Category> listCategories = service.listCategoriesUsedInForm();

            model.addAttribute("category", category);
            model.addAttribute("pageTitle", "Edit category (ID: " + id + ")");
            model.addAttribute("listCategories", listCategories);
            return "categories/category_form";
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
            return "redirect:/categories";
        }
    }

    // edit category
    @GetMapping("/categories/{id}/enabled/{status}")
    public String updateCategoryEnabledStatus(@PathVariable("id") Integer id,
                                          @PathVariable("status") boolean enabled,
                                          RedirectAttributes redirectAttributes) {
        service.updateCategoryEnabledStatus(id, enabled);
        String status = (enabled) ? "enabled" : "disabled";
        String message = "The category ID " + id + " has been " + status;
        redirectAttributes.addFlashAttribute("message", message);

        return "redirect:/categories";
    }

    // delete category
    @GetMapping("/categories/delete/{id}")
    public String deleteUser(@PathVariable(name = "id") Integer id, Model model,
                             RedirectAttributes redirectAttributes) {
        try {
            service.delete(id);
            String categoryDir = "../category-images/" + id;
            FileUploadUtil.removeDir(categoryDir);
            redirectAttributes.addFlashAttribute("message", "The category ID " + id
                    + " has been deleted successfully");
        } catch (CategoryNotFoundException ex) {
            redirectAttributes.addFlashAttribute("message", ex.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "redirect:/categories";
    }


}