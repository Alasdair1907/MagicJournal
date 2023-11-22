import groovy.transform.Field

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import java.security.GeneralSecurityException
import java.security.cert.CertificateException
import java.security.cert.X509Certificate

@Field
//String api = "http://localhost:8080/admin/jsonApi.jsp"
String api = "https://terrestrialjournal.com/admin/jsonApi.jsp"

@Field
//String imageSource = "http://localhost:8080/getImage.jsp?filename="
String imageSource = "https://terrestrialjournal.com/getImage.jsp?filename="
@Field
List<String> apiTests = [
        "action=getArticleVOByArticleIdPreprocessed&data=1",
        "action=fromBase64Utf8&data=eyJ3ZWJzaXRlTmFtZSI6InRlc3QiLCJ3ZWJzaXRlVVJMIjoiaHR0cDovL2xvY2FsaG9zdDo4MDgwIiwiYWJvdXQiOiIiLCJoZWFkZXJJbmplY3Rpb24iOiIiLCJiaW5nQXBpS2V5IjoiIiwibWFwVHlwZUlkU3RyIjpudWxsLCJhbGxvd0RlbW9Bbm9uIjpmYWxzZSwiaW1hZ2VTdG9yYWdlUGF0aCI6bnVsbCwidGVtcG9yYXJ5Rm9sZGVyUGF0aCI6IkM6L2ZpbGVzL3Rlc3QvVE1QIiwib3RoZXJGaWxlc1N0b3JhZ2VQYXRoIjpudWxsLCJwcmV2aWV3WCI6MTI4MCwicHJldmlld1kiOjEyODAsInRodW1iWCI6ODAwLCJ0aHVtYlkiOjgwMCwic2hvd0Nvb2tpZVdhcm5pbmciOmZhbHNlLCJjb29raWVXYXJuaW5nTWVzc2FnZSI6IiIsIml0ZW1zUGVyUGFnZSI6MjAsInR3aXR0ZXJQcm9maWxlIjoiIiwiZmFjZWJvb2tQcm9maWxlIjoiIiwiaW5zdGFncmFtUHJvZmlsZSI6IiIsInBpbnRlcmVzdFByb2ZpbGUiOiIiLCJmbGlja3JQcm9maWxlIjoiIn0%3D",
        "action=processPagingRequestUnified&data=%7B%22needArticles%22%3Atrue%2C%22needPhotos%22%3Atrue%2C%22needGalleries%22%3Atrue%2C%22page%22%3A0%2C%22tags%22%3A%5B%22europe%22%5D%7D",
        "action=getGalleryVOByGalleryId&data=10",
        "data=%7B%22postAttribution%22%3A0%2C%22postId%22%3A%2210%22%2C%22limitLatest%22%3A10%7D&action=getSidePanelPosts",
        "data=%5B%7B%22limit%22%3A10%7D%2C%7B%22limit%22%3A16%7D%2C%7B%22limit%22%3A6%2C%22galleryRepresentationImages%22%3A8%7D%5D&action=listHomepage",
        "data=%5B%7B%22limit%22%3A100%7D%2C%7B%22limit%22%3A100%7D%2C%7B%22limit%22%3A100%2C%22galleryRepresentationImages%22%3A8%7D%5D&action=listHomepage",
        "action=getArticleVOByArticleIdPreprocessed&data=12",
        "data=%7B%22postAttribution%22%3A2%2C%22postId%22%3A%2212%22%2C%22limitLatest%22%3A10%7D&action=getSidePanelPosts",
        "action=preFilterTags&data=%7B%22page%22%3A0%2C%22needArticles%22%3Atrue%2C%22needPhotos%22%3Atrue%2C%22needGalleries%22%3Atrue%2C%22requireGeo%22%3Atrue%7D",
        "action=processPagingRequestUnified&data=%7B%22page%22%3A0%2C%22needArticles%22%3Atrue%2C%22needPhotos%22%3Atrue%2C%22needGalleries%22%3Atrue%2C%22requireGeo%22%3Atrue%2C%22tags%22%3A%5B%5D%2C%22itemsPerPage%22%3A10000%7D",
        "action=getArticleVOByArticleIdPreprocessed&data=15",
        "action=getArticleVOByArticleIdPreprocessed&data=14",
        "action=getArticleVOByArticleIdPreprocessed&data=13",
        "action=getArticleVOByArticleIdPreprocessed&data=11",
        "action=getArticleVOByArticleIdPreprocessed&data=10",
        "action=getArticleVOByArticleIdPreprocessed&data=9",
        "action=getArticleVOByArticleIdPreprocessed&data=6",
        "action=getArticleVOByArticleIdPreprocessed&data=5",
        "action=getArticleVOByArticleIdPreprocessed&data=4",
        "action=getGalleryVOByGalleryId&data=8",
        "action=getGalleryVOByGalleryId&data=7",
        "action=getGalleryVOByGalleryId&data=6",
]

@Field
String imageFileId = "b6052338-a709-481f-8e06-8052cd5803b2.jpg"

@Field
int repeats = 1000
@Field
int sleepMillis = 10 // between repeats
@Field
int imageDownloaderConnections = 100
@Field
int imageDownloadSleepMillis = 10 // between downloading each buffer, not whole image!

ignoreSSLErrors()

def threads = []
apiTests.each { apiTest ->
    Thread thread = new Thread({
        performTest(api, apiTest)
    })

    threads << thread
    thread.start()
}


for (int c = 0; c < imageDownloaderConnections; c++){
    Thread thread = new Thread({
        println "Image Download Thread: " + Thread.currentThread().getId()
        downloadImage(imageSource + imageFileId)
    })

    threads << thread
    thread.start()
}


println("What about now, what about today?")


void performTest(String targetURL, String urlParameters){
    for (int i = 0; i < repeats; i++){
        println "API Thread: " + Thread.currentThread() + " iteration " + i
        fetchPost(targetURL, urlParameters)
        Thread.sleep(sleepMillis)
    }
}

def downloadImage(String urlString) {
    def url = new URL(urlString)
    HttpURLConnection connection = (HttpURLConnection) url.openConnection()
    connection.setRequestMethod("GET")
    connection.connect()

    if (connection.responseCode == 200) {
        InputStream inStream = connection.getInputStream()

        byte[] buffer = new byte[1024]
        int length

        int downloaded = 0
        while ((length = inStream.read(buffer, 0, 1024)) != -1) {
            downloaded += length
            Thread.sleep(imageDownloadSleepMillis)
        }

        inStream.close()
        println("Image download " + downloaded + " bytes done")
    } else {
        println("Failed to download image. Response Code: " + connection.responseCode)
    }
    connection.disconnect()
}


void fetchPost(String targetURL, String urlParameters){
    URL url = new URL(targetURL)
    HttpURLConnection connection = (HttpURLConnection) url.openConnection()

    try {
        // Set up the connection properties
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length))
        connection.setRequestProperty("Content-Language", "en-US")

        // Send the request
        DataOutputStream wr = new DataOutputStream(connection.outputStream)
        wr.writeBytes(urlParameters)
        wr.close()

        // Get the response
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.inputStream))
        StringBuilder response = new StringBuilder()
        String line
        while ((line = reader.readLine()) != null) {
            response.append(line)
            response.append('\r')
        }
        reader.close()
        println response.size()

    } catch (Exception e) {
        e.printStackTrace()
    } finally {
        connection.disconnect()
    }
}

void ignoreSSLErrors() {
    TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                @Override
                void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                @Override
                void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                }

                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            }
    }

    try {
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, new java.security.SecureRandom());
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (GeneralSecurityException e) {
        // Handle the error as needed
        e.printStackTrace()
    }

    HostnameVerifier allHostsValid = new HostnameVerifier() {
        @Override
        boolean verify(String s, SSLSession sslSession) {
            return true
        }
    }

// Install the all-trusting host verifier
    HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
}