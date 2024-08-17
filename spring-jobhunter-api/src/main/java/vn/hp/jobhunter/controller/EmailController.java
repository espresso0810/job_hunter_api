package vn.hp.jobhunter.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.hp.jobhunter.service.EmailService;
import vn.hp.jobhunter.util.annotation.ApiMessage;

@RestController
@RequestMapping("api/v1")
public class EmailController {
    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @GetMapping("email")
    @ApiMessage("Send simple email")
    public String sendSimpleEmail(){
        //this.emailService.sendEmailSync("amazingname09@gmail.com", "test spring boot", "<h1><b>Oke!</b></h1>", false, true);
        this.emailService.sendEmailFromTemplateSync("amazingname09@gmail.com", "Test", "job");
        return "Ok!";
    }
}
