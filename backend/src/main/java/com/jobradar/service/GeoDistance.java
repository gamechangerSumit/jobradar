package com.jobradar.service;
public final class GeoDistance {
 private static final double EARTH_KM=6371.0088;
 private GeoDistance(){}
 public static double betweenKm(double lat1,double lon1,double lat2,double lon2){
  double dLat=Math.toRadians(lat2-lat1),dLon=Math.toRadians(lon2-lon1);
  double a=Math.pow(Math.sin(dLat/2),2)+Math.cos(Math.toRadians(lat1))*Math.cos(Math.toRadians(lat2))*Math.pow(Math.sin(dLon/2),2);
  return 2*EARTH_KM*Math.asin(Math.sqrt(a));
 }
}