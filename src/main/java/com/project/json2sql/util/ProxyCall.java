package com.project.json2sql.util;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

import com.google.gson.Gson;
import com.project.json2sql.dto.InputProxyDto;
import com.project.json2sql.dto.Root;
import com.project.json2sql.model.ConfigProperties;
import com.project.json2sql.model.OwnerProcess;

public class ProxyCall {
	
/*
	public static void main(String s[]) throws Exception {
		try {
			Properties systemSettings = System.getProperties();
			systemSettings.put("proxySet", "true");
			systemSettings.put("http.proxyHost", "proxy.mycompany1.local");
			systemSettings.put("http.proxyPort", "80");

			URL u = new URL("http://www.google.com");
			HttpURLConnection con = (HttpURLConnection)u.openConnection();
			System.out.println(con.getResponseCode() + " : " + con.getResponseMessage());
			System.out.println(con.getResponseCode() == HttpURLConnection.HTTP_OK);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(false);
		}
		//Check proxy
		System.setProperty("java.net.useSystemProxies", "true");
		Proxy proxy = (Proxy) ProxySelector.getDefault().select(new URI(
				"http://www.yahoo.com/")).iterator().
				next();;
				System.out.println("proxy hostname : " + proxy.type());
				InetSocketAddress addr = (InetSocketAddress)proxy.address();

				if (addr == null) {
					System.out.println("No Proxy");
				} else {
					System.out.println("proxy hostname : " + addr.getHostName());
					System.out.println("proxy port : " + addr.getPort());
				}
	}*/

	public static Root callProxy(ConfigProperties configDtoObj, InputProxyDto inputObj) throws Exception {
		Root rootObj = new Root();
		URL u = new URL(configDtoObj.getUrl());
		HttpURLConnection conn = (HttpURLConnection)u.openConnection();
		try {
			/*Properties systemSettings = System.getProperties();
			systemSettings.put("proxySet", "true");
			systemSettings.put("http.proxyHost", configPropertiesObj.getHost());
			systemSettings.put("http.proxyPort", configPropertiesObj.getPort());*/

			conn.setDoOutput( true );
			conn.setInstanceFollowRedirects( false );
			conn.setRequestMethod( "POST" );
			conn.setRequestProperty( "Content-Type", "application/json"); 
			conn.setRequestProperty( "charset", "utf-8");
			conn.setRequestProperty( "Authorization", configDtoObj.getAuthorization());
			conn.setRequestProperty( "Accept", "application/json");
			conn.setRequestProperty( "Accept-Encoding", "gzip");
			conn.setRequestProperty( "Cache-Control", "no-cache");
			//conn.setRequestProperty( "User-Agent", "");
			conn.setUseCaches( false );
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setConnectTimeout(5000);

			//conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

			OutputStream out = conn.getOutputStream(); 
			//out.write(inputObj.toString().getBytes()); 
			out.write(inputObj.toString().getBytes("UTF-8"));
			out.close();
			
			// read the response
			/*InputStream ip = conn.getInputStream(); 
			BufferedReader br1 = new BufferedReader(new InputStreamReader(ip)); */
			
			InputStream in = new BufferedInputStream(conn.getInputStream());
            String result = IOUtils.toString(in, "UTF-8");
            /*JSONParser parser = new JSONParser(); 
            JSONObject json = (JSONObject) parser. parse(result);*/
            
            Gson g = new Gson(); 
            rootObj = g.fromJson(result, Root.class);
            		
            //JSONObject jsonObject = new JSONObject(result);
			
			System.out.println(conn.getResponseCode() + " : " + conn.getResponseMessage());
			System.out.println(conn.getResponseCode() == HttpURLConnection.HTTP_OK);
			
			if(conn.getResponseCode() !=200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ conn.getResponseCode());
			}

			System.out.println("Using proxy:" + conn.usingProxy()); 
			
			/*StringBuilder response = new StringBuilder(); 
			String responseSingle = null; 
			while ((responseSingle = br1.readLine()) != null)  
			{ 
				response.append(responseSingle); 
			} 
			String result = response.toString(); 
			System.out.println(result); */

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(false);
		}finally {
			conn.disconnect();
		}
		
		return rootObj;
	}
}
