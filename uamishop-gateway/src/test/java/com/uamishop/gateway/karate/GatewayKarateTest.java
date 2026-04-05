package com.uamishop.gateway.karate;

import com.intuit.karate.junit5.Karate;

class GatewayKarateTest {

    @Karate.Test
    Karate testGateway() {
        return Karate.run("classpath:karate/gateway.feature");
    }
}
