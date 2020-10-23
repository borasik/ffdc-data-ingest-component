package com.finastra.ffdc.dataset.component.dtos;

import lombok.Data;

@Data
public class TokenDto {
    public String access_token;
    public String expires_in;
    public String refresh_token;
    public String refresh_expires_in;
    public String scope;
    public String token_type;    
    public String id_token;    
}
