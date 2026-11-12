import flexygames.DeveloperDataSeeder
import flexygames.ShiroWildcardDbRealm
import org.apache.shiro.authc.credential.HashedCredentialsMatcher
import org.apache.shiro.crypto.hash.Sha512Hash
import org.apache.shiro.web.mgt.DefaultWebSecurityManager

beans = {
    credentialsMatcher(HashedCredentialsMatcher) {
        hashAlgorithmName = Sha512Hash.ALGORITHM_NAME
        storedCredentialsHexEncoded = true
    }
    
    shiroRealm(ShiroWildcardDbRealm) {
        credentialMatcher = ref('credentialsMatcher')
    }
    
    shiroSecurityManager(DefaultWebSecurityManager) {
        realms = [ref('shiroRealm')]
    }
    
    databaseSeeder(DeveloperDataSeeder)
}
