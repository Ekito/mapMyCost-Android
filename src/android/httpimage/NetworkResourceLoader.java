package android.httpimage;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.params.ConnPerRouteBean;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.net.Uri;
import android.util.Log;


/**
 * resource loader using apache HTTP client. support HTTP and HTTPS request.
 * 
 * @author zonghai@gmail.com
 */
public class NetworkResourceLoader {
    public static final String TAG = "NetworkResourceLoader";
    public static final boolean DEBUG = false;

    private HttpClient mHttpClient = createHttpClient();;


    public InputStream load (Uri uri) throws IOException{
        if (DEBUG) Log.d(TAG, "Requesting: " + uri);
        HttpGet httpGet = new HttpGet(uri.toString());
        httpGet.addHeader("Accept-Encoding", "gzip");
        HttpResponse response = mHttpClient.execute(httpGet);
        HttpEntity entity = response.getEntity();
        return getUngzippedContent(entity);
    }
    
    
    /**
     * Gets the input stream from a response entity. If the entity is gzipped then this will get a
     * stream over the uncompressed data.
     *
     * @param entity the entity whose content should be read
     * @return the input stream to read from
     * @throws IOException
     */
    public InputStream getUngzippedContent(HttpEntity entity) throws IOException {
        InputStream responseStream = entity.getContent();
        if (responseStream == null) {
            return responseStream;
        }
        Header header = entity.getContentEncoding();
        if (header == null) {
            return responseStream;
        }
        String contentEncoding = header.getValue();
        if (contentEncoding == null) {
            return responseStream;
        }
        if (contentEncoding.contains("gzip")) {
            responseStream = new GZIPInputStream(responseStream);
        }
        return new PatchInputStream( responseStream );
    }

    
    /**
     * Create a thread-safe client. This client does not do redirecting, to allow us to capture
     * correct "error" codes.
     *
     * @return HttpClient
     */
    public final DefaultHttpClient createHttpClient() {
        HttpParams params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);  
        HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);  
        HttpProtocolParams.setUseExpectContinue(params, true);  
        // Turn off stale checking. Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        // Default connection and socket timeout of 30 seconds. Tweak to taste.
        HttpConnectionParams.setConnectionTimeout(params, 10*1000);
        HttpConnectionParams.setSoTimeout(params, 20*1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);
        
        ConnManagerParams.setTimeout(params, 5 * 1000);
        ConnManagerParams.setMaxConnectionsPerRoute(params, new ConnPerRouteBean(50));
        ConnManagerParams.setMaxTotalConnections(params, 200);
        
        // Sets up the http part of the service.
        final SchemeRegistry supportedSchemes = new SchemeRegistry();

        // Register the "http" protocol scheme, it is required
        // by the default operator to look up socket factories.
        final SocketFactory sf = PlainSocketFactory.getSocketFactory();
        supportedSchemes.register(new Scheme("http", sf, 80));
        supportedSchemes.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));  
        final ThreadSafeClientConnManager ccm = new ThreadSafeClientConnManager(params,
                supportedSchemes);
        
        DefaultHttpClient httpClient = new DefaultHttpClient(ccm, params);
        
        httpClient.setHttpRequestRetryHandler(new DefaultHttpRequestRetryHandler(3, true));
        
        return httpClient;
    }

    
    /*
     * The BitmapFactory.decodeStream() method fails to read a JPEG image (i.e.
     * returns null) if the skip() method of the used InputStream skip less bytes
     * than the required amount.
     */
    private static class PatchInputStream extends FilterInputStream {
        
        public PatchInputStream(InputStream in) {
            super(in);
        }
          
          
        public long skip(long n) throws IOException {
            long m = 0L;
            while (m < n) {
                long _m = in.skip(n-m);
                if (_m == 0L) break;
                m += _m;
             }
             return m;
        }
    }
}
