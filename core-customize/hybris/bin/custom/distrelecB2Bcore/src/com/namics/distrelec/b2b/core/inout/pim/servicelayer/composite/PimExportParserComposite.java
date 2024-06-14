package com.namics.distrelec.b2b.core.inout.pim.servicelayer.composite;

import com.namics.distrelec.b2b.core.inout.pim.PimExportParser;
import com.namics.distrelec.b2b.core.inout.pim.servicelayer.ImportContext;
import org.apache.commons.collections4.list.UnmodifiableList;
import org.springframework.beans.factory.annotation.Required;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.Comparator.comparing;

public class PimExportParserComposite {

    private List<AbstractCompositeEntry> entries;

    public ParseResult parse(File file) {
        if (file == null) {
            throw new IllegalArgumentException("File is null!");
        }

        PimExportParser<? extends ImportContext> parser = findParser(file);

        return parser.parseFile(file);
    }

    private PimExportParser<? extends ImportContext> findParser(File file) {
        return entries.stream()
                      .filter(e -> e.isSupported(file))
                      .min(comparing(AbstractCompositeEntry::getOrder))
                      .map(AbstractCompositeEntry::getParser)
                      .orElseThrow(() -> new IllegalStateException(String.format("No parser could be determined for this file[%s]!", file.getName())));
    }

    private void validateEntries(List<AbstractCompositeEntry> entries) {
        if (entries == null) {
            throw new IllegalArgumentException("Entries are null!");
        }
        checkUniqueOrder(entries);
    }

    private void checkUniqueOrder(List<AbstractCompositeEntry> entries) {
        Set<Integer> unique = new HashSet<>();
        entries.forEach(e -> {
            if (!unique.add(e.getOrder())) {
                throw new IllegalStateException(String.format("Order value [%d] is not unique across all entries!", e.getOrder()));
            }
        });
    }

    private List<AbstractCompositeEntry> makeUnmodifiable(List<AbstractCompositeEntry> entries) {
        if (!(entries instanceof UnmodifiableList)) {
            entries = new UnmodifiableList<>(entries);
        }
        return entries;
    }

    @Required
    public void setEntries(List<AbstractCompositeEntry> entries) {
        validateEntries(entries);
        this.entries = makeUnmodifiable(entries);
    }
}
