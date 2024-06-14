package com.namics.distrelec.patches.actions;

import de.hybris.platform.patches.Patch;
import de.hybris.platform.patches.actions.AbstractImportPatchAction;
import de.hybris.platform.patches.actions.PatchAction;
import de.hybris.platform.patches.actions.data.PatchActionData;
import de.hybris.platform.patches.actions.data.PatchActionDataOption;
import de.hybris.platform.patches.internal.logger.PatchLogger;
import de.hybris.platform.patches.internal.logger.PatchLogger.LoggingMode;
import de.hybris.platform.patches.internal.logger.PatchLoggerFactory;
import de.hybris.platform.scripting.engine.ScriptExecutable;
import de.hybris.platform.scripting.engine.ScriptingLanguagesService;
import de.hybris.platform.scripting.engine.content.ScriptContent;
import de.hybris.platform.scripting.engine.content.impl.SimpleScriptContent;
import de.hybris.platform.tx.Transaction;
import de.hybris.platform.tx.TransactionBody;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

public class GroovyPatchAction extends AbstractImportPatchAction implements PatchAction {

    private static final PatchLogger LOG = PatchLoggerFactory.getLogger(GroovyPatchAction.class);

    @Autowired
    private ScriptingLanguagesService scriptingLanguagesService;

    @Override
    public void perform(PatchActionData patchActionData) {
        String fileName = getFileName(patchActionData);

        LOG.info(LoggingMode.HAC_CONSOLE, "Executing import: {}...", fileName);
        if (!isValidGroovyFileSuffix(fileName)) {
            LOG.error(LoggingMode.CONSOLE, "Incorrect suffix for file: '{}'", fileName);
            LOG.error(LoggingMode.HAC, "Executing import: {} failed", fileName);
        } else {
            LOG.info(LoggingMode.CONSOLE, "Execution groovy script: {}", fileName);
            Patch patch = patchActionData.getPatch();
            String basePath = getBasePath(patch);
            String globalPath = getGlobalPath();

            String filePath = basePath + globalPath + "/" + fileName;

            String script = null;

            URL resource = this.getClass().getResource(filePath);
            try (InputStream inputStream = resource.openStream()) {
                script = IOUtils.toString(inputStream);
            } catch (IOException e) {
                LOG.error(LoggingMode.HAC_CONSOLE, String.format("Unable to import groovy script: %s ", fileName), e);
            }

            if (isNotBlank(script)) {
                ScriptContent scriptContent = new SimpleScriptContent("groovy", script);
                ScriptExecutable executable = scriptingLanguagesService.getExecutableByContent(scriptContent);
                try {
                    Transaction.current().execute(new TransactionBody() {
                        public Object execute() throws Exception {
                            executable.execute();
                            return null;
                        }
                    });
                } catch (Exception e) {
                    LOG.error(LoggingMode.HAC_CONSOLE, String.format("Executing groovy: %s failed", fileName), e);
                }
            }
        }
    }

    protected boolean isValidGroovyFileSuffix(String filePath) {
        return StringUtils.endsWith(filePath, ".groovy");
    }


    protected String getFileName(PatchActionData data) {
        return data.getStringOption(PatchActionDataOption.Impex.FILE_NAME);
    }

    public ScriptingLanguagesService getScriptingLanguagesService() {
        return scriptingLanguagesService;
    }

    public void setScriptingLanguagesService(ScriptingLanguagesService scriptingLanguagesService) {
        this.scriptingLanguagesService = scriptingLanguagesService;
    }
}
