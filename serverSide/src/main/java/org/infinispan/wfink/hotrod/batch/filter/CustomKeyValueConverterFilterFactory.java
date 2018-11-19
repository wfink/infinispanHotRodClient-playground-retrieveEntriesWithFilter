package org.infinispan.wfink.hotrod.batch.filter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import org.infinispan.filter.AbstractKeyValueFilterConverter;
import org.infinispan.filter.KeyValueFilterConverter;
import org.infinispan.filter.NamedFactory;
import org.infinispan.filter.ParamKeyValueFilterConverterFactory;
import org.infinispan.metadata.Metadata;
import org.infinispan.wfink.hotrod.batch.domain.MySpecialKey;
import org.infinispan.wfink.hotrod.batch.domain.MySpecialValue;

@NamedFactory(name = "CustomFilterConverterFactory")
public class CustomKeyValueConverterFilterFactory implements ParamKeyValueFilterConverterFactory<MySpecialKey, MySpecialValue, MySpecialValue> {

	@Override
   public KeyValueFilterConverter<MySpecialKey, MySpecialValue, MySpecialValue> getFilterConverter(Object[] params) {
      return new CustomKeyValueFilterConverter(params);
   }

	// Filter implementation. Should be serializable or externalizable for DIST caches
	static class CustomKeyValueFilterConverter extends AbstractKeyValueFilterConverter<MySpecialKey, MySpecialValue, MySpecialValue> implements Serializable {
		private static final Logger log = Logger.getLogger(CustomKeyValueFilterConverter.class.getName());
		private final HashMap<String, String> conditions = new HashMap<String, String>();

		public CustomKeyValueFilterConverter(Object[] params) {
			if (params != null && params.length > 0) {
				for (Object object : params) {
					StringTokenizer t = new StringTokenizer((String)object, "=");
					if(t.countTokens()!=2) throw new IllegalArgumentException("Invalid parameter " + object);
					conditions.put(t.nextToken(), t.nextToken());
				}
			}
		}

		@Override
		public MySpecialValue filterAndConvert(MySpecialKey key, MySpecialValue entity, Metadata metadata) {
			// returning null will case the entry to be filtered out
			// return SampleEntity2 will convert from the cache type
			// SampleEntity1
			log.info("key '" + key + "'  entity '" + entity + "' Meta " + metadata);
			boolean match = true;
			if(conditions.containsKey("keyNameContains")) {
				if(!key.getName().contains(conditions.get("keyNameContains"))) match = false;
			}
			if(match && conditions.containsKey("valueIsDeleted")) {
				if(!entity.getDeleted().equals(Boolean.parseBoolean(conditions.get("valueIsDeleted")))) match = false;
			}
			if(match && conditions.containsKey("valueCounter")) {
				if(entity.getCounter() != Integer.parseInt(conditions.get("valueCounter"))) match = false;
			}
			return match ? entity : null;
		}
	}
}