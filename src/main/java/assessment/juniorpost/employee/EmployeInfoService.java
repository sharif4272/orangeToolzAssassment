package assessment.juniorpost.employee;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Service
public class EmployeInfoService {

    @Autowired
    private EmployeInfoRepository employeInfoRepository;


    private static final int BATCH_SIZE = 3;

    public Map storeData(MultipartFile file) {
        Map<String, String> errors = new LinkedHashMap<>();

        try {
           byte[] bytes = file.getBytes();
            String completeData = new String(bytes);
            String[] rows = completeData.split("\r\n");

            int totalRows = rows.length - 1;
            int numThreads = Math.min(Runtime.getRuntime().availableProcessors(), totalRows / BATCH_SIZE);
            ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
            List<Runnable> tasks = new ArrayList<>();
            for (int startRow = 1; startRow < totalRows; startRow += BATCH_SIZE) {
                final int finalStartRow = startRow;
                Runnable task = () -> processRows(rows, finalStartRow, finalStartRow + BATCH_SIZE);
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

    private void processRows(String[] rows, int startRow, int endRow) {
        for (int i = startRow; i < endRow; i++) {
            try {
                String[] columns = null;
                columns = rows[i].split(",");
                if (columns.length == 4) {

                    if (columns[0].contains("Employee Name") || columns[1].contains("Employee Age") || columns[2].contains("Designation") || columns[3].contains("Mobile"))
                        continue;

                    EmployeInfo employeInfo = new EmployeInfo();
                    employeInfo.setEmpName(columns[0]);
                    employeInfo.setEmpAge(columns[1]);
                    employeInfo.setEmpDesignation(columns[2]);
                    employeInfo.setMobileNo(columns[3]);

                    employeInfoRepository.save(employeInfo);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public List<EmployeInfo> findAll() {
        return employeInfoRepository.findAll();
    }

}
