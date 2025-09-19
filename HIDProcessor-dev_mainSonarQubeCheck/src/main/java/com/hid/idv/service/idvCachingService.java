package com.hid.idv.service;

 public interface IdvCachingService {
     public void addToCache(String key, String value, int time);
     public String retrieveFromCache(String key);
     public void removeFromCache(String key);
}
