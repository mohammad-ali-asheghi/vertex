package com.vertex.core.dto;

import com.vertex.core.dto.base.BaseModel;
import lombok.*;

@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper = true)
public class UserInfoModel extends BaseModel {

    @Getter
    @Setter
    public static class Create extends UserInfoModel {
        private String firstName;
        private String lastName;
        private String nationalNo;
        private String userInfoTypeCodeTypeItemId;
        private String mobileNumber;
        private String phoneNumber;
        private String fingerPrint;
        private String username;
        private String password;
        private String confirmPassword;
    }

    @Getter
    @Setter
    public static class Update extends UserInfoModel {
        private String firstName;
        private String lastName;
        private String nationalNo;
        private String userInfoTypeCodeTypeItemId;
        private String mobileNumber;
        private String phoneNumber;
        private String fingerPrint;
    }

    @Getter
    @Setter
    public static class Response extends UserInfoModel {
        private String firstName;
        private String lastName;
        private String nationalNo;
        private String userInfoTypeCodeTypeItemId;
        private String mobileNumber;
        private String phoneNumber;
        private String fingerPrint;
        private String username;
        private Boolean active;
    }

    public record Search(
            String firstName,
            String lastName,
            String nationalNo,
            String userInfoTypeCodeTypeItemId
    ) {
    }

    @Builder
    public record UserRecord(
            String username,
            String password
    ) {
    }
}
