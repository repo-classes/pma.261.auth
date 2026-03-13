package store.auth;

import lombok.Builder;

@Builder
public record LoginIn(

    String email,
    String password

) {
    
}
