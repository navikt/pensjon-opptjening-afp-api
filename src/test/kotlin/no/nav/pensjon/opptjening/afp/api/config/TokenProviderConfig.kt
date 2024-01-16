package no.nav.pensjon.opptjening.afp.api.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import pensjon.opptjening.azure.ad.client.TokenProvider
import pensjon.opptjening.azure.ad.client.mock.MockTokenProvider

@Configuration
class TokenProviderConfig {

    @Bean("poppTokenProvider")
    fun tokenProvider(): TokenProvider = MockTokenProvider(MOCK_TOKEN)

    @Bean("pdlTokenProvider")
    fun pdlTokenProvider(): TokenProvider = MockTokenProvider(MOCK_TOKEN)

    companion object {
        const val MOCK_TOKEN = "test.token.test"
    }
}