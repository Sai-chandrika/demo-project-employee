package com.example.organizationEmployeeProjectManagement.security.dto.changePassword;

import com.example.organizationEmployeeProjectManagement.exception.NotFoundException;
import com.example.organizationEmployeeProjectManagement.validation.Validation;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chandrika
 * @ProjectName Organization_Employee_ProjectManagement
 * @since 13-04-2023
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangePassWordDto {
   private String email;
   private String newPassword;
   private String confirmPassword;

   public String validateEmail() {
      if (!Validation.isValid(this.getEmail())) {
         return "email id mandatory";
      }
      return "";
   }



   public String validateRequest() {
      if (!Validation.isValid(this.getNewPassword())) {
         return "password is mandatory";
      }else
      if (!Validation.isValidPassword(this.getNewPassword())) {
       return "password pattern is not match";
      }else
      if(!Validation.isValid(this.getConfirmPassword())){
         return "conform password is not match";
      }
      return "";
   }
}

