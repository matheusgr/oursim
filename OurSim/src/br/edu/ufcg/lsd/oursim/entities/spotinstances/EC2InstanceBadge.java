package br.edu.ufcg.lsd.oursim.entities.spotinstances;

import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;

public class EC2InstanceBadge {

	public String region;
	
	public String so;
	
	public double price;

	@Override
	public String toString() {
		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).append("region", region).append("so", so).append("price", price).toString();
	}
	
}
