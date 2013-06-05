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
var Http = require('http');
var Util = require('util');

var Server = function() {
	
	var launchTime = 0;
	var requestCount = 0;
	this.httpServer_ = Http.createServer(function(req, res) {
		var responseData = 'Hello, client serving ' + requestCount + '/s.\n';
		requestCount+=1;
		res.write(responseData);
		res.end();
                console.log('Serving ' + requestCount + ' requests/s on 8081');
	});
	this.httpServer_.listen(8081, function() {
		console.log("Server Listening on port: " + 8081);
		launchTime = Date.now() / 1000;
	});
	
	var lastLogged = 0;
	
	var perSecond = function() {
		if(requestCount != 0) {
			
		}
		setTimeout(perSecond, 1000);
		requestCount = 0;
	};
	
	setTimeout(perSecond(), 1000);
};

new Server();

exports.Server = Server;

