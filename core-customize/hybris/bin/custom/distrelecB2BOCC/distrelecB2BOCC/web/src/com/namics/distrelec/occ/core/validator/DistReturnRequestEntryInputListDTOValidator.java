package com.namics.distrelec.occ.core.validator;


import com.namics.distrelec.occ.core.order.ws.dto.DistReturnRequestEntryInputListWsDTO;
import com.namics.distrelec.occ.core.order.ws.dto.DistReturnRequestEntryInputWsDTO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

public class DistReturnRequestEntryInputListDTOValidator implements Validator {
    private static final String FIELD_REQUIRED_MESSAGE_ID = "field.required";

    @Override
    public boolean supports(final Class clazz) {
        return DistReturnRequestEntryInputListWsDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(final Object target, final Errors errors) {
        final DistReturnRequestEntryInputListWsDTO returnRequestEntryInputList = (DistReturnRequestEntryInputListWsDTO) target;

        if (StringUtils.isEmpty(returnRequestEntryInputList.getOrderCode())) {
            errors.rejectValue("orderCode", FIELD_REQUIRED_MESSAGE_ID);
        }

        final List<DistReturnRequestEntryInputWsDTO> returnRequestEntryInputs = returnRequestEntryInputList.getReturnRequestEntryInputs();

        if (CollectionUtils.isEmpty(returnRequestEntryInputs)) {
            errors.rejectValue("returnRequestEntryInputs", FIELD_REQUIRED_MESSAGE_ID);
        } else {
            IntStream.range(0, returnRequestEntryInputs.size()).filter(i -> Objects.isNull(returnRequestEntryInputs.get(i).getOrderEntryNumber()))
                    .forEach(i -> errors.rejectValue(String.format("returnRequestEntryInputs[%d].orderEntryNumber", i), FIELD_REQUIRED_MESSAGE_ID));

            IntStream.range(0, returnRequestEntryInputs.size()).filter(i -> Objects.isNull(returnRequestEntryInputs.get(i).getQuantity()))
                    .forEach(i -> errors.rejectValue(String.format("returnRequestEntryInputs[%d].quantity", i), FIELD_REQUIRED_MESSAGE_ID));
        }
    }
}
