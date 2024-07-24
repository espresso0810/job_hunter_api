package vn.hp.jobhunter.domain.response.user;

import lombok.Getter;
import lombok.Setter;
import vn.hp.jobhunter.util.constant.GenderEnum;

import java.time.Instant;

@Getter
@Setter
public class ResUpdateUserDTO {
    private long id;
    private String name;
    private String email;
    private GenderEnum gender;
    private String address;
    private int age;
    private Instant updatedAt;

    private CompanyUser company;

    @Getter
    @Setter
    public static class CompanyUser{
        private long id;
        private String name;
    }
}
