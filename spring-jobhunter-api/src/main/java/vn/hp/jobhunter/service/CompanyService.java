package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.domain.Company;
import vn.hp.jobhunter.domain.dto.Meta;
import vn.hp.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hp.jobhunter.repository.CompanyRepository;

import java.util.List;

@Service
public class CompanyService {
    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public Company createCompany(Company company) {
        return this.companyRepository.save(company);
    }

    public ResultPaginationDTO fetchAllCompanies(Specification<Company> specification, Pageable pageable) {
        Page<Company> companyPage = this.companyRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        Meta meta = new Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(companyPage.getTotalPages());
        meta.setTotal(companyPage.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(companyPage.getContent());
        return rs;
    }

    public Company updateCompany(Company c) {
//        Optional<Company> companyOptional = this.companyRepository.findById(c.getId());
//        if (companyOptional.isPresent()) {
//            Company currentCompany = companyOptional.get();
//            currentCompany.setLogo(c.getLogo());
//            currentCompany.setName(c.getName());
//            currentCompany.setDescription(c.getDescription());
//            currentCompany.setAddress(c.getAddress());
//            return this.companyRepository.save(currentCompany);
//        }
//        return null;
        return this.companyRepository.findById(c.getId()).map(e -> {
            e.setLogo(c.getLogo());
            e.setName(c.getName());
            e.setDescription(c.getDescription());
            e.setAddress(c.getAddress());
            return this.companyRepository.save(e);
        }).orElse(null);
    }

    public void deleteCompany(long companyId) {
        this.companyRepository.deleteById(companyId);
    }
}
