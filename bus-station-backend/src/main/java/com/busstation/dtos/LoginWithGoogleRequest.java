package com.busstation.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginWithGoogleRequest implements Serializable {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String avatar;
}
