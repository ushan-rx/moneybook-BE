package com.moneybook;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import com.moneybook.config.TestRedisConfig;
import org.springframework.context.annotation.Import;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestRedisConfig.class)
class MoneybookApplicationTests {

    @Test
    void contextLoads() {
    }

}
