import java.io.IOException;

import junit.framework.JUnit4TestAdapter;
import junit.framework.Test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import oursim.OurGridPersistentSchedulerTest;
import oursim.OurGridReplicationSchedulerTest;
import oursim.OurSimAPIVolatilityTest;
import oursim.entities.ProcessorTest;
import oursim.entities.TaskTest;
import oursim.policy.NoFSharingPolicyTest;

@RunWith(Suite.class)
@SuiteClasses( { ProcessorTest.class, TaskTest.class, NoFSharingPolicyTest.class, OurGridPersistentSchedulerTest.class, OurSimAPIVolatilityTest.class,
	OurGridReplicationSchedulerTest.class, })
public class TestSuite {

	static class Compatibility {

		static Test suite() throws IOException {
			return new JUnit4TestAdapter(TestSuite.class);
		}
	}

}