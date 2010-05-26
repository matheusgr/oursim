import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import oursim.OurSimAPIReplicationTest;
import oursim.OurSimAPITest;
import oursim.OurSimAPIVolatilityTest;
import oursim.entities.ProcessorTest;
import oursim.entities.TaskTest;
import oursim.policy.NoFSharingPolicyTest;

@RunWith(Suite.class)
@SuiteClasses( { ProcessorTest.class, TaskTest.class, NoFSharingPolicyTest.class, OurSimAPITest.class, OurSimAPIVolatilityTest.class,
		OurSimAPIReplicationTest.class, })
public class TestSuite {

	static class Compatibility {

		static Test suite() throws IOException {
			return new JUnit4TestAdapter(TestSuite.class);
		}
	}

}