/*
 * [y] hybris Platform
 *
 * Copyright (c) 2000-2012 hybris AG
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of hybris
 * ("Confidential Information"). You shall not disclose such Confidential
 * Information and shall use it only in accordance with the terms of the
 * license agreement you entered into with hybris.
 *
 *
 */
package com.namics.distrelec.b2b.storefront.controllers.util;

import com.namics.distrelec.b2b.core.constants.DistConstants;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

/**
 * Displays "confirmation, information, error" messages.
 */
public class GlobalMessages {
    public static final String CONF_MESSAGES_HOLDER = "accConfMsgs";
    public static final String INFO_MESSAGES_HOLDER = "accInfoMsgs";
    public static final String WARN_MESSAGES_HOLDER = "accWarnMsgs";
    public static final String ERROR_MESSAGES_HOLDER = "accErrorMsgs";
    public static final String ARGUMENTS_HOLDER = "accArguments";
    public static final String ARGUMENT_SEPARATOR_HOLDER = "accArgumentSeparator";

    /**
     * Adds confirmation message to the model
     *
     * @param model
     * @param messageKey
     */
    public static void addConfMessage(final Model model, final String messageKey) {
        addMessage(model, CONF_MESSAGES_HOLDER, messageKey, null, null);
    }

    /**
     * Adds confirmation message to the model
     *
     * @param model
     * @param messageKey
     * @param arguments  the arguments in the sames order as it is supposed to be filled in by spring.
     */
    public static void addConfMessage(final Model model, final String messageKey, final String[] arguments) {
        addMessage(model, CONF_MESSAGES_HOLDER, messageKey, arguments, null);
    }

    /**
     * Adds info message to the model
     *
     * @param model
     * @param messageKey
     */
    public static void addInfoMessage(final Model model, final String messageKey) {
        addMessage(model, INFO_MESSAGES_HOLDER, messageKey, null, null);
    }

    /**
     * Adds info message to the model
     *
     * @param model
     * @param messageKey
     * @param arguments  the arguments in the sames order as it is supposed to be filled in by spring.
     */
    public static void addInfoMessage(final Model model, final String messageKey, final String[] arguments) {
        addMessage(model, INFO_MESSAGES_HOLDER, messageKey, arguments, null);
    }

    /**
     * Adds warning message to the model
     *
     * @param model
     * @param messageKey
     */
    public static void addWarnMessage(final Model model, final String messageKey) {
        addWarnMessage(model, messageKey, null);
    }

    /**
     * Adds warning message to the model
     *
     * @param model
     * @param messageKey
     * @param arguments  the arguments in the same order as it is supposed to be filled in by spring.
     */
    public static void addWarnMessage(final Model model, final String messageKey, final String[] arguments) {
        addMessage(model, WARN_MESSAGES_HOLDER, messageKey, arguments, null);
    }

    /**
     * Adds error message to the model
     *
     * @param model
     * @param messageKey
     */
    public static void addErrorMessage(final Model model, final String messageKey) {
        addMessage(model, ERROR_MESSAGES_HOLDER, messageKey, null, null);
    }

    /**
     * Adds error message to the model
     *
     * @param model
     * @param messageKey
     * @param arguments  the arguments in the sames order as it is supposed to be filled in by spring.
     */
    public static void addErrorMessage(final Model model, final String messageKey, final String[] arguments) {
        addMessage(model, ERROR_MESSAGES_HOLDER, messageKey, arguments, null);
    }

    /**
     * Adds error message to the model
     *
     * @param model
     * @param messageKey
     * @param arguments the arguments in the sames order as it is supposed to be filled in by spring.
     * @param argumentSeparator the separator character to be used for splitting the arguments string value, default is 'comma' (',').
     */
    public static void addErrorMessage(final Model model, final String messageKey, final String[] arguments, final String argumentSeparator) {
        addMessage(model, ERROR_MESSAGES_HOLDER, messageKey, arguments, argumentSeparator);
    }

    public static void addRedirectErrorMessage(RedirectAttributes redirectAttributes, final String messageKey, final String... arguments) {
        addRedirectMessage(redirectAttributes, ERROR_MESSAGES_HOLDER, messageKey, arguments);
    }

    public static boolean hasErrorMessage(final Model model) {
        return model.containsAttribute(ERROR_MESSAGES_HOLDER);
    }

    protected static void addMessage(final Model model, final String messageHolder, final String messageKey, final String[] arguments, String argumentSeparator) {
        final Map<String, Object> modelMap = model.asMap();
        if (model.containsAttribute(messageHolder)) {
            final List<String> messageKeys = null != modelMap.get(messageHolder) ? new ArrayList<>((List<String>) modelMap.get(messageHolder)) : new ArrayList<>();
            messageKeys.add(messageKey);
            model.addAttribute(messageHolder, messageKeys);
        } else {
            model.addAttribute(messageHolder, Collections.singletonList(messageKey));
        }
        if (ArrayUtils.isNotEmpty(arguments)) {
            Map<String, String> parameters = null != modelMap.get(ARGUMENTS_HOLDER) ? new HashMap<>((Map<String, String>) modelMap.get(ARGUMENTS_HOLDER)) : new HashMap<>();
            String argSeparator = argumentSeparator != null ? argumentSeparator : DistConstants.Punctuation.COMMA;
            parameters.put(messageKey, StringUtils.join(arguments, argSeparator));
            model.addAttribute(ARGUMENTS_HOLDER, parameters);
            model.addAttribute(ARGUMENT_SEPARATOR_HOLDER, argSeparator);
        }
    }

    protected static void addRedirectMessage(final RedirectAttributes redirectAttributes, final String messageHolder, final String messageKey, final String[] arguments) {
        redirectAttributes.addFlashAttribute(messageHolder, Collections.singletonList(messageKey));

        if (ArrayUtils.isNotEmpty(arguments)) {
            redirectAttributes.addFlashAttribute(ARGUMENTS_HOLDER, Map.of(messageKey, StringUtils.join(arguments, DistConstants.Punctuation.COMMA)));
        }
    }
}
