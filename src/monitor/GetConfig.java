/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package monitor;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 *
 * @author Administrator
 */
public class GetConfig {

    protected static ResourceBundle rb;

    /*
    static {
        String proFilePath = System.getProperty("user.dir") + "/config/monitor.properties";
        try {
            inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
            rb = new PropertyResourceBundle(inputStream);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */
    public static ResourceBundle getfile(String path) {

        String proFilePath = path + "monitor.properties";
        BufferedInputStream inputStream = null;
        try {
            inputStream = new BufferedInputStream(new FileInputStream(proFilePath));
            rb = new PropertyResourceBundle(inputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return rb;
    }
}
