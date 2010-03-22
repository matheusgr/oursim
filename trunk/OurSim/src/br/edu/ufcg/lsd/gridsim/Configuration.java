package br.edu.ufcg.lsd.gridsim;

public class Configuration {

	private static Configuration instance;

	public static synchronized Configuration getInstance() {
		if (instance == null) {
			instance = new Configuration();
		}
		return instance;
	}

	private boolean useGLiteAsPeer = false;
	private boolean useOGAsCluster = false;
	private boolean useGateway = false;
	private boolean checkpoint = true;
        private boolean useNoF = true;
	private boolean replication;
	
	private Configuration() {
		
	}

	public void setUseGLiteAsPeer(boolean useGLiteAsPeer) {
		this.useGLiteAsPeer = useGLiteAsPeer;
	}
	
	public boolean useGLiteAsPeer() {
		return useGLiteAsPeer;
	}

	public void setUseOGAsCluster(boolean useOGAsCluster) {
		this.useOGAsCluster = useOGAsCluster;
	}
	
	public boolean useOGAsCluster() {
		return useOGAsCluster;
	}

	public void setUseGateway(boolean useGateway) {
		this.useGateway = useGateway;
	}
	
	public boolean useGateway() {
		return this.useGateway;
	}

        public void setUseNoF(boolean useNoF) {
                this.useNoF = useNoF;
        }

        public boolean useNoF() {
                return this.useNoF;
        }

	public void setReplication(boolean replication) {
		this.replication = replication;
	}
	
	public boolean getReplication() {
		return this.replication;
	}
	
	public void setCheckpoint(boolean checkpoint) {
		this.checkpoint = checkpoint;
	}
	
	public boolean checkpointEnabled() {
		return this.checkpoint;
	}
	
}
