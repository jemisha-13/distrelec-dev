package com.namics.distrelec.b2b.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
/**
 * 
 * @author datneerajs
 * use this programe when you have 
 * File like this which you want to 
 * /environment/release-notes/v5.6.0/src/post_installation/script_XX.impex
 *
 */
public class DistGenerateimpexForAllSitesUtil {

    private static final Logger LOG = LoggerFactory.getLogger(DistGenerateimpexForAllSitesUtil.class);

	private static String infile = "D:\\tmp\\branch\\environment\\release-notes\\v5.7.2\\src\\post_installation\\tabed_cms_component_XX.impex";
	private static String outfile = "D:\\tmp\\branch\\environment\\release-notes\\v5.7.2\\src\\post_installation\\tabed_cms_component_XX.out.impex";
	
	public static void main(final String[] args) {

        String list[] = { "AT", "BE", "CH", "CZ", "DE", "DK", "EE", "EX", "FI", "HU", "IT", "LT", "LV", "NL", "NO", "PL", "RO", "SE", "SK", "TR" };
        for (String site : list) {
            try {
                File file = new File(infile);
                FileReader fileReader = new FileReader(file);
                BufferedReader bufferedReader = new BufferedReader(fileReader);
                StringBuffer stringBuffer = new StringBuffer();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line.replace("XX", site));
                    stringBuffer.append("\n");
                }
                fileReader.close();

                File wrfile = new File(outfile);

                // if file doesnt exists, then create it
                if (!file.exists()) {
                    file.createNewFile();
                }

                FileWriter fw = new FileWriter(wrfile.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(stringBuffer.toString());
                bw.close();

                LOG.info("Contents of file: {}", stringBuffer.toString());
            } catch (IOException e) {
                LOG.warn("Exception occurred during impex generation", e);
            }
        }
    }

}
