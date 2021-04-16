package io.gamioo.sandbox.proxy;

public class ProxyTest {
    public static void main(String[] args) {

        MapService.get(1).march(1001,1);

        MapService.get(2).march(1001,1);
//        MapService mapService=new MapService();
//        AbstractService service   =  MapServiceProxy.getProxy(mapService);
//        service.say("hello world");
//
//        service   =  MapServiceProxy.getProxy(mapService);
//        service.high(100);


    }
}
