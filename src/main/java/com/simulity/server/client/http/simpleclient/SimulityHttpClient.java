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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.AbstractHttpParams;
import org.apache.http.params.BasicHttpParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The SimulityHttpClient is a simple HTTP client that has rich logging,
 * provided by both the implementation and Apache allowing for simple debugging
 * and for requests to be made at high throughput via a threadpool or via single
 * instances via the methods provided.
 *
 * @author Christopher Burke <christopher.burke@simulity.com>
 */
public class SimulityHttpClient {

    private static SimulityHttpClient _clientInstance;
    private static Logger log = LoggerFactory.getLogger(SimulityHttpClient.class);
    private AbstractHttpParams _httpParams;
    private SchemeRegistry _httpRegistry;
    private Scheme _httpScheme;
    private ClientConnectionManager _connectionManager;
    private HttpClient _httpClient;

    /**
     * @return Basic instance of the SimulityHttpClient
     */
    public static SimulityHttpClient getInstance() {
        if (_clientInstance == null) {
            log.debug("Constructing new " + SimulityHttpClient.class.getName());
            _clientInstance = new SimulityHttpClient();
        }
        return _clientInstance;
    }

    /**
     * @return HTTP instance (setup) of the SimulityHttpClient
     */
    public static SimulityHttpClient getHttpInstance() {
        log.debug("Constructing new HttpInstance of " + SimulityHttpClient.class.getName());
        return getInstance().basicHttpParams().setupRegistry().supportHttp().setupThreadPool();
    }

    /**
     * @return HTTPS instance (setup) of the SimulityHttpClient
     */
    public static SimulityHttpClient getHttpsInstance() {
        log.debug("Constructing new HttpsInstance of " + SimulityHttpClient.class.getName());
        return getInstance().basicHttpParams().setupRegistry().supportHttps().setupThreadPool();
    }

    /**
     * @return Complete instance (setup) -- HTTP&HTTPS of the SimulityHttpClient
     */
    public static SimulityHttpClient getCompleteInstance() {
        log.debug("Constructing new Complete (HTTP/HTTPS) Instance of " + SimulityHttpClient.class.getName());
        return getInstance().basicHttpParams().setupRegistry().supportHttp().supportHttps().setupThreadPool();
    }

    public static SimulityHttpClient getSingleInstance() {
        log.debug("Constructed a new Single Complete (HTTP/HTTPS) Instance of " + SimulityHttpClient.class.getName());
        return getInstance().basicHttpParams().setupRegistry().supportHttp().supportHttps().singleThreaded();
    }

    /**
     * @return SimulityHttpClient with basic HTTP parameters applied.
     */
    public SimulityHttpClient basicHttpParams() {
        log.debug("Setting up Basic HTTP Parameters.");
        _httpParams = new BasicHttpParams();
        log.debug("Parameters setup: " + _httpParams);
        return this;
    }

    /**
     * @return SimulityHttpClient with SchemeRegistry applied.
     */
    public SimulityHttpClient setupRegistry() {
        log.debug("Setting up HttpRegistry.");
        _httpRegistry = new SchemeRegistry();
        log.debug("HttpRegistry setup: " + _httpRegistry);
        return this;
    }

    /**
     * @return SimulityHttpClient with HTTP support applied.
     */
    public SimulityHttpClient supportHttp() {
        log.debug("Staging support for HTTP Scheme.");
        _httpScheme = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
        log.debug("Http Scheme constructed: " + _httpScheme);
        log.debug("Adding HttScheme to Register.");
        Scheme get = _httpRegistry.get("http");
        if (get == null) {
            _httpRegistry.register(_httpScheme);
            log.debug("HttpScheme has been registered.");
        } else {
            log.debug("HttpScheme was already registered, probably by a previous invocation.");
        }

        return this;
    }

    /**
     * @return SimulityHttpClient with HTTPS support applied.
     */
    public SimulityHttpClient supportHttps() {
        log.debug("Staging support for HTTP Scheme.");
        _httpScheme = new Scheme("https", 443, SSLSocketFactory.getSocketFactory());
        log.debug("Http Scheme constructed: " + _httpScheme);
        log.debug("Adding HttScheme to Register.");
        Scheme get = _httpRegistry.get("http");
        if (get == null) {
            _httpRegistry.register(_httpScheme);
            log.debug("HttpScheme has been registered.");
        } else {
            log.debug("HttpScheme was already registered, probably by a previous invocation.");
        }
        return this;
    }

    /**
     * @return SimulityHttpClient with a connection manager applied (for use in
     * Threaded situations as the single implementation will crash on more than
     * one request in the pipeline at any given time)
     */
    public SimulityHttpClient setupThreadPool() {
        log.debug("Setting up ThreadPool.");
        if (_connectionManager == null) {
            log.debug("Constructing new PoolingClientConnectionManager.");
            _connectionManager = new PoolingClientConnectionManager(_httpRegistry);
            log.debug("PoolingClientConnectionManager constructed.");
        } else {
            log.debug("Using connection manager pre-defined by previous invocation.");
        }
        log.debug("ThreadPool setup.");
        return this;
    }
    
    public SimulityHttpClient singleThreaded() {
        log.debug("Invoking destroy to ensure that solution is not using ThreadPool.");
        destroy();
        log.debug("Single Threaded solution presented.");
        return this;
    }

    /**
     * @return HttpClient which allows for requests to be beared to a
     * destination.
     */
    public HttpClient getClient() {
        log.debug("Client Requested.");
        if (_httpClient == null || _connectionManager == null) {
            log.debug("Constructing new HttpClient or ConnectionManager as member was null.");
            if (_connectionManager == null) {
                log.debug("Connection manager was not present, using single-threaded model.");
                _httpClient = new DefaultHttpClient(_httpParams);
                log.debug("HttpClient created: " + _httpClient);
            } else {
                log.debug("Connection manager was present, using ThreadPool model.");
                _httpClient = new DefaultHttpClient(_connectionManager, _httpParams);
                log.debug("HttpClient created: " + _httpClient);
            }

        } else {
            log.debug("Using member found HttpClient: " + _httpClient);
        }
        return _httpClient;
    }

    /**
     * Make a HTTP GET request (construction only)
     *
     * @param url request url
     * @return built object
     */
    public HttpGet get(String url) {
        log.debug("Http GET construction request for URL: " + url);
        return new HttpGet(url);
    }

    /**
     * Make a HTTP POST request (construction only)
     *
     * @param url request url
     * @return built object
     */
    public HttpPost post(String url, ArrayList<NameValuePair> parameters) throws UnsupportedEncodingException {
        log.debug("Http POST construction request for URL: " + url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(parameters));
        return httpPost;
    }

    /**
     * Make a HTTP DELETE request (construction only)
     *
     * @param url request url
     * @return built object
     */
    public HttpDelete delete(String url) {
        log.debug("Http DELETE construction request for URL: " + url);
        return new HttpDelete(url);
    }

    /**
     * Execute (send) the request to the destination
     *
     * @param request request (HTTP) that should be pre-constructed
     * @return HttpResponse data from the request
     * @throws IOException
     */
    public HttpResponse execute(HttpUriRequest request) throws IOException {
        log.debug("Http Execution request for " + request.getURI().toString());
        return _httpClient.execute(request);
    }
    
    /**
     * PUT
     * 
     * @param url request url
     * @return built object
     */
    public HttpPut put(String url, ArrayList<NameValuePair> parameters) throws UnsupportedEncodingException{
        HttpPut httpPut = new HttpPut(url);
        httpPut.setEntity(new UrlEncodedFormEntity(parameters));
        log.debug("Http PUT construction request for URL: " + url);
        return new HttpPut(url);
    }
    
     /**
     * PATCH
     * 
     * @param url request url
     * @return built object
     */
    public HttpPatch patch(String url, ArrayList<NameValuePair> parameters) throws UnsupportedEncodingException{
        HttpPatch httpPatch = new HttpPatch(url);
        httpPatch.setEntity(new UrlEncodedFormEntity(parameters));
        log.debug("Http PATCH construction request for URL: " + url);
        return new HttpPatch(url);
    }

    /**
     * Destroys the connection manager.
     */
    public void destroy() {
        log.debug("Destroying connection manager.");
        if (_connectionManager == null) {
            log.debug("Connection manager has already been destroyed.");
        } else {
            _connectionManager.shutdown();
            log.debug("Connection manager destroyed.");
        }
        log.debug("Destroying connection manager references.");
        _connectionManager = null;
        log.debug("Connection manager references destroyed.");

    }

    ////////////////////////////////////////////////////////////////////////////
    /*
     * Helper Methods
     */
    ////////////////////////////////////////////////////////////////////////////
    /**
     * Conversion of the response data into a String
     *
     * @param response Response data from the execute call
     * @param request the original request (for abortions)
     * @return The Stringify of the request
     * @throws IOException If the request is still in use (bad thread handling)
     */
    public String responseToString(HttpResponse response, HttpUriRequest request) throws IOException {
        log.debug("Request for entity stringify for request " + request.getURI().toString());
        String returnString = "";
        HttpEntity entity = response.getEntity();
        if (entity != null) {
            InputStream content = entity.getContent();
            try {

                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    log.debug("Response Data (line): " + line);
                    returnString += line;
                }

            } catch (IOException ex) {
                /*
                 * According to Apache, the connection will be released back to
                 * the connection manager automatically.
                 */
                log.error("This request will be released. Please revise setup.", ex);
                throw ex;
            } catch (RuntimeException ex) {
                /*
                 * In case of an unexpected (runtime) exception, the HttpRequest
                 * (which should be provided in the argvs) should be aborted,
                 * however it would be good practice to capture the event and
                 * shutdown the underlying connection so that it can be released
                 * back to the connection manager.
                 */
                log.error("Aborting request: " + request.getURI().toString());
                request.abort();
                log.error("Request aborted.");
                log.error("This request will be released on a best-attempt effort, "
                        + "therefore, it would be pragmatic to ensure that this does "
                        + "not happen too often.", ex);
                throw ex;
            } finally {
                /*
                 * Upon closure of the InputStream, the connection manager will
                 * free the connection.
                 */
                log.debug("Reading completed; closing connection content stream.");
                content.close();
            }
        }

        return returnString;
    }

    /**
     * Sends a simple HTTP GET request to the provided URL and returns a String
     * of the response. This is NOT thread safe. Do not use in production,
     * unless you are happy with being able to only support a single connection
     * at a time.
     *
     * @param url Request URL
     * @return Response data
     * @throws IOException If something bad happened.
     */
    public static String simpleGetRequest(String url) throws IOException {
        SimulityHttpClient instance = getSingleInstance();
        HttpClient client = instance.getClient();
        HttpGet get = instance.get(url);
        HttpResponse response = client.execute(get);
        String responseToString = instance.responseToString(response, get);
        instance.destroy();
        return responseToString;
    }

    /**
     * Sends a simple HTTP POST request to the provided URL and returns a String
     * of the response. This is NOT thread safe. Do not use in production,
     * unless you are happy with being able to only support a single connection
     * at a time.
     *
     * @param url Request URL
     * @return Response data
     * @throws IOException If something bad happened.
     */
    public static String simplePostRequest(String url, ArrayList<NameValuePair> parameters) throws UnsupportedEncodingException, IOException {
        SimulityHttpClient instance = getSingleInstance();
        HttpClient client = instance.getClient();
        HttpPost post = instance.post(url, parameters);
        HttpResponse response = client.execute(post);
        String responseToString = instance.responseToString(response, post);
        instance.destroy();
        return responseToString;
    }

    /**
     * Sends a simple HTTP DELETE request to the provided URL and returns a
     * String of the response. This is NOT thread safe. Do not use in
     * production, unless you are happy with being able to only support a single
     * connection at a time.
     *
     * @param url Request URL
     * @return Response data
     * @throws IOException If something bad happened.
     */
    public static String simpleDeleteRequest(String url) throws IOException {
        SimulityHttpClient instance = getSingleInstance();
        HttpClient client = instance.getClient();
        HttpDelete delete = instance.delete(url);
        HttpResponse response = client.execute(delete);
        String responseToString = instance.responseToString(response, delete);
        instance.destroy();
        return responseToString;
    }
    
    /**
     * Sends a simple HTTP PUT request to the provided URL and returns a String
     * of the response. This is NOT thread safe. Do not use in production,
     * unless you are happy with being able to only support a single connection
     * at a time.
     * 
     * @param url Request url
     * @return response data
     * @throws IOException If something bad happened
     */
    
    public static String simplePutRequest(String url, ArrayList<NameValuePair> parameters) throws UnsupportedEncodingException, IOException {
        SimulityHttpClient instance = getSingleInstance();
        HttpClient client = instance.getClient();
        HttpPut put = instance.put(url, parameters);
        HttpResponse response = client.execute(put);
        String responseToString = instance.responseToString(response, put);
        instance.destroy();
        return responseToString;       
    }
    
    /**
     * Sends a simple HTTP PATCH request to the provided URL and returns a String
     * of the response. This is NOT thread safe. Do not use in production,
     * unless you are happy with being able to only support a single connection
     * at a time.
     * 
     * @param url Request url
     * @return response data
     * @throws IOException If something bad happened
     */
    
    public static String simplePatchRequest(String url, ArrayList<NameValuePair> parameters) throws UnsupportedEncodingException, IOException {
        SimulityHttpClient instance = getSingleInstance();
        HttpClient client = instance.getClient();
        HttpPatch patch = instance.patch(url, parameters);
        HttpResponse response = client.execute(patch);
        String responseToString = instance.responseToString(response, patch);
        instance.destroy();
        return responseToString;       
    }
}
