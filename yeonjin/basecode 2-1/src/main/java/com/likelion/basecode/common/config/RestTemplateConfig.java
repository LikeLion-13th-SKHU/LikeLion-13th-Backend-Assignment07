package com.likelion.basecode.common.config;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // 기존 컨버터 리스트 복사 (기본값)
        List<HttpMessageConverter<?>> converters = new ArrayList<>(restTemplate.getMessageConverters());

        // XML 메시지 컨버터 추가
        converters.add(new MappingJackson2XmlHttpMessageConverter(new XmlMapper()));

        restTemplate.setMessageConverters(converters);

        return restTemplate;
    }
}
