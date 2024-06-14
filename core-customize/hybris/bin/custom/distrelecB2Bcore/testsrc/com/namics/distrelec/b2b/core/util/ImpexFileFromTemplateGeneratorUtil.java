/*
 * Copyright 2000-2017 Elfa Distrelec. All rights reserved.
 */

package com.namics.distrelec.b2b.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author nshandilya, Elfa Distrelec
 * @since Distrelec v4.14.1 Purpose of this Utility is to generate one single impex file from a given template. Typical use case is when you
 *        have to generate impex file for cms stuff for all ContentCatelog. This tool is helpful by replace all XX character with predefined
 *        list of ContentCatelog. Below is a sample template file for example.
 */
/**
 * ############################################################################################ # Category Page for XX ContentCatalog
 * ############################################################################################
 * 
 * $contentCatalog=distrelec_XXContentCatalog $contentCV=catalogVersion(catalog(id[default=$contentCatalog]),version[default='Staged'])
 * $jarResourceCms=jar:com.namics.distrelec.b2b.core.setup.CoreSystemSetup&/distrelecB2Bcore/import/project/cockpits/cmscockpit
 * 
 * 
 * INSERT_UPDATE
 * CategoryPage;$contentCV[unique=true];uid[unique=true];name;masterTemplate(uid,$contentCV);defaultPage;approvalStatus(code)[default='
 * approved'] ;;category;Default Category Page;CategoryPageTemplate;true;
 * 
 * 
 * INSERT_UPDATE ContentSlot;$contentCV[unique=true];uid[unique=true];name;active ;;FF-Carousel-Top-CategoryPage;FF Top Carousel Content
 * Slot for CategoryPage;true ;;FF-Campaign-Slot-CategoryPage;FF campaign Content Slot for CategoryPage;true
 * ;;FF-Carousel-Bottom-CategoryPage;FF Bottom Carousel Content Slot for CategoryPage;true
 * 
 * 
 * INSERT_UPDATE
 * ContentSlotForPage;$contentCV[unique=true];uid[unique=true];position[unique=true];page(uid,$contentCV)[unique=true][default='category'];
 * contentSlot(uid,$contentCV)[unique=true] ;;FF-Carousel-Top-CategoryPage;ContentFFTopCarousel;;FF-Carousel-Top-CategoryPage
 * ;;FF-Campaign-Slot-CategoryPage;ContentCampaignSlot;;FF-Campaign-Slot-CategoryPage
 * ;;FF-Carousel-Bottom-CategoryPage;ContentFFBottomCarousel;;FF-Carousel-Bottom-CategoryPage
 */
public class ImpexFileFromTemplateGeneratorUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ImpexFileFromTemplateGeneratorUtil.class);

    /**
     * change this path where you template file is kept in you local system
     */
    private static final String FILENAME = "/tmp/Component.impex";

    public static void main(String[] args) {

        BufferedReader br = null;
        FileReader fr = null;

        try {

            fr = new FileReader(FILENAME);
            br = new BufferedReader(fr);
            String sCurrentLine;
            br = new BufferedReader(new FileReader(FILENAME));
            String aString = "";
            while ((sCurrentLine = br.readLine()) != null) {
                aString += sCurrentLine + "\n";
            }
            String array[] = { "AT", "BE", "CH", "CZ", "DE", "DK", "EE", "EX", "FI", "HU", "IT", "LT", "LV", "NL", "NO", "PL", "RO", "SE", "SK", "TR" };
            for (String arr : array) {
                write(FILENAME, aString.replace("XX", arr));
                LOG.info("done for " + arr);
            }
            LOG.info("check generated file on location :" + FILENAME);
        } catch (IOException e) {
            LOG.warn("Exception occurred while reading", e);
        } finally {
            try {
                if (br != null)
                    br.close();

                if (fr != null)
                    fr.close();
            } catch (IOException ex) {
                LOG.warn("Exception occurred while closing", ex);
            }
        }
    }

    public static void write(String FILENAME, String content) {
        BufferedWriter bw = null;
        FileWriter fw = null;

        try {
            fw = new FileWriter(FILENAME, true);
            bw = new BufferedWriter(fw);
            bw.write(content);
        } catch (IOException e) {
            LOG.warn("Exception occurred while writing", e);
        } finally {
            try {
                if (bw != null)
                    bw.close();
                if (fw != null)
                    fw.close();
            } catch (IOException ex) {
                LOG.warn("Exception occurred while closing", ex);
            }
        }
    }
}
