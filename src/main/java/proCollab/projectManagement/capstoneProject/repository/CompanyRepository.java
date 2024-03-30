package proCollab.projectManagement.capstoneProject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import proCollab.projectManagement.capstoneProject.model.Company;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Company findByName(String name);

}
