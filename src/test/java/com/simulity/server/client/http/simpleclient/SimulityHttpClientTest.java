/*
 * 
 * Simulity Labs Ltd.
 * 
 * Copyright (c) Simulity Labs Ltd. All rights reserved.
 *
 * This source code is the property of Simulity Labs Ltd. Redistribution and
 * use in source (source code) or binary (object code) forms with or without 
 * modification, for commercial, educational or research purposes is not
 * permitted without the prior written consent of Simulity Labs Limited 
 *
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
 * THE SOFTWARE, UNLESS PRIOR WRITTEN CONSENT STATES OTHERWISE.
 * 
 *
 */
package com.simulity.server.client.http.simpleclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class SimulityHttpClientTest {

    public static String serverLog = "";

    @BeforeClass
    public static void beforeClass() {
        new Thread() {
            @Override
            public void run() {
                String outStream = null;
                String errStream = null;
                try {
                    // run the Fortune command
                    Process p = Runtime.getRuntime().exec(new String[]{"/usr/local/bin/node", "src/main/resources/NodeJsEchoServer.js"});

                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    while ((outStream = stdIn.readLine()) != null) {
                        Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.INFO, "SERVER STDOUT: {0}", outStream);
                        serverLog += outStream + "\n";
                    }

                    while ((errStream = stdErr.readLine()) != null) {
                        Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.INFO, "SERVER STDERR: {0}", errStream);
                    }

                } catch (IOException ex) {
                    Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.WARNING, "IOException when attmpeting to process Fortune command.", ex);
                }
            }
        }.start();
    }

    @Test
    public void testSimpleGetRequests() {
        serverLog += "\n\n\nSimple GET requests started.\n\n\n";
        try {
            for (int i = 0; i < 1000; i++) {
                String simpleGetRequest = SimulityHttpClient.simpleGetRequest("http://localhost:8081");
            }
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverLog += "\n\n\nSimple GET requests ended.\n\n\n";
    }

    @Test
    public void testSimplePostRequests() {
        serverLog += "\n\n\nSimple POST requests started.\n\n\n";
        try {
            for (int i = 0; i < 1000; i++) {
                String simplePostRequest = SimulityHttpClient.simplePostRequest("http://localhost:8081", new ArrayList<NameValuePair>());
            }
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverLog += "\n\n\nSimple POST requests ended.\n\n\n";
    }

    @Test
    public void testSimpleDeleteRequests() {
        serverLog += "\n\n\nSimple DELETE requests started.\n\n\n";
        try {
            for (int i = 0; i < 1000; i++) {
                String simpleDeleteRequest = SimulityHttpClient.simpleDeleteRequest("http://localhost:8081");
            }
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverLog += "\n\n\nSimple DELETE requests ended.\n\n\n";
    }
    
    @Test
    public void testSimplePutRequests() {
        serverLog += "\n\n\nSimple PUT requests started.\n\n\n";
        try {
            for (int i = 0; i < 1000; i++) {
                String simplePutRequest = SimulityHttpClient.simplePutRequest("http://localhost:8081", new ArrayList<NameValuePair>());
            }
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverLog += "\n\n\nSimple PUT requests ended.\n\n\n";
    }
    
    @Test
    public void testSimplePatchReqyests(){
        serverLog += "\n\n\nSimple PATCH requests started.\n\n\n";
        try {
            for (int i = 0; i < 1000; i++){
                String simplePatchRequest = SimulityHttpClient.simplePatchRequest("http://localhost:8081", new ArrayList<NameValuePair>());
            }
        } catch (IOException ex){
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        serverLog += "\n\n\nSimple PATCH requests ended.\n\n\n";
    }

    @Test
    public void testIterativeGetConnectionPool() {
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        try {
            serverLog += "\n\n\nIterative ThreadPool GET requests started.\n\n\n";
            for (int i = 0; i < 10000; i++) {
                HttpGet get = completeInstance.get("http://localhost:8081");
                HttpResponse execute = client.execute(get);
                completeInstance.responseToString(execute, get);
            }
            serverLog += "\n\n\nIterative ThreadPool GET requests ended.\n\n\n";
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        completeInstance.destroy();
    }

    @Test
    public void testIterativePostConnectionPool() {
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        try {
            serverLog += "\n\n\nIterative ThreadPool POST requests started.\n\n\n";
            for (int i = 0; i < 10000; i++) {
                HttpPost post = completeInstance.post("http://localhost:8081", new ArrayList<NameValuePair>());
                HttpResponse execute = client.execute(post);
                completeInstance.responseToString(execute, post);
            }
            serverLog += "\n\n\nIterative ThreadPool POST requests ended.\n\n\n";
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        completeInstance.destroy();
    }

    @Test
    public void testIterativeDeleteConnectionPool() {
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        try {
            serverLog += "\n\n\nIterative ThreadPool DELETE requests started.\n\n\n";
            for (int i = 0; i < 10000; i++) {
                HttpDelete delete = completeInstance.delete("http://localhost:8081");
                HttpResponse execute = client.execute(delete);
                completeInstance.responseToString(execute, delete);
            }
            serverLog += "\n\n\nIterative ThreadPool DELETE requests ended.\n\n\n";
        } catch (IOException ex) {
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        completeInstance.destroy();
    }
    
    @Test
    public void testIterativePutConnectionPool(){
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        try{
            serverLog += "\n\n\nIterative ThreadPool PUT requests started.\n\n\n";
            for (int i = 0; i < 10000; i++){
                HttpPut put = completeInstance.put("http://localhost:8081", new ArrayList<NameValuePair>());
                HttpResponse execute = client.execute(put);
                completeInstance.responseToString(execute, put);
            }
            serverLog += "\n\n\nIterative ThreadPool PUT requests ended.\n\n\n";
        }
        catch(IOException ex){
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        completeInstance.destroy();
    }
    
    @Test
    public void testIterativePatchConnectionPool(){
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        try{
            serverLog += "\n\n\nIterative ThreadPool PATCH requests started.\n\n\n";
            for (int i = 0; i < 10000; i++){
                HttpPatch patch = completeInstance.patch("http://localhost:8081", new ArrayList<NameValuePair>());
                HttpResponse execute = client.execute(patch);
                completeInstance.responseToString(execute, patch);
            }
            serverLog += "\n\n\nIterative ThreadPool PATCH requests ended.\n\n\n";
        }
        catch(IOException ex){
            Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        completeInstance.destroy();
    }

    @Test
    public void testThreadedGetConnectionPool() {
        ThreadHolder[] threads = new ThreadHolder[1024];
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        for (int i = 0; i < threads.length; i++) {
            HttpGet get = completeInstance.get("http://localhost:8081");
            threads[i] = new ThreadHolder(client, get, completeInstance);
        }

        
        serverLog += "\n\n\nMulti-Threaded ThreadPool GET requests started.\n\n\n";
        for (ThreadHolder threadHolder : threads) {
            threadHolder.start();
        }

        for (ThreadHolder threadHolder : threads) {
            try {
                threadHolder.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        boolean alive = false;
        for (ThreadHolder threadHolder : threads) {
            if(threadHolder.isAlive()) {
                alive = true;
            }
        }
        serverLog += "\n\n\nMulti-Threaded ThreadPool GET requests ended.\n\n\n";
        
        if(alive) {
            System.out.println("Threads were still alive.");
        } else {
            completeInstance.destroy();
        }  
    }
    
    @Test
    public void testThreadedPostConnectionPool() throws UnsupportedEncodingException {
        ThreadHolder[] threads = new ThreadHolder[1024];
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        for (int i = 0; i < threads.length; i++) {
            HttpPost get = completeInstance.post("http://localhost:8081", new ArrayList<NameValuePair>());
            threads[i] = new ThreadHolder(client, get, completeInstance);
        }

        
        serverLog += "\n\n\nMulti-Threaded ThreadPool POST requests started.\n\n\n";
        for (ThreadHolder threadHolder : threads) {
            threadHolder.start();
        }

        for (ThreadHolder threadHolder : threads) {
            try {
                threadHolder.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        boolean alive = false;
        for (ThreadHolder threadHolder : threads) {
            if(threadHolder.isAlive()) {
                alive = true;
            }
        }
        serverLog += "\n\n\nMulti-Threaded ThreadPool POST requests ended.\n\n\n";
        
        if(alive) {
            System.out.println("Threads were still alive.");
        } else {
            completeInstance.destroy();
        }  
    }
    
    @Test
    public void testThreadedDeleteConnectionPool() {
        ThreadHolder[] threads = new ThreadHolder[1024];
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        for (int i = 0; i < threads.length; i++) {
            HttpDelete get = completeInstance.delete("http://localhost:8081");
            threads[i] = new ThreadHolder(client, get, completeInstance);
        }

        
        serverLog += "\n\n\nMulti-Threaded ThreadPool DELETE requests started.\n\n\n";
        for (ThreadHolder threadHolder : threads) {
            threadHolder.start();
        }

        for (ThreadHolder threadHolder : threads) {
            try {
                threadHolder.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        boolean alive = false;
        for (ThreadHolder threadHolder : threads) {
            if(threadHolder.isAlive()) {
                alive = true;
            }
        }
        serverLog += "\n\n\nMulti-Threaded ThreadPool DELETE requests ended.\n\n\n";
        
        if(alive) {
            System.out.println("Threads were still alive.");
        } else {
            completeInstance.destroy();
        }  
    }
    
    @Test
    public void TestThreadedPutConnectionPool() throws UnsupportedEncodingException{
        ThreadHolder[] threads = new ThreadHolder[1024];
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        for (int i = 0; i < threads.length; i++){
            HttpPut put = completeInstance.put("http://localhost:8081", new ArrayList<NameValuePair>());
            threads[i] = new ThreadHolder(client, put, completeInstance);
        }
        
        serverLog += "\n\n\nMultiThreaded Threadpool PUT request started.\n\n\n";
        
        for (ThreadHolder threadHolder : threads){
            threadHolder.start();
        }
        
        for (ThreadHolder threadHolder : threads){
            try{
                threadHolder.join();
            } catch (InterruptedException ex){
                Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        boolean alive = false;
        for(ThreadHolder threadHolder : threads){
            if(threadHolder.isAlive()){
                alive = true;
            }
        }
        serverLog += "\n\n\nMulti-Threaded ThreadPool PUT requests ended.\n\n\n";
        
        if(alive){
            System.out.println("Threads were still alive.");
        } else {
            completeInstance.destroy();
        }
    }
    
    @Test
    public void TestThreadedPatchConnectionPool() throws UnsupportedEncodingException{
        ThreadHolder[] threads = new ThreadHolder[1024];
        SimulityHttpClient completeInstance = SimulityHttpClient.getCompleteInstance();
        HttpClient client = completeInstance.getClient();
        for (int i = 0; i < threads.length; i++){
            HttpPatch patch = completeInstance.patch("http://localhost:8081", new ArrayList<NameValuePair>());
            threads[i] = new ThreadHolder(client, patch, completeInstance);
        }
        
        serverLog += "\n\n\nMultiThreaded Threadpool PATCH request started.\n\n\n";
        
        for (ThreadHolder threadHolder : threads){
            threadHolder.start();
        }
        
        for (ThreadHolder threadHolder : threads){
            try{
                threadHolder.join();
            } catch (InterruptedException ex){
                Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        boolean alive = false;
        for(ThreadHolder threadHolder : threads){
            if(threadHolder.isAlive()){
                alive = true;
            }
        }
        serverLog += "\n\n\nMulti-Threaded ThreadPool PATCH requests ended.\n\n\n";
        
        if(alive){
            System.out.println("Threads were still alive.");
        } else {
            completeInstance.destroy();
        }
    }

    static class ThreadHolder extends Thread {

        private final HttpClient httpClient;
        private final SimulityHttpClient instance;
        private final HttpUriRequest httpget;
        private volatile boolean shutdown;

        public ThreadHolder(HttpClient httpClient, HttpUriRequest httpget, SimulityHttpClient instance) {
            this.httpClient = httpClient;
            this.httpget = httpget;
            this.instance = instance;
        }

        @Override
        public void run() {
            if (!shutdown) {
                try {
                    HttpResponse execute = this.httpClient.execute(httpget);
                    String responseToString = instance.responseToString(execute, httpget);
                } catch (Exception ex) {
                    this.httpget.abort();
                }
            }

        }

        // TODO: Cburke -- complete implementation.
        public void shutdown() {
            shutdown = true;
            synchronized (this) {
                notifyAll();
            }
        }
    }

    @AfterClass
    public static void afterClass() {

        new Thread() {
            @Override
            public void run() {
                String outStream = null;
                String errStream = null;
                try {
                    // run the Fortune command
                    Process p = Runtime.getRuntime().exec(new String[]{"killall", "node"});

                    BufferedReader stdIn = new BufferedReader(new InputStreamReader(p.getInputStream()));

                    BufferedReader stdErr = new BufferedReader(new InputStreamReader(p.getErrorStream()));

                    while ((outStream = stdIn.readLine()) != null) {
                        Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.INFO, "SERVER STDOUT: {0}", outStream);
                    }

                    while ((errStream = stdErr.readLine()) != null) {
                        Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.INFO, "SERVER STDERR: {0}", errStream);
                    }

                } catch (IOException ex) {
                    Logger.getLogger(SimulityHttpClientTest.class.getName()).log(Level.WARNING, "IOException when attmpeting to process Fortune command.", ex);
                }
            }
        }.start();
        System.out.println(serverLog);
    }
}
