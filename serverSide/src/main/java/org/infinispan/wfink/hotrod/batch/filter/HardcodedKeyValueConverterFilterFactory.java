package org.infinispan.wfink.hotrod.batch.filter;

import java.io.Serializable;
import java.util.logging.Logger;

import org.infinispan.filter.AbstractKeyValueFilterConverter;
import org.infinispan.filter.KeyValueFilterConverter;
import org.infinispan.filter.KeyValueFilterConverterFactory;
import org.infinispan.filter.NamedFactory;
import org.infinispan.metadata.Metadata;

@NamedFactory(name = "HardcodedStringFilterConverterFactory")
public class HardcodedKeyValueConverterFilterFactory implements KeyValueFilterConverterFactory<String, String, String> {

	@Override
   public KeyValueFilterConverter<String, String, String> getFilterConverter() {
      return new SubStringKeyValueFilterConverter();
   }

	// Filter implementation. Should be serializable or externalizable for DIST caches
	static class SubStringKeyValueFilterConverter extends AbstractKeyValueFilterConverter<String, String, String> implements Serializable {
		private static final Logger log = Logger.getLogger(SubStringKeyValueFilterConverter.class.getName());

		@Override
		public String filterAndConvert(String key, String entity, Metadata metadata) {
			// returning null will case the entry to be filtered out
			// return SampleEntity2 will convert from the cache type
			// SampleEntity1
			log.info("key '" + key + "'  entity '" + entity + "' Meta " + metadata);
			if(key.contains("yTest2y")) {
				log.info("Key contains 'yTest2y'");
				return entity;
			}
			return null;
		}
	}
}