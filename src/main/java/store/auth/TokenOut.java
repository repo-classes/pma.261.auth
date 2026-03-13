package store.auth;

import lombok.Builder;

@Builder
public record TokenOut(

    String token

) {
    
}
