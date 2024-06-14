package com.namics.distrelec.occ.core.mapping.converters;

import static com.namics.distrelec.b2b.core.util.LocalDateUtil.convertDateToLocalDate;

import java.time.LocalDate;
import java.util.Date;

import de.hybris.platform.webservicescommons.mapping.WsDTOMapping;
import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.metadata.Type;

@WsDTOMapping
public class DistDateConverter extends CustomConverter<Date, LocalDate> {

    @Override
    public LocalDate convert(Date source, Type<? extends LocalDate> destinationType, MappingContext mappingContext) {
        return convertDateToLocalDate(source);
    }
}
