package com.uamishop.gateway;

import com.intuit.karate.junit5.Karate;

class KarateTests {

    @Karate.Test
    Karate testAll() {
        return Karate.run("classpath:karate/features").relativeTo(getClass());
    }
}
