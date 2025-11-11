package com.crud.backend.google;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class GoogleDTO {
    private String googleId;
    private String name;
    private String givenName;
    private String familyName;
    private String email;
    private String picture;
    private String locale;

    public GoogleUserDTO(OAuth2User principal) {
        this.googleId = principal.getAttribute("sub");
        this.name = principal.getAttribute("name");
        this.givenName = principal.getAttribute("given_name");
        this.familyName = principal.getAttribute("family_name");
        this.email = principal.getAttribute("email");
        this.picture = principal.getAttribute("picture");
        this.locale = principal.getAttribute("locale");
    }
}
