package no.nav.pensjon.opptjening.afp.api.config

import com.nimbusds.jwt.JWTParser
import no.nav.pensjon.opptjening.afp.api.api.TestTokenIssuer
import no.nav.security.token.support.spring.test.EnableMockOAuth2Server
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@EnableMockOAuth2Server
class JwtTokenIssuerSpecificConsumerResolverTest {

    @Autowired
    private lateinit var tokenIssuer: TestTokenIssuer

    @Autowired
    private lateinit var resolver: JwtTokenIssuerSpecificConsumerResolver

    @Test
    fun `identifiserer app ved azp_name for azure tokens`() {
        val jwt = JWTParser.parse(tokenIssuer.token(issuerId = TokenScopeConfig.AZURE_CONFIG_ALIAS))

        assertThat(resolver.resolve(jwt)).isEqualTo("name-of-azure-app-ish")
    }

    @Test
    fun `identifiserer app ved consumer+id for maskinporten tokens`() {
        val jwt = JWTParser.parse(tokenIssuer.token(issuerId = TokenScopeConfig.MASKINPORTEN_CONFIG_ALIAS))

        assertThat(resolver.resolve(jwt)).isEqualTo("0192:consumer-org-no")
    }
}