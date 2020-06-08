/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ssamant.utilities;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 *
 * @author sunil
 */
public class WriteFile {
    
    private String path;
    private boolean appendToFile=false;
    
    public WriteFile(String filePath){
        path = filePath;
    }
    
    public WriteFile( String filePath , boolean append_value ) {

        path = filePath;
        appendToFile = append_value;
    } 
    public void writeToFile( String textLine ) throws IOException {
        FileWriter write = new FileWriter( path , appendToFile);
        PrintWriter print_line = new PrintWriter( write );
        print_line.printf( "%s" + "%n" , textLine);
        print_line.close();
        
    }
    
}
