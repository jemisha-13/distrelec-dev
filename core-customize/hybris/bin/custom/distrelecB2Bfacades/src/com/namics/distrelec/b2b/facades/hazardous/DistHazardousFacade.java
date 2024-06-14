package com.namics.distrelec.b2b.facades.hazardous;

import com.namics.distrelec.b2b.facades.hazard.data.HazardStatementData;
import com.namics.distrelec.b2b.facades.hazard.data.SupplementalHazardInfoData;

import java.util.List;

public interface DistHazardousFacade {

	public List<HazardStatementData> getAllDistHazardStatement();

	public List<SupplementalHazardInfoData> getAllDistSupplementalHazardInfo();

}
