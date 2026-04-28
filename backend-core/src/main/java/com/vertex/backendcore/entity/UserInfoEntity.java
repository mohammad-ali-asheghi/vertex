package com.vertex.backendcore.entity;

import com.vertex.backendcore.entity.base.PO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("unused")
@Entity()
@Table(name = "sec_user_info")
@Setter
@Getter
public class UserInfoEntity extends PO {

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @NotNull
    @Column(name = "national_no")
    private String nationalNo;

    @NotNull
    @Column(name = "user_info_type_code_type_item_id")
    private String userInfoTypeCodeTypeItemId;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "finger_print")
    private String fingerPrint;
}
