package org.infinispan.wfink.hotrod.batch.filter;

import java.io.Serializable;
import java.util.logging.Logger;

import org.infinispan.filter.AbstractKeyValueFilterConverter;
import org.infinispan.filter.KeyValueFilterConverter;
import org.infinispan.filter.NamedFactory;
import org.infinispan.filter.ParamKeyValueFilterConverterFactory;
import org.infinispan.metadata.Metadata;

@NamedFactory(name = "SubStringFilterConverterFactory")
public class SubStringKeyValueConverterFilterFactory implements ParamKeyValueFilterConverterFactory<String, String, String> {

	@Override
   public KeyValueFilterConverter<String, String, String> getFilterConverter(Object[] params) {
      return new SubStringKeyValueFilterConverter(params);
   }

	// Filter implementation. Should be serializable or externalizable for DIST caches
	static class SubStringKeyValueFilterConverter extends AbstractKeyValueFilterConverter<String, String, String> implements Serializable {
		private static final Logger log = Logger.getLogger(SubStringKeyValueFilterConverter.class.getName());
		private final String substring;

		public SubStringKeyValueFilterConverter(Object[] params) {
			if (params == null || params.length < 1) {
				substring = null;
			} else {
				substring = (String) params[0];
				if (params.length > 1)
					log.warning("Too many parameters for StringKeyValueConverter " + params);
			}
		}

		@Override
		public String filterAndConvert(String key, String entity, Metadata metadata) {
			// returning null will case the entry to be filtered out
			// return SampleEntity2 will convert from the cache type
			// SampleEntity1
			log.info("key '" + key + "'  entity '" + entity + "' Meta " + metadata);
			if(key.contains(substring)) {
				log.info("Key contains substring '" + substring + "'");
				return entity;
			}
			return null;
		}
	}
}