package assessment.juniorpost.employee;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeInfoService {
    @Autowired
    private EmployeInfoRepository employeInfoRepository;


    private static final int BATCH_SIZE = 1000;

    public Map storeData(MultipartFile file) {

        Map<String, String> errors = new LinkedHashMap<>();
        Map<String, String> stringMap = new HashMap<>();

        try {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file.getInputStream());
            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);

            int totalRows = xssfSheet.getLastRowNum()+1;
            int numThreads = Math.min(Runtime.getRuntime().availableProcessors(),totalRows/BATCH_SIZE);
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            List<Runnable> tasks = new ArrayList<>();

            for (int startRow = 0; startRow < totalRows; startRow += BATCH_SIZE){
                final int finalStartRow= startRow;
                Runnable task = () -> processRows(xssfSheet, finalStartRow, finalStartRow + BATCH_SIZE);
                tasks.add(task);
            }

            for (Runnable task : tasks) {
                executorService.execute(task);
            }

            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.HOURS);


        } catch (IOException | InterruptedException e) {
            System.err.println(e.getMessage());
        }

        return errors;
    }
    private void processRows(XSSFSheet xssfSheet, int startRow, int endRow){
        for (int i = startRow; i < endRow; i++){
            XSSFRow row = xssfSheet.getRow(i);

            if (row.getCell(1) != null && row.getCell(1).toString().length() > 1){
                EmployeInfo employeInfo = new EmployeInfo();
                String employeeName = row.getCell(1) != null ? row.getCell(1).toString():"";
                String employeeAge = row.getCell(2) != null ? row.getCell(2).toString():"";
                String employeeDesignation = row.getCell(3) != null ? row.getCell(3).toString():"";
                String employeeMobileNo = row.getCell(4) != null ? row.getCell(4).toString():"";

                employeInfo.setEmpName(employeeName);
                employeInfo.setEmpAge(employeeAge);
                employeInfo.setEmpDesignation(employeeDesignation);
                employeInfo.setMobileNo(employeeMobileNo);

                employeInfoRepository.save(employeInfo);
            }
        }
    }

//    public Map storeData(MultipartFile file) {
//
//        Map<String, String> errors = new LinkedHashMap<>();
//        List<EmployeInfo> employeInfoList = new ArrayList<>();
//
//        try {
//            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(file.getInputStream());
//            XSSFSheet xssfSheet = xssfWorkbook.getSheetAt(0);
//            for (int i = 1; i < xssfSheet.getPhysicalNumberOfRows(); i++) {
//                XSSFRow row = xssfSheet.getRow(i);
//
//
//                if (row.getCell(0) != null && row.getCell(0).toString().length() > 1) {
//                    EmployeInfo employeInfo = new EmployeInfo();
//
//                    employeInfo.setEmpName(row.getCell(0) != null ? row.getCell(0).toString():"");
//                    employeInfo.setEmpAge(row.getCell(1) != null ? row.getCell(1).toString():"");
//                    employeInfo.setEmpDesignation(row.getCell(2) != null ? row.getCell(2).toString():"");
//                    employeInfo.setMobileNo(row.getCell(3) != null ? row.getCell(3).toString():"");
//
//                    employeInfoList.add(employeInfo);
//
//                    //this is the batch operation , for big data saving purposes when 1000 data save then clear the saving data and iterate again the condition
//                    if (employeInfoList.size() == 1000) {
//                        employeInfoRepository.saveAll(employeInfoList);
//                        employeInfoList.clear();
//                    }
//                }
//                //this is the batch operation , for big data saving purposes when data size is greater then 0 or less then 1000 then  save and also clear the saving data and iterate again the condition
//                if (employeInfoList.size() > 0 && employeInfoList.size() < 1000){
//                    employeInfoRepository.saveAll(employeInfoList);
//                    employeInfoList.clear();
//                }
//            }
//
//        } catch (IOException e) {
//            System.err.println(e.getMessage());
//        }
//        return errors;
//    }

    public List<EmployeInfo> findAll() {
        return employeInfoRepository.findAll();
    }
}
