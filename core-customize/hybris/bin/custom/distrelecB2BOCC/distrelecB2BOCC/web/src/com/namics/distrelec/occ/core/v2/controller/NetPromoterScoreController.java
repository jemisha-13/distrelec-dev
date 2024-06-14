/**
 *
 */
package com.namics.distrelec.occ.core.v2.controller;

import com.namics.distrelec.occ.core.swagger.CommonQueryParams;
import de.hybris.platform.commercewebservicescommons.dto.order.NpsResponseWsDTO;
import de.hybris.platform.enumeration.EnumerationService;
import de.hybris.platform.servicelayer.exceptions.ModelNotFoundException;
import de.hybris.platform.util.localization.Localization;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.enums.NPSReason;
import com.namics.distrelec.b2b.core.enums.NPSSubReason;
import com.namics.distrelec.b2b.core.enums.NPSType;
import com.namics.distrelec.b2b.core.service.nps.data.DistNetPromoterScoreData;
import com.namics.distrelec.b2b.facades.constants.WebConstants;
import com.namics.distrelec.b2b.facades.nps.DistNetPromoterScoreFacade;
import com.namics.distrelec.occ.core.exceptions.UnsupportedRequestException;
import com.namics.distrelec.occ.core.order.ws.dto.FeedbackNPSForm;
import com.namics.distrelec.occ.core.v2.helper.UsersHelper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;


/**
 * @author ajinkya.patil
 *
 */

@Controller
@RequestMapping("/{baseSiteId}/feedback")
@Tag(name = "NetPromoterScore")
@CommonQueryParams
public class NetPromoterScoreController
{

	private static final Logger LOG = LogManager.getLogger(NetPromoterScoreController.class);
	private static final String NPS_CMS_PAGE = "netPromoterScorePage";
	private static final String MODEL_KEY_HAS_ERRORS = "hasErrors";
	private static final String DELIVERY = "delivery";
	public static final String DATE_FORMAT = "MM/dd/yyyy";

	@Autowired
	private DistNetPromoterScoreFacade distNetPromoterScoreFacade;

	@Autowired
	private EnumerationService enumService;

	@Autowired
	private UsersHelper usersHelper;

	@RequestMapping(method = RequestMethod.POST)
	@Operation(operationId = "postNpsDetails", summary = "post NPS Details On Page Load", description = "Post NPS details on Page Load")
	@ApiBaseSiteIdParam
	@ResponseBody
	public NpsResponseWsDTO getNpsDetails(@Parameter(description = "NPS Form")
	@RequestBody
	final FeedbackNPSForm npsForm, @Parameter(description = "site identifier.", required = true)
	@PathVariable
	final String baseSiteId) throws UnsupportedRequestException
	{

		final Date lastNpsDate = getDistNetPromoterScoreFacade().getLastSubmittedNPSDate(npsForm.getEmail());

		if (lastNpsDate != null)
		{
			throw new UnsupportedRequestException(
					Localization.getLocalizedString("feedback.nps.duplicate") + ":" + lastNpsDate.toString());
		}
		else
		{
			createNPS(npsForm, baseSiteId);
		}

		final NpsResponseWsDTO response = new NpsResponseWsDTO();

		final List<String> reasons = Stream.of(NPSReason.values()).map(NPSReason::getCode).collect(Collectors.toList());
		final List<String> subReasons = Stream.of(NPSSubReason.values()).map(NPSSubReason::getCode).collect(Collectors.toList());
		response.setNpsFormReasons(reasons);
		response.setNpsFormSubReasons(subReasons);
		response.setValue(npsForm.getValue());
		response.setReason(npsForm.getReason());
		response.setSubreason(npsForm.getSubreason());
		response.setType(npsForm.getType());
		response.setFeedback(npsForm.getFeedback());
		response.setOrder(npsForm.getOrder());
		response.setEmail(npsForm.getEmail());
		response.setFname(npsForm.getFname());
		response.setNamn(npsForm.getNamn());
		response.setCompany(npsForm.getCompany());
		response.setCnumber(npsForm.getCnumber());
		response.setContactnum(npsForm.getContactnum());
		response.setDelivery(npsForm.getDelivery());
		response.setId(npsForm.getId());
		return response;

	}

	/***
	 * Creates the first pass of recording an initial NPS Score
	 *
	 * @param npsForm
	 */
	private void createNPS(final FeedbackNPSForm npsForm, final String baseSiteId)
	{
		final DistNetPromoterScoreData npsData = createNPSData(npsForm);
		getDistNetPromoterScoreFacade().createNPS(npsData);
		npsForm.setId(npsData.getCode());
	}

	/**
	 * Convert the NPS form to a NPS data object.
	 *
	 * @param form
	 *           the source object.
	 * @return a new instance of {@code DistNetPromoterScoreData}
	 */
	private DistNetPromoterScoreData createNPSData(final FeedbackNPSForm form)
	{
		final DistNetPromoterScoreData data = new DistNetPromoterScoreData();
		/* Setting the type */
		data.setType(Stream.of(NPSType.values()).filter(type -> type.getCode().equals(form.getType())).findFirst()
				.orElse(NPSType.ORDERCONFIRMATION));
		// Setting other fields
		data.setSalesOrg(usersHelper.getCurrentSalesOrg().getCode());
		data.setEmail(form.getEmail());
		data.setFirstname(form.getFname());
		data.setLastname(form.getNamn());
		data.setCompanyName(form.getCompany());
		data.setErpContactID(form.getContactnum());
		data.setErpCustomerID(form.getCnumber());
		data.setOrderNumber(form.getOrder());
		data.setDeliveryDate(form.getDelivery());
		try {
			final DistNetPromoterScoreData npsData = getDistNetPromoterScoreFacade().getNPSByCode(form.getId());
			data.setValue(null == npsData ? WebConstants.DEFAULT_STARTING_NPS_SCORE : form.getValue());
		}catch(ModelNotFoundException e)
		{
			data.setValue(WebConstants.DEFAULT_STARTING_NPS_SCORE);
		}
		data.setText(form.getFeedback());
		data.setCode(form.getId());
		return data;
	}

	@RequestMapping(value = "/submit", method = RequestMethod.POST)
	@Operation(operationId = "update NPS Details on form submit", summary = "Update NPS Details on form submit", description = "Update NPS details")
	@ApiBaseSiteIdParam
	@ResponseBody
	public String updateNPSDetails(@Parameter(description = "NPS Form")
	@RequestBody
	final FeedbackNPSForm form) throws UnsupportedRequestException
	{
		if (StringUtils.isEmpty(form.getId()))
		{
			throw new UnsupportedRequestException("Some inputs are not valid!");
		}
		final DistNetPromoterScoreData npsData = getDistNetPromoterScoreFacade().getNPSByCode(form.getId());
		if (npsData != null && npsData.getReason() != null)
		{
			return "NOK";
		}
		else
		{
			getDistNetPromoterScoreFacade().updateNPS(updateNPSData(form));

		}
		return "OK";

	}

	private DistNetPromoterScoreData updateNPSData(final FeedbackNPSForm form)
	{
		final DistNetPromoterScoreData data = createNPSData(form);

		// SET NPS REASON
		if (StringUtils.isNotBlank(form.getReason()))
		{
			try
			{
				data.setReason(enumService.getEnumerationValue(NPSReason.class, form.getReason()));
			}
			catch (final Exception exp)
			{
				LOG.warn("Unknown NPS Reason code", exp);
			}
		}

		if (StringUtils.isNotBlank(form.getSubreason()))
		{
			try
			{
				data.setSubreason(enumService.getEnumerationValue(NPSSubReason.class, form.getSubreason()));
			}
			catch (final Exception exp)
			{
				LOG.warn("Unknown NPS Sub Reason code", exp);
			}
		}
		return data;
	}

	// Getters & Setters

	public DistNetPromoterScoreFacade getDistNetPromoterScoreFacade()
	{
		return distNetPromoterScoreFacade;
	}

	public void setDistNetPromoterScoreFacade(final DistNetPromoterScoreFacade distNetPromoterScoreFacade)
	{
		this.distNetPromoterScoreFacade = distNetPromoterScoreFacade;
	}
}
