package assessment.juniorpost.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.util.Map;

@Controller
public class EmployeeController {

    @Autowired
    private EmployeInfoService employeInfoService;

    @GetMapping("/")
    public String viewHomePage(){
        return "employee";
    }
    @GetMapping("/form-upload")
    public String uploadForm(){

        return "fileUploadForm";
    }

    @GetMapping("/list-page")
    public String listPage(Model model){
        model.addAttribute("employeeList", employeInfoService.findAll());
        return "employee";
    }

    @PostMapping("/file-upload-save")
    public String uploadAndSaveExcelFile(@RequestParam("file") MultipartFile file, HttpSession session) {
        Map errors = employeInfoService.storeData(file);
        session.setAttribute("employeFileUploadError", errors);

        return "redirect:/list-page";

    }
}
