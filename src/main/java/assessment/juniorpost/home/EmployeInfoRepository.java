package assessment.juniorpost.home;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface EmployeInfoRepository extends JpaRepository<EmployeInfo, Long> {
}
