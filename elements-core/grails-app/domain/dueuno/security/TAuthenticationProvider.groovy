package dueuno.security

class TAuthenticationProvider {
    Long id

    String name
    AuthenticationProviderType type
    Integer sequence
    Boolean enabled

    // LDAP
    String ldapUrl
    String ldapBaseDn
    String ldapUserDnPattern
    String ldapManagerDn
    String ldapManagerPassword
    String ldapGroupSearchBase
    String ldapSearchFilter

    // OAuth2
    String clientId
    String clientSecret
    String authorizationUri
    String tokenUri
    String userInfoUri
    String redirectUri
    String scope

    static constraints = {
        name nullable: false, blank: false, unique: true
        type nullable: false
        sequence nullable: false
        enabled nullable: false

        // LDAP
        ldapUrl nullable: true
        ldapBaseDn nullable: true
        ldapUserDnPattern nullable: true
        ldapManagerDn nullable: true
        ldapManagerPassword nullable: true
        ldapGroupSearchBase nullable: true
        ldapSearchFilter nullable: true

        // OAuth2
        clientId nullable: true
        clientSecret nullable: true
        authorizationUri nullable: true
        tokenUri nullable: true
        userInfoUri nullable: true
        redirectUri nullable: true
        scope nullable: true
    }
}
