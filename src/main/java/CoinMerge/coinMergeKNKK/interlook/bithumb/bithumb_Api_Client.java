package CoinMerge.coinMergeKNKK.interlook.bithumb;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;


import java.net.URLEncoder;
import java.security.InvalidKeyException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.buf.HexUtils;
//import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;



@SuppressWarnings("unused")
public class bithumb_Api_Client {
    protected String api_url = "https://api.bithumb.com";
    protected String api_key;
    protected String api_secret;

    public bithumb_Api_Client(String api_key, String api_secret) {
	this.api_key = api_key;
	this.api_secret = api_secret;
    }

    /**
     * ������ �ð��� ns�� �����Ѵ�.(1/1,000,000,000 ��)
     * 
     * @return int
     */
    private String usecTime() {
    	/*
		long start = System.nanoTime();
		// do stuff
		long nanoseconds = System.nanoTime();
		long microseconds = TimeUnit.NANOSECONDS.toMicros(nanoseconds);
		long seconds = TimeUnit.NANOSECONDS.toSeconds(nanoseconds);
	
		int elapsedTime = (int) (microseconds + seconds);
	
		System.out.println("elapsedTime ==> " + microseconds + " : " + seconds);
		*/
    	
		return String.valueOf(System.currentTimeMillis());
    }

    private String request(String strHost, String strMemod, HashMap<String, String> rgParams,  HashMap<String, String> httpHeaders) {
    	String response = "";

		// SSL ����
		if (strHost.startsWith("https://")) {
		    bithumb_HttpRequest request = bithumb_HttpRequest.get(strHost);
		    // Accept all certificates
		    request.trustAllCerts();
		    // Accept all hostnames
		    request.trustAllHosts();
		}
	
		if (strMemod.toUpperCase().equals("HEAD")) {
		} else {
		    bithumb_HttpRequest request = null;
	
		    // POST/GET ����
		    if (strMemod.toUpperCase().equals("POST")) {
				request = new bithumb_HttpRequest(strHost, "POST");
				request.readTimeout(10000);

				System.out.println("POST ==> " + request.url());

				if (httpHeaders != null && !httpHeaders.isEmpty()) {
					httpHeaders.put("api-client-type", "2");
					request.headers(httpHeaders);
					System.out.println(httpHeaders.toString());
				}
				if (rgParams != null && !rgParams.isEmpty()) {
					request.form(rgParams);
					System.out.println("rgParams was: " + rgParams.toString());
				}
		    } else {
				request = bithumb_HttpRequest.get(strHost
					+ bithumb_Util.mapToQueryString(rgParams));
				request.readTimeout(10000);

				System.out.println("Response was: " + response);
		    }

		    if (request.ok()) {
				System.out.println("Response was: " + response);
				response = request.body();
		    } else {
				response = "error : " + request.code() + ", message : "
					+ request.body();
		    }
		    request.disconnect();
		}

		System.out.println("Response was: " + response);
		return response;
    }
    
    public static String encodeURIComponent(String s)
    {
      String result = null;
   
      try
      {
        result = URLEncoder.encode(s, "UTF-8")
                           .replaceAll("\\+", "%20")
                           .replaceAll("\\%21", "!")
                           .replaceAll("\\%27", "'")
                           .replaceAll("\\%28", "(")
                           .replaceAll("\\%29", ")")
                           .replaceAll("\\%26", "&")
                           .replaceAll("\\%3D", "=")
                           .replaceAll("\\%7E", "~");
      }
   
      // This exception should never occur.
      catch (UnsupportedEncodingException e)
      {
        result = s;
      }

      return result;
    }

    private HashMap<String, String> getHttpHeaders(String endpoint, HashMap<String, String> rgData, String apiKey, String apiSecret) {
	    	
		String strData = bithumb_Util.mapToQueryString(rgData).replace("?", "");
		String nNonce = usecTime();
		
		strData = strData.substring(0, strData.length()-1);
	
	
		System.out.println("1 : " + strData);
		
		strData = encodeURIComponent(strData);
		
		HashMap<String, String> array = new HashMap<String, String>();
	
		
		String str = endpoint + ";"	+ strData + ";" + nNonce;
		//String str = "/info/balance;order_currency=BTC&payment_currency=KRW&endpoint=%2Finfo%2Fbalance;272184496";
		
        String encoded = asHex(hmacSha512(str, apiSecret));
		
		System.out.println("strData was: " + str);
		System.out.println("apiSecret was: " + apiSecret);
		array.put("Api-Key", apiKey);
		array.put("Api-Sign", encoded);
		array.put("Api-Nonce", String.valueOf(nNonce));
	
		return array;
		
    }
    
    private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String HMAC_SHA512 = "HmacSHA512";
	 
	public static byte[] hmacSha512(String value, String key){
	    try {
	        SecretKeySpec keySpec = new SecretKeySpec(
	                key.getBytes(DEFAULT_ENCODING),
	                HMAC_SHA512);
	 
	        Mac mac = Mac.getInstance(HMAC_SHA512);
	        mac.init(keySpec);
	
	        final byte[] macData = mac.doFinal( value.getBytes( ) );
	        byte[] hex = HexUtils.toHexString(macData).getBytes();
			/*int length = temp.length();
			byte[] hex = new byte[length/2];
			for (int i = 0; i < length; i += 2) {
				hex[i / 2] = (byte) ((Character.digit(temp.charAt(i), 16) << 4)
						+ Character.digit(temp.charAt(i + 1), 16));
			}*/
			//byte[] hex = new java.math.BigInteger(16, macData).toByteArray();
	        //return mac.doFinal(value.getBytes(DEFAULT_ENCODING));
	        return hex;
	 
	    } catch (NoSuchAlgorithmException e) {
	        throw new RuntimeException(e);
	    } catch (InvalidKeyException e) {
	        throw new RuntimeException(e);
	    } catch (UnsupportedEncodingException e) {
	        throw new RuntimeException(e);
	    }
	}
	 
	public static String asHex(byte[] bytes){
		Base64.Encoder encoder = Base64.getEncoder();
	    return new String(encoder.encode(bytes));
	}

    @SuppressWarnings("unchecked")
    public String callApi(String endpoint, HashMap<String, String> params) {
		String rgResultDecode = "";
		HashMap<String, String> rgParams = new HashMap<String, String>();
		rgParams.put("endpoint", endpoint);
	
		if (params != null) {
		    rgParams.putAll(params);
		}
	
		String api_host = api_url + endpoint;
		HashMap<String, String> httpHeaders = getHttpHeaders(endpoint, rgParams, api_key, api_secret);
	
		rgResultDecode = request(api_host, "POST", rgParams, httpHeaders);
	
		if (!rgResultDecode.startsWith("error")) {
		    // json �Ľ�
		    HashMap<String, String> result;
		    try {
				result = new ObjectMapper().readValue(rgResultDecode,
					HashMap.class);

				System.out.println("==== ��� ��� ====");
				System.out.println(result.get("status"));
		    } catch (IOException e) {
				e.printStackTrace();
		    }
		}
		return rgResultDecode;
    }
}