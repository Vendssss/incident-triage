package com.vendiola.triage;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "triage.jwt.secret=dGVzdC1vbmx5LXNpZ25pbmcta2V5LW5vdC1mb3ItcHJvZHVjdGlvbi11c2U=")
class IncidentTriageApplicationTests {

	@Test
	void contextLoads() {
	}

}
