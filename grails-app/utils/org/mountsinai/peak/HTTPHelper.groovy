package org.mountsinai.peak;


import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLException
import javax.net.ssl.SSLSession
import javax.net.ssl.SSLSocket
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import org.apache.http.Header
import org.apache.http.HttpEntity
import org.apache.http.HttpResponse
import org.apache.http.NameValuePair
import org.apache.http.client.HttpClient
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpGet
import org.apache.http.client.methods.HttpPost
import org.apache.http.client.protocol.ClientContext
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ClientConnectionManager
import org.apache.http.conn.scheme.Scheme
import org.apache.http.conn.scheme.SchemeRegistry
import org.apache.http.conn.ssl.SSLSocketFactory
import org.apache.http.conn.ssl.X509HostnameVerifier
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.message.BasicNameValuePair
import org.apache.http.params.BasicHttpParams
import org.apache.http.protocol.BasicHttpContext
import org.apache.http.protocol.HttpContext
import org.codehaus.groovy.grails.web.json.JSONObject


public final class HTTPHelper {
	
	static private HTTPHelper myInstance = null;
	private HTTPHelper() { }
	
	public static HTTPHelper getInstance() {
		if(myInstance == null) { myInstance =  new HTTPHelper() }
		return myInstance
	}
	
	public static String getHTTPCall(String urlBase, String urlResource, Map paramsValueMap) {
		String returnString = null
		BufferedReader rd = null
		DefaultHttpClient httpclient = getHttpClient();
		if(httpclient == null) {
			return null;
		}
		
		try {
			
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			//Set parameters
			params.add(new BasicNameValuePair("dummy", "dummy"))
			
			for(String key :  paramsValueMap.keySet()) {
				params.add(new BasicNameValuePair(key, paramsValueMap.get(key)))
			}
			
			String getQueryString = URLEncodedUtils.format(params, "UTF-8")
			
			
			String urlPath = urlBase + (urlResource==null?'':urlResource) + '?' + getQueryString
			HttpGet httpGet = new HttpGet(urlPath);
			//print 'Called URL: '+ httpGet.toString()
			
			HttpResponse response = httpclient.execute(httpGet);	
			String statusLine = response.getStatusLine().toString();
			print 'Got response: '+statusLine
			
			boolean isRequestSuccess = statusLine.matches("HTTP[S]?/([0-9].[0-9]) 200 OK");
			if (isRequestSuccess) {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = rd.readLine()) != null) {
				  stringBuffer.append(line);
				}
				
				return stringBuffer.toString()
			} else {
				print 'HTTP Failed: statusLine - '+statusLine
				return null
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null
		} finally {
			//httpclient.getConnectionManager().shutdown();
			if(rd != null) { rd.close() }
		}
			returnString = "SUCCESS"
		
		return returnString;
	}
	
	public static String postHTTPCall(String urlBase, String urlResource, Map paramsValueMap, String postBodyContent) {
		String returnString = null
		BufferedReader rd = null
		DefaultHttpClient httpclient = getHttpClient();
		if(httpclient == null) {
			return null;
		}
		
		try {
			
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			//Set parameters
			params.add(new BasicNameValuePair("dummy", "dummy"))
			
			for(String key :  paramsValueMap?.keySet()) {
				params.add(new BasicNameValuePair(key, paramsValueMap.get(key)))
			}
			
			String getQueryString = URLEncodedUtils.format(params, "UTF-8")
			String urlPath = urlBase + (urlResource==null?'':urlResource) + '?' + getQueryString
			
			HttpPost httpPost = new HttpPost(urlPath);
			httpPost.setEntity(createStringEntity(postBodyContent))
			httpPost.setHeader("Content-Type", "application/json")
			httpPost.setHeader("AUTH_TOKEN","#155B-76A8091728C61A7041AAF987E410#")
			
			//print 'Called URL: '+ httpPost.toString()
			//print 'POST Entity: '+   postBodyContent
			
			HttpResponse response = httpclient.execute(httpPost);
			String statusLine = response.getStatusLine().toString();
			print 'Got response: '+statusLine
			
			boolean isRequestSuccess = statusLine.matches("HTTP[S]?/([0-9].[0-9]) 200 OK");
			if (true || isRequestSuccess) {
				rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				String line = "";
				StringBuffer stringBuffer = new StringBuffer();
				while ((line = rd.readLine()) != null) {
				  stringBuffer.append(line);
				}
				
				return stringBuffer.toString()
			} else {
				print 'HTTP Failed: statusLine - '+statusLine
				return null
			}
		} catch(Exception e) {
			e.printStackTrace();
			return null
		} finally {
			//httpclient.getConnectionManager().shutdown();
			if(rd != null) { rd.close() }
		}
			returnString = "SUCCESS"
		
		return returnString;
	}
	
	public static void main(String[] args) {


	}
	
	
	public static DefaultHttpClient getHttpClient() {
	   DefaultHttpClient base = new DefaultHttpClient()
	   try {
		   SSLContext ctx = SSLContext.getInstance("SSL");
		   X509TrustManager tm = new X509TrustManager() {

			   public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			   }

			   public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
			   }

			   public X509Certificate[] getAcceptedIssuers() {
				   return null;
			   }
		   };
	   
		   X509HostnameVerifier verifier = new X509HostnameVerifier() {

			   @Override
			   public void verify(String string, SSLSocket ssls) throws IOException {
			   }

			   @Override
			   public void verify(String string, X509Certificate xc) throws SSLException {
			   }

			   @Override
			   public void verify(String string, String[] strings, String[] strings1) throws SSLException {
			   }

			   @Override
			   public boolean verify(String string, SSLSession ssls) {
				   return true;
			   }
		   };
		   ctx.init(null, null, null);
		   SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		   ssf.setHostnameVerifier(verifier);
		   ClientConnectionManager ccm = base.getConnectionManager();
		   SchemeRegistry sr = ccm.getSchemeRegistry();
		   sr.register(new Scheme("https", ssf, 443));
		   
		   return base;
	   } catch (Exception ex) {
		   ex.printStackTrace();
		   return null;
	   }
   }
	   
	private static HttpEntity createStringEntity(String httpBody ) {
		StringEntity se = null;
		try {
			se = new StringEntity(httpBody);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace()
		}
		return se;
	}

}
