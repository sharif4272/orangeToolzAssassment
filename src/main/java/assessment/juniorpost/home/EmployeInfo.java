package assessment.juniorpost.home;

import lombok.Data;
import javax.persistence.Id;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "EMPLOYEE_INFORMATION")
public class EmployeInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String empName;
    private String empAge;
    private String empDesignation;
    private String mobileNo;
}
