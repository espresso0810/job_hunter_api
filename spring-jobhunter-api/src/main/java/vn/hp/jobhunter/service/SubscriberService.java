package vn.hp.jobhunter.service;

import org.springframework.stereotype.Service;
import vn.hp.jobhunter.domain.Skill;
import vn.hp.jobhunter.domain.Subscriber;
import vn.hp.jobhunter.repository.SkillRepository;
import vn.hp.jobhunter.repository.SubscriberRepository;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriberService {
    private final SubscriberRepository subscriberRepository;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberRepository subscriberRepository, SkillRepository skillRepository) {
        this.subscriberRepository = subscriberRepository;
        this.skillRepository = skillRepository;
    }

    public boolean isEmailExisted(String email){
        return this.subscriberRepository.existsByEmail(email);
    }
    public Subscriber getSubscriber(long id){
        return this.subscriberRepository.findById(id).orElse(null);
    }
    public Subscriber create(Subscriber subscriber){
        //check skill is valid
        if (subscriber.getSkills() != null){
            List<Long> reqSkills = subscriber.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subscriber);
    }

    public Subscriber update(Subscriber subsDB, Subscriber subscriberReq){
        //check skill is valid
        if (subscriberReq.getSkills() != null){
            List<Long> reqSkills = subscriberReq.getSkills().stream().map(Skill::getId).toList();
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);
            subsDB.setSkills(dbSkills);
        }
        return this.subscriberRepository.save(subsDB);
    }
}
