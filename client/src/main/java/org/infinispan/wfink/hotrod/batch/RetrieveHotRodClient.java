package org.infinispan.wfink.hotrod.batch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.util.CloseableIterator;

public class RetrieveHotRodClient {

   private RemoteCacheManager remoteCacheManager;
   private RemoteCache<String, String> remoteCache;

   public RetrieveHotRodClient(String host, String port) {
      ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
      remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
      remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
      remoteCache = remoteCacheManager.getCache();
   }

	private void insert(Map<String, String> values) {
		System.out.println("Inserting data into cache...");
		for (int i = 0; i < 10; i++) {
			for (Map.Entry<String, String> entry : values.entrySet()) {
				remoteCache.put(entry.getKey(), entry.getValue());
			}
		}
	}

   private void verify(Map<String, String> values) {
      System.out.println("\nVerifying data...");
      
      HashMap<String,String> verify = new HashMap<String, String>(values);

      for (Map.Entry<String, String> entry : values.entrySet()) {
    	  String key = entry.getKey();
         String value = remoteCache.get(entry.getKey());
         if (value == null) {
            System.out.println(" No value for '" + key + "' found!");
         } else if (!value.equals(entry.getValue())) {
            System.out.println(" Key:" + key + " Value '" + value + "' differ from '" + entry.getValue() + "'");
         } else {
            System.out.println(" Key:" + key + " ok");
         }
         verify.remove(entry.getKey());
      }
      if(verify.size() != 0) {
    	  System.out.println("  Missing entries are : " + verify);
      }
   }
   
   private void removeEntriesFiltered(String substring) {
	   try (CloseableIterator<Entry<Object, Object>> iterator = remoteCache.retrieveEntries("SubStringFilterConverterFactory", new String[] { substring }, null, 1)) {
		   iterator.forEachRemaining(e -> {
		      System.out.println("----  delete -> " + e);
		      remoteCache.remove(e.getKey());
		   });
		}
   }
   
   private void removeEntriesHardcoded() {
	   try (CloseableIterator<Entry<Object, Object>> iterator = remoteCache.retrieveEntries("HardcodedStringFilterConverterFactory", 1)) {
		   iterator.forEachRemaining(e -> {
		      System.out.println("----  delete -> " + e);
		      remoteCache.remove(e.getKey());
		   });
		}
   }

   private void stop() {
      remoteCacheManager.stop();
   }

   public static void main(String[] args) {
      String host = "localhost";
      String port = "11222";

      if (args.length > 0) {
         port = args[0];
      }
      if (args.length > 1) {
         port = args[1];
      }
      RetrieveHotRodClient client = new RetrieveHotRodClient(host, port);

      // create data
      HashMap<String, String> values = new HashMap<String, String>();
      values.put("blaTest1bla", "bla Test1 bla value");
      values.put("xxxxTest2xxxx", "xxx Test2 value");
      values.put("xxxxTest1xx", "xxxx Test1 xx value");
      values.put("yyyTest2yyyyy", "yyy Test2 yyyyy value");
      values.put("zzzTest1zzz", "zzz Test1 zzz value");
      values.put("zzzTest2zzz", "zzz Test2 zzz value");
      client.insert(values);
      // check data
      client.verify(values);
            
      // remove the entries from the cache
      client.removeEntriesFiltered("Test1");
      values.remove("xxxxTest1xx");
      values.remove("zzzTest1zzz");
      values.remove("blaTest1bla");
      // check the entries are deleted
      client.verify(values);
      
      client.removeEntriesHardcoded();  // all 'yTest2y'
      values.remove("yyyTest2yyyyy");
      client.verify(values);
      
      client.stop();
      System.out.println("\nDone !");
   }
}
