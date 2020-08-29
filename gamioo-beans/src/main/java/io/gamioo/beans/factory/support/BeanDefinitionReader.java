package io.gamioo.beans.factory.support;

public interface BeanDefinitionReader {
	void loadBeanDefinitions(String location) throws Exception;
}
