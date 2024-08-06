package vn.hp.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hp.jobhunter.domain.Resume;
import vn.hp.jobhunter.domain.response.ResultPaginationDTO;
import vn.hp.jobhunter.domain.response.resume.ResCreateResumeDTO;
import vn.hp.jobhunter.domain.response.resume.ResResumeDTO;
import vn.hp.jobhunter.domain.response.resume.ResUpdateResumeDTO;
import vn.hp.jobhunter.service.ResumeService;
import vn.hp.jobhunter.util.annotation.ApiMessage;
import vn.hp.jobhunter.util.error.IdInvalidException;

import java.util.Optional;

@RestController
@RequestMapping("api/v1")
public class ResumeController {
    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @PostMapping("resumes")
    @ApiMessage("Create resume")
    public ResponseEntity<ResCreateResumeDTO> createResume(@Valid @RequestBody Resume resume) throws IdInvalidException {
        boolean check = this.resumeService.checkResumeExistByUserAndJob(resume);
        if (!check){
            throw new IdInvalidException("User/Job_id không tồn tại ");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.resumeService.create(resume));
    }

    @PutMapping("resumes")
    @ApiMessage("Update resume")
    public ResponseEntity<ResUpdateResumeDTO> updateResume(@RequestBody Resume resume) throws IdInvalidException {
        Optional<Resume> reqResumeOptional = this.resumeService.fetchById(resume.getId());
        if (reqResumeOptional.isEmpty()) {
            throw new IdInvalidException("Resume id = " + resume.getId() + " không tồn tại");
        }

        Resume reqResume = reqResumeOptional.get();
        reqResume.setStatus(resume.getStatus());

        return ResponseEntity.ok().body(this.resumeService.update(reqResume));
    }

    @DeleteMapping("resumes/{id}")
    @ApiMessage("Delete resume")
    public ResponseEntity<Void> deleteResume(@PathVariable("id") long id) throws IdInvalidException {
        if (!this.resumeService.isResumeExisted(id)){
            throw new IdInvalidException("Resume id = " + id + " không tồn tại");
        }
        this.resumeService.delete(id);
        return ResponseEntity.ok(null);
    }

    @GetMapping("resumes/{id}")
    @ApiMessage("Get a resume")
    public ResponseEntity<ResResumeDTO> getResume(@PathVariable("id") long id) throws IdInvalidException {
        Optional<Resume> resume = this.resumeService.fetchById(id);
        if (resume.isEmpty()){
            throw new IdInvalidException("Resume id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok(this.resumeService.getResume(resume.get()));
    }

    @GetMapping("resumes")
    @ApiMessage("Get all resumes")
    public ResponseEntity<ResultPaginationDTO> getAllResumes(@Filter Specification<Resume> specification, Pageable pageable){
        return ResponseEntity.ok(this.resumeService.getAllResumes(specification,pageable));
    }

    @PostMapping("resumes/by-user")
    @ApiMessage("Get list resume by user")
    public ResponseEntity<ResultPaginationDTO> fetchResumeByUser(Pageable pageable){
        return ResponseEntity.ok(this.resumeService.fetchResumeByUser(pageable));
    }
}
