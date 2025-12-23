package com.redis.learning.geoSpatial;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.args.GeoUnit;
import redis.clients.jedis.params.GeoSearchParam;
import redis.clients.jedis.resps.GeoRadiusResponse;

import java.util.List;

public class RedisGeoService {
    private final JedisPool jedisPool ;
    public RedisGeoService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public void addRestaurants(String restaurantId, double longitude, double latitude){
        try (Jedis jedis = jedisPool.getResource()){
            jedis.geoadd("restaurant", longitude, latitude, restaurantId);
        }
    }

    public List<GeoRadiusResponse> getNeraByRestaurants(double longitude, double latitude, double radius, int unit){
        try(Jedis jedis = jedisPool.getResource()){
            GeoSearchParam geoSearchParam = new GeoSearchParam();
            geoSearchParam.count(unit);
            geoSearchParam.byRadius(radius, GeoUnit.KM);
            geoSearchParam.fromLonLat(longitude, latitude);
            return jedis.geosearch("restaurant", geoSearchParam);
        }
    }
}
