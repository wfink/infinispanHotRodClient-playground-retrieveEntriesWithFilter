package org.infinispan.wfink.hotrod.batch;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.infinispan.client.hotrod.RemoteCache;
import org.infinispan.client.hotrod.RemoteCacheManager;
import org.infinispan.client.hotrod.configuration.ConfigurationBuilder;
import org.infinispan.commons.util.CloseableIterator;
import org.infinispan.wfink.hotrod.batch.domain.MySpecialKey;
import org.infinispan.wfink.hotrod.batch.domain.MySpecialValue;

public class RetrieveCustomObjectHotRodClient {

   private RemoteCacheManager remoteCacheManager;
   private RemoteCache<MySpecialKey, MySpecialValue> remoteCache;

   public RetrieveCustomObjectHotRodClient(String host, String port) {
      ConfigurationBuilder remoteBuilder = new ConfigurationBuilder();
      remoteBuilder.addServer().host(host).port(Integer.parseInt(port));
      remoteCacheManager = new RemoteCacheManager(remoteBuilder.build());
      remoteCache = remoteCacheManager.getCache();
   }

	private void insert(Map<MySpecialKey, MySpecialValue> values) {
		System.out.println("Inserting data into cache...");
		for (int i = 0; i < 10; i++) {
			for (Map.Entry<MySpecialKey, MySpecialValue> entry : values.entrySet()) {
				remoteCache.put(entry.getKey(), entry.getValue());
			}
		}
	}

   private void verify(Map<MySpecialKey, MySpecialValue> values) {
      System.out.println("\nVerifying data...");
      
      HashMap<MySpecialKey, MySpecialValue> verify = new HashMap<MySpecialKey, MySpecialValue>(values);

      for (Map.Entry<MySpecialKey, MySpecialValue> entry : values.entrySet()) {
    	  MySpecialKey key = entry.getKey();
         MySpecialValue value = remoteCache.get(entry.getKey());
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
   
   private void removeEntriesFiltered(String[] conditions) {
	   try (CloseableIterator<Entry<Object, Object>> iterator = remoteCache.retrieveEntries("CustomFilterConverterFactory", conditions, null, 1)) {
		   iterator.forEachRemaining(e -> {
		      System.out.println("----  delete -> " + e);
		      remoteCache.remove(e.getKey());
		   });
		}
   }
   
   private void checkSize(int expected) {
	  if(remoteCache.size() != expected) throw new RuntimeException("Cache has not the expected size " + expected + " but is "+remoteCache.size());
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
      RetrieveCustomObjectHotRodClient client = new RetrieveCustomObjectHotRodClient(host, port);

      // create data
      HashMap<MySpecialKey, MySpecialValue> values = new HashMap<MySpecialKey, MySpecialValue>();
      values.put(new MySpecialKey("blaTest1bla", 0), new MySpecialValue("bla Test1 bla value"));
      values.put(new MySpecialKey("xxxxTest2xxxx", 0), new MySpecialValue("xxx Test2 value"));
      values.put(new MySpecialKey("xxxxTest1xx", 0), new MySpecialValue("xxxx Test1 xx value"));
      values.put(new MySpecialKey("yyyTest2yyyyy", 0), new MySpecialValue("yyy Test2 yyyyy value"));
      values.put(new MySpecialKey("zzzTest1zzz", 0), new MySpecialValue("zzz Test1 zzz value", 1, false));
      values.put(new MySpecialKey("zzzTest2zzz", 0), new MySpecialValue("zzz Test2 zzz value", 0, true));
      client.insert(values);
      // check data
      client.verify(values);
            
      // remove the entries from the cache
      client.removeEntriesFiltered(new String[] {"keyNameContains=yyyy"});
      // check the entries are deleted
      client.checkSize(5);

      client.removeEntriesFiltered(new String[] {"valueIsDeleted=true"});
      client.checkSize(4);
      
      client.removeEntriesFiltered(new String[] {"valueCounter=1"});
      client.checkSize(3);
      
      client.stop();
      System.out.println("\nDone !");
   }
}
