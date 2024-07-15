package vn.hp.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.domain.Company;
import vn.hp.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hp.jobhunter.service.CompanyService;
import vn.hp.jobhunter.util.annotation.ApiMessage;

import java.util.List;
import java.util.Optional;

@RestController
public class CompanyController {
    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("companies")
    @ApiMessage("Fetch All Companies")
    public ResponseEntity<ResultPaginationDTO> getAllCompanies(
            @Filter Specification<Company> spec,
            Pageable pageable) {
        return ResponseEntity.ok(this.companyService.fetchAllCompanies(spec, pageable));
    }

    @PostMapping("companies")
    public ResponseEntity<Company> createCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.createCompany(company));
    }

    @PutMapping("companies")
    public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company company) {
        return ResponseEntity.ok(this.companyService.updateCompany(company));
    }

    @DeleteMapping("/companies/{id}")
    public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
        this.companyService.deleteCompany(id);
        return ResponseEntity.ok(null);
    }

}
