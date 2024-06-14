package com.namics.distrelec.occ.core.v2.controller;

import static org.apache.commons.lang.StringUtils.startsWith;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.ok;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.namics.distrelec.b2b.core.model.DistManufacturerModel;
import com.namics.distrelec.b2b.core.service.manufacturer.DistManufacturerService;
import com.namics.distrelec.b2b.facades.manufacturer.DistManufacturerFacade;
import com.namics.distrelec.b2b.facades.manufacturer.converters.DistManufacturerConverter;
import com.namics.distrelec.b2b.facades.manufacturer.data.DistManufacturerData;
import com.namics.distrelec.b2b.facades.product.DistrelecProductFacade;
import com.namics.distrelec.occ.core.readonly.ReadOnly;
import com.namics.distrelec.occ.core.security.SecuredAccessConstants;

import de.hybris.platform.commerceservices.request.mapping.annotation.ApiVersion;
import de.hybris.platform.commercewebservicescommons.dto.manufacturer.DistManufacturerWsDTO;
import de.hybris.platform.webservicescommons.swagger.ApiBaseSiteIdParam;
import de.hybris.platform.webservicescommons.swagger.ApiFieldsParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@Controller
@RequestMapping(value = "/{baseSiteId}/manufacturer")
@Tag(name = "Manufacturer Store Details")
@ApiVersion("v2")
public class DistrelecManufacturerStoreDetailsController extends BaseController {

    protected static final Logger LOG = LogManager.getLogger(DistrelecManufacturerStoreDetailsController.class);

    @Autowired
    private DistManufacturerService distManufacturerService;

    @Autowired
    private DistManufacturerFacade distManufacturerFacade;

    @Autowired
    private DistManufacturerConverter distManufacturerConverter;

    @Autowired
    private DistrelecProductFacade productFacade;

    @ReadOnly
    @Secured({ SecuredAccessConstants.ROLE_CUSTOMERGROUP, SecuredAccessConstants.ROLE_TRUSTED_CLIENT,
               SecuredAccessConstants.ROLE_CLIENT, SecuredAccessConstants.ROLE_ANONYMOUS })
    @GetMapping(value = "/{manufacturerCode:.*}")
    @ResponseBody
    @Operation(operationId = "getManufacturerStoreDetails", summary = "Retrieve manufacturer details", description = "Returns the details about the manufacturer")
    @ApiBaseSiteIdParam
    public ResponseEntity<DistManufacturerWsDTO> getManufacturerStoreDetails(@Parameter(description = "Manufacturer code", required = true) @PathVariable("manufacturerCode") final String manufacturerCode,
                                                                             @ApiFieldsParam @RequestParam(defaultValue = DEFAULT_FIELD_SET) final String fields) {
        if (productFacade.enablePunchoutFilterLogic() && distManufacturerFacade.isManufacturerExcluded(manufacturerCode)) {
            return badRequest().build();
        }

        DistManufacturerModel manufacturerModel = distManufacturerService.getManufacturerByCode(getCleanManufacturerCode(manufacturerCode));
        DistManufacturerData manufacturerData = distManufacturerConverter.convert(manufacturerModel);
        return ok(getDataMapper().map(manufacturerData, DistManufacturerWsDTO.class, fields));
    }

    private String getCleanManufacturerCode(String manufacturerCode) {
        return startsWith(manufacturerCode, "man_") ? manufacturerCode : "man_" + manufacturerCode;
    }

}
