/*
 * The MIT License
 *
 * Copyright 2020 Sunil.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.ssamant.utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Sunil
 */
public class GetPropertyFileKeyValues {

    public static String getSshKeyLocation() {
        Properties prop = null;
        String sshKeyLoc = "";
        try {
            prop = PropertyFileReader.readPropertyFile();
            sshKeyLoc = prop.getProperty("com.ssamant.utilities.ssh.keylocation");
        } catch (Exception ex) {
            Logger.getLogger(GetPropertyFileKeyValues.class.getName()).log(Level.SEVERE, null, ex);
        }

        return sshKeyLoc;
    }

    public static String getSshKeyName() {
        Properties prop = null;
        String sshKeyName = "";
        try {
            prop = PropertyFileReader.readPropertyFile();
            sshKeyName = prop.getProperty("com.ssamant.utilities.ssh.key");
        } catch (Exception ex) {
            Logger.getLogger(GetPropertyFileKeyValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sshKeyName;
    }

    public static List<String> getSecurityGroups() {
        Properties prop = null;
        String securtiyGroups = "";
        String[] secGroups = null;
        List<String> secGroupIds = new ArrayList<>();
        try {
            prop = PropertyFileReader.readPropertyFile();
            securtiyGroups = prop.getProperty("com.ssamant.utilities.securitygroups");

            secGroups = securtiyGroups.split(",");
            secGroupIds = Arrays.asList(secGroups);

        } catch (Exception ex) {
            Logger.getLogger(GetPropertyFileKeyValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return secGroupIds;
    }

    public static Float[] getInstancePrice() {
        Properties prop = null;
        String instancePrices = "";
        String[] instPrices = null;
        Float[] instPriceArray = null;
        try {
            prop = PropertyFileReader.readPropertyFile();
            instancePrices = prop.getProperty("com.ssamant.pocresourcemanagement.instanceprices");

            instPrices = instancePrices.split(",");
            instPriceArray = Arrays.stream(instPrices).map(Float::valueOf).toArray(Float[]::new);
            //secGroupIds = Arrays.asList(instPrices);

        } catch (Exception ex) {
            Logger.getLogger(GetPropertyFileKeyValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return instPriceArray;
    }

    public static int getDeltaMinAQoS() {
        Properties prop = null;
        String temp = "";
        try {
            prop = PropertyFileReader.readPropertyFile();
            temp = prop.getProperty("com.ssamant.pocresourcemanagement.deltaminA");
        } catch (Exception ex) {
            Logger.getLogger(GetPropertyFileKeyValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.parseInt(temp);
    }

    public static int getDeltaMinBQoS() {
        Properties prop = null;
        String temp = "";
        try {
            prop = PropertyFileReader.readPropertyFile();
            temp = prop.getProperty("com.ssamant.pocresourcemanagement.deltaminB");
        } catch (Exception ex) {
            Logger.getLogger(GetPropertyFileKeyValues.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Integer.parseInt(temp);
    }
}
