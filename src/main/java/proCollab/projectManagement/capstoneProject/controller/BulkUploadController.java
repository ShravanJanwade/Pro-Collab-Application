package proCollab.projectManagement.capstoneProject.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import proCollab.projectManagement.capstoneProject.service.BulkUploadService;


@Controller
@RequestMapping("/upload/fromExcel")
public class BulkUploadController {

    @Autowired
    private BulkUploadService bulkUploadService;

    @GetMapping
    public String uploadForm() {
        return "views/uploadForm";
    }

    @PostMapping
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            bulkUploadService.uploadUsersFromExcel(file);
            redirectAttributes.addFlashAttribute("successMessage", "File uploaded successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to upload file. Please try again.");
        }
        return "redirect:/upload/fromExcel";
    }
}