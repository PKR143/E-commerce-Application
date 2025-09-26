package com.java.jwt.dto;

import com.java.jwt.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @AllArgsConstructor @NoArgsConstructor
@Builder
public class UserResponse implements Response{
    private String id;
    private String username;
    private String name;

    public UserResponse(User user){
        this.id = user.getId();
        this.username = user.getUserName();
        this.name = user.getFirstName()+" "+user.getLastName();
    }

}
