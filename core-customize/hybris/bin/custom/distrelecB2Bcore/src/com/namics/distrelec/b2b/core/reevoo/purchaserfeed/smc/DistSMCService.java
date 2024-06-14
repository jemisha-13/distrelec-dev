/**
 * 
 */
package com.namics.distrelec.b2b.core.reevoo.purchaserfeed.smc;

/**
 * @author datpatilaj
 *
 */
public interface DistSMCService {
	
	boolean isUserSubscribedToEmail(String email);
	
	boolean isUserSubscribedToSurvey(String email);
}
