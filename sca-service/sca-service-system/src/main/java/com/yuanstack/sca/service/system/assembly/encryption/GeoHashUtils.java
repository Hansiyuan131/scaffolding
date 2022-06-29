package com.yuanstack.sca.service.system.assembly.encryption;

import ch.hsr.geohash.GeoHash;
import ch.hsr.geohash.WGS84Point;
import com.yuanstack.sca.service.system.common.log.LogUtils;
import lombok.extern.slf4j.Slf4j;

import static com.yuanstack.sca.service.system.common.constants.ModelEnum.COMMON_UTILS;

/**
 * @description: GeoHashUtils
 * @author: hansiyuan
 * @date: 2022/6/29 11:50 AM
 */
@Slf4j
public class GeoHashUtils {

    /**
     * GeoHash编码字符的长度（最大为12）
     */
    private static final int PRECISION = 8;

    /**
     * 获取GeoHash字符串，默认8位
     */
    public static String encode(double lat, double lng) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lng, PRECISION);

        return geoHash.toBase32();
    }

    /**
     * 获取GeoHash 5位:2.4公里
     */
    public static String encode(double lat, double lng, int precision) {
        GeoHash geoHash = GeoHash.withCharacterPrecision(lat, lng, precision);

        return geoHash.toBase32();
    }

    /**
     * 解码，转换为经纬度
     */
    public static WGS84Point decode(String geoHashStr) {
        GeoHash geoHash = GeoHash.fromGeohashString(geoHashStr);

        return geoHash.getOriginatingPoint();
    }

    public static void main(String[] args) {
        // 纬度坐标
        double lat = 30.549608;
        // 经度坐标
        double lng = 114.376971;

        LogUtils.info(log, COMMON_UTILS, encode(lat, lng));
    }
}
