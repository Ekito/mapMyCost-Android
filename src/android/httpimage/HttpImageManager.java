package android.httpimage;


import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;


/**
 * HttpImageManager uses 3-level caching to download and store network images.
 * <p>
 *     ---------------<br>
 *     memory cache<br>
 *     ---------------<br>
 *     persistent storage (DB/FS)<br>
 *     ---------------<br>
 *     network loader<br>
 *     ---------------
 *     
 * <p>
 * HttpImageManager will first look up the memory cache, return the image bitmap if it was already
 * cached in memory. Upon missing, it will further look at the 2nd level cache, 
 * which is the persistence layer. It only goes to network if the resource has never been downloaded.
 * 
 * <p>
 * The downloading process is handled in asynchronous manner. To get notification of the response, 
 * one can add an OnLoadResponseListener to the LoadRequest object.
 * 
 * <p>
 * HttpImageManager is usually used for ImageView to display a network image. To simplify the code, 
 * One can register an ImageView object as target to the LoadRequest instead of an 
 * OnLoadResponseListener. HttpImageManager will try to feed the loaded resource to the target ImageView
 * upon successful download. Following code snippet shows how it is used in a customer list adapter.
 * 
 * <p>
 * <pre>
 *         ...
 *         String imageUrl = userInfo.getUserImage();
 *         ImageView imageView = holder.image;
 * 
 *         imageView.setImageResource(R.drawable.default_image);
 * 
 *         if(!TextUtils.isEmpty(imageUrl)){
 *             Bitmap bitmap = mHttpImageManager.loadImage(new HttpImageManager.LoadRequest(Uri.parse(imageUrl), imageView));
 *            if (bitmap != null) {
 *                imageView.setImageBitmap(bitmap);
 *            }
 *        }
 *
 * </pre>
 * 
 * @author zonghai@gmail.com
 */
public class HttpImageManager{
    
    private static final String TAG = "HttpImageManager";
    private static final boolean DEBUG = true;
    
    public static final int DEFAULT_CACHE_SIZE = 64;
    
    
    public static class LoadRequest {
        
        public LoadRequest (Uri uri) {
            this(uri, null, null);
        }
        
        
        public LoadRequest(Uri uri, ImageView v){
            this(uri, v, null);
        }

        
        public LoadRequest(Uri uri, OnLoadResponseListener l){
            this( uri, null, l);
        }
        
        
        public LoadRequest(Uri uri, ImageView v, OnLoadResponseListener l){
            if(uri == null) 
                throw new NullPointerException("uri must not be null");
            
            mUri = uri;
            mHashedUri = computeHashedName(uri.toString());
            mImageView = v;
            mListener = l;
        }
        

        public ImageView getImageView() {
            return mImageView;
        }

        
        public Uri getUri() {
            return mUri;
        }
        
        
        public String getHashedUri () {
            return this.mHashedUri;
        }
        
        
        @Override 
        public int hashCode() {
            return mUri.hashCode();
        }
        
        
        @Override 
        public boolean equals(Object b){
            if(b instanceof LoadRequest)
                return mUri.equals(((LoadRequest)b).getUri());

            return false;
        }
        
        
        /* B64 encoded Hash over the input name */
        private String computeHashedName (String name) {
            try {
                 MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
                 digest.update(name.getBytes());
                 return Base64.encodeBytes(digest.digest()).replace("/", "_");
            } 
            catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        
        
        private Uri mUri;
        private String mHashedUri;
        
        private OnLoadResponseListener mListener;
        private ImageView mImageView;
    }
    

    public static interface OnLoadResponseListener {
        public void onLoadResponse(LoadRequest r, Bitmap data);
        public void onLoadError(LoadRequest r, Throwable e);
    }
    
    
    public HttpImageManager (BitmapCache cache,  BitmapCache persistence ) {
        mCache = cache;
        mPersistence = persistence;
    }
    
    
    public HttpImageManager ( BitmapCache persistence ) {
        this(new BasicBitmapCache(DEFAULT_CACHE_SIZE), persistence);
    }
    
    
    public Bitmap loadImage(Uri uri) {
        return loadImage(new LoadRequest(uri));
    }
    
    
    /**
     * Nonblocking call, return null if the bitmap is not in cache.
     * @param r
     * @return
     */
    public Bitmap loadImage( LoadRequest r ) {
        if(r == null || r.getUri() == null || TextUtils.isEmpty(r.getUri().toString())) 
            throw new IllegalArgumentException( "null or empty request");
        
        ImageView v = r.getImageView();
         if(v != null){
             synchronized ( v ) {
                 v.setTag(r.getUri()); // bind URI to the ImageView, to prevent image write-back of earlier requests.
             }
         }
         
        String key = r.getHashedUri();
        
        if(mCache.exists(key)) {
            return mCache.loadData(key);
        }
        else { 
            // not ready yet, try to retrieve it asynchronously.
            mExecutor.submit(newRequestCall(r));
            return null;
        }
    }
    
    
    ////PRIVATE
    private Callable<LoadRequest> newRequestCall(final LoadRequest request) {
        return new Callable<LoadRequest>() {
            public LoadRequest call() {
                
                synchronized (mActiveRequests) {
                    // If there's been already request pending for the same URL, we just wait until it is handled.
                    while (mActiveRequests.contains(request)) {
                        try {
                            mActiveRequests.wait();
                        } catch(InterruptedException e) {}
                    }
                    
                    mActiveRequests.add(request);
                }
                
                Bitmap data = null;
                
                try {
                    String key = request.getHashedUri();
                    
                    //first we lookup memory cache
                    data = mCache.loadData(key);
                    if(data == null) {
                        if(DEBUG)  Log.d(TAG, "cache missing " + request.getUri().toString());
                        //then check the persistent storage
                        data = mPersistence.loadData(key);
                        if(data != null) {
                            if(DEBUG)  Log.d(TAG, "found in persistent: " + request.getUri().toString());
                            // load it into memory
                            mCache.storeData(key, data);
                        }
                        else {
                            // we go to network
                            if(DEBUG)  Log.d(TAG, "go to network " + request.getUri().toString());
                            InputStream in = mNetworkResourceLoader.load(request.getUri());
                            data = BitmapFactory.decodeStream(in);
                            if(data == null) 
                                throw new RuntimeException("data from remote can't be decoded to bitmap");
                            
                            if(DEBUG) Log.d(TAG, "" + data.getWidth() + ", " + data.getHeight());
                            
                            // load it into memory
                            mCache.storeData(key, data);
                            
                            // persist it
                            mPersistence.storeData(key, data);
                        }
                    }
                    
                    if(data != null && request.getImageView() != null) {
                        final Bitmap theData = data;
                        final ImageView iv = request.getImageView();
                        
                        synchronized ( iv ) {
                            if ( iv.getTag() == request.getUri() ) {
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                         if ( iv.getTag() == request.getUri()) {
                                             iv.setImageBitmap(theData);
                                         }
                                    }
                                });
                            }
                        }
                        
                        OnLoadResponseListener l = request.mListener;
                        if( l != null) l.onLoadResponse(request, data);
                    }
                  
                }
                catch (Throwable e) {
                    OnLoadResponseListener l = request.mListener;
                    if( l != null) l.onLoadError(request, e);
                    if(DEBUG) Log.e(TAG, "error handling request " + request.getUri(), e);
                }
                finally{
                    synchronized (mActiveRequests) {
                         mActiveRequests.remove(request);
                         mActiveRequests.notifyAll();  // wake up pending requests who's querying the same URL. 
                    }
                   
                    if (DEBUG) Log.d(TAG, "finished request for: " + request.getUri());
                }
                
                return request;
            }
        };
    }

    
    private BitmapCache mCache;
    private BitmapCache mPersistence;
    private NetworkResourceLoader mNetworkResourceLoader = new NetworkResourceLoader(); 
    
    private Handler mHandler = new Handler();
    private ExecutorService mExecutor = Executors.newCachedThreadPool();
  
    private Set<LoadRequest> mActiveRequests = new HashSet<LoadRequest>();
}
