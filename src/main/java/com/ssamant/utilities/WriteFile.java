/* 
 * The MIT License
 *
 * Copyright 2020 sunil.
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
