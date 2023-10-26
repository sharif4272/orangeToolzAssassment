package assessment.juniorpost.home;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class EmployeInfoService {
    @Autowired
    private EmployeInfoRepository employeInfoRepository;

    public Map storeData(MultipartFile file) {

        Map<String, String> errors = new LinkedHashMap<>();
        List<EmployeInfo> employeInfoList = new ArrayList<>();

        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
            for (int i = 1; i < xssfSheet.getPhysicalNumberOfRows(); i++) {
                XSSFRow row = xssfSheet.getRow(i);


                if (row.getCell(0) != null && row.getCell(0).toString().length() > 1) {
                    EmployeInfo employeInfo = new EmployeInfo();

                    employeInfo.setEmpName(row.getCell(0) != null ? row.getCell(0).toString():"");
                    employeInfo.setEmpAge(row.getCell(1) != null ? row.getCell(1).toString():"");
                    employeInfo.setEmpDesignation(row.getCell(2) != null ? row.getCell(2).toString():"");
                    employeInfo.setMobileNo(row.getCell(3) != null ? row.getCell(3).toString():"");

                    employeInfoList.add(employeInfo);

                    //this is the batch operation , for big data saving purposes when 1000 data save then clear the saving data and iterate again the condition
                    if (employeInfoList.size() == 1000) {
                        employeInfoRepository.saveAll(employeInfoList);
                        employeInfoList.clear();
                    }
                }
                //this is the batch operation , for big data saving purposes when data size is greater then 0 or less then 1000 then  save and also clear the saving data and iterate again the condition
                if (employeInfoList.size() > 0 && employeInfoList.size() < 1000){
                    employeInfoRepository.saveAll(employeInfoList);
                    employeInfoList.clear();
                }
            }

        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        return errors;
    }

    public List<EmployeInfo> findAll() {
        return employeInfoRepository.findAll();
    }
}
