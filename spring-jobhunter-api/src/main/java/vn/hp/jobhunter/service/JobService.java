package vn.hp.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import vn.hp.jobhunter.domain.Job;
import vn.hp.jobhunter.domain.Skill;
import vn.hp.jobhunter.domain.response.ResultPaginationDTO;
import vn.hp.jobhunter.domain.response.job.ResCreateJobDTO;
import vn.hp.jobhunter.domain.response.job.ResUpdateJobDTO;
import vn.hp.jobhunter.repository.JobRepository;
import vn.hp.jobhunter.repository.SkillRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class JobService {
    private final JobRepository jobRepository;
    private final SkillRepository skillRepository;

    public JobService(JobRepository jobRepository, SkillRepository skillRepository) {
        this.jobRepository = jobRepository;
        this.skillRepository = skillRepository;
    }

    public ResCreateJobDTO create(Job job){
        if (job.getSkills() != null){
            List<Long> reqSkills = job.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        Job curr = this.jobRepository.save(job);
        // Convert response
        ResCreateJobDTO res = new ResCreateJobDTO();
        res.setId(curr.getId());
        res.setName(curr.getName());
        res.setLocation(curr.getLocation());
        res.setSalary(curr.getSalary());
        res.setLevel(curr.getLevel());
        res.setStartDate(curr.getStartDate());
        res.setEndDate (curr.getEndDate());
        res.setActive (curr.isActive());
        res.setCreatedAt (curr.getCreatedAt());
        res.setCreatedBy (curr.getCreatedBy());
        if (curr.getSkills() != null) {
            List<String> skills = curr.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            res.setSkills (skills);
        }
        return res;
    }

    public ResUpdateJobDTO update(Job job){
        if (job.getSkills() != null){
            List<Long> reqSkills = job.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            job.setSkills(dbSkills);
        }

        Job curr = this.jobRepository.save(job);
        // Convert response
        ResUpdateJobDTO res = new ResUpdateJobDTO();
        res.setId(curr.getId());
        res.setName(curr.getName());
        res.setLocation(curr.getLocation());
        res.setSalary(curr.getSalary());
        res.setLevel(curr.getLevel());
        res.setStartDate(curr.getStartDate());
        res.setEndDate (curr.getEndDate());
        res.setActive (curr.isActive());
        res.setUpdatedAt(curr.getUpdatedAt());
        res.setUpdatedBy(curr.getUpdatedBy());
        if (curr.getSkills() != null) {
            List<String> skills = curr.getSkills()
                    .stream().map(Skill::getName)
                    .collect(Collectors.toList());
            res.setSkills (skills);
        }
        return res;
    }

    public boolean jobExisted(long id){
        return this.jobRepository.existsById(id);
    }

    public void delete(long id){
        this.jobRepository.deleteById(id);
    }

    public Optional<Job> getAJob(long id){
        return this.jobRepository.findById(id);
    }

    public ResultPaginationDTO getAllJobs(Specification<Job> specification, Pageable pageable){
        Page<Job> jobPage = this.jobRepository.findAll(specification, pageable);
        ResultPaginationDTO rs = new ResultPaginationDTO();
        ResultPaginationDTO.Meta meta = new ResultPaginationDTO.Meta();

        meta.setPage(pageable.getPageNumber() + 1);
        meta.setPageSize(pageable.getPageSize());
        meta.setPages(jobPage.getTotalPages());
        meta.setTotal(jobPage.getTotalElements());

        rs.setMeta(meta);
        rs.setResult(jobPage.getContent());
        return rs;
    }
}
