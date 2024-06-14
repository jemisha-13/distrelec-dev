package com.namics.distrelec.b2b.facades.bomtool;

import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.List;

public interface DistBomFileParser {

    List<String[]> createArrayFromCsv(final MediaModel file, final Integer referencePosition) throws BomToolFacadeException;

    List<String[]> createArrayFromXLS(final MediaModel file, final int sheetNumber, final Integer referencePosition)
            throws BomToolFacadeException;

    List<String[]> createArrayFromXLSX(final MediaModel file, final int sheetNumber, final Integer referencePosition)
            throws BomToolFacadeException;
}
