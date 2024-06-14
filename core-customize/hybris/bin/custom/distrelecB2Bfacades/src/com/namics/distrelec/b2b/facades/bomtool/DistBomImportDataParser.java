package com.namics.distrelec.b2b.facades.bomtool;

import com.namics.distrelec.b2b.facades.bomtool.exception.BomToolFacadeException;
import de.hybris.platform.core.model.media.MediaModel;

import java.util.List;

public interface DistBomImportDataParser {

    List<String[]> parseFromString(String data, int bomToolRows, int quantityPosition, int referencePosition) throws BomToolFacadeException;
    List<String[]> parseFromFile(MediaModel file, int articleNumberPosition, int quantityPosition, Integer referencePosition, boolean ignoreFirstRow) throws BomToolFacadeException;

    List<String[]> getLinesFromFile(MediaModel file) throws BomToolFacadeException;
}
